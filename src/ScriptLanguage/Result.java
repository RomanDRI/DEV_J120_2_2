package ScriptLanguage;

import java.io.File;

public class Result {
    public static void main(String[] args)  {
        if (args.length == 0) {
            System.out.println("Задайте аргумент в виде файла формата txt. Это можно сделать в командной строке или" +
                    " в компиляторе Run -> edit Configuration -> Program arguments (пример test.txt).");
            System.exit(1);
        }
        new ScriptReader(new File(args[0]));
    }
}
