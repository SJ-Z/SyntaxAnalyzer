# SyntaxAnalyzer
java实现的语法分析器及词法分析器


语法分析器是编译原理的一个实验，本文将会详细给出实现的具体步骤，利用java进行示例讲解，源博客地址 https://blog.csdn.net/qq_40121502/article/details/86069528
## 一、实验目的
设计、编写一个语法分析程序，加深对语法分析原理的理解。
## 二、实验原理
语法分析器是在词法分析之后，根据词法分析的结果和定义的语法规则判断输入的程序是否有语法错误，LL(1)分析是使用显式栈而不是递归调用来完成分析。以标准方式表示这个栈非常有用，这样LL(1)分析程序的动作就可以快捷地显现出来。LL(1)的含义是：第一个L表明自顶向下分析是从左向右扫描输入串，第2个L表明分析过程中将使用最左推导，1表明只需向右看一个符号便可决定如何推导，即选择哪个产生式(规则)进行推导。
## 三、实验内容
（1）程序利用Java语言，给出其词法的产生式。希望这个语言中包含数学运算表达式，赋值，函数调用，控制流语句（分支或循环），类型声明等基本元素。
（2）本实验设计的是基于LL(1) 分析的语法分析器，并用程序实现了对所写语言的LL(1) 分析测试。
## 四、实验方法
（1）定义文法语言，将文法用产生式表示。
（2）提取公共左因子，消除左递归。
（3）求FIRST和FOLLOW集。
（4）构造LL(1)预测分析表。
（5）根据分析表编写程序，测试程序。
## 五、实验设计
#### 1.定义语法分析使用的文法语言：
<程序> ::= <语句><程序> | Ɛ;
<语句> ::= <变量定义语句> | <赋值语句> | <函数调用语句> | <if语句>
           | <循环语句> | Ɛ
<变量定义语句> ::= <变量类型><标识符表>;                       
<赋值语句> ::= <标识符> = <表达式>;
<函数调用语句> ::= <标识符> ( <标识符表> );
<if语句> ::= if ( <条件表达式> ) { <语句> } <else语句>
<else语句> ::= else{ <语句> } | Ɛ
<循环语句> ::= while ( <条件表达式> ) { <语句> }
<标识符表> ::= <标识符>| <标识符表>,<标识符>   	         
<条件表达式> ::= <表达式><比较运算符><表达式>
<比较运算符> ::= > | >= | < | <= | != | ==
<变量类型> ::= char | short | int | long | float | double
<表达式> ::= +T | -T | T | <表达式> + T | <表达式>-T       
T ::= F | T*F | T/F 
F ::= <标识符> | <无符号整数> | (<表达式 >)
#### 2.将上述文法用产生式表示：
其中，S’:程序（语句的组合），S:语句，Q:else语句，L:标识符表，E:表达式，X:条件表达式，R:比较运算符，id:标识符，num:常量
	S’ → S S’| Ɛ
S → A L; | id=E; | id(L); | if(X){S}Q | while(X){S} | Ɛ
A → char | short | int | long | float | double
Q → else{S} | Ɛ
L → id | L , id
X → ERE
R → > | >= | < | <= | == | !=
E → +T | -T | T | E+T | E-T
T → F | T*F | T/F
F → id | num | (E)
#### 3.提取公共左因子：
S’ → S S’| Ɛ
S → A L; | id B | if(X){S}Q | while(X){S} | Ɛ
A → char | short | int | long | float | double
B → (L); | =E;
L → id | L , id
Q → else{S} | Ɛ
X → ERE
R → > | >= | < | <= | == | !=
E → +T | -T | T | EM
M → +T | -T
T → F | TN
N → *F |/F
F → id | num | (E)
#### 4.消除左递归： 
S’ → S S’
S’ → Ɛ          
S → A L;             
S → id B                     
S → if(X){S}Q     
S → while(X){S}    
S → Ɛ
B → (L);          
B → =E;            
L → id L’        
L’→ , id L’         
L’→ Ɛ            
Q → else{S}        
Q → Ɛ            
X → ERE      
E → TE’         
E → +TE’           
E → -TE’        
E’ → ME’       
E’ → Ɛ       
M → +T       
M → -T         
T → FT’       
T’ → NT’       
T’ → Ɛ          
N → *F        
N → /F           
F → id           
F → num            
F → (E)            
R → >              
R → >=             
R → <          
R → <=           
R → ==             
R → != 
A → char
A → short
A → int 
A → long
A → float
A → double
#### 5.求FIRST集：
First(S’)={ char , short , int , long , float , double , id , if , while , Ɛ }
First(S)={ char , short , int , long , float , double , id , if , while , Ɛ }
First(A)={ char , short , int , long , float , double }
First(B)={ ( , = }
First(L)={ id }
First(L’)={ ，, Ɛ }
First(Q)={ else , Ɛ }
First(X)={ + , - , id , num , ( }
First\(R\)={ > , >= , < , <= , != , == }
First(E)={ + , - , id , num , ( }
First(E’)={ + , - , Ɛ }
First(M)={ + , - }
First(T)={ id , num , ( }
First(T’)={ * , / , Ɛ }
First(N)={ * , / }
First(F)={ id , num , ( }
#### 6.求FOLLOW集：
Follow (S’)={ $ }
Follow (S)={ $ , } }
Follow (B)={ $ , } }
Follow (L)={ $ , ) , ; , } } 
Follow (L’)={ $ , ) , ; , } }
Follow (Q)={ $ , } }
Follow (X)={ ) }
Follow \(R\)={ + , - , id , num , ( }
Follow (E)={ ) , ; , > , >= , < , <= , != , == }
Follow (E’)={ ) , ; , > , >= , < , <= , != , == }
Follow (M)={ ) , ; , > , >= , < , <= , != , == , + , - }
Follow (T)={ ) , ; , > , >= , < , <= , != , == , + , - }
Follow (T’)={ ) , ; , > , >= , < , <= , != , == , + , - }
Follow (N)={ ) , ; , > , >= , < , <= , != , == , + , - , * , / }
Follow (F)={ ) , ; , > , >= , < , <= , != , == , + , - , * , / }
#### 7.构造LL(1)的预测分析表：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190108130724684.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwMTIxNTAy,size_16,color_FFFFFF,t_70)
LL（1）预测分析表中的数字分别代表的产生式如下：         
0：S → A L;            
1：S → id B
2：S → if(X){P}Q
3：S → while(X){P}
4：S → Ɛ     
5：B → (L);        
6：B → =E;            
7：L → id L’       
8：L’→ ,id L’      
9：L’→ Ɛ           
10：Q → else{S}         
11：Q → Ɛ             
12：X → ERE           
13：E → +TE’          
14：E → -TE’           
15：E → TE’          
16：E’→ ME’        
17：E’→ Ɛ             
18：M → +T            
19：M → -T             
20：T → FT’           
21：T’→ NT’        
22：T’→ Ɛ             
23：N → *F            
24：N → /F          
25：F → id             
26：F → num             
27：F → (E)            
28：R → >             
29：R → >=          
30：R → <              
31：R → <=           
32：R → ==            
33：R → !=
34：S’ → S S’
35：S’ → Ɛ
36：A → char
37：A → short
38：A → int 
39：A → long
40：A → float
41：A → double
## 六、数据结构
（1）使用ArrayList<String>来存储当前栈的内容
（2）使用ArrayList<Integer>来存储待读队列的内容，Integer此处为单词的种别码，用于表示词法分析器的分析结果。
自定义类Production，产生式类，包含String类型的完整产生式、String类型的产生式左侧符号、String[]类型的产生式右侧符号。（此处左右是相对于产生式中的→而言的）。
## 七、实现代码
语法分析器接收词法分析器的结果作为输入，即输入为单词种别码和相应的单词的序列。
例如：（种别码和单词间的逗号仅表示分隔）
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190108151507830.png)
#### 1.初始化
首先定义了五个全局变量：
```java
private static ArrayList<String> stack = new ArrayList<>(); // 当前栈
private static ArrayList<Integer> reader = new ArrayList<>(); // 待读队列
private static Production[] productions = new Production[42]; // 产生式数组
private static HashMap<Integer, String> map_i2s; // 种别码Map，种别码为键，单词为值
private static HashMap<String, Integer> map_s2i; // 种别码Map，单词为键，种别码为值
```
初始化种别码Map，要和词法分析器内单词对应的种别码相同：
```java
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
```
初始化产生式：规则自己定义
```java
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
```
main()函数的部分：
```java
int stackTop = 1;
int readerTop = 0;
int index = 0; // 当前步骤数
initMap(); // 初始化种别码Map
initProductions(); // 产生式初始化
stack.add(0, String.valueOf(map_s2i.get("$"))); // 在stack底部加上$
stack.add(stackTop, "S'"); // 将S'压入栈
```
#### 2.读入词法分析结果
```java
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
```
注意到我们在stack和reader末尾都添加了$，当两者的$匹配时表示语法分析结束。
#### 3.语法分析
语法分析的输出需要描述分析的过程，每一步分析需要列出当前栈、待读队列、下一步所用产生式。
示例输出如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190108154307193.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwMTIxNTAy,size_16,color_FFFFFF,t_70)
语法分析的步骤是：
1.输出当前栈的内容：从栈内依次取出栈顶符号，判断该符号是种别码还是字符，若为种别码，需将其对应转换成单词，若转换时抛出NumberFormatException，则说明是字符。将结果放入输出的buffer中。
注意，此处引入StringBuffer sb仅为控制在控制台的输出格式对齐。
2.输出待读队列：遍历输出reader的内容。
3.将reader队列第一个和stack当前栈栈顶元素进行匹配。若相同，说明是同一个终结符；若不同，则根据LL(1)预测分析表预测下一步推导所用产生式。

main()函数部分：
```java
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
```
检测reader头部和stack顶部是否匹配，返回匹配结果
```java
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
```
利用LL(1)预测分析表进行分析，返回单词种别码。
```java
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
```
#### 4.输出结果到文件
```java
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
```
## 八、运行结果
输入词法分析器的代码块：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190108155705389.png)
词法分析器结果：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190108155720542.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/201901081557281.png)
语法分析结果：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190108155802710.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwMTIxNTAy,size_16,color_FFFFFF,t_70)
……
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190108155813328.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQwMTIxNTAy,size_16,color_FFFFFF,t_70)
至此，语法分析器编写完成。如有问题，欢迎交流指正。

---------------------------------------------------------------
#### 2019-10-16补充stackPush方法：

```java
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
```
