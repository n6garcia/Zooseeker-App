package com.example.zooseeker_cse_110_team_30;

import android.content.Context;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.lang.Math;

/**
 * Utility class for calculating various routes through the zoo.
 *
 * Note: ALWAYS CALL setContext/setDatabase BEFORE RUNNING ANY OTHER METHODS
 */
public class Directions {
    //variables from ZooData
    private static Graph<String, IdentifiedWeightedEdge> graph;
    private static Map<String, ZooData.VertexInfo> vertexInfo;
    private static Map<String, ZooData.EdgeInfo> edgeInfo;

    private static ExhibitDao dao; //exhibit database

    private static List<Exhibit> visited; //"visited" exhibits to exclude from search

    private static final double latToFeetConverter = 363843.57;
    private static final double lonToFeetConverter = 307515.50;

    /**
     * Getter for the static zoo graph
     * @return the graph object Directions uses for pathfinding.
     */
    public static Graph<String, IdentifiedWeightedEdge> getGraph() {
        return graph;
    }

    /**
     * Getter for the static vertex data structure.
     * @return the vertex info map Directions uses for getting details about vertices.
     */
    public static Map<String, ZooData.VertexInfo> getVertexInfo() {
        return vertexInfo;
    }

    /**
     * Getter for the static edge data structure.
     * @return the edge info map Directions uses for getting details about edges.
     */
    public static Map<String, ZooData.EdgeInfo> getEdgeInfo() {
        return edgeInfo;
    }

    /**
     * Getter for the exhibit database.
     * @return the exhibit database Directions uses to get Exhibit objects
     */
    public static ExhibitDao getDao() {
        return dao;
    }

    /**
     * Sets the application context of Directions.
     * Note: ALWAYS CALL THIS METHOD BEFORE USING ANY OTHER METHODS
     * @param cont the Context that Directions uses to initialize everything
     */
    public static void setContext(Context cont) {
        setDatabase(cont, ExhibitDatabase.getSingleton(cont)); //get database from ExhibitDatabase
    }

    /**
     * Sets the application context and ExhibitDatabase of Directions. Mainly for testing.
     * @param cont the Context that Directions uses to initialize everything
     */
    public static void setDatabase(Context cont, ExhibitDatabase exhibitDatabase) {
        dao = exhibitDatabase.exhibitDao();
        visited = new ArrayList<>(); //need arraylist features

        //load our static graph info variables
        graph = ZooData.loadZooGraphJSON(cont, "zoo_graph.json");
        vertexInfo = ZooData.loadVertexInfoJSON(cont, "node_info.json");
        edgeInfo = ZooData.loadEdgeInfoJSON(cont, "edge_info.json");
    }

    /**
     * Finds the shortest path from one zoo location to another
     * @param start starting location — MUST be a node in zoo_graph.json
     * @param end ending location — MUST be a node in zoo_graph.json
     * @return list of edges representing shortest path from start to end
     */
    public static List<IdentifiedWeightedEdge> findShortestPath(Exhibit start, Exhibit end) {
        return DijkstraShortestPath.findPathBetween(graph, getParent(start).identity, getParent(end).identity)
                .getEdgeList();
    }

    /**
     * Calculates the total weight along a given list of edges
     * @param edgeList list of edges that represent a path
     * @return total weight along list of edges
     */
    public static int calculatePathWeight(List<IdentifiedWeightedEdge> edgeList) {
        int weight = 0;
        for (IdentifiedWeightedEdge e : edgeList) {
            weight += graph.getEdgeWeight(e);
        }
        return weight;
    }

    /**
     * Returns the parent Exhibit of this exhibit.
     * @param exhibit The Exhibit to get the parent of.
     * @return The exhibit group that contains this exhibit, or the exhibit itself if not grouped.
     */
    public static Exhibit getParent(Exhibit exhibit) {
        if(exhibit.group_id == null) {
            return exhibit; //not part of group, just return itself
        }
        return dao.get(exhibit.group_id); //return exhibit's parent
    }

    /**
     * Gets the closest unvisited Exhibit to the parameter Exhibit by edge weight
     * Note: used for intended route and off track detection
     * @param curr_exhibit the Exhibit to search around
     * @return the Exhibit object which is the closest by edge weight (Dijkstra's)
     */
    public static Exhibit getClosestUnvisitedExhibit(Exhibit curr_exhibit) {
        List<Exhibit> unvisited = dao.getUnvisited(); //only search unvisited exhibits
        Exhibit closestTarget = null;
        int shortestDist = Integer.MAX_VALUE;

        // For our current exhibit, find the next closest exhibit in our visit list
        for (Exhibit target : unvisited) {
            // Ignore an exhibit if it's already been visited
            if (target.visited != -1) {
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

    /**
     * Gets the closest unvisited Exhibit (not in this.visited) to the parameter Exhibit by edge weight
     * Note: used for intended route plan
     * @param curr_exhibit the Exhibit to search around
     * @return the Exhibit object which is the closest by edge weight (Dijkstra's)
     */
    public static Exhibit getClosestUnvisitedForPlan(Exhibit curr_exhibit) {
        List<Exhibit> toVisit = dao.getSelected(); //consider all selected exhibits
        Exhibit closestTarget = null;
        int shortestDist = Integer.MAX_VALUE;

        // For our current exhibit, find the next closest exhibit in our visit list
        for (Exhibit target : toVisit) {
            // Ignore an exhibit if it's already been added to visit plan
            if(visited.contains(target)) {
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

    /**
     * Returns the distance (in feet) between two coordinates specified in lat/lon
     * @param exibLatDeg The latitude of the Exhibit, in degrees
     * @param exibLonDeg The longitude of the Exhibit, in degrees
     * @param userLatDeg The latitude of the user, in degrees
     * @param userLonDeg The longitude of the user, in degrees
     * @return distance in feet between two lat/lon points
     */
    public static double getDistanceDifference(double exibLatDeg, double exibLonDeg, double userLatDeg, double userLonDeg) {

        // Convert lat/lon to feet representation
        double exibLatFeet = exibLatDeg * latToFeetConverter;
        double exibLonFeet = exibLonDeg * lonToFeetConverter;
        double userLatFeet = userLatDeg * latToFeetConverter;
        double userLonFeet = userLonDeg * lonToFeetConverter;

        // Distance formula
        return Math.sqrt(Math.pow((exibLatFeet - userLatFeet), 2) + Math.pow((exibLonFeet - userLonFeet), 2));
    }

    /**
     * Gets the closest but different unvisited Exhibit to the parameter Exhibit by edge weight
     * Note: used for next preview
     * @param curr_exhibit the Exhibit to search around
     * @return the closest Exhibit object by edge weight (Dijkstra's), excluding curr_exhibit
     */
    public static Exhibit getNextUnvisitedExhibit(Exhibit curr_exhibit) {
        visited.add(curr_exhibit); //don't want to return itself
        Exhibit next = getClosestUnvisitedForPlan(curr_exhibit);
        visited.clear(); //reset visited

        return next;
    }

    /**
     * General method - returns the absolute closest Exhibit object to the given location coordinates.
     * Note: Used for live directions updating
     * @param userLat the latitude coordinate.
     * @param userLon the longitude coordinate.
     * @return the unconditionally closest Exhibit from the given location.
     */
    public static Exhibit getClosestAbsoluteExhibit(double userLat, double userLon) {
        List<Exhibit> zooNodes = dao.getAll(); //consider all exhibits
        double minDist = Double.MAX_VALUE;
        Exhibit closestExhibit = null;

        for (Exhibit exhibit : zooNodes) {
            if (getDistanceDifference(exhibit.latitude, exhibit.longitude, userLat, userLon) < minDist) {
                minDist = getDistanceDifference(exhibit.latitude, exhibit.longitude, userLat, userLon);
                closestExhibit = exhibit;
            }
        }

        return closestExhibit;
    }

    /**
     * Given a list of exhibits to visit, finds an optimal path that begins at the entrance,
     * visits each exhibit exactly once, and ends at the exit
     *
     * @param visitList the List of Exhibit objects to find the route through
     * @return ordered list of Exhibits representing optimal route
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
        visited.clear(); //reset in case there is garbage data inside

        // Auxiliary variables
        Exhibit curr_exhibit = dao.get("entrance_exit_gate"); // Set entrance as our starting exhibit
        route.add(curr_exhibit);

        // Given a list of N exhibits to visit, we need to find N-1 optimal "paths"
        for (int idx = 0; idx < visitList.size(); idx++) {
            visited.add(curr_exhibit);
            Exhibit next_exhibit = getClosestUnvisitedForPlan(curr_exhibit);

            route.add(next_exhibit);
            curr_exhibit = next_exhibit; //increment current exhibit
        }
        // Add directions back to entrance
        route.add(dao.get("entrance_exit_gate"));

        visited.clear(); //reset visited
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
