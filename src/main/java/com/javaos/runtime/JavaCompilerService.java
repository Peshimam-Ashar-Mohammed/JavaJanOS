package com.javaos.runtime;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.nio.file.Path;

public final class JavaCompilerService {

    public boolean compile(Path sourceFile) {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {
            System.out.println("Error: Java compiler not found.");
            System.out.println("Make sure JavaOS is running on a JDK.");
            return false;
        }

        if (!sourceFile.toString().endsWith(".java")) {
            System.out.println("Error: Source file must have a .java extension.");
            return false;
        }

        if (!java.nio.file.Files.exists(sourceFile)) {
            System.out.println("Error: File not found: " + sourceFile);
            return false;
        }

        System.out.println("Compiling " + sourceFile.getFileName() + "...");

        int result = compiler.run(
                null,
                System.out,
                System.err,
                "-d",
                sourceFile.toAbsolutePath().getParent().toString(),
                sourceFile.toString()
        );

        if (result == 0) {
            System.out.println("Compilation successful!");
            return true;
        }

        System.out.println("Compilation failed.");
        return false;
    }
}