import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class Common {

    static String mRootInputPath = "";
    static String mRootOutputPath = "";
    static String mSavePath = "";

    static final DataKey<Integer> VariableId = new DataKey<Integer>() {};
    static final DataKey<String> VariableName = new DataKey<String>() {};

    static ArrayList<Path> getFilePaths(String rootPath) {
        ArrayList<Path> listOfPaths = new ArrayList<>();
        final FilenameFilter filter = (dir, name) -> dir.isDirectory() && name.toLowerCase().endsWith(".txt");
        File[] listOfFiles = new File(rootPath).listFiles(filter);
        if (listOfFiles == null) return new ArrayList<>();
        for (File file : listOfFiles) {
            Path codePath = Paths.get(file.getPath());
            listOfPaths.add(codePath);
        }
        return listOfPaths;
    }

    public static void inspectSourceCode(Object obj, File javaFile) {
    }

    static void setOutputPath(Object obj, File javaFile) {
        //assume '/transforms' in output path
        Common.mSavePath = Common.mRootOutputPath.replace("/augmentations",
                "/augmentations/" + obj.getClass().getSimpleName());
    }

    static CompilationUnit getParseUnit(File javaFile) {
        CompilationUnit root = null;
        try {
            String txtCode = new String(Files.readAllBytes(javaFile.toPath()));
            try {
                root = StaticJavaParser.parse(txtCode);
            } catch (Exception ignore) {
                try {
                    txtCode = "class T { \n" + txtCode + "\n}";
                    root = StaticJavaParser.parse(txtCode);
                } catch (Exception ex) {
                    System.out.println("\n" + "Exception: " + javaFile.getPath());
                    ex.printStackTrace();
                    String error_dir = Common.mSavePath + "java_parser_error.txt";
                    Common.saveErrText(error_dir, javaFile);
                }
            }
        } catch (Exception ignore) { }
        return root;
    }

    static void applyToPlace(Object obj, CompilationUnit com, File javaFile, ArrayList<Node> nodeList) {
        // apply to single place
        int place = 0;
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            CompilationUnit newCom = applyByObj(obj, i+1, javaFile, com.clone(), node.clone());
            if (newCom != null && Common.checkTransformation(com, newCom, javaFile, false)) {
                place = place + 1;
                Common.saveTransformation(newCom, javaFile, String.valueOf(place));
            }
        }

        // apply to all place
        if (nodeList.size() > 1 && isAllPlaceApplicable(obj)) {
            CompilationUnit oldCom = com.clone();
            nodeList.forEach((node) -> applyByObj(obj, -1, javaFile, com, node));
            if (Common.checkTransformation(oldCom, com, javaFile, true)) {
                Common.saveTransformation(com, javaFile, String.valueOf(0));
            }
        }
    }

    static CompilationUnit applyByObj(Object obj, int pos, File javaFile, CompilationUnit com, Node node) {
        CompilationUnit newCom = null;
        try {
            if (obj instanceof FunctionAugmentation) {
                newCom = ((FunctionAugmentation) obj).applyAugmentation(com, node);
            } else if (obj instanceof StatementAugmentation) {
                newCom = ((StatementAugmentation) obj).applyAugmentation(com, node);
            } else if (obj instanceof LoopAugmentation) {
                newCom = ((LoopAugmentation) obj).applyAugmentation(com, pos, node);
            } else if (obj instanceof SwitchAugmentation) {
                newCom = ((SwitchAugmentation) obj).applyAugmentation(com, pos, node);
            } else if (obj instanceof OperatorAugmentation) {
                newCom = ((OperatorAugmentation) obj).applyAugmentation(com, pos, node);
            }
        } catch (Exception ex) {
            System.out.println("\n" + "Exception: " + javaFile.getPath());
            ex.printStackTrace();
        }
        return newCom;
    }

    static Boolean checkTransformation(CompilationUnit bRoot, CompilationUnit aRoot,
                                       File javaFile, boolean writeFile) {
        String mdBeforeStr = bRoot.toString().replaceAll("\\s+", "");
        String mdAfterStr = aRoot.toString().replaceAll("\\s+", "");
        if (mdBeforeStr.compareTo(mdAfterStr) == 0) {
            if (writeFile) {
                String no_dir = Common.mSavePath + "no_transformation.txt";
                File targetFile = new File(no_dir);
                Common.saveErrText(no_dir, javaFile);
            }
            return false;
        }
        return true;
    }

    static void saveTransformation(CompilationUnit newCom, File javaFile, String place) {
        String output_dir = Common.mSavePath + javaFile.getPath().replaceFirst(Common.mRootInputPath, "");
        output_dir = output_dir.substring(0, output_dir.lastIndexOf(".java")) + "_" + place + ".java";
        Common.writeSourceCode(newCom, output_dir);
    }

    static void saveErrText(String error_dir, File javaFile) {
        try {
            File targetFile = new File(error_dir);
            if (targetFile.getParentFile().exists() || targetFile.getParentFile().mkdirs()) {
                if (targetFile.exists() || targetFile.createNewFile()) {
                    Files.write(Paths.get(error_dir),
                            (javaFile.getPath() + "\n").getBytes(),
                            StandardOpenOption.APPEND);
                }
            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    static void writeSourceCode(Object obj, String codePath) {
        File targetFile = new File(codePath).getParentFile();
        if (targetFile.exists() || targetFile.mkdirs()) {
            try (PrintStream ps = new PrintStream(codePath)) {
                String tfSourceCode = obj.toString();
                ps.println(tfSourceCode);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    static boolean isNotPermeableStatement(Node node) {
        return (node instanceof EmptyStmt
                || node instanceof LabeledStmt
                || node instanceof BreakStmt
                || node instanceof ContinueStmt
                || node instanceof ReturnStmt);
    }

    static boolean isAllPlaceApplicable(Object obj) {
        return false;
    }
}
