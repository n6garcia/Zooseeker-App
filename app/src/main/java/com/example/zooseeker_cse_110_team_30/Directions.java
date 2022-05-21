package com.example.zooseeker_cse_110_team_30;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModelProvider;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for calculating the visit plan route through the zoo.
 */
public class Directions {
    private static final Graph<String, IdentifiedWeightedEdge> graph;
    private static final Map<String, ZooData.VertexInfo> vertexInfo;
    private static final Map<String, ZooData.EdgeInfo> edgeInfo;

    private static final ExhibitDao dao;
    private static List<Exhibit> visited; //DO NOT MODIFY OUTSIDE OF findVisitPlan()!!!!!!

    static{
        Context context = MainActivity.getAppContext();
        dao = ExhibitDatabase.getSingleton(context).exhibitDao(); //TODO bad practice? should be thru viewmodel
        visited = new ArrayList<>();

        graph = ZooData.loadZooGraphJSON(context, "zoo_graph.json");
        vertexInfo = ZooData.loadVertexInfoJSON(context, "node_info.json");
        edgeInfo = ZooData.loadEdgeInfoJSON(context, "edge_info.json");
    }

    public static Graph<String, IdentifiedWeightedEdge> getGraph() {
        return graph;
    }

    public static Map<String, ZooData.VertexInfo> getVertexInfo() {
        return vertexInfo;
    }

    public static Map<String, ZooData.EdgeInfo> getEdgeInfo() {
        return edgeInfo;
    }

    /**
     * Finds the shortest path from one zoo location to another
     *
     * @param start starting location — MUST be a node in zoo_graph.json
     * @param end ending location — MUST be a node in zoo_graph.json
     * @return list of edges representing shortest path from start to end
     */
    public static List<IdentifiedWeightedEdge> findShortestPath(Exhibit start, Exhibit end) {
        return DijkstraShortestPath.findPathBetween(graph, start.identity, end.identity)
                .getEdgeList();
    }

    /**
     * Calculates the total weight along a given list of edges
     *
     * @param edge_list list of edges that represent a path
     * @return total weight along list of edges
     */
    public static int calculatePathWeight(List<IdentifiedWeightedEdge> edge_list) {
        int weight = 0;
        for (IdentifiedWeightedEdge e : edge_list) {
            weight += graph.getEdgeWeight(e);
        }
        return weight;
    }

    /**
     * Very general method - gets the closest unvisited Exhibit to the parameter Exhibit by edge weight
     * Note: used for intended route
     * @param curr_exhibit the Exhibit to search around
     * @return the Exhibit object which is the closest by edge weight (Dijkstra's)
     */
    public static Exhibit getClosestUnvisitedExhibit(Exhibit curr_exhibit) {
        List<Exhibit> unvisited = dao.getUnvisited();
        Exhibit closestTarget = null;
        int shortestDist = Integer.MAX_VALUE;

        // For our current exhibit, find the next closest exhibit in our visit list
        for (Exhibit target : unvisited) {
            // Ignore an exhibit if it's the same as our current exhibit or if it has
            // already been visited or added to visit plan
            if (target.equals(curr_exhibit) || visited.contains(target)) { //TODO does this break anything
                continue;
            }
            //get distance from current exhibit to this candidate exhibit
            int dist = calculatePathWeight(findShortestPath(curr_exhibit, target));
            if (dist < shortestDist) { //new lowest distance
                shortestDist = dist;
                closestTarget = target;
            }
        }

        return closestTarget;
    }

    //TODO
    /**
     * Returns the closest unvisited exhibit from the set of input coordinates.
     * Note: used for off track detection
     * @param latitude the latitude coordinate.
     * @param longitude the longitude coordinate.
     * @return the closest selected yet unvisited exhibit from the given location.
     */
    public static Exhibit getClosestUnvisitedExhibit(double latitude, double longitude) {
        return null;
    }

    //TODO
    /**
     * General method - returns the absolute closest Exhibit object to the given location coordinates.
     * Note: Used for live directions updating
     * @param latitude the latitude coordinate.
     * @param longitude the longitude coordinate.
     * @return the unconditionally closest Exhibit from the given location.
     */
    public static Exhibit getClosestAbsoluteExhibit(double latitude, double longitude) {
        return null;
    }

    /**
     * Given a list of exhibits to visit, finds an optimal path that begins at the entrance,
     * visits each exhibit exactly once, and ends at the exit
     *
     * @param visitList the List of Exhibit objects to find the route through
     * @return list of Exhibits for optimal route
     *
     * Note 1: The first and last elements of route are always the entrance and exit gate.
     * route.get(1) represents the first visited exhibit in the plan, etc.
     *
     * Note 2: For simplicity, we will refer to the entrance/exit gate as an "exhibit" in our
     * below comments
     */
    public static List<Exhibit> findVisitPlan(List<Exhibit> visitList) {
        // Route to return
        List<Exhibit> route = new ArrayList<>();

        // Auxiliary variables
        Exhibit curr_exhibit = dao.get("entrance_exit_gate"); // Set entrance as our starting exhibit
        route.add(curr_exhibit);

        // Given a list of N exhibits to visit, we need to find N-1 optimal "paths"
        for (int idx = 0; idx < visitList.size(); idx++) {
            Exhibit next_exhibit = getClosestUnvisitedExhibit(curr_exhibit);

            route.add(next_exhibit);
            visited.add(curr_exhibit);
            curr_exhibit = next_exhibit; //increment current exhibit
        }
        // Add directions back to entrance
        route.add(dao.get("entrance_exit_gate"));

        visited.clear();
        return route;
    }

    /**
     * Finds the optimal route through all selected Exhibits.
     * Note: Simply calls overloaded findVisitPlan with the list of all exhibits
     *
     * @return list of Exhibits representing the visit plan through all selected Exhibits.
     */
    public static List<Exhibit> findVisitPlan() {
        return findVisitPlan(dao.getSelected());
    }
}
