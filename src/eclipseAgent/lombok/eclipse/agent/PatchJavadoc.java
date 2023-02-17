/*
 * Copyright (C) 2020-2021 The Project Lombok Authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lombok.eclipse.agent;

import static lombok.eclipse.EcjAugments.CompilationUnit_javadoc;

import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2;

import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.permit.Permit;

public class PatchJavadoc {

    public static String getHTMLContentFromSource(String original, Object member) {
        if (original != null) {
            return original;
        }

        if (member instanceof SourceMethod) {
            SourceMethod sourceMethod = (SourceMethod) member;
            ICompilationUnit iCompilationUnit = sourceMethod.getCompilationUnit();
            if (iCompilationUnit instanceof CompilationUnit) {
                CompilationUnit compilationUnit = (CompilationUnit) iCompilationUnit;
                Map<String, String> docs = CompilationUnit_javadoc.get(compilationUnit);
                if (docs == null) return null;

                String signature = Signature.getSignature(sourceMethod);
                String rawJavadoc = docs.get(signature);
                if (rawJavadoc == null) return null;

                return Reflection.javadoc2HTML((IMember) member, (IJavaElement) member, rawJavadoc);
            }
        }

        return null;
    }

    public static StringBuffer printMethod(AbstractMethodDeclaration methodDeclaration, Integer tab, StringBuffer output, TypeDeclaration type) {
        Map<String, String> docs = CompilationUnit_javadoc.get(methodDeclaration.compilationResult.compilationUnit);
        if (docs != null) {
            String signature = EclipseHandlerUtil.getSignature(type, methodDeclaration);
            String rawJavadoc = docs.get(signature);
            if (rawJavadoc != null) {
                for (String line : rawJavadoc.split("\r?\n")) {
                    ASTNode.printIndent(tab, output).append(line).append("\n");
                }
            }
        }
        return methodDeclaration.print(tab, output);
    }

    private static class Signature {
        static final String getSignature(SourceMethod sourceMethod) {
            StringBuilder sb = new StringBuilder();
            sb.append(sourceMethod.getParent().getElementName());
            sb.append(".");
            sb.append(sourceMethod.getElementName());
            sb.append("(");
            for (String type : sourceMethod.getParameterTypes()) {
                sb.append(org.eclipse.jdt.core.Signature.toString(type));
            }
            sb.append(")");

            return sb.toString();
        }
    }

    /**
     * The method <code>javadoc2HTML</code> changed 2014-12 to accept an
     * additional IJavaElement parameter. To support older versions, try to
     * find that one too.
     */
    private static class Reflection {
        private static final Method javadoc2HTML;
        private static final Method oldJavadoc2HTML;
        private static final Method lsJavadoc2HTML;

        static {
            Method a = null, b = null, c = null;

            try {
                a = Permit.getMethod(JavadocContentAccess2.class, "javadoc2HTML", IMember.class, IJavaElement.class, String.class);
            } catch (Throwable t) {
            }
            try {
                b = Permit.getMethod(JavadocContentAccess2.class, "javadoc2HTML", IMember.class, String.class);
            } catch (Throwable t) {
            }
            try {
                c = Permit.getMethod(Class.forName("org.eclipse.jdt.ls.core.internal.javadoc.JavadocContentAccess2"), "javadoc2HTML", IMember.class, IJavaElement.class, String.class);
            } catch (Throwable t) {
            }

            javadoc2HTML = a;
            oldJavadoc2HTML = b;
            lsJavadoc2HTML = c;
        }

        private static String javadoc2HTML(IMember member, IJavaElement element, String rawJavadoc) {
            if (javadoc2HTML != null) {
                try {
                    return (String) javadoc2HTML.invoke(null, member, element, rawJavadoc);
                } catch (Throwable t) {
                    return null;
                }
            }
            if (lsJavadoc2HTML != null) {
                try {
                    return (String) lsJavadoc2HTML.invoke(null, member, element, rawJavadoc);
                } catch (Throwable t) {
                    return null;
                }
            }
            if (oldJavadoc2HTML != null) {
                try {
                    return (String) oldJavadoc2HTML.invoke(null, member, rawJavadoc);
                } catch (Throwable t) {
                    return null;
                }
            }
            return null;
        }
    }
}
