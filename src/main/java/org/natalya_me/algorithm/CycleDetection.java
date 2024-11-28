package org.natalya_me.algorithm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class for finding a cycle in a directed graph. If there are multiple cycles in the graph, it detects only one of them.
 */
public class CycleDetection {

    private CycleDetection() {
        throw new UnsupportedOperationException(String.format("Instantiation of class %s is forbidden.", CycleDetection.class.getName()));
    }

    /**
     * Finds a cycle in the graph if it is present.
     *
     * @param graph a directed graph
     * @return a list of node ids that make a cycle (in a corresponding order)
     */
    public static List<String> findCycle(DirectedGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("graph cannot be null");
        }
        if (graph.isEmpty()) return Collections.emptyList();

        // Start searching with a node that might be a member of a cycle, i.e. nodes that have incoming and outgoing arcs
        Set<DirectedGraph.Node> nodes = graph.getNodes(n -> !n.referenceFromIsEmpty() && !n.referenceToIsEmpty());
        if (nodes.isEmpty()) return Collections.emptyList();

        List<String> cycle = new ArrayList<>();
        // Variable for a copy in case we would have to modify the graph
        DirectedGraph graphCopy = null;
        while (!nodes.isEmpty()) {
            // Visited nodes
            Set<DirectedGraph.Node> visited = new HashSet<>();
            // Visit stack
            Deque<DirectedGraph.Node> stack = new ArrayDeque<>();
            // Any node from the chosen ones
            stack.add(nodes.iterator().next());
            if (detectCycle(visited, stack, cycle)) {
                return cycle;
            }
            if (graphCopy == null) {
                // If all the nodes have been visited, we don't need to continue the algorithm
                if (visited.size() == graph.size()) {
                    return Collections.emptyList();
                }
                // Creating a copy of the graph, removing the nodes that cannot be members of a cycle
                graphCopy = graph.deepCopy();
                for (DirectedGraph.Node n: graphCopy.getNodes(n -> n.referenceFromIsEmpty() || n.referenceToIsEmpty())) {
                    graphCopy.removeNode(n.getId());
                }
            }
            // Remove all visited nodes because they can't be members of a cycle
            for (DirectedGraph.Node v: visited) {
                graphCopy.removeNode(v.getId());
            }
            nodes = graphCopy.getNodes(null);
        }
        return cycle;
    }

    private static boolean detectCycle(Set<DirectedGraph.Node> visited, Deque<DirectedGraph.Node> stack, List<String> cycle) {
        if (stack.isEmpty()) return false;
        DirectedGraph.Node current = stack.peekLast();
        // Mark last node in the stack as visited
        // If already visited, write information about the cycle and return true
        if (!visited.add(current)) {
            stack.pollLast();
            cycle.add(current.getId());
            while (!stack.isEmpty() && (stack.peekLast() != current)) {
                cycle.add(stack.pollLast().getId());
            }
            return true;
        }
        // Visit each incoming node (opposite direction of an arc)
        // It is done so the order of node ids is right in the result list
        for (DirectedGraph.Node from: current.getReferenceFromIterable()) {
            stack.add(from);
            if(detectCycle(visited, stack, cycle)) return true;
        }
        // Pop current element
        stack.pollLast();
        return false;
    }
}
