package com.mmlok.plugin.asm.TimeMonitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class TimeMonitorVisitor extends ClassVisitor {

    private String mClassName;

    public TimeMonitorVisitor(String className, ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
        this.mClassName = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {

        if (name.equals("onCreate") ) {
            MethodVisitor mv = cv.visitMethod(access,name,descriptor,signature,exceptions);
            return new TimeMonitorAdapter(mv, access,name,descriptor);
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }



    class TimeMonitorAdapter extends AdviceAdapter {

        private MethodVisitor mMethodVisitor;

        protected TimeMonitorAdapter(MethodVisitor methodVisitor, int access,
                                      String name, String descriptor) {
            super(Opcodes.ASM5, methodVisitor, access, name, descriptor);
            this.mMethodVisitor = methodVisitor;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name,
                                    String descriptor, boolean isInterface) {

            if (opcode == INVOKESTATIC &&
                    name.equals("initialize") &&
                    owner.equals("com/facebook/drawee/backends/pipeline/Fresco") &&
                    descriptor.equals("(Landroid/content/Context;)V")) {

                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System",
                        "currentTimeMillis", "()J", false);
                mv.visitVarInsn(LSTORE, 2);
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

                mv.visitLdcInsn("MainActivity");
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitLdcInsn("fresco \u521d\u59cb\u5316\u8017\u65f6\uff1a");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                mv.visitVarInsn(LLOAD, 2);
                mv.visitInsn(LSUB);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
                mv.visitLdcInsn(" ms");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false);
                mv.visitInsn(POP);
                return;
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
