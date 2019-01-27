package io.gitlab.arturbosch.javaparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.gitlab.arturbosch.javaparser.support.Inspection;

import java.util.List;

import static com.github.javaparser.ast.Modifier.Keyword.ABSTRACT;
import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;

/**
 * @author Artur Bosch
 */
public class InterfaceCleaner {

    public static void main(String[] args) {
        Inspection.TEMPLATE.accept(args, /*saveModification*/true,
                sourceRoot -> new InterfaceCleaner().run(sourceRoot.getCompilationUnits()));
    }

    private void run(List<CompilationUnit> units) {
        InterfaceVisitor visitor = new InterfaceVisitor();
        units.forEach(unit -> unit.accept(visitor, null));
    }

    private class InterfaceVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            if (n.isInterface()) {
                if (n.hasModifier(ABSTRACT)) n.removeModifier(ABSTRACT);
                InterfaceMethodVisitor visitor = new InterfaceMethodVisitor();
                n.accept(visitor, arg);
            }
            super.visit(n, arg);
        }
    }

    private class InterfaceMethodVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (n.hasModifier(ABSTRACT)) n.removeModifier(ABSTRACT);
            if (n.hasModifier(PUBLIC)) n.removeModifier(PUBLIC);
            super.visit(n, arg);
        }
    }
}
