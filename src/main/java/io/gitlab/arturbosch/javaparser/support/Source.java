package io.gitlab.arturbosch.javaparser.support;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Artur Bosch
 */
public final class Source {

    private Source() {
    }

    /**
     * Translates the given root path to a javaparser SourceRoot. If the root is a file
     * we need to adjust the SourceRoot.
     *
     * @param root the path
     * @return object which is aware of parsing and saving java files
     * @throws IOException if parsing errors occur
     */
    public static SourceRoot compile(Path root) throws IOException {
        if (Files.isRegularFile(root)) {
            CompilationUnit unit = JavaParser.parse(root.toFile());
            Path parent = root.getParent();
            SourceRoot sourceRoot = new SourceRoot(parent);
            sourceRoot.add("", root.getFileName().toString(), unit);
            return sourceRoot;
        } else {
            SourceRoot sourceRoot = new SourceRoot(root);
            sourceRoot.tryToParse();
            return sourceRoot;
        }
    }

}
