package io.gitlab.arturbosch.javaparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.gitlab.arturbosch.javaparser.support.Inspection;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Artur Bosch
 */
public class InterfaceCleaner {

    public static void main(String[] args) throws IOException {
        Inspection.TEMPLATE.accept(args,
                sourceRoot -> new InterfaceCleaner().run(sourceRoot.getCompilationUnits()));
    }

    private void run(List<CompilationUnit> units) {
        InterfaceVisitor visitor = new InterfaceVisitor();
        units.forEach(unit -> unit.accept(visitor, null));
    }

    private class InterfaceVisitor extends VoidVisitorAdapter<Object> {
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

    private class InterfaceMethodVisitor extends VoidVisitorAdapter<Object> {
        @Override
        public void visit(MethodDeclaration n, Object arg) {
            EnumSet<Modifier> modifiers = n.getModifiers();
            if (modifiers.contains(Modifier.ABSTRACT)) modifiers.remove(Modifier.ABSTRACT);
            if (modifiers.contains(Modifier.PUBLIC)) modifiers.remove(Modifier.PUBLIC);
            super.visit(n, arg);
        }
    }
}
