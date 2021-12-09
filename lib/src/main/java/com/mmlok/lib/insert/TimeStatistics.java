package com.mmlok.lib.insert;

import com.mmlok.lib.HelloWorld;
import com.mmlok.lib.util.ClassPrint;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.lang.reflect.Method;

public class TimeStatistics extends ClassLoader{

    public static void main(String[] args) throws Exception {
        ClassReader reader = new ClassReader(HelloWorld.class.getName());
        //COMPUTE_MAXS是让asm自动计算栈最大深度和局部变量表的最大容量
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);

        ClassVisitor visitor = new HelloWorldVisitor(Opcodes.ASM5, writer);
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
        byte[] bytes = writer.toByteArray();

        //测试========
        ClassPrint.output(bytes, "HelloWorldChanged");
        Class<?> clazz = new TimeStatistics().defineClass("com.mmlok.lib.HelloWorld",
                bytes,0, bytes.length);
        Method method = clazz.getMethod("main", String[].class);
        String[] params = new String[]{"1", "2"};
        method.invoke(clazz, (Object) params);
    }


    public static class HelloWorldVisitor extends ClassVisitor {

        private ClassVisitor mClassVisitor;

        public HelloWorldVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
            mClassVisitor = classVisitor;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor,
                                         String signature, String[] exceptions) {
            if (name.equals("main")) {
                MethodVisitor mv = mClassVisitor.visitMethod(access, name,
                        descriptor, signature, exceptions);
                return new TimingVisitor(Opcodes.ASM5, mv, access, name, descriptor);
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    public static class TimingVisitor extends AdviceAdapter {

        private int timeIndex; //本地变量的index,不是值

        public TimingVisitor(int i, MethodVisitor methodVisitor, int i1,
                             String name, String s1) {
            super(i, methodVisitor, i1, name, s1);
        }

        @Override
        protected void onMethodEnter() {
            //获取当前时间，存到本地变量表
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System",
                    "currentTimeMillis", "()J", false);

            timeIndex = newLocal(Type.LONG_TYPE); //跨方法
            mv.visitVarInsn(Opcodes.LSTORE, timeIndex);
        }


        @Override
        protected void onMethodExit(int opcode) {
            if ((IRETURN <= opcode && opcode <= RETURN) || opcode == ATHROW) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
                        "Ljava/io/PrintStream;");
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder",
                        "<init>", "()V", false);
                mv.visitLdcInsn("\u8017\u65f6\uff1a");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
                        "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System",
                        "currentTimeMillis", "()J", false);
                mv.visitVarInsn(LLOAD, timeIndex);
                mv.visitInsn(LSUB);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
                        "append", "(J)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
                        "toString", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream",
                        "println", "(Ljava/lang/String;)V", false);
            }
        }


    }


}
