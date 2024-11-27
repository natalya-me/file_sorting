package org.natalya_me.algorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A class of a directed graph. Cycles are allowed.
 * Nodes are identified by a string id. Note that a node cannot be modified or replaced after creation.
 * <p>
 * Not thread safe.
 */
public class DirectedGraph {

    private final Map<String, Node> nodes = new HashMap<>();

    /**
     * Creates a directed graph using the given adjacency lists of ids.
     * Note that nodes for EACH id in the map are created, not only key ids.
     *
     * @param adjacencyList Map of node ids, there key is a source node id and value is a collection of all target ids for the key.
     * @return an instance of {@link org.natalya_me.algorithm.DirectedGraph}
     * @param <T> container type
     */
    public static <T extends Collection<String>> DirectedGraph createFromAdjacencyList(Map<String, T> adjacencyList) {
        DirectedGraph graph = new DirectedGraph();
        for (Map.Entry<String, T> e: adjacencyList.entrySet()) {
            Node from = graph.addOrFindNode(e.getKey());
            for (String toId: e.getValue()) {
                Node to = graph.addOrFindNode(toId);
                graph.addArc(from, to);
            }
        }
        return graph;
    }

    public DirectedGraph() {

    }

    /**
     * Add a new node with the given id.
     * If a node with this id already exists, a new node won't be created.
     *
     * @param id  id of a node
     * @return    true if a node was successfully created, false otherwise
     *
     * @throws NullPointerException when id == null
     */
    public boolean addNode(String id) {
        if (nodes.containsKey(id)) return false;
        nodes.put(id, new Node(id));
        return true;
    }

    /**
     * Creates an arc between nodes with the given ids.
     *
     * @param idFrom id of the source node
     * @param idTo   id of the target node
     * @return       true if both nodes exist and an arc between was created;
     *               false if at least one of the nodes doesn't exit or the arc already exists
     */
    public boolean addArc(String idFrom, String idTo) {
        Node from = nodes.get(idFrom);
        if (from == null) return false;
        Node to = nodes.get(idTo);
        if (to == null) return false;
        return addArc(from, to);
    }

    /**
     * Remove a node with the given id from the graph, including all arcs.
     * It doesn't do anything if there is no such node in the graph.
     *
     * @param id node id
     * @return true if the node existed and was successfully removed.
     */
    public boolean removeNode(String id) {
        Node node = nodes.get(id);
        if (node != null) {
            for (Node from: node.getReferenceFromIterable()) {
                from.removeReferenceTo(node);
            }
            for (Node to: node.getReferenceToIterable()) {
                to.removeReferenceFrom(node);
            }
            return nodes.remove(id) == node;
        }
        return false;
    }

    /**
     * Removes a directed arc between the nodes if it exists.
     *
     * @param idFrom source node id
     * @param idTo   target node id
     */
    public boolean removeArc(String idFrom, String idTo) {
        Node from = nodes.get(idFrom);
        if (from == null) return false;
        Node to = nodes.get(idTo);
        if (to == null) return false;
        return from.removeReferenceTo(to) && to.removeReferenceFrom(from);
    }

    /**
     * Checks if there is a node with the given id in the graph.
     *
     * @param id node id
     * @return   true if the node exists
     */
    public boolean hasNode(String id) {
        return nodes.containsKey(id);
    }

    /**
     * Checks if there is an arc between nodes with the given ids.
     *
     * @param idFrom source node id
     * @param idTo   target node id
     * @return       true if the arc exists
     */
    public boolean hasArc(String idFrom, String idTo) {
        return Optional.ofNullable(nodes.get(idFrom))
                .map(n -> n.hasReferenceTo(nodes.get(idTo)))
                .orElse(false);
    }

    /**
     * Checks if there are no nodes in the graph.
     */
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    /**
     * Creates a new instance of {@link DirectedGraph} with the same set of nodes and arcs as this graph.
     * New graph doesn't depend on this one because it doesn't share any mutable object with it.
     *
     * @return a copy of this graph
     */
    public DirectedGraph deepCopy() {
        DirectedGraph graphCopy = new DirectedGraph();
        for (Node n: nodes.values()) {
            Node nCopy = graphCopy.addOrFindNode(n.getId());
            // It is enough to iterate referenceFrom set, because each arc is represented twice in the graph:
            // in one node's "from" set and another node's "to" set
            for (Node from: n.getReferenceFromIterable()) {
                Node fromCopy = graphCopy.addOrFindNode(from.getId());
                graphCopy.addArc(fromCopy, nCopy);
            }
        }
        return graphCopy;
    }

    protected Node addOrFindNode(String id) {
        return nodes.computeIfAbsent(id, (k) -> new Node(id));
    }

    protected Node findNode(String id) {
        return nodes.get(id);
    }

    private boolean addArc(Node from, Node to) {
        if (from.hasReferenceTo(to)) return false;
        return from.addReferenceTo(to) && to.addReferenceFrom(from);
    }

    /**
     * This class represents a node of {@link DirectedGraph}.
     * Each arc is stored in both source and target nodes.
     * Method {@link #equals(Object)} should not be overridden because reference equality is important in {@link DirectedGraph}.
     * Please, make sure your modifying operations are consistent with lazy initialization of {@link #referenceFrom} and {@link #referenceTo}
     * (use getters when needed).
     * <p>
     * This class is not thread safe.
     */
    protected static class Node {

        private final String id;
        private Set<Node> referenceFrom = Collections.emptySet();
        private Set<Node> referenceTo = Collections.emptySet();
        private boolean refFromInit = false;
        private boolean refToInit = false;


        public Node(String id) {
            this.id = Objects.requireNonNull(id, "Node id cannot be null.");
        }

        /**
         * Checks if there is an arc o -> this
         *
         * @param o source node
         * @return true if current object is referenced by o
         */
        public boolean hasReferenceFrom(Node o) {
            return referenceFrom.contains(o);
        }

        /**
         * Checks if there is an arc this -> o
         *
         * @param o target node
         * @return  true if current object is referencing o
         */
        public boolean hasReferenceTo(Node o) {
            return referenceTo.contains(o);
        }

        /**
         * Check if the current node is not referenced by any other node.
         *
         * @return true if the current node doesn't have any incoming arcs
         */
        public boolean referenceFromIsEmpty() {
            return referenceFrom.isEmpty();
        }

        /**
         * Check if the current node is not referencing any other node.
         *
         * @return true if the current node doesn't have any outgoing arcs
         */
        public boolean referenceToIsEmpty() {
            return referenceTo.isEmpty();
        }

        /**
         * @return node id
         */
        public String getId() {
            return id;
        }

        /**
         * Creates an iterable object for {@link #referenceFrom} set.
         */
        public Iterable<Node> getReferenceFromIterable() {
            return () -> referenceFrom.iterator();
        }

        /**
         * Creates an iterable object for {@link #referenceTo} set.
         */
        public Iterable<Node> getReferenceToIterable() {
            return () -> referenceTo.iterator();
        }

        /**
         * Create an arc o -> this.
         */
        private boolean addReferenceFrom(Node o) {
            return getReferenceFrom().add(o);
        }

        /**
         * Creates an arc this -> o.
         */
        private boolean addReferenceTo(Node o) {
            return getReferenceTo().add(o);
        }

        /**
         * Removes the arc o -> this
         */
        private boolean removeReferenceFrom(Node o) {
            return referenceFrom.remove(o);
        }

        /**
         * Removes the arc this -> o
         */
        private boolean removeReferenceTo(Node o) {
            return referenceTo.remove(o);
        }

        /**
         * Getter for writing operations on {@link #referenceFrom} set.
         */
        private Set<Node> getReferenceFrom() {
            if (!refFromInit) {
                refFromInit = true;
                referenceFrom = new HashSet<>();
            }
            return referenceFrom;
        }

        /**
         * Getter for writing operations on {@link #referenceTo} set.
         */
        private Set<Node> getReferenceTo() {
            if (!refToInit) {
                refToInit = true;
                referenceTo = new HashSet<>();
            }
            return referenceTo;
        }
    }
}
