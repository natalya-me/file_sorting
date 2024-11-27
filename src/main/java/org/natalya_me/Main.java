package org.natalya_me;

import org.natalya_me.algorithm.DirectedGraph;

import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        try {
            String path = args[0];
            // TODO a path to a place for writing the result
            Map<String, Set<String>> dependencies = DependencyReader.getDependencyMap(path, true);
            DirectedGraph graph = DirectedGraph.createFromAdjacencyList(dependencies);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("A mandatory argument 'path' was not provided.");
        }
    }
}