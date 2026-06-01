package com.neuralforge.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jdt.core.dom.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class JavaASTAnalysisPipeline {

    public ASTAnalysisResult analyze(String sourceCode, String fileName) {
        log.info("Running AST analysis on: {}", fileName);

        ASTParser parser = ASTParser.newParser(AST.JLS21);
        parser.setSource(sourceCode.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(false);

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        List<String> classes = new ArrayList<>();
        List<String> methods = new ArrayList<>();
        List<String> nullRisks = new ArrayList<>();
        List<String> complexMethods = new ArrayList<>();

        cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration node) {
                classes.add(node.getName().getIdentifier());
                return true;
            }

            @Override
            public boolean visit(MethodDeclaration node) {
                String methodName = node.getName().getIdentifier();
                methods.add(methodName);

                // Detect high cyclomatic complexity (> 10 branches)
                int complexity = countBranches(node);
                if (complexity > 10) {
                    complexMethods.add(methodName + " (complexity: " + complexity + ")");
                }
                return true;
            }

            @Override
            public boolean visit(NullLiteral node) {
                ASTNode parent = node.getParent();
                if (parent instanceof ReturnStatement) {
                    nullRisks.add("Null return at line: " +
                            cu.getLineNumber(node.getStartPosition()));
                }
                return true;
            }
        });

        int totalLines = sourceCode.split("\n").length;
        double maintainabilityIndex = calculateMaintainabilityIndex(totalLines, methods.size(), complexMethods.size());

        return new ASTAnalysisResult(
                fileName, classes, methods, nullRisks,
                complexMethods, totalLines, maintainabilityIndex
        );
    }

    private int countBranches(MethodDeclaration method) {
        int[] count = {1};
        method.accept(new ASTVisitor() {
            @Override public boolean visit(IfStatement node) { count[0]++; return true; }
            @Override public boolean visit(ForStatement node) { count[0]++; return true; }
            @Override public boolean visit(WhileStatement node) { count[0]++; return true; }
            @Override public boolean visit(SwitchCase node) { count[0]++; return true; }
            @Override public boolean visit(CatchClause node) { count[0]++; return true; }
        });
        return count[0];
    }

    private double calculateMaintainabilityIndex(int lines, int methods, int complexMethods) {
        double base = 171.0 - 5.2 * Math.log(lines) - 0.23 * complexMethods - 16.2 * Math.log(methods + 1);
        return Math.max(0, Math.min(100, base));
    }

    public record ASTAnalysisResult(
            String fileName,
            List<String> classes,
            List<String> methods,
            List<String> nullRisks,
            List<String> complexMethods,
            int totalLines,
            double maintainabilityIndex
    ) {}
}
