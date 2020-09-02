package com.cuilifan.asmplugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class ASMTransform extends Transform {

    @Override
    public String getName() {
        return "ASMTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput transformInput : inputs) {
            Collection<DirectoryInput> directoryInputs = transformInput.getDirectoryInputs();
            for (DirectoryInput directoryInput : directoryInputs) {
                File file = directoryInput.getFile();
                handleFile(file, transformInvocation, directoryInput);
                File contentLocation = transformInvocation.getOutputProvider().getContentLocation(directoryInput.getName(), directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
                FileUtils.copyDirectory(file, contentLocation);
            }

        }
    }

    private void handleFile(File file, TransformInvocation transformInvocation, DirectoryInput directoryInput) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subFile : files) {
                handleFile(subFile, transformInvocation, directoryInput);
            }
        } else {
            String name = file.getName();
            System.out.println("Find File2:".concat(name));
            if (name.endsWith("class")) {
                try {
                    ClassReader classReader = new ClassReader(new FileInputStream(file));
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                    ClassVisitor classVisitor = new ASMClassVisitor(Opcodes.ASM5, classWriter);
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
                    byte[] byteArray = classWriter.toByteArray();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(byteArray);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
