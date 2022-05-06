package com.example.zooseeker_cse_110_team_30;

import android.content.Context;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for calculating the visit plan route through the zoo.
 */
public class Directions {
    public Graph<String, IdentifiedWeightedEdge> graph;
    public Map<String, ZooData.VertexInfo> vertexInfo;
    public Map<String, ZooData.EdgeInfo> edgeInfo;

    public Directions(Context context) {
        graph = ZooData.loadZooGraphJSON(context,"sample_zoo_graph.json");
        vertexInfo = ZooData.loadVertexInfoJSON(context, "sample_node_info.json");
        edgeInfo = ZooData.loadEdgeInfoJSON(context,"sample_edge_info.json");
    }

    public Graph<String, IdentifiedWeightedEdge> getGraph() {
        return this.graph;
    }

    public Map<String, ZooData.VertexInfo> getVertexInfo() {
        return this.vertexInfo;
    }

    public Map<String, ZooData.EdgeInfo> getEdgeInfo() {
        return this.edgeInfo;
    }

    /**
     * Finds the shortest path from one zoo location to another
     *
     * @param start starting location — MUST be a node in sample_zoo_graph.json
     * @param end ending location — MUST be a node in sample_zoo_graph.json
     * @return list of edges representing shortest path from start to end
     */
    public List<IdentifiedWeightedEdge> findShortestPath(String start, String end) {
        return DijkstraShortestPath.findPathBetween(graph, start, end).getEdgeList();
    }

    /**
     * Calculates the total weight along a given list of edges
     *
     * @param edge_list list of edges that represent a path
     * @return total weight along list of edges
     */
    public int calculatePathWeight(List<IdentifiedWeightedEdge> edge_list) {
        int weight = 0;
        for (IdentifiedWeightedEdge e : edge_list) {
            weight += graph.getEdgeWeight(e);
        }
        return weight;
    }

    /**
     * Given a list of exhibits to visit, finds an optimal path that begins at the entrance,
     * visits each exhibit exactly once, and ends at the exit
     *
     * @param visitList list of exhibits to visit
     * @return list of directions for optimal route
     *
     * Note 1: route.get(0) represents the list of edges needed to get from the entrance to
     * the first optimal exhibit. route.get(1) represents the list of edges needed to get from
     * the first optimal exhibit to the second optimal exhibit... and so on and so-forth.
     *
     * Note 2: For simplicity, we will refer to the entrance/exit gate as an "exhibit" in our
     * below comments
     */
    public List<List<IdentifiedWeightedEdge>> findShortestRoute(List<Exhibit> visitList) {
        //unpack identity Strings from exhibits
        List<String> toVisit = new ArrayList<>();
        for(Exhibit e : visitList) { toVisit.add(e.identity); }

        // Route to return
        List<List<IdentifiedWeightedEdge>> route = new ArrayList<>();

        // Used to keep track of exhibits we have already visited
        List<String> visited = new ArrayList<>();

        // Auxiliary variables
        List<IdentifiedWeightedEdge> nextShortestPath = new ArrayList<>(); // Used to keep track of path to next most optimal exhibit
        String curr_exhibit = "entrance_exit_gate"; // Set entrance as our starting exhibit
        String next_exhibit = "";
        int min_dist;
        int curr_dist;

        // Given a list of N exhibits to visit, we need to find N-1 optimal "paths"
        for (int idx = 0; idx < toVisit.size(); idx++) {
            min_dist = Integer.MAX_VALUE;
            nextShortestPath.clear();
            visited.add(curr_exhibit);

            // For our current exhibit, find the next closest exhibit in our visit list
            for (String exhibit : toVisit) {

                // Ignore an exhibit if it's the same as our current exhibit or if it has
                // already been visited
                if (exhibit.equals(curr_exhibit) || visited.contains(exhibit)) {
                    continue;
                }
                List<IdentifiedWeightedEdge> path = findShortestPath(curr_exhibit, exhibit);
                curr_dist = calculatePathWeight(path);
                if (curr_dist < min_dist) {
                    min_dist = curr_dist;
                    next_exhibit = exhibit;
                    nextShortestPath = path;
                }
            }
            curr_exhibit = next_exhibit;
            route.add(nextShortestPath);
        }

        // Add directions back to entrance
        route.add(findShortestPath(curr_exhibit, "entrance_exit_gate"));

        return route;
    }
}
