import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 语法分析器
 */
public class SyntaxAnalyzer {

    private static ArrayList<String> stack = new ArrayList<>(); // 当前栈
    private static ArrayList<Integer> reader = new ArrayList<>(); // 待读队列
    private static Production[] productions = new Production[42]; // 产生式数组
    private static HashMap<Integer, String> map_i2s; // 种别码Map，种别码为键，单词为值
    private static HashMap<String, Integer> map_s2i; // 种别码Map，单词为键，种别码为值

    public static void main(String[] args) {
        int stackTop = 1;
        int readerTop = 0;
        int index = 0; // 当前步骤数
        initMap(); // 初始化种别码Map
        initProductions(); // 产生式初始化
        stack.add(0, String.valueOf(map_s2i.get("$"))); // 在stack底部加上$
        stack.add(stackTop, "S'"); // 将S'压入栈
        System.out.print("请输入词法分析结果的文件路径：");
        Scanner scanner = new Scanner(System.in);
        String filepath = scanner.next();
        StringBuffer outputBuffer = new StringBuffer(); // 输出到文件的StringBuffer

        // 通过词法分析器的输出结果，初始化reader
        try {
            readToReader(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        reader.add(map_s2i.get("$")); // 在reader末尾加上$
        while (stackTop >= 0) {
            System.out.printf("%-6s", "第" + ++index + "步：");
            System.out.printf("%-10s", "当前栈：");
            outputBuffer.append("第" + index + "步：    当前栈：");
            StringBuffer sb = new StringBuffer(); // 引入StringBuffer仅为控制在控制台的输出格式对齐
            for (int i = 0; i <= stackTop; i++) {
                String str = null;
                try {
                    str = map_i2s.get(Integer.valueOf(stack.get(i)));
                    if (str != null) {
                        sb.append(str + " ");
                        outputBuffer.append(str + " ");
                    }
                } catch (NumberFormatException e) {
                    sb.append(stack.get(i) + " ");
                    outputBuffer.append(stack.get(i) + " ");
                }
            }
            System.out.printf("%-30s", sb.toString());
            System.out.print("待读队列：");
            outputBuffer.append("             待读队列：");
            sb = new StringBuffer();
            for (int i = 0; i < reader.size(); i++) {
                sb.append(map_i2s.get(reader.get(i)) + " ");
                outputBuffer.append(map_i2s.get(reader.get(i)) + " ");
            }
            System.out.printf("%-55s", sb.toString());

            if (match(stackTop, readerTop)) {
                stackTop--;
                System.out.print("\n");
                outputBuffer.append("\n");
            } else {
                int i = ll1_table(stackTop, readerTop);
                stackTop += stackPush(stackTop, productions[i]); // 压栈
                System.out.printf("%-30s", "下一步所用产生式：" + productions[i].prod);
                System.out.println();
                outputBuffer.append("         下一步所用产生式：" + productions[i].prod + "\n");
            }
        }
        if (stackTop == -1) {
            System.out.println("语法分析成功");
            outputBuffer.append("Accept");
        }

        System.out.print("请输入语法分析结果文件的保存路径：");
        String outputPath = scanner.next();
        // 将StringBuffer的内容输出到文件
        File outputFile = new File(outputPath);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        PrintWriter writer = null;
        try {
            outputFile.createNewFile();
            writer = new PrintWriter(new FileOutputStream(outputFile));
            writer.write(outputBuffer.toString());
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static void readToReader(String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        line = br.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            int pos = line.indexOf(",");
            reader.add(Integer.valueOf(line.substring(0, pos)));
            line = br.readLine(); // 读取下一行
        }
        br.close();
        is.close();
    }

    private static int stackPush(int stackTop, Production production) {
        int len = production.r_str.length;
        stack.remove(stackTop);
        if ("ε".equals(production.r_str[0])) {
        } else {
            for (int i = len - 1; i >= 0; i--) {
                stack.add(production.r_str[i]);
            }
            return len - 1;
        }
        return -1;
    }

    // 利用LL(1)预测分析表进行分析
    private static int ll1_table(int stackTop, int readerTop) {
        if ("S".equals(stack.get(stackTop))) {
            if ("char".equals(map_i2s.get(reader.get(readerTop)))) {
                return 0;
            } else if ("short".equals(map_i2s.get(reader.get(readerTop)))) {
                return 0;
            } else if ("int".equals(map_i2s.get(reader.get(readerTop)))) {
                return 0;
            } else if ("long".equals(map_i2s.get(reader.get(readerTop)))) {
                return 0;
            } else if ("float".equals(map_i2s.get(reader.get(readerTop)))) {
                return 0;
            } else if ("double".equals(map_i2s.get(reader.get(readerTop)))) {
                return 0;
            } else if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 1;
            } else if ("if".equals(map_i2s.get(reader.get(readerTop)))) {
                return 2;
            } else if ("while".equals(map_i2s.get(reader.get(readerTop)))) {
                return 3;
            } else if ("}".equals(map_i2s.get(reader.get(readerTop)))) {
                return 4;
            } else if ("$".equals(map_i2s.get(reader.get(readerTop)))) {
                return 4;
            } else {
                return -1;
            }
        } else if ("B".equals(stack.get(stackTop))) {
            if ("(".equals(map_i2s.get(reader.get(readerTop)))) {
                return 5;
            } else if ("=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 6;
            } else {
                return -1;
            }
        } else if ("L".equals(stack.get(stackTop))) {
            if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 7;
            } else {
                return -1;
            }
        } else if ("L'".equals(stack.get(stackTop))) {
            if (";".equals(map_i2s.get(reader.get(readerTop)))) {
                return 9;
            } else if (")".equals(map_i2s.get(reader.get(readerTop)))) {
                return 9;
            } else if ("}".equals(map_i2s.get(reader.get(readerTop)))) {
                return 9;
            } else if ("$".equals(map_i2s.get(reader.get(readerTop)))) {
                return 9;
            } else if (",".equals(map_i2s.get(reader.get(readerTop)))) {
                return 8;
            } else {
                return -1;
            }
        } else if ("Q".equals(stack.get(stackTop))) {
            if ("}".equals(map_i2s.get(reader.get(readerTop)))) {
                return 11;
            } else if ("$".equals(map_i2s.get(reader.get(readerTop)))) {
                return 11;
            } else if ("else".equals(map_i2s.get(reader.get(readerTop)))) {
                return 10;
            } else {
                return -1;
            }
        } else if ("X".equals(stack.get(stackTop))) {
            if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 12;
            } else if ("num".equals(map_i2s.get(reader.get(readerTop)))) {
                return 12;
            } else if ("+".equals(map_i2s.get(reader.get(readerTop)))) {
                return 12;
            } else if ("-".equals(map_i2s.get(reader.get(readerTop)))) {
                return 12;
            } else if ("(".equals(map_i2s.get(reader.get(readerTop)))) {
                return 12;
            } else {
                return -1;
            }
        } else if ("E".equals(stack.get(stackTop))) {
            if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 15;
            } else if ("num".equals(map_i2s.get(reader.get(readerTop)))) {
                return 15;
            } else if ("(".equals(map_i2s.get(reader.get(readerTop)))) {
                return 15;
            } else if ("+".equals(map_i2s.get(reader.get(readerTop)))) {
                return 13;
            } else if ("-".equals(map_i2s.get(reader.get(readerTop)))) {
                return 14;
            } else {
                return -1;
            }
        } else if ("E'".equals(stack.get(stackTop))) {
            if ("+".equals(map_i2s.get(reader.get(readerTop)))) {
                return 16;
            } else if ("-".equals(map_i2s.get(reader.get(readerTop)))) {
                return 16;
            } else if (">".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if (">=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if ("<".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if ("<=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if ("==".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if ("!=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if (";".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else if (")".equals(map_i2s.get(reader.get(readerTop)))) {
                return 17;
            } else {
                return -1;
            }
        } else if ("M".equals(stack.get(stackTop))) {
            if ("+".equals(map_i2s.get(reader.get(readerTop)))) {
                return 18;
            } else if ("-".equals(map_i2s.get(reader.get(readerTop)))) {
                return 19;
            } else {
                return -1;
            }
        } else if ("T".equals(stack.get(stackTop))) {
            if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 20;
            } else if ("num".equals(map_i2s.get(reader.get(readerTop)))) {
                return 20;
            } else if ("(".equals(map_i2s.get(reader.get(readerTop)))) {
                return 20;
            }
        } else if ("T'".equals(stack.get(stackTop))) {
            if ("+".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if ("-".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if ("*".equals(map_i2s.get(reader.get(readerTop)))) {
                return 21;
            } else if ("/".equals(map_i2s.get(reader.get(readerTop)))) {
                return 21;
            } else if (">".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if (">=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if ("<".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if ("<=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if ("==".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if ("!=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if (";".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else if (")".equals(map_i2s.get(reader.get(readerTop)))) {
                return 22;
            } else {
                return -1;
            }
        } else if ("N".equals(stack.get(stackTop))) {
            if ("*".equals(map_i2s.get(reader.get(readerTop)))) {
                return 23;
            } else if ("/".equals(map_i2s.get(reader.get(readerTop)))) {
                return 24;
            } else {
                return -1;
            }
        } else if ("F".equals(stack.get(stackTop))) {
            if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 25;
            } else if ("num".equals(map_i2s.get(reader.get(readerTop)))) {
                return 26;
            } else if ("(".equals(map_i2s.get(reader.get(readerTop)))) {
                return 27;
            } else {
                return -1;
            }
        } else if ("R".equals(stack.get(stackTop))) {
            if (">".equals(map_i2s.get(reader.get(readerTop)))) {
                return 28;
            } else if (">=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 29;
            } else if ("<".equals(map_i2s.get(reader.get(readerTop)))) {
                return 30;
            } else if ("<=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 31;
            } else if ("==".equals(map_i2s.get(reader.get(readerTop)))) {
                return 32;
            } else if ("!=".equals(map_i2s.get(reader.get(readerTop)))) {
                return 33;
            } else {
                return -1;
            }
        } else if ("S'".equals(stack.get(stackTop))) {
            if ("char".equals(map_i2s.get(reader.get(readerTop)))) {
                return 34;
            } else if ("short".equals(map_i2s.get(reader.get(readerTop)))) {
                return 34;
            } else if ("int".equals(map_i2s.get(reader.get(readerTop)))) {
                return 34;
            } else if ("long".equals(map_i2s.get(reader.get(readerTop)))) {
                return 34;
            } else if ("float".equals(map_i2s.get(reader.get(readerTop)))) {
                return 34;
            } else if ("double".equals(map_i2s.get(reader.get(readerTop)))) {
                return 34;
            } else if ("id".equals(map_i2s.get(reader.get(readerTop)))) {
                return 34;
            } else if ("if".equals(map_i2s.get(reader.get(readerTop)))) {
                return 34;
            } else if ("while".equals(map_i2s.get(reader.get(readerTop)))) {
                return 34;
            } else if ("$".equals(map_i2s.get(reader.get(readerTop)))) {
                return 35;
            } else {
                return -1;
            }
        } else if ("A".equals(stack.get(stackTop))) {
            if ("char".equals(map_i2s.get(reader.get(readerTop)))) {
                return 36;
            } else if ("short".equals(map_i2s.get(reader.get(readerTop)))) {
                return 37;
            } else if ("int".equals(map_i2s.get(reader.get(readerTop)))) {
                return 38;
            } else if ("long".equals(map_i2s.get(reader.get(readerTop)))) {
                return 39;
            } else if ("float".equals(map_i2s.get(reader.get(readerTop)))) {
                return 40;
            } else if ("double".equals(map_i2s.get(reader.get(readerTop)))) {
                return 41;
            } else {
                return -1;
            }
        } else {
            System.out.println("语法错误");
        }
        return -1;
    }

    private static boolean match(int stackTop, int readerTop) {
        try {
            int stackTopVal = Integer.valueOf(stack.get(stackTop)); // 未抛出异常说明是终结符
            if (stackTopVal == reader.get(0)) {
                stack.remove(stackTop);
                reader.remove(readerTop);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            // 抛出异常说明是非终结符
            return false;
        }
    }

    private static void initProductions() {
        productions[0] = new Production("S",
                new String[]{"A", "L", String.valueOf(map_s2i.get(";"))},
                "S --> A L;");
        productions[1] = new Production("S",
                new String[]{String.valueOf(map_s2i.get("id")), "B"},
                "S --> id B");
        productions[2] = new Production("S",
                new String[]{String.valueOf(map_s2i.get("if")), String.valueOf(map_s2i.get("(")), "X", String.valueOf(map_s2i.get(")")), String.valueOf(map_s2i.get("{")), "S", String.valueOf(map_s2i.get("}")), "Q"},
                "S --> if(X){S}Q");
        productions[3] = new Production("S",
                new String[]{String.valueOf(map_s2i.get("while")), String.valueOf(map_s2i.get("(")), "X", String.valueOf(map_s2i.get(")")), String.valueOf(map_s2i.get("{")), "S", String.valueOf(map_s2i.get("}"))},
                "S --> while(X){S}");
        productions[4] = new Production("S",
                new String[]{"ε"},
                "S --> ε");
        productions[5] = new Production("B",
                new String[]{String.valueOf(map_s2i.get("(")), "L", String.valueOf(map_s2i.get(")")), String.valueOf(map_s2i.get(";"))},
                "B --> (L);");
        productions[6] = new Production("B",
                new String[]{String.valueOf(map_s2i.get("=")), "E", String.valueOf(map_s2i.get(";"))},
                "B --> =E;");
        productions[7] = new Production("L",
                new String[]{String.valueOf(map_s2i.get("id")), "L'"},
                "L --> id L'");
        productions[8] = new Production("L'",
                new String[]{String.valueOf(map_s2i.get(",")), String.valueOf(map_s2i.get("id")), "L'"},
                "L' --> ,id L'");
        productions[9] = new Production("L'",
                new String[]{"ε"},
                "L' --> ε");
        productions[10] = new Production("Q",
                new String[]{String.valueOf(map_s2i.get("else")), String.valueOf(map_s2i.get("{")), "S", String.valueOf(map_s2i.get("}"))},
                "Q --> else{S}");
        productions[11] = new Production("Q",
                new String[]{"ε"},
                "Q --> ε");
        productions[12] = new Production("X",
                new String[]{"E", "R", "E"},
                "X --> ERE");
        productions[13] = new Production("E",
                new String[]{String.valueOf(map_s2i.get("+")), "T", "E'"},
                "E --> +TE'");
        productions[14] = new Production("E",
                new String[]{String.valueOf(map_s2i.get("-")), "T", "E'"},
                "E --> -TE'");
        productions[15] = new Production("E",
                new String[]{"T", "E'"},
                "E --> TE'");
        productions[16] = new Production("E'",
                new String[]{"M", "E'"},
                "E' --> ME'");
        productions[17] = new Production("E'",
                new String[]{"ε"},
                "E' --> ε");
        productions[18] = new Production("M",
                new String[]{String.valueOf(map_s2i.get("+")), "T"},
                "M --> +T");
        productions[19] = new Production("M",
                new String[]{String.valueOf(map_s2i.get("-")), "T"},
                "M --> -T");
        productions[20] = new Production("T",
                new String[]{"F", "T'"},
                "T --> FT'");
        productions[21] = new Production("T'",
                new String[]{"N", "T'"},
                "T' --> NT'");
        productions[22] = new Production("T'",
                new String[]{"ε"},
                "T' --> ε");
        productions[23] = new Production("N",
                new String[]{String.valueOf(map_s2i.get("*")), "F"},
                "N --> *F");
        productions[24] = new Production("N",
                new String[]{String.valueOf(map_s2i.get("/")), "F"},
                "N --> /F");
        productions[25] = new Production("F",
                new String[]{String.valueOf(map_s2i.get("id"))},
                "F --> id");
        productions[26] = new Production("F",
                new String[]{String.valueOf(map_s2i.get("num"))},
                "F --> num");
        productions[27] = new Production("F",
                new String[]{String.valueOf(map_s2i.get("(")), "E", String.valueOf(map_s2i.get(")"))},
                "F --> (E)");
        productions[28] = new Production("R",
                new String[]{String.valueOf(map_s2i.get(">"))},
                "R --> >");
        productions[29] = new Production("R",
                new String[]{String.valueOf(map_s2i.get(">="))},
                "R --> >=");
        productions[30] = new Production("R",
                new String[]{String.valueOf(map_s2i.get("<"))},
                "R --> <");
        productions[31] = new Production("R",
                new String[]{String.valueOf(map_s2i.get("<="))},
                "R --> <=");
        productions[32] = new Production("R",
                new String[]{String.valueOf(map_s2i.get("=="))},
                "R --> ==");
        productions[33] = new Production("R",
                new String[]{String.valueOf(map_s2i.get("!="))},
                "R --> !=");
        productions[34] = new Production("S'",
                new String[]{"S", "S'"},
                "S' --> SS'");
        productions[35] = new Production("S'",
                new String[]{"ε"},
                "S' --> ε");
        productions[36] = new Production("A",
                new String[]{String.valueOf(map_s2i.get("char"))},
                "A --> char");
        productions[37] = new Production("A",
                new String[]{String.valueOf(map_s2i.get("short"))},
                "A --> short");
        productions[38] = new Production("A",
                new String[]{String.valueOf(map_s2i.get("int"))},
                "A --> int");
        productions[39] = new Production("A",
                new String[]{String.valueOf(map_s2i.get("long"))},
                "A --> long");
        productions[40] = new Production("A'",
                new String[]{String.valueOf(map_s2i.get("float"))},
                "A --> float");
        productions[41] = new Production("A",
                new String[]{String.valueOf(map_s2i.get("double"))},
                "A --> double");
    }

    private static void initMap() {
        map_s2i = new HashMap<>();
        map_s2i.put("char", 1);
        map_s2i.put("short", 2);
        map_s2i.put("int", 3);
        map_s2i.put("long", 4);
        map_s2i.put("float", 5);
        map_s2i.put("double", 6);
        map_s2i.put("final", 7);
        map_s2i.put("static", 8);
        map_s2i.put("if", 9);
        map_s2i.put("else", 10);
        map_s2i.put("while", 11);
        map_s2i.put("do", 12);
        map_s2i.put("for", 13);
        map_s2i.put("break", 14);
        map_s2i.put("continue", 15);
        map_s2i.put("void", 16);
        map_s2i.put("id", 20);
        map_s2i.put("num", 30);
        map_s2i.put("=", 31);
        map_s2i.put("==", 32);
        map_s2i.put(">", 33);
        map_s2i.put("<", 34);
        map_s2i.put(">=", 35);
        map_s2i.put("<=", 36);
        map_s2i.put("+", 37);
        map_s2i.put("-", 38);
        map_s2i.put("*", 39);
        map_s2i.put("/", 40);
        map_s2i.put("(", 41);
        map_s2i.put(")", 42);
        map_s2i.put("[", 43);
        map_s2i.put("]", 44);
        map_s2i.put("{", 45);
        map_s2i.put("}", 46);
        map_s2i.put(",", 47);
        map_s2i.put(":", 48);
        map_s2i.put(";", 49);
        map_s2i.put("!=", 50);
        map_s2i.put("$", 60);

        map_i2s = new HashMap<>();
        map_i2s.put(1, "char");
        map_i2s.put(2, "short");
        map_i2s.put(3, "int");
        map_i2s.put(4, "long");
        map_i2s.put(5, "float");
        map_i2s.put(6, "double");
        map_i2s.put(7, "final");
        map_i2s.put(8, "static");
        map_i2s.put(9, "if");
        map_i2s.put(10, "else");
        map_i2s.put(11, "while");
        map_i2s.put(12, "do");
        map_i2s.put(13, "for");
        map_i2s.put(14, "break");
        map_i2s.put(15, "continue");
        map_i2s.put(16, "void");
        map_i2s.put(20, "id");
        map_i2s.put(30, "num");
        map_i2s.put(31, "=");
        map_i2s.put(32, "==");
        map_i2s.put(33, ">");
        map_i2s.put(34, "<");
        map_i2s.put(35, ">=");
        map_i2s.put(36, "<=");
        map_i2s.put(37, "+");
        map_i2s.put(38, "-");
        map_i2s.put(39, "*");
        map_i2s.put(40, "/");
        map_i2s.put(41, "(");
        map_i2s.put(42, ")");
        map_i2s.put(43, "[");
        map_i2s.put(44, "]");
        map_i2s.put(45, "{");
        map_i2s.put(46, "}");
        map_i2s.put(47, ",");
        map_i2s.put(48, ":");
        map_i2s.put(49, ";");
        map_i2s.put(50, "!=");
        map_i2s.put(60, "$");
    }

    /**
     * 产生式类
     */
    private static class Production {
        String l_str;
        String[] r_str;
        String prod;
        public Production(String l_str, String[] r_str, String prod) {
            this.l_str = l_str;
            this.r_str = r_str;
            this.prod = prod;
        }
    }
}
