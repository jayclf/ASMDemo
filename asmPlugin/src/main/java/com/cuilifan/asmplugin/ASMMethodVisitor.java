package com.cuilifan.asmplugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ASMMethodVisitor extends MethodVisitor {
    private final String className;
    private final String methodName;

    public ASMMethodVisitor(MethodVisitor methodVisitor, String classname, String methodname) {
        super(Opcodes.ASM5, methodVisitor);
        this.className = classname;
        this.methodName = methodname;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        System.out.println("visit code ".concat(methodName));

        mv.visitLdcInsn("TAG");
        mv.visitLdcInsn(className + "------>" + methodName);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
    }
}
