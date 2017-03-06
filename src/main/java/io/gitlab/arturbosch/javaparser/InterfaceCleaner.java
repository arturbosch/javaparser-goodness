package io.gitlab.arturbosch.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Artur Bosch
 */
public class InterfaceCleaner {

    public static void main(String[] args) throws IOException {
        Path root = checkValidRoot(args);
        SourceRoot sourceRoot = compile(root);
        cleanInterfaces(sourceRoot.getCompilationUnits());
        sourceRoot.saveAll();
    }

    private static Path checkValidRoot(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("Just one argument - a directory or file - is allowed!");
        Path root = Paths.get(args[0]);
        if (Files.notExists(root)) throw new IllegalStateException("Path must lead to a file or a directory!");
        return root;
    }

    private static SourceRoot compile(Path root) throws IOException {
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

    private static void cleanInterfaces(List<CompilationUnit> units) {
        InterfaceVisitor visitor = new InterfaceVisitor();
        units.forEach(unit -> unit.accept(visitor, null));
    }

    private static class InterfaceVisitor extends VoidVisitorAdapter<Object> {
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            if (n.isInterface()) {
                EnumSet<Modifier> modifiers = n.getModifiers();
                if (modifiers.contains(Modifier.ABSTRACT)) modifiers.remove(Modifier.ABSTRACT);
                InterfaceMethodVisitor visitor = new InterfaceMethodVisitor();
                n.accept(visitor, arg);
            }
            super.visit(n, arg);
        }
    }

    private static class InterfaceMethodVisitor extends VoidVisitorAdapter<Object> {

        @Override
        public void visit(MethodDeclaration n, Object arg) {
            EnumSet<Modifier> modifiers = n.getModifiers();
            if (modifiers.contains(Modifier.ABSTRACT)) modifiers.remove(Modifier.ABSTRACT);
            if (modifiers.contains(Modifier.PUBLIC)) modifiers.remove(Modifier.PUBLIC);
            super.visit(n, arg);
        }

    }
}
