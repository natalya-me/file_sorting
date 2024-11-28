package org.natalya_me.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.natalya_me.algorithm.TestCycleDetection.cyclesAreEqual;

public class TestTopologicalOrdering {

    private DirectedGraph g;
    private final TopologicalOrdering ordering = new TopologicalOrdering(Comparator.comparing(DirectedGraph.Node::getId));
    private final List<String> ids = Arrays.asList("1", "2", "3", "4", "5", "6");

    @BeforeEach
    void createGraph() {
        g = new DirectedGraph();
        for(String id: ids) {
            g.addNode(id);
        }
    }

    @Test
    void testSortWhenGraphIsNull() {
        assertThrowsExactly(IllegalArgumentException.class, () -> ordering.sort(null));
    }

    @Test
    void testSortWhenGraphEmpty() {
        TopologicalOrdering.TopologicalOrderingResult result = ordering.sort(new DirectedGraph());
        assertEquals(TopologicalOrdering.TopologicalOrderingResult.TYPE.ORDER, result.getType());
        assertEquals(Collections.emptyList(), result.getResult());
    }

    @Test
    void testSortWhenNoArcs() {
        TopologicalOrdering.TopologicalOrderingResult result = ordering.sort(g);
        assertEquals(TopologicalOrdering.TopologicalOrderingResult.TYPE.ORDER, result.getType());
        assertEquals(ids, result.getResult());
    }

    @Test
    void testSortWhenNoCyclesOneComponent() {
        g.addArc("3", "1");
        g.addArc("3", "2");
        g.addArc("3", "4");
        g.addArc("4", "6");
        g.addArc("4", "5");
        g.addArc("6", "1");
        TopologicalOrdering.TopologicalOrderingResult result = ordering.sort(g);
        assertEquals(TopologicalOrdering.TopologicalOrderingResult.TYPE.ORDER, result.getType());
        assertEquals(Arrays.asList("3", "2", "4", "5", "6", "1"), result.getResult());
    }

    @Test
    void testSortWhenNoCyclesTwoComponents() {
        g.addArc("6", "1");
        g.addArc("1", "2");
        g.addArc("2", "4");
        g.addArc("3", "5");
        TopologicalOrdering.TopologicalOrderingResult result = ordering.sort(g);
        assertEquals(TopologicalOrdering.TopologicalOrderingResult.TYPE.ORDER, result.getType());
        assertEquals(Arrays.asList("3", "5", "6", "1", "2", "4"), result.getResult());
    }

    @Test
    void testSortWhenOneCycleOneComponent() {
        g.addArc("3", "1");
        g.addArc("2", "3");
        g.addArc("3", "4");
        g.addArc("4", "6");
        g.addArc("4", "5");
        g.addArc("6", "2");
        TopologicalOrdering.TopologicalOrderingResult result = ordering.sort(g);
        assertEquals(TopologicalOrdering.TopologicalOrderingResult.TYPE.CYCLE, result.getType());
        assertTrue(cyclesAreEqual(Arrays.asList("2", "3", "4", "6"), result.getResult()));
    }

    @Test
    void testSortWhenOneCycleTwoComponents() {
        g.addArc("6", "1");
        g.addArc("1", "2");
        g.addArc("2", "4");
        g.addArc("3", "5");
        g.addArc("2", "6");
        TopologicalOrdering.TopologicalOrderingResult result = ordering.sort(g);
        assertEquals(TopologicalOrdering.TopologicalOrderingResult.TYPE.CYCLE, result.getType());
        assertTrue(cyclesAreEqual(Arrays.asList("6", "1", "2"), result.getResult()));
    }

    @Test
    void testSortWhenSelfReferencing() {
        g.addArc("6", "1");
        g.addArc("1", "2");
        g.addArc("2", "4");
        g.addArc("3", "5");
        g.addArc("2", "2");
        TopologicalOrdering.TopologicalOrderingResult result = ordering.sort(g);
        assertEquals(TopologicalOrdering.TopologicalOrderingResult.TYPE.CYCLE, result.getType());
        assertTrue(cyclesAreEqual(Collections.singletonList("2"), result.getResult()));
    }

    @RepeatedTest(15)
    void testSortWhenMultipleCyclesOneComponent() {
        g.addArc("1", "5");
        g.addArc("4", "1");
        g.addArc("4", "5");
        g.addArc("3", "4");
        g.addArc("5", "3");
        g.addArc("3", "1");
        g.addArc("3", "6");
        TopologicalOrdering.TopologicalOrderingResult result = ordering.sort(g);
        assertEquals(TopologicalOrdering.TopologicalOrderingResult.TYPE.CYCLE, result.getType());
        assertTrue(cyclesAreEqual(Arrays.asList("3", "4", "5"), result.getResult())
                || cyclesAreEqual(Arrays.asList("4", "1", "5", "3"), result.getResult())
                || cyclesAreEqual(Arrays.asList("1", "5", "3"), result.getResult()));
    }

    @RepeatedTest(15)
    void testSortWhenMultipleCyclesTwoComponents() {
        g.addArc("1", "5");
        g.addArc("4", "1");
        g.addArc("4", "5");
        g.addArc("3", "4");
        g.addArc("5", "3");
        g.addArc("3", "1");
        g.addArc("2", "6");
        g.addArc("6", "6");
        TopologicalOrdering.TopologicalOrderingResult result = ordering.sort(g);
        assertEquals(TopologicalOrdering.TopologicalOrderingResult.TYPE.CYCLE, result.getType());
        assertTrue(cyclesAreEqual(Arrays.asList("3", "4", "5"), result.getResult())
                || cyclesAreEqual(Arrays.asList("4", "1", "5", "3"), result.getResult())
                || cyclesAreEqual(Arrays.asList("1", "5", "3"), result.getResult())
                || cyclesAreEqual(Collections.singletonList("6"), result.getResult()));
    }
}
