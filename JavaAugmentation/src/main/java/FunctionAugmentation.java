import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;

@SuppressWarnings({"WeakerAccess", "unused"})
public class FunctionAugmentation extends VoidVisitorAdapter<Object> {
    private File mJavaFile = null;
    ArrayList<Node> mFunctionNodes = new ArrayList<>();
    private ArrayList<Node> mDummyNodes = new ArrayList<>();

    FunctionAugmentation() {
        //System.out.println("\n[ FunctionAugmentation ]\n");
    }

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
        mFunctionNodes = locateFunctions(com, obj);
        mDummyNodes.add(new EmptyStmt());
        Common.applyToPlace(this, com, mJavaFile, mDummyNodes);
        super.visit(com, obj);
    }

    private ArrayList<Node> locateFunctions(CompilationUnit com, Object obj) {
        ArrayList<Node> functionNodes = new ArrayList<>();
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof MethodDeclaration) {
                    functionNodes.add(node);
                }
            }
        }.visitPreOrder(com);
        return functionNodes;
    }

    public CompilationUnit applyAugmentation(CompilationUnit com, Node unused) {
        int cnt = 0;
        for (int i = 0; i < mFunctionNodes.size(); i++) {
            for (int j = i + 1; j < mFunctionNodes.size(); j++) {
                MethodDeclaration md_i = (MethodDeclaration) mFunctionNodes.get(i);
                MethodDeclaration md_j = (MethodDeclaration) mFunctionNodes.get(j);
                if (md_i.getParentNode().equals(md_j.getParentNode())) {
                    swapFunctionNodes(com, i, j, ++cnt);
                }
            }
        }
        return null;
    }

    private void swapFunctionNodes(CompilationUnit com, int i, int j, int cnt) {
        CompilationUnit newCom = com.clone();
        ArrayList<Node> functionNodes = locateFunctions(newCom, null);
        MethodDeclaration md_i = (MethodDeclaration) functionNodes.get(i);
        MethodDeclaration md_j = (MethodDeclaration) functionNodes.get(j);
        md_i.replace(md_j.clone());
        md_j.replace(md_i.clone());
        Common.saveTransformation(newCom, mJavaFile, String.valueOf(cnt));
    }

}
