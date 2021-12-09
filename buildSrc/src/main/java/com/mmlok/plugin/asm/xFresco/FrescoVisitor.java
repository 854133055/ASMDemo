package com.mmlok.plugin.asm.xFresco;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class FrescoVisitor extends ClassVisitor {

    private String mClassName;
    private final String FRESCO_INITIALIZE_DESC =
            "(Landroid/content/Context;Lcom/facebook/imagepipeline/core/ImagePipelineConfig;Lcom/facebook/drawee/backends/pipeline/DraweeConfig;)V";

    public FrescoVisitor(String className, ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
        this.mClassName = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        if (name.equals("initialize") && FRESCO_INITIALIZE_DESC.equals(descriptor)) {
            MethodVisitor mv = cv.visitMethod(access,name,descriptor,signature,exceptions);
            return new FrescoAdviceAdapter(mv, access,name,descriptor);
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }



    class FrescoAdviceAdapter extends AdviceAdapter {

        private MethodVisitor mMethodVisitor;

        protected FrescoAdviceAdapter(MethodVisitor methodVisitor, int access,
                                      String name, String descriptor) {
            super(Opcodes.ASM5, methodVisitor, access, name, descriptor);
            this.mMethodVisitor = methodVisitor;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name,
                                    String descriptor, boolean isInterface) {

            if (opcode == 184 && owner.equals("com/facebook/common/logging/FLog") &&
                    descriptor.equals("(Ljava/lang/Class;Ljava/lang/String;)V")) {

                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                mv.visitInsn(RETURN);
                return;
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
