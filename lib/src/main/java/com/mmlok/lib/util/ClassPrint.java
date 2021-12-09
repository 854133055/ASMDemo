package com.mmlok.lib.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClassPrint {

    public static void output(byte[] bytes, String className) {
        // 输出类字节码
        FileOutputStream out = null;
        try {
            String pathName = ClassPrint.class.getResource("/").getPath()  + className +".class";
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
