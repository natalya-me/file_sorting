package org.natalya_me;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Utility class for reading file dependencies for all files in the given directory.
 * The class cannot be instantiated. Use method {@link #getDependencyMap(String) getDependencyMap()}
 * to utilize the class functionality.
 */
public class DependencyReader {

    // Object for extracting requirements from files
    private static final DataExtractor DATA_EXTRACTOR = new DataExtractor("require *' *(.*?) *'", DependencyReader::extract);

    private DependencyReader() {
        throw new UnsupportedOperationException(String.format("Instantiation of class %s is not supported.", DependencyReader.class.getName()));
    }

    /**
     * Traverse the given path and creates a map with required file paths for each file in the directory.
     * If a file doesn't have any requirement, it presents in the map with an empty set as a value.
     *
     * @param rootPath path to a root directory
     * @return Map with required file paths for each file in the directory
     */
    public static Map<String, Set<String>> getDependencyMap(String rootPath) {
        if (rootPath == null) {
            throw new IllegalArgumentException("Root path value cannot be null.");
        }
        File rootFile = new File(rootPath);
        if (!rootFile.isDirectory()) {
            throw new IllegalArgumentException(String.format("%s does not exist or it is not a directory.", rootPath));
        }
        Map<String, Set<String>> result = new HashMap<>();
        fillFileRequirements(rootFile, result, rootPath);
        return result;
    }

    /**
     * Fills the dependency map while visiting all files using depth-first algorithm.
     * Incorrect paths are not included in the resulting map.
     *
     * @param file          current file path
     * @param dependencyMap the map instance being filled
     * @param rootPath      path to the root directory for building an absolute path for each dependency
     */
    private static void fillFileRequirements(File file, Map<String, Set<String>> dependencyMap, String rootPath) {
        if (file.isDirectory()) {
            for (File child: file.listFiles()) {
                fillFileRequirements(child, dependencyMap, rootPath);
            }
        }
        if (file.isFile() && file.canRead()) {
            Set<String> dependencies = DATA_EXTRACTOR.findAll(file).stream()
                            .map(p -> new File(rootPath, p))
                            .filter(File::isFile)
                            .map(File::getAbsolutePath)
                            .collect(Collectors.toSet());
            dependencyMap.put(file.getAbsolutePath(), dependencies);
        }
    }

    /**
     * An algorithm for extracting only paths from a line of a file.
     * It supports multiple requirements in one line.
     *
     * @param matcher Matcher instance for a given line and predefined Pattern
     * @return list of all found requirements in the line. May contain duplicates or be empty
     */
    private static List<String> extract(Matcher matcher) {
        List<String> result = Collections.emptyList();
        while (matcher.find()) {
            if (result.isEmpty()) {
                result = new ArrayList<>();
            }
            result.add(matcher.group(1));
        }
        return result;
    }
}
