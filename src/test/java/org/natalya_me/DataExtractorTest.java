package org.natalya_me;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

public class DataExtractorTest {

    private final DataExtractor extractor = new DataExtractor("require *' *(.*?) *'", this::extract);
    private static final File RESOURCES = new File("src/test/resources/data_extractor_test");

    @Test
    void testFindAllWhenSourceNull() {
        assertThrowsExactly(IllegalArgumentException.class, () -> extractor.findAll(null));
    }

    @Test
    void testFindAllWhenSourceDoesNotExist() {
        assertThrowsExactly(IllegalArgumentException.class, () -> extractor.findAll(new File("no_such_directory")));
    }

    @Test
    void testFindAllWhenSourceEmpty() {
        assertEquals(Collections.emptyList(), extractor.findAll(new File(RESOURCES, "empty.txt")));
    }

    @Test
    void testFindAllWhenNoMatch() {
        assertEquals(Collections.emptyList(), extractor.findAll(new File(RESOURCES, "nomatch.txt")));
    }

    @Test
    void testFindAllWhenMatchesPresent() {
        List<String> expected = Arrays.asList("/path1", "/path2", "/path1", "/path3", "/path4", "/path5");
        assertEquals(expected, extractor.findAll(new File(RESOURCES, "match.txt")));
    }

    @Test
    void testFindAllWhenMatchedPatternIsSplit() {
        List<String> expected = Collections.singletonList("/path5");
        assertEquals(expected, extractor.findAll(new File(RESOURCES, "brokenline.txt")));
    }

    @Test
    void testFindAllWhenMatchesPresentWithSpaces() {
        List<String> expected = Arrays.asList("/path1", "/path2", "/path1", "/path3", "/path4", "/path5");
        assertEquals(expected, extractor.findAll(new File(RESOURCES, "matchwithspaces.txt")));
    }

    private List<String> extract(Matcher matcher) {
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
