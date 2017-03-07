package io.gitlab.arturbosch.javaparser.support;

import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Artur Bosch
 */
public final class Inspection {

    private Inspection() {
    }

    public static final BiConsumer<String[], Consumer<SourceRoot>> TEMPLATE = ((args, procedure) -> {
        try {
            Path root = Validation.extractValidRoot(args);
            SourceRoot sourceRoot = Source.compile(root);
            procedure.accept(sourceRoot);
            sourceRoot.saveAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    });
}
