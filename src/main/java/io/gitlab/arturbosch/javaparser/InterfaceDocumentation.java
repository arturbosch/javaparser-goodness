package io.gitlab.arturbosch.javaparser;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.gitlab.arturbosch.javaparser.support.Inspection;
import io.gitlab.arturbosch.javaparser.support.Out;

import java.io.IOException;
import java.util.List;

/**
 * @author Artur Bosch
 */
public class InterfaceDocumentation {

    public static void main(String[] args) throws IOException {
        Inspection.TEMPLATE.accept(args, /*saveModification*/false,
                sourceRoot -> new InterfaceDocumentation().run(sourceRoot.getCompilationUnits()));
    }

    private void run(List<CompilationUnit> units) {
        InterfaceVisitor visitor = new InterfaceVisitor();
        units.forEach(unit -> unit.accept(visitor, unit));
    }

    private class InterfaceVisitor extends VoidVisitorAdapter<CompilationUnit> {
        @Override
        public void visit(ClassOrInterfaceDeclaration n, CompilationUnit arg) {
            if (n.isInterface()) {
                if (n.hasModifier(Modifier.Keyword.PUBLIC)) {
                    String packageName = arg.getPackageDeclaration()
                            .map(NodeWithName::getNameAsString)
                            .orElse("<default>");
                    if (!n.getJavadocComment().isPresent()) {
                        Out.println(packageName + "." + n.getNameAsString() + " at " +
                                n.getRange().map(Range::toString).orElse("-1/-1") + " misses javadoc!");
                    }
                    InterfaceMethodVisitor visitor = new InterfaceMethodVisitor(packageName);
                    n.accept(visitor, n);
                }
            }
            super.visit(n, arg);
        }
    }

    private class InterfaceMethodVisitor extends VoidVisitorAdapter<ClassOrInterfaceDeclaration> {
        private String packageName;

        public InterfaceMethodVisitor(String packageName) {
            this.packageName = packageName;
        }

        @Override
        public void visit(MethodDeclaration n, ClassOrInterfaceDeclaration arg) {
            if (!n.getJavadocComment().isPresent()) {
                String signature = packageName + "." + arg.getNameAsString() + "#" + n.getDeclarationAsString();
                Out.println(signature + " at " + n.getRange().map(Range::toString).orElse("-1/-1") + " misses javadoc!");
            }
            super.visit(n, arg);
        }
    }
}
