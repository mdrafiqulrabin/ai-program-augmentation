import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;

public class OperatorAugmentation extends VoidVisitorAdapter<Object> {
    private File mJavaFile = null;

    public void inspectSourceCode(File javaFile) {
        this.mJavaFile = javaFile;
        Common.setOutputPath(this, mJavaFile);
        CompilationUnit root = Common.getParseUnit(mJavaFile);
        if (root != null) {
            this.visit(root.clone(), null);
        }
    }

    @Override
    public void visit(CompilationUnit com, Object obj) {
        ArrayList<Node> operatorNodes = locateOperator(com, obj);
        Common.applyToPlace(this, com, mJavaFile, operatorNodes);
        super.visit(com, obj);
    }

    private ArrayList<Node> locateOperator(CompilationUnit com, Object obj) {
        ArrayList<Node> operatorNodes = new ArrayList<>();
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof BinaryExpr && isAugmentationApplicable(((BinaryExpr) node).getOperator())) {
                    operatorNodes.add(node);
                }
            }
        }.visitPreOrder(com);
        return operatorNodes;
    }

    public CompilationUnit applyAugmentation(CompilationUnit com, int pos, Node opNode) {
        final int[] num = {0};
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node.equals(opNode)) {
                    num[0] = num[0] + 1;
                    if (pos == num[0] || pos < 0) {
                        BinaryExpr replNode = (BinaryExpr) opNode.clone();
                        switch (((BinaryExpr) node).getOperator()) {
                            case LESS:
                                replNode.setOperator(BinaryExpr.Operator.GREATER);
                                replNode.setLeft(((BinaryExpr) node).getRight());
                                replNode.setRight(((BinaryExpr) node).getLeft());
                                break;
                            case LESS_EQUALS:
                                replNode.setOperator(BinaryExpr.Operator.GREATER_EQUALS);
                                replNode.setLeft(((BinaryExpr) node).getRight());
                                replNode.setRight(((BinaryExpr) node).getLeft());
                                break;
                            case GREATER:
                                replNode.setOperator(BinaryExpr.Operator.LESS);
                                replNode.setLeft(((BinaryExpr) node).getRight());
                                replNode.setRight(((BinaryExpr) node).getLeft());
                                break;
                            case GREATER_EQUALS:
                                replNode.setOperator(BinaryExpr.Operator.LESS_EQUALS);
                                replNode.setLeft(((BinaryExpr) node).getRight());
                                replNode.setRight(((BinaryExpr) node).getLeft());
                                break;
                            case EQUALS:
                            case NOT_EQUALS:
                            case OR:
                            case AND:
                            case PLUS:
                            case MULTIPLY:
                                replNode.setLeft(((BinaryExpr) node).getRight());
                                replNode.setRight(((BinaryExpr) node).getLeft());
                                break;
                        }
                        node.replace(replNode);
                    }
                }
            }
        }.visitPreOrder(com);
        return com;
    }

    private boolean isAugmentationApplicable(BinaryExpr.Operator op) {
        switch (op) {
            case LESS:
            case LESS_EQUALS:
            case GREATER:
            case GREATER_EQUALS:
            case EQUALS:
            case NOT_EQUALS:
            case OR:
            case AND:
            case PLUS:
            case MULTIPLY:
                return true;
        }
        return false;
    }

}
