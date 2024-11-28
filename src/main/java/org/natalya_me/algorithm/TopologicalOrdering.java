package org.natalya_me.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Class for sorting directed graph nodes using topological ordering algorithm.
 * Uses Comparable instance as a secondary sorting rule.
 * One instance of this class can be used for sorting multiple graphs.
 */
public class TopologicalOrdering {

    private final Comparator<? super DirectedGraph.Node> comparator;

    public TopologicalOrdering(Comparator<? super DirectedGraph.Node> comparator) {
        this.comparator = comparator;
    }

    public TopologicalOrderingResult sort(DirectedGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("graph cannot be null");
        }
        if (graph.isEmpty()) return TopologicalOrderingResult.EMPTY_RESULT;

        // Creating a copy of the graph because we are going to remove visited nodes from it
        graph = graph.deepCopy();
        // List of sorted node ids
        List<String> resultList = new ArrayList<>(graph.size());
        // Set of nodes that don't have incoming arcs which are sorted by the comparator
        SortedSet<DirectedGraph.Node> nodesToInsert = new TreeSet<>(comparator);
        nodesToInsert.addAll(graph.getNodes(DirectedGraph.Node::referenceFromIsEmpty));

        while (!nodesToInsert.isEmpty()) {
            // Node with the lowest value according to the comparator
            DirectedGraph.Node node = nodesToInsert.first();
            nodesToInsert.remove(node);
            // Add to the result list
            resultList.add(node.getId());
            // Add all target nodes to nodesToInsert if they don't have any other incoming arcs
            for (DirectedGraph.Node referenceTo: node.getReferenceToCopy()) {
                graph.removeArc(node.getId(), referenceTo.getId());
                if (referenceTo.referenceFromIsEmpty()) {
                    nodesToInsert.add(referenceTo);
                }
            }
            // Remove current node from the graph
            graph.removeNode(node.getId());
        }
        if (!graph.isEmpty()) {
            return new TopologicalOrderingResult(TopologicalOrderingResult.TYPE.CYCLE, CycleDetection.findCycle(graph));
        }
        return new TopologicalOrderingResult(TopologicalOrderingResult.TYPE.ORDER, resultList);
    }

    /**
     * Container for the sorting algorithm result.
     */
    public static class TopologicalOrderingResult {

        private static final TopologicalOrderingResult EMPTY_RESULT = new TopologicalOrderingResult(TYPE.ORDER, Collections.emptyList());

        public enum TYPE {
            ORDER, CYCLE
        }
        private final TYPE type;
        private final List<String> result;

        private TopologicalOrderingResult(TYPE type, List<String> result) {
            this.type = type;
            this.result = new ArrayList<>(result);
        }

        public TYPE getType() {
            return type;
        }

        public List<String> getResult() {
            return result;
        }
    }
}
