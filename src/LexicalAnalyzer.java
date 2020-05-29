import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 词法分析器
 */
public class LexicalAnalyzer {

    // 单词种别码, 1-17为关键字种别码
    public static final int CHAR = 1;
    public static final int SHORT = 2;
    public static final int INT = 3;
    public static final int LONG = 4;
    public static final int FLOAT = 5;
    public static final int DOUBLE = 6;
    public static final int FINAL = 7;
    public static final int STATIC = 8;
    public static final int IF = 9;
    public static final int ELSE = 10;
    public static final int WHILE = 11;
    public static final int DO = 12;
    public static final int FOR = 13;
    public static final int BREAK = 14;
    public static final int CONTINUE = 15;
    public static final int VOID = 16;
    public static final int RETURN = 17;

    // 20为标识符种别码
    public static final int ID = 20;

    // 30为常量种别码
    public static final int NUM = 30;

    // 31-40为运算符种别码
    public static final int AS = 31; // =
    public static final int EQ = 32; // ==
    public static final int GT = 33; // >
    public static final int LT = 34; // <
    public static final int GE = 35; // >=
    public static final int LE = 36; // <=
    public static final int ADD = 37; // +
    public static final int SUB = 38; // -
    public static final int MUL = 39; // *
    public static final int DIV = 40; // /

    // 41-49为界限符种别码
    public static final int LP = 41; // (
    public static final int RP = 42; // )
    public static final int LBT = 43; // [
    public static final int RBT = 44; // ]
    public static final int LBS = 45; // {
    public static final int RBS = 46; // }
    public static final int COM = 47; // ,
    public static final int COL = 48; // :
    public static final int SEM = 49; // ;

    // -1为无法识别的字符标志码
    public static final int ERROR = -1;
    public static int errorNum = 0; // 记录词法分析错误的个数

    // 判断是否为字母
    public static boolean isLetter(char c) {
        if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'))) {
            return true;
        }
        return false;
    }

    // 判断是否为关键字，若是则返回关键字种别码
    public static int isKeyID(String str) {
        String keystr[] = {"char", "short", "int", "long", "float", "double", "final", "static", "if", "else", "while",
                "do", "for", "break", "continue", "void", "return"};
        for (int i = 0; i < keystr.length; i++) {
            if (str.equals(keystr[i])) {
                return i + 1;
            }
        }
        return 0;
    }

    // 判断是否为常量（整数、小数、浮点数）
    public static boolean isNum(String str) {
        int dot = 0; // .的个数
        int notNum = 0; // 不是数字的个数
        for (int i = 0; i < str.length(); i++) {
            if (!(str.charAt(i) >= '0' && str.charAt(i) <= '9')) {
                notNum++;
                if (notNum > dot + 1) {
                    System.out.println("该常量" + str + "的词法不正确");
                    return false;
                } else if (str.charAt(i) == '.') {
                    dot++;
                    if (dot > 1)
                    {
                        System.out.println("该常量" + str + "的词法不正确");
                        return false;
                    }
                } else if ((str.charAt(i-1) >= '0' && str.charAt(i-1) <= '9') && (str.charAt(i) == 'E')
                        && (i == str.length() - 1 || (str.charAt(i+1) >= '0' && str.charAt(i+1) <= '9'))) {
                    continue;
                } else {
                    System.out.println("该常量" + str + "的词法不正确");
                    return false;
                }
            }
        }
        return true;
    }

    // 词法分析函数
    public static ArrayList<Pair> analyse(ArrayList<String> arrayList) {
        ArrayList<Pair> result = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).length() == 1) {
                if (arrayList.get(i).equals("=")) { // 运算符"="
                    if (arrayList.get(i+1).equals("=")) { // 若后面跟的是"="，则是运算符"=="
                        result.add(new Pair(EQ, arrayList.get(i) + arrayList.get(++i)));
                    } else { // 否则是运算符"="
                        result.add(new Pair(AS, arrayList.get(i)));
                    }
                } else if (arrayList.get(i).equals(">")) { // 运算符">"
                    if (arrayList.get(i+1).equals("=")) { // 若后面跟的是"="，则是运算符">="
                        result.add(new Pair(GE, arrayList.get(i) + arrayList.get(++i)));
                    } else { // 否则是运算符">"
                        result.add(new Pair(GT, arrayList.get(i)));
                    }
                } else if (arrayList.get(i).equals("<")) { // 运算符"<"
                    if (arrayList.get(i+1).equals("=")) { // 若后面跟的是"="，则是运算符"<="
                        result.add(new Pair(LE, arrayList.get(i) + arrayList.get(++i)));
                    } else { // 否则是运算符"<"
                        result.add(new Pair(LT, arrayList.get(i)));
                    }
                } else if (arrayList.get(i).equals("+")) { // 运算符"+"
                    if ((arrayList.get(i-1).equals("=") || arrayList.get(i-1).equals("("))
                        && isNum(arrayList.get(i+1))) { // 判断是否是有符号常量（正数）
                        result.add(new Pair(NUM, arrayList.get(i) + arrayList.get(++i)));
                    } else { // 否则是运算符"+"
                        result.add(new Pair(ADD, arrayList.get(i)));
                    }
                } else if (arrayList.get(i).equals("-")) { // 运算符"-"
                    if ((arrayList.get(i-1).equals("=") || arrayList.get(i-1).equals("("))
                            && isNum(arrayList.get(i+1))) { // 判断是否是有符号常量（负数）
                        result.add(new Pair(NUM, arrayList.get(i) + arrayList.get(++i)));
                    } else { // 否则是运算符"-"
                        result.add(new Pair(SUB, arrayList.get(i)));
                    }
                } else if (arrayList.get(i).equals("*")) { // 运算符"*"
                    result.add(new Pair(MUL, arrayList.get(i)));
                } else if (arrayList.get(i).equals("/")) { // 运算符"/"
                    result.add(new Pair(DIV, arrayList.get(i)));
                } else if (arrayList.get(i).equals("(")) { // 界限符"("
                    result.add(new Pair(LP, arrayList.get(i)));
                } else if (arrayList.get(i).equals(")")) { // 界限符")"
                    result.add(new Pair(RP, arrayList.get(i)));
                } else if (arrayList.get(i).equals("[")) { // 界限符"["
                    result.add(new Pair(LBT, arrayList.get(i)));
                } else if (arrayList.get(i).equals("]")) { // 界限符"]"
                    result.add(new Pair(RBT, arrayList.get(i)));
                } else if (arrayList.get(i).equals("{")) { // 界限符"{"
                    result.add(new Pair(LBS, arrayList.get(i)));
                } else if (arrayList.get(i).equals("}")) { // 界限符"}"
                    result.add(new Pair(RBS, arrayList.get(i)));
                } else if (arrayList.get(i).equals(",")) { // 界限符","
                    result.add(new Pair(COM, arrayList.get(i)));
                } else if (arrayList.get(i).equals(":")) { // 界限符":"
                    result.add(new Pair(COL, arrayList.get(i)));
                } else if (arrayList.get(i).equals(";")) { // 界限符";"
                    result.add(new Pair(SEM, arrayList.get(i)));
                } else if (arrayList.get(i).charAt(0) >= '0' && arrayList.get(i).charAt(0) <= '9') { // 判断是否是一位数字常量
                    result.add(new Pair(NUM, arrayList.get(i)));
                } else if (isLetter(arrayList.get(i).charAt(0))) { // 判断是否是一位字母标识符
                    result.add(new Pair(ID, arrayList.get(i)));
                } else { // 否则是无法识别的字符
                    result.add(new Pair(ERROR, arrayList.get(i)));
                    errorNum++;
                }
            } else if ((arrayList.get(i).charAt(0) >= '0' && arrayList.get(i).charAt(0) <= '9')
                        || arrayList.get(i).charAt(0) == '.') { // 判断是否是正确的常量
                if (!isNum(arrayList.get(i))) { // 不是常量，则是无法识别的字符
                    result.add(new Pair(ERROR, arrayList.get(i)));
                    errorNum++;
                } else if ((arrayList.get(i+1).charAt(0) == '+' || arrayList.get(i+1).charAt(0) == '-')
                        && isNum(arrayList.get(i+2))) { // 判断是否是有符号的常量
                    result.add(new Pair(NUM, arrayList.get(i) + arrayList.get(++i) + arrayList.get(++i)));
                } else { // 否则是无符号的常量
                    result.add(new Pair(NUM, arrayList.get(i)));
                }
            } else if (isKeyID(arrayList.get(i)) != 0) { // 判断是否为关键字
                result.add(new Pair(isKeyID(arrayList.get(i)), arrayList.get(i)));
            } else if (isLetter(arrayList.get(i).charAt(0)) || arrayList.get(i).charAt(0) == '_') { // 判断是否为标识符（以字母或者下划线开头）
                result.add(new Pair(ID, arrayList.get(i)));
            } else { // 否则是无法识别的单词
                result.add(new Pair(ERROR, arrayList.get(i)));
                errorNum++;
            }
        }
        return result;
    }

    /**
     * 去除字符串开头的空格和换行符，找到第一个有效字符的位置
     * @param str 目标字符串
     * @param begin 开始位置
     * @return 第一个有效字符在字符串中的位置
     */
    public static int getFirstChar(String str, int begin) {
        while (true) {
            if (str.charAt(begin) != ' ' && str.charAt(begin) != '\n' ) {
                return begin;
            }
            begin++;
        }
    }

    /**
     * 获取一个单词
     * @param str 目标字符串
     * @param begin 查找的开始位置
     * @return 单词
     */
    public static String getWord(String str, int begin) {
        String regEx = "\\s+|\\n|\\+|-|\\*|/|=|\\(|\\)|\\[|]|\\{|}|,|:|;"; // 正则
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        int end;
        if (m.find(begin)) {
            end =  m.start();
        } else {
            return "";
        }
        if (begin == end) {
            return String.valueOf(str.charAt(begin));
        }
        return str.substring(begin, end);
    }

    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
    }

    public static void main(String[] args) {
        final String blank = " ";
        final String newLine = "\n";
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入测试文件的路径：");
        String filepath = scanner.next();
        StringBuffer sb = new StringBuffer();
        String fileStr = "";
        try {
            readToBuffer(sb, filepath);
            fileStr = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int begin = 0, end = 0;   //分别表示单词的第一个和最后一个字符位置
        ArrayList<String> arrayList = new ArrayList<>();

        try {
            do {
                begin = getFirstChar(fileStr, begin);
                String word = getWord(fileStr, begin);
                end = begin + word.length() - 1; // 单词最后一个字符在原字符串的位置
                if (word.equals("")) {
                    break;
                }
                if (!(word.equals(blank) || word.equals(newLine))) {
                    arrayList.add(word);
                }
                begin = end + 1;
            } while (true);
        } catch (IndexOutOfBoundsException e) { // 文件读取结束
            ArrayList<Pair> result = analyse(arrayList);
            StringBuffer outputBuffer = new StringBuffer();
            for (Pair pair : result) {
                outputBuffer.append(pair.key + "," + pair.value + "\n");
            }
            System.out.println("词法分析结果如下：");
            System.out.println(outputBuffer.toString());

            System.out.print("请输入词法分析结果文件的保存路径：");
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
    }

    public static class Pair {
        Integer key;
        String value;
        Pair(Integer key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}
