package com.mmlok.plugin;

import com.mmlok.plugin.asm.TimeMonitor.TimeMonitorProxy;
import com.mmlok.plugin.asm.xFresco.FrescoProxy;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class JarModifyUtil {

    public static File changedJarFile(File tempDir, File jarFile) throws IOException {
//        String suffix = DigestUtils.md5Hex(jarFile.getAbsolutePath()).substring(0, 4);
        File jarOutPutFile = new File(tempDir, jarFile.getName());
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarOutPutFile));

        JarFile jarFiled = new JarFile(jarFile);
        Enumeration<JarEntry> entries = jarFiled.entries();
        while (entries.hasMoreElements()) {
            //可以理解为jar中的每个class
            JarEntry jarEntry = entries.nextElement();
            jarOutputStream.putNextEntry(new ZipEntry(jarEntry.getName()));
            byte[] resultClassByte = modifyJarClass(jarFiled, jarEntry);
            jarOutputStream.write(resultClassByte);
            jarOutputStream.closeEntry();
        }
        jarOutputStream.close();
        return jarOutPutFile;
    }


    public static byte[] modifyJarClass(JarFile jarFile, JarEntry jarEntry) throws IOException {
        byte[] outPutClassByte = IOUtils.toByteArray(jarFile.getInputStream(jarEntry));

        if (jarEntry.getName().startsWith("com/facebook/drawee/backends/pipeline")) {
            System.out.println("modifyJarClass: " + jarEntry.getName());
        }
        //各业务注入
        outPutClassByte = new FrescoProxy().invoke(outPutClassByte, jarEntry.getName());

        return outPutClassByte;
    }

    /**
     * 所有的都转成byte了，需要优化，只有需要插桩的才转换
     */
    public static File modifyClassFile(File classFile) throws IOException {
        byte[] outputClassByte = IOUtils.toByteArray(new FileInputStream(classFile));
        //各业务注入
        outputClassByte = new TimeMonitorProxy().invoke(outputClassByte,classFile.getName());

        FileOutputStream fileOutputStream = new FileOutputStream(classFile);
        fileOutputStream.write(outputClassByte);
        fileOutputStream.close();
        return classFile;
    }
}
