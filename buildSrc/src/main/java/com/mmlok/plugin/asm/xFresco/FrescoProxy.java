package com.mmlok.plugin.asm.xFresco;

import com.mmlok.plugin.asm.XProxy;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class FrescoProxy implements XProxy {

    @Override
    public byte[] invoke(byte[] sourceClassByte, String className) {
        if (className == null || !className.contains("Fresco.class")) {
            return sourceClassByte;
        }
        ClassReader classReader = new ClassReader(sourceClassByte);
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
        FrescoVisitor frescoVisitor = new FrescoVisitor(className, classWriter);
        //跳过栈图和栈表StackMap and StackMapTable的访问
        classReader.accept(frescoVisitor, ClassReader.SKIP_FRAMES);
        return classWriter.toByteArray();
    }
}
