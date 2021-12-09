package com.mmlok.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class Test2Transform extends Transform {

    @Override
    public String getName() { //该transform的名字
        return "Test2TransformTask";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() { //输入类型，作为输入过滤的手段。
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() { //用于指明Transform的作用域
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException,
            InterruptedException, IOException {
        super.transform(transformInvocation);

        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        for(TransformInput input : inputs) {

            //jar包
            for(JarInput jarInput : input.getJarInputs()) {
                File jarFile = jarInput.getFile();
                File tmpDir = transformInvocation.getContext().getTemporaryDir();
                System.out.println("JarInput Input: " + jarFile.getName());
                //1、处理完字节码
                File changedJarFile = JarModifyUtil.changedJarFile(tmpDir, jarFile);

                //2、获取应该输出的位置
                File outputDir = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);

                //3、输出处理完的jar包
                FileUtils.copyFile(changedJarFile, outputDir);
            }
            //业务
            for(DirectoryInput directoryInput : input.getDirectoryInputs()) {

                File fileDir = directoryInput.getFile();

                for (File file : FileUtils.getAllFiles(fileDir)) {
                    System.out.println("directoryInput Input: " + file.getName());
                    JarModifyUtil.modifyClassFile(file);
                }

                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);

//                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                FileUtils.copyDirectory(directoryInput.getFile(), dest);
//
            }
        }
        Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs();

    }
}
