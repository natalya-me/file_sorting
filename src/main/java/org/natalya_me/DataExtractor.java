package org.natalya_me;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  This class extracts required data from a text file according to the defined regex pattern and custom extract function.
 *  Note that pattern is applied within a single line.
 *  <p>
 *  Once created, an instance of the class can be used multiple times on different sources.
 */
public class DataExtractor {

    private final Pattern pattern;
    private final Function<Matcher, List<String>> extract;

    /**
     * Constructor.
     *
     * @param reg      regex pattern
     * @param extract  function, extracting required data from a Matcher instance
     */
    public DataExtractor(String reg, Function<Matcher, List<String>> extract) {
        pattern = Pattern.compile(reg);
        this.extract = extract;
    }

    /**
     * Finds all matches and extracts required data within the file.
     *
     * @param file file path
     * @return     list of all extracted strings (duplicates are allowed),
     *             or an empty list, if no matches have been found
     */
    public List<String> findAll(File file) {
        if (file == null || !file.isFile()) {
            throw new IllegalArgumentException("File path is incorrect: " + Optional.ofNullable(file).map(File::getPath).orElse(null));
        }

        try (Scanner scanner = new Scanner(file)) {
            List<String> result = Collections.emptyList();
            while (scanner.hasNextLine()) {
                List<String> extracted = extract.apply(pattern.matcher(scanner.nextLine()));
                if (!extracted.isEmpty()) {
                    if (result.isEmpty()) {
                        result = new ArrayList<>();
                    }
                    result.addAll(extracted);
                }
            }
            return result;
        } catch (FileNotFoundException ex) {
            throw new IllegalArgumentException("Not correct file path was provided: " + file.getPath());
        }
    }
}
