package com.mmlok.plugin.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

    public static void outputClazz(byte[] bytes, String className) {
        // 输出类字节码
        FileOutputStream out = null;
        try { //TODO agent如何输出修改后代码
            String pathName = "/Users/mml/workProject/AndroidProject/PluginDemo/buildSrc/build/classes/java/main/com/mmlok/plugin/" + className + ".class";
            out = new FileOutputStream(new File(pathName));
            System.out.println("ASM类输出路径：" + pathName);
            out.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
