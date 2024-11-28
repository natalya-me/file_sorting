package org.natalya_me;

import org.natalya_me.algorithm.DirectedGraph;
import org.natalya_me.algorithm.TopologicalOrdering;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class Main {

    private static final TopologicalOrdering algorithm = new TopologicalOrdering(Comparator.comparing(Main::extractFileName)
                                                                                           .thenComparing(DirectedGraph.Node::getId));

    public static void main(String[] args) {
        try {
            // Source and target paths
            String path = args[0];
            String targetPath = null;
            try {
                targetPath = args[1];
            } catch (ArrayIndexOutOfBoundsException ignored) {}
            if (targetPath == null) {
                targetPath = "target.txt";
            }
            // Read files and requirements
            Map<String, Set<String>> dependencies = DependencyReader.getDependencyMap(path, true);
            if (dependencies.isEmpty()) {
                System.out.println("There is no files in the given directory, nothing to write");
                return;
            }
            // Sort files
            DirectedGraph graph = DirectedGraph.createFromAdjacencyList(dependencies);
            TopologicalOrdering.TopologicalOrderingResult sorted = algorithm.sort(graph);
            // Result output
            if (sorted.getType() == TopologicalOrdering.TopologicalOrderingResult.TYPE.ORDER) {
                File target = new File(targetPath);
                if (target.isDirectory()) {
                    target = new File(target, "target.txt");
                }
                try (FileOutputStream targetStream = new FileOutputStream(target, false)) {
                    for (String sourcePath: sorted.getResult()) {
                        Files.copy(Paths.get(sourcePath), targetStream);
                    }
                } catch (IOException ex) {
                    System.out.printf("File %s cannot be open or created.", target.getPath());
                }
            } else if (sorted.getType() == TopologicalOrdering.TopologicalOrderingResult.TYPE.CYCLE) {
                System.out.println("A cycle was detected in the dependency graph:");
                System.out.println(String.join(" <- ", sorted.getResult()));
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("A mandatory argument 'path' was not provided.");
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static String extractFileName(DirectedGraph.Node node) {
        return new File(node.getId()).getName();
    }
}