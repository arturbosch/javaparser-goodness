package io.gitlab.arturbosch.javaparser.support;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Artur Bosch
 */
public final class Validation {

    private Validation() {
    }

    /**
     * Validates if the argument vector has only one element, the root path.
     *
     * @param args parameters which came from the main method
     * @return the root path for further analysis
     */
    public static Path extractValidRoot(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("Just one argument - a directory or file - is allowed!");
        Path root = Paths.get(args[0]);
        if (Files.notExists(root)) throw new IllegalStateException("Path must lead to a file or a directory!");
        return root;
    }

}
