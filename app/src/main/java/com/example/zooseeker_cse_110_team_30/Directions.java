package com.example.zooseeker_cse_110_team_30;

import org.jgrapht.Graph;

import java.util.Map;

public class Directions {
    Graph<String, IdentifiedWeightedEdge> graph;
    Map<String, ZooData.VertexInfo> vertices;
    Map<String, ZooData.EdgeInfo> edges;

    public Directions() {
        graph = ZooData.loadZooGraphJSON("sample_zoo_graph.json");
        vertices = ZooData.loadVertexInfoJSON("sample_node_info.json");
        edges = ZooData.loadEdgeInfoJSON("sample_edge_info.json");
    }

    /**
     * for Galen and Liam
     *  -look at App.java file to see how to use instance variables in this class to calculate
     *  shortest path between two hard coded vertices
     *  -adjust file to your needs
     */
    public void calculateShortestRoute() {

    }
}
