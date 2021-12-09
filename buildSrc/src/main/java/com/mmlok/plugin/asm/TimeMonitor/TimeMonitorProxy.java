package com.mmlok.plugin.asm.TimeMonitor;

import com.mmlok.plugin.asm.XProxy;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class TimeMonitorProxy implements XProxy {

    @Override
    public byte[] invoke(byte[] sourceClassByte, String className) {

        if (className == null || !className.contains("MainActivity.class")) {
            return sourceClassByte;
        }
        ClassReader classReader = new ClassReader(sourceClassByte);
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
        TimeMonitorVisitor timeMonitorVisitor = new TimeMonitorVisitor(className, classWriter);
        //跳过栈图和栈表StackMap and StackMapTable的访问
        classReader.accept(timeMonitorVisitor, ClassReader.SKIP_FRAMES);
        return classWriter.toByteArray();
    }
}
