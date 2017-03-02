package me.soshin.model;

import java.util.ArrayList;
import java.util.List;


public class Graph {
    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();


    public Graph() {
    }

    public Graph(final List<Node> nodes, final List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public void add(final Node n) {

        this.nodes.add(n);
    }

    public void add(final Edge e) {
        this.edges.add(e);
    }

    public List<Node> getNodes() {
        return this.nodes;
    }

    public List<Edge> getEdges() {
        return this.edges;
    }
}
