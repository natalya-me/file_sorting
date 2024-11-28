package org.natalya_me;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class DependencyReaderTest {

    private final String PATH = "src/test/resources/dependency_reader_test/";

    @Test
    void testGetDependencyMapWhenPathNull() {
        assertThrowsExactly(IllegalArgumentException.class, () -> DependencyReader.getDependencyMap(null, true));
    }

    @Test
    void testGetDependencyMapWhenPathDoesNotExist() {
        assertThrowsExactly(IllegalArgumentException.class, () -> DependencyReader.getDependencyMap("no_such_directory/", true));
    }

    @Test
    void testGetDependencyMapWhenPathIsNotDirectory() {
        assertThrowsExactly(IllegalArgumentException.class, () -> DependencyReader.getDependencyMap(PATH + "file1.txt", true));
    }

    @Test
    void testGetDependencyMapWhenInvertFalse() {
        Map<String, Set<String>> expected = new HashMap<>();
        expected.put("file1.txt", new HashSet<>(Arrays.asList("file2.txt", "file3.txt")));
        expected.put("file2.txt", Collections.singleton("file3.txt"));
        expected.put("file3.txt", Collections.emptySet());
        expected.put("file4.txt", Collections.singleton("file1.txt"));
        expected.put("file5.txt", Collections.emptySet());
        expected = transformShortPathToAbsolute(expected);

        assertEquals(expected, DependencyReader.getDependencyMap(PATH, false));
    }

    @Test
    void testGetDependencyMapWhenInvertTrue() {
        Map<String, Set<String>> expected = new HashMap<>();
        expected.put("file1.txt", Collections.singleton("file4.txt"));
        expected.put("file2.txt", Collections.singleton("file1.txt"));
        expected.put("file3.txt", new HashSet<>(Arrays.asList("file1.txt", "file2.txt")));
        expected.put("file4.txt", Collections.emptySet());
        expected.put("file5.txt", Collections.emptySet());
        expected = transformShortPathToAbsolute(expected);

        assertEquals(expected, DependencyReader.getDependencyMap(PATH, true));
    }

    private Map<String, Set<String>> transformShortPathToAbsolute(Map<String, Set<String>> shortPathMap) {
        Map<String, String> fileShortcuts = new HashMap<>();
        fillFileShortCuts(new File(PATH), fileShortcuts);
        Map<String, Set<String>> result = new HashMap<>();
        for (Map.Entry<String, Set<String>> e: shortPathMap.entrySet()) {
            result.put(fileShortcuts.get(e.getKey()), e.getValue().stream()
                    .map(fileShortcuts::get)
                    .collect(Collectors.toSet()));
        }
        return result;
    }

    // Make sure your files have unique names within the root folder
    private void fillFileShortCuts(File file, Map<String, String> fileShortcuts) {
        if (file.isDirectory()) {
            for (File child: file.listFiles()) {
                fillFileShortCuts(child, fileShortcuts);
            }
            return;
        }
        if (file.isFile()) {
            fileShortcuts.put(file.getName(), file.getAbsolutePath());
        }
    }
}
