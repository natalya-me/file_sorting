package org.natalya_me;

import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        try {
            String path = args[0];
            // TODO a path to a place for writing the result
            Map<String, Set<String>> dependencies = DependencyReader.getDependencyMap(path);
            System.out.println(dependencies);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("A mandatory argument 'path' was not provided.");
        }
    }
}