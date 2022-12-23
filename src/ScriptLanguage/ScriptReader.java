package ScriptLanguage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScriptReader {
    private final Map<String, String> reader = new HashMap<>();
    public ScriptReader (File file) {
            fileReader(file);
        }

    private void fileReader (File file) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                if (file.canRead()) {
                    String a;
                    while ((a = bufferedReader.readLine()) != null) {
                        if (a.isEmpty()) continue;
                        StringBuilder stringBuilder = new StringBuilder();
                        char[] chars = a.toCharArray();
                        Character character;
                        for (int i = 0; i < a.length(); i++) {
                            character = chars[i];
                            if (character == null) continue;
                            if (chars[0] == '#') break;
                            if (String.valueOf(stringBuilder).equals(Operator.print.toString())) {
                                print(a.substring(i + 1));
                                break;
                            }
                            if (String.valueOf(stringBuilder).equals(Operator.set.toString())) {
                                set(a.substring(i + 1));
                                break;
                            }
                            if (Character.isLetter(character)) {
                                stringBuilder.append(character);
                                if (i != a.length() - 1) continue;
                            }
                        }
                    }
                }
            } catch(IOException ex) {
                Logger.getLogger(ScriptReader.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(file + " не существует или не может быть прочтен.");
                System.exit(1);
            }
    }

    private void print(String a){
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder resultBuilder = new StringBuilder();
        char[] chars = a.toCharArray();
        Character character;
        int flag = 0;
        for (int i = 0; i < a.length(); i++) {
            character = chars[i];
            if (i==a.length()-1){
                if(!character.equals('"')) {
                    stringBuilder.append(character);
                }
                if(reader.containsKey(String.valueOf(stringBuilder))) {
                    System.out.println(reader.get(String.valueOf(stringBuilder)));
                } else {
                    System.out.println(stringBuilder);
                    break;
                }
                }

                if (character.equals('"')) {
                    if (!Character.isWhitespace(character)) {
                        if (reader.containsKey(String.valueOf(stringBuilder)))
                            System.out.print(reader.get(String.valueOf(stringBuilder)));
                        else
                            System.out.print(stringBuilder);
                        stringBuilder.delete(0,stringBuilder.length());
                    }
                    flag++;
                    if (flag==1)
                        continue;
            }

            // создаем слово внутри скобок
            if (flag == 1) {
                stringBuilder.append(character);
                continue;
            }

            // формируем финальную строку, выходим из скобок
            if (flag == 2) {
                flag=0;
                resultBuilder.append(stringBuilder);

                stringBuilder.delete(0, stringBuilder.length());
                continue;
            }

            // формируем текущее слово
            if (Character.isLetter(character) || character.equals('$')
                    || Character.isDigit(character) || character.equals('_')) {
                stringBuilder.append(character);
            }
        }
        System.out.println(resultBuilder);
    }



    private void set(String a) {
        String value = "";
        boolean fVal = false;
        char[] charArray = a.toCharArray();
        Character character;
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder wordName = new StringBuilder();
        StringBuilder finalStr = new StringBuilder();
        for (int i = 0; i < a.length(); i++) {
            character = charArray[i];
            // формируем значение переменной
            if (fVal) {

                if (character.equals('='))
                    continue;

                if (character.equals('$') || Character.isLetter(character)
                        || Character.isDigit(character) || character.equals('_')) {
                    stringBuilder.append(character);
                    if (!String.valueOf(stringBuilder).contains("$"))
                        value = String.valueOf(stringBuilder);
                    if (i!=a.length()) continue;
                }

                if (Character.isWhitespace(character)) {
                    if (String.valueOf(stringBuilder).equals(""))
                        continue;

                    if (!String.valueOf(stringBuilder).contains("$")) {
                        finalStr.append(stringBuilder);
                        value = String.valueOf(stringBuilder);
                        stringBuilder.delete(0,stringBuilder.length());
                        continue;
                    }

                    if (reader.containsKey(String.valueOf(stringBuilder)))
                        finalStr.append(reader.get(String.valueOf(stringBuilder)));
                    else {
                        System.out.println("Неизвестная переменная " + stringBuilder);
                        System.exit(1);
                    }
                    if (!String.valueOf(stringBuilder).contains("$"))
                        value = String.valueOf(stringBuilder);
                    stringBuilder.delete(0, stringBuilder.length());
                    continue;
                }

                if (character.equals('-') || character.equals('+'))
                    finalStr.append(character);

                if (Character.isDigit(character)) {
                    value = value + character;
                    if (i!=a.length()) continue;
                }
            }

            // формируем название переменной
            if (Character.isLetter(character) || character.equals('$') ||
                    Character.isDigit(character) || character.equals('_')) {
                wordName.append(character);
            } else {
                fVal = true;
                continue;
            }
        }

        finalStr.append(value);
        value = String.valueOf(calculate(String.valueOf(finalStr)));


        // положим в коллекцию переменную со значением
        if (!value.equals(""))
            reader.put(String.valueOf(wordName), value);


    }

    public static int calculate(String str) {
        StringBuilder calculateList = new StringBuilder();
        StringBuilder strForCalc = new StringBuilder();
        char chars;
        Deque<Integer> stack = new ArrayDeque<>();;
        StringTokenizer st;

        // сформируем две строки с операндами и числами
        for (int i = 0; i < str.length(); i++) {
            chars = str.charAt(i);
            if (chars=='-' || chars=='+') {
                while (calculateList.length() > 0) {
                    strForCalc.append(" ");
                    break;
                }
                strForCalc.append(" ");
                calculateList.append(chars);
            }   else {
                strForCalc.append(chars);
            }
        }

        // добавим операнды к числам в обратном порядке
        while (calculateList.length() > 0) {
            strForCalc.append(" ").append(calculateList.substring(calculateList.length()-1));
            calculateList.setLength(calculateList.length()-1);
        }

        // посчитаем выражение
        int val1,
                val2;
        String curStr;
        st = new StringTokenizer(strForCalc.toString());
        while(st.hasMoreTokens()) {
            curStr = st.nextToken().trim();
            if (curStr.length() == 1) {
                val2 = stack.pop();
                val1 = stack.pop();
                switch (curStr.charAt(0)) {
                    case '+':
                        val1 += val2;
                        break;
                    case '-':
                        val1 -= val2;
                        break;
                    default:
                }
                stack.push(val1);
            } else {
                try {
                    val1 = Integer.parseInt(curStr);
                    stack.push(val1);
                } catch (NumberFormatException e) {
                    System.out.println("Некорректное значение");
                    System.exit(1);
                }
            }
        }
        return stack.pop();
    }
            }