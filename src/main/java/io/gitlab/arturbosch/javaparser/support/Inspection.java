package io.gitlab.arturbosch.javaparser.support;

import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * @author Artur Bosch
 */
public final class Inspection {

    private Inspection() {
    }

    @FunctionalInterface
    public interface TriConsumer<T, U, W> {
        void accept(T t, U u, W w);
    }

    public static final TriConsumer<String[], Boolean, Consumer<SourceRoot>> TEMPLATE = ((args, saveAll, procedure) -> {
        try {
            Path root = Validation.extractValidRoot(args);
            SourceRoot sourceRoot = Source.compile(root);
            procedure.accept(sourceRoot);
            if (saveAll) sourceRoot.saveAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    });
}
