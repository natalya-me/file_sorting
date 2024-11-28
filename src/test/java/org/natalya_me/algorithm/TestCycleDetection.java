package org.natalya_me.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCycleDetection {

    private DirectedGraph g;

    @Test
    void testFindCycleWhenGraphNull() {
        assertThrowsExactly(IllegalArgumentException.class, () -> CycleDetection.findCycle(null));
    }

    @Test
    void testFindCycleWhenEmptyGraph() {
        assertEquals(Collections.emptyList(), CycleDetection.findCycle(new DirectedGraph()));
    }

    @BeforeEach
    void createGraph() {
        g = new DirectedGraph();
        for (String id: Arrays.asList("1", "2", "3", "4")) {
            g.addNode(id);
        }
    }

    @Test
    void testFindCycleWhenNoCycle() {
        g.addArc("1", "2");
        g.addArc("2", "3");
        g.addArc("3", "4");
        assertEquals(Collections.emptyList(), CycleDetection.findCycle(g));
    }

    @Test
    void testFindCycleWhenTwoComponentsNoCycle() {
        g.addNode("5");
        g.addArc("1", "2");
        g.addArc("3", "4");
        g.addArc("4", "5");
        assertEquals(Collections.emptyList(), CycleDetection.findCycle(g));
    }

    @RepeatedTest(15)
    void testFindCycleWhenOneComponentOneCycle() {
        g.addNode("5");
        g.addNode("6");
        g.addArc("1", "2");
        g.addArc("2", "3");
        g.addArc("3", "4");
        g.addArc("4", "5");
        g.addArc("5", "6");
        g.addArc("4", "2");
        assertTrue(cyclesAreEqual(Arrays.asList("2", "3", "4"), CycleDetection.findCycle(g)));
    }

    @RepeatedTest(15)
    void testFindCycleWhenSelfReference() {
        g.addArc("1", "2");
        g.addArc("2", "3");
        g.addArc("3", "4");
        g.addArc("2", "2");
        assertEquals(Collections.singletonList("2"), CycleDetection.findCycle(g));
    }

    @RepeatedTest(15)
    void testFindCycleWhenTwoComponentOneCycle() {
        g.addNode("5");
        g.addArc("1", "2");
        g.addArc("3", "4");
        g.addArc("4", "5");
        g.addArc("5", "4");
        assertTrue(cyclesAreEqual(Arrays.asList("5", "4"), CycleDetection.findCycle(g)));
    }

    @RepeatedTest(15)
    void testFindCycleWhenOneComponentMultipleCycles() {
        g.addNode("5");
        g.addArc("1", "5");
        g.addArc("4", "1");
        g.addArc("4", "5");
        g.addArc("3", "4");
        g.addArc("5", "3");
        g.addArc("3", "1");
        List<String> cycle = CycleDetection.findCycle(g);
        assertTrue(cyclesAreEqual(Arrays.asList("3", "4", "5"), cycle)
                || cyclesAreEqual(Arrays.asList("4", "1", "5", "3"), cycle)
                || cyclesAreEqual(Arrays.asList("1", "5", "3"), cycle));
    }

    @RepeatedTest(15)
    void testFindCycleWhenTwoComponentsMultipleCycles() {
        g.addNode("5");
        g.addArc("2", "4");
        g.addArc("4", "2");
        g.addArc("1", "1");
        g.addArc("5", "1");
        g.addArc("1", "3");
        g.addArc("3", "5");
        List<String> cycle = CycleDetection.findCycle(g);
        assertTrue(cyclesAreEqual(Arrays.asList("1", "3", "5"), cycle)
                || cyclesAreEqual(Arrays.asList("2", "4"), cycle)
                || cyclesAreEqual(Collections.singletonList("1"), cycle));
    }

    static boolean cyclesAreEqual(List<String> l1, List<String> l2) {
        if (l1.size() != l2.size()) return false;
        int dif = l2.indexOf(l1.get(0));
        if (dif < 0 || dif >= l2.size()) return false;
        for (int i = 0; i < l1.size(); i++) {
            if (!l1.get(i).equals(l2.get((i + dif) % l2.size()))) return false;
        }
        return true;
    }

}
