package me.soshin.fetcher;

import me.soshin.model.Edge;
import me.soshin.model.Graph;
import me.soshin.model.Node;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static fetcher that returns an example graph based on Seinfeld characters
 */
@Component
public class SeinfeldFetcher implements Fetcher {
    public Graph fetch() {
        final List<Node> nodes = new ArrayList<>(Arrays.asList(new Node("j", "Jerry"), new Node("e", "Elaine"), new Node("k", "Kramer"), new Node("g", "George")));
        final List<Edge> edges = new ArrayList<>(Arrays.asList(
                new Edge("j", "e", "BCD"),
                new Edge("j", "k"),
                new Edge("j", "g"),
                new Edge("e", "j"),
                new Edge("e", "k"),
                new Edge("k", "j"),
                new Edge("k", "e"),
                new Edge("k", "g"),
                new Edge("g", "j")));
        return new Graph(nodes, edges);
    }
}
