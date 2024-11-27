package org.natalya_me;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.natalya_me.algorithm.DirectedGraph;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDirectedGraph {

    DirectedGraph g;

    @BeforeEach
    void initializeGraph() {
        g = new DirectedGraph();
    }

    @Test
    void testAddNodeWhenNull() {
        assertThrowsExactly(NullPointerException.class, () -> g.addNode(null));
    }

    @Test
    void testAddNodeWhenValidValue() {
        assertTrue(g.addNode("1"));
    }

    @Test
    void testAddNodeWhenSameId() {
        g.addNode("1");
        assertTrue(g.hasNode("1"));
        assertFalse(g.addNode("1"));
        assertTrue(g.hasNode("1"));
    }

    @Test
    void testHasNodeWhenNull() {
        assertFalse(g.hasNode(null));
    }

    @Test
    void testHasNodeWhenPresent() {
        g.addNode("12");
        assertTrue(g.hasNode(new String("12")));
    }

    @Test
    void testHasNodeWhenNotPresent() {
        assertFalse(g.hasNode("15"));
        g.addNode("15");
        assertFalse(g.hasNode("73"));
    }

    @Test
    void testAddArcWhenNull() {
        assertFalse(g.addArc(null, null));
        assertFalse(g.addArc("3", null));
        assertFalse(g.addArc(null, "5"));
    }

    @Test
    void testAddArcWhenNodeNotPresent() {
        assertFalse(g.addArc("1", "2"));
        g.addNode("1");
        assertFalse(g.addArc("1", "2"));
    }

    @Test
    void testAddArcWhenNodePresent() {
        g.addNode("1");
        g.addNode("2");
        assertTrue(g.addArc("1", "2"));
    }

    @Test
    void testHasArcWhenNull() {
        assertFalse(g.hasArc(null, null));
        assertFalse(g.hasArc("2", null));
        assertFalse(g.hasArc(null, "4"));
    }

    @Test
    void testHasArcWhenNotPresent() {
        assertFalse(g.hasArc("1", "2"));
        g.addNode("1");
        assertFalse(g.hasArc("1", "2"));
        g.addNode("2");
        assertFalse(g.hasArc("1", "2"));
    }

    @Test
    void testHasArcWhenPresent() {
        g.addNode("1");
        g.addNode("2");
        g.addArc("1", "2");
        assertTrue(g.hasArc("1", "2"));
        assertFalse(g.hasArc("2", "1"));
    }

    @Test
    void testRemoveArcWhenNull() {
        assertFalse(g.removeArc(null, null));
        assertFalse(g.removeArc("1", null));
        assertFalse(g.removeArc(null, "2"));
    }

    @Test
    void testRemoveArcWhenNotPresent() {
        assertFalse(g.removeArc("1", "2"));
        g.addNode("1");
        g.addNode("2");
        assertFalse(g.removeArc("1", "2"));
        assertTrue(g.hasNode("1"));
        assertTrue(g.hasNode("2"));
        g.addArc("2", "1");
        assertFalse(g.removeArc("1", "2"));
        assertTrue(g.hasArc("2", "1"));
    }

    @Test
    void testRemoveArcWhenPresent() {
        g.addNode("1");
        g.addNode("2");
        g.addArc("1", "2");
        assertTrue(g.hasArc("1", "2"));
        assertTrue(g.removeArc("1", "2"));
        assertFalse(g.hasArc("1", "2"));
    }

    @Test
    void testRemoveNodeWhenNull() {
        assertFalse(g.removeNode(null));
    }

    @Test
    void testRemoveNodeWhenNotPresent() {
        assertFalse(g.removeNode("1"));
    }

    @Test
    void testRemoveNodeWhenPresentRemoveTo() {
        g.addNode("1");
        g.addNode("2");
        g.addArc("1", "2");
        assertTrue(g.removeNode("2"));
        assertFalse(g.hasNode("2"));
        assertFalse(g.hasArc("1", "2"));
    }

    @Test
    void testRemoveNodeWhenPresentRemoveFrom() {
        g.addNode("1");
        g.addNode("2");
        g.addArc("1", "2");
        assertTrue(g.removeNode("1"));
        assertFalse(g.hasNode("1"));
        assertFalse(g.hasArc("1", "2"));
    }

    @Test
    void testDeepCopy() {
        List<String> ids = Arrays.asList("1", "2", "3");
        for (String id: ids) {
            g.addNode(id);
        }
        g.addArc("1", "2");
        g.addArc("1", "3");
        g.addArc("3", "2");
        DirectedGraph gCopy = g.deepCopy();
        for (String id: ids) {
            assertTrue(gCopy.hasNode(id));
            for (String otherId: ids) {
                assertEquals(g.hasArc(id, otherId), gCopy.hasArc(id, otherId));
            }
        }
        g.addNode("4");
        g.addArc("3", "4");
        assertFalse(gCopy.hasNode("4"));
        assertFalse(gCopy.hasArc("3", "4"));
    }

}
