/*
 * Copyright (C) 2012-2022 The Project Lombok Authors.
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
package lombok.javac.handlers;

import static lombok.core.handlers.HandlerUtil.*;
import static lombok.javac.Javac.*;
import static lombok.javac.handlers.JavacHandlerUtil.*;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.With;
import lombok.core.AST.Kind;
import lombok.core.AnnotationValues;
import lombok.core.configuration.CheckerFrameworkVersion;
import lombok.experimental.Accessors;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil.CopyJavadoc;
import lombok.spi.Provides;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCConditional;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.JCDiagnostic.DiagnosticPosition;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;

/**
 * Handles the {@code lombok.With} annotation for javac.
 */
@Provides
public class HandleWith extends JavacAnnotationHandler<With> {
    public void generateWithForType(JavacNode typeNode, JavacNode errorNode, AccessLevel level, boolean checkForTypeLevelWith) {
        if (checkForTypeLevelWith) {
            if (hasAnnotation(With.class, typeNode)) {
                //The annotation will make it happen, so we can skip it.
                return;
            }
        }

        JCClassDecl typeDecl = null;
        if (typeNode.get() instanceof JCClassDecl) typeDecl = (JCClassDecl) typeNode.get();
        long modifiers = typeDecl == null ? 0 : typeDecl.mods.flags;
        boolean notAClass = (modifiers & (Flags.INTERFACE | Flags.ANNOTATION | Flags.ENUM)) != 0;

        if (typeDecl == null || notAClass) {
            errorNode.addError("@With is only supported on a class or a field.");
            return;
        }

        for (JavacNode field : typeNode.down()) {
            if (field.getKind() != Kind.FIELD) continue;
            JCVariableDecl fieldDecl = (JCVariableDecl) field.get();
            //Skip fields that start with $
            if (fieldDecl.name.toString().startsWith("$")) continue;
            //Skip static fields.
            if ((fieldDecl.mods.flags & Flags.STATIC) != 0) continue;
            //Skip final initialized fields.
            if ((fieldDecl.mods.flags & Flags.FINAL) != 0 && fieldDecl.init != null) continue;

            generateWithForField(field, errorNode.get(), level);
        }
    }

    /**
     * Generates a with on the stated field.
     * <p>
     * Used by {@link HandleValue}.
     * <p>
     * The difference between this call and the handle method is as follows:
     * <p>
     * If there is a {@code lombok.With} annotation on the field, it is used and the
     * same rules apply (e.g. warning if the method already exists, stated access level applies).
     * If not, the with is still generated if it isn't already there, though there will not
     * be a warning if its already there. The default access level is used.
     *
     * @param fieldNode The node representing the field you want a with for.
     * @param pos       The node responsible for generating the with (the {@code @Value} or {@code @With} annotation).
     */
    public void generateWithForField(JavacNode fieldNode, DiagnosticPosition pos, AccessLevel level) {
        if (hasAnnotation(With.class, fieldNode)) {
            //The annotation will make it happen, so we can skip it.
            return;
        }

        createWithForField(level, fieldNode, fieldNode, false, List.<JCAnnotation>nil(), List.<JCAnnotation>nil());
    }

    @Override
    public void handle(AnnotationValues<With> annotation, JCAnnotation ast, JavacNode annotationNode) {
        handleFlagUsage(annotationNode, ConfigurationKeys.WITH_FLAG_USAGE, "@With");

        Collection<JavacNode> fields = annotationNode.upFromAnnotationToFields();
        deleteAnnotationIfNeccessary(annotationNode, With.class, "lombok.experimental.Wither");
        deleteImportFromCompilationUnit(annotationNode, "lombok.AccessLevel");
        JavacNode node = annotationNode.up();
        AccessLevel level = annotation.getInstance().value();

        if (level == AccessLevel.NONE || node == null) return;

        List<JCAnnotation> onMethod = unboxAndRemoveAnnotationParameter(ast, "onMethod", "@With(onMethod", annotationNode);
        List<JCAnnotation> onParam = unboxAndRemoveAnnotationParameter(ast, "onParam", "@With(onParam", annotationNode);

        switch (node.getKind()) {
            case FIELD:
                createWithForFields(level, fields, annotationNode, true, onMethod, onParam);
                break;
            case TYPE:
                if (!onMethod.isEmpty()) annotationNode.addError("'onMethod' is not supported for @With on a type.");
                if (!onParam.isEmpty()) annotationNode.addError("'onParam' is not supported for @With on a type.");
                generateWithForType(node, annotationNode, level, false);
                break;
        }
    }

    public void createWithForFields(AccessLevel level, Collection<JavacNode> fieldNodes, JavacNode errorNode, boolean whineIfExists, List<JCAnnotation> onMethod, List<JCAnnotation> onParam) {
        for (JavacNode fieldNode : fieldNodes) {
            createWithForField(level, fieldNode, errorNode, whineIfExists, onMethod, onParam);
        }
    }

    public void createWithForField(AccessLevel level, JavacNode fieldNode, JavacNode source, boolean strictMode, List<JCAnnotation> onMethod, List<JCAnnotation> onParam) {
        JavacNode typeNode = fieldNode.up();
        boolean makeAbstract = typeNode != null && typeNode.getKind() == Kind.TYPE && (((JCClassDecl) typeNode.get()).mods.flags & Flags.ABSTRACT) != 0;

        if (fieldNode.getKind() != Kind.FIELD) {
            fieldNode.addError("@With is only supported on a class or a field.");
            return;
        }

        AnnotationValues<Accessors> accessors = getAccessorsForField(fieldNode);
        JCVariableDecl fieldDecl = (JCVariableDecl) fieldNode.get();
        String methodName = toWithName(fieldNode, accessors);

        if (methodName == null) {
            fieldNode.addWarning("Not generating a withX method for this field: It does not fit your @Accessors prefix list.");
            return;
        }

        if ((fieldDecl.mods.flags & Flags.STATIC) != 0) {
            if (strictMode) {
                fieldNode.addWarning("Not generating " + methodName + " for this field: With methods cannot be generated for static fields.");
            }
            return;
        }

        if ((fieldDecl.mods.flags & Flags.FINAL) != 0 && fieldDecl.init != null) {
            if (strictMode) {
                fieldNode.addWarning("Not generating " + methodName + " for this field: With methods cannot be generated for final, initialized fields.");
            }
            return;
        }

        if (fieldDecl.name.toString().startsWith("$")) {
            if (strictMode) {
                fieldNode.addWarning("Not generating " + methodName + " for this field: With methods cannot be generated for fields starting with $.");
            }
            return;
        }

        for (String altName : toAllWithNames(fieldNode, accessors)) {
            switch (methodExists(altName, fieldNode, false, 1)) {
                case EXISTS_BY_LOMBOK:
                    return;
                case EXISTS_BY_USER:
                    if (strictMode) {
                        String altNameExpl = "";
                        if (!altName.equals(methodName)) altNameExpl = String.format(" (%s)", altName);
                        fieldNode.addWarning(
                                String.format("Not generating %s(): A method with that name already exists%s", methodName, altNameExpl));
                    }
                    return;
                default:
                case NOT_EXISTS:
                    //continue scanning the other alt names.
            }
        }

        long access = toJavacModifier(level);

        JCMethodDecl createdWith = createWith(access, fieldNode, fieldNode.getTreeMaker(), source, onMethod, onParam, makeAbstract);
        createRelevantNonNullAnnotation(fieldNode, createdWith);
        recursiveSetGeneratedBy(createdWith, source);
        injectMethod(typeNode, createdWith);
    }

    public JCMethodDecl createWith(long access, JavacNode field, JavacTreeMaker maker, JavacNode source, List<JCAnnotation> onMethod, List<JCAnnotation> onParam, boolean makeAbstract) {
        String withName = toWithName(field);
        if (withName == null) return null;

        JCVariableDecl fieldDecl = (JCVariableDecl) field.get();

        List<JCAnnotation> copyableAnnotations = findCopyableAnnotations(field);

        Name methodName = field.toName(withName);

        JCExpression returnType = cloneSelfType(field);

        JCBlock methodBody = null;
        long flags = JavacHandlerUtil.addFinalIfNeeded(Flags.PARAMETER, field.getContext());
        List<JCAnnotation> annsOnParam = copyAnnotations(onParam).appendList(copyableAnnotations);

        JCExpression pType = cloneType(maker, fieldDecl.vartype, source);
        JCVariableDecl param = maker.VarDef(maker.Modifiers(flags, annsOnParam), fieldDecl.name, pType, null);

        if (!makeAbstract) {
            ListBuffer<JCStatement> statements = new ListBuffer<JCStatement>();

            JCExpression selfType = cloneSelfType(field);
            if (selfType == null) return null;

            ListBuffer<JCExpression> args = new ListBuffer<JCExpression>();
            for (JavacNode child : field.up().down()) {
                if (child.getKind() != Kind.FIELD) continue;
                JCVariableDecl childDecl = (JCVariableDecl) child.get();
                // Skip fields that start with $
                if (childDecl.name.toString().startsWith("$")) continue;
                long fieldFlags = childDecl.mods.flags;
                // Skip static fields.
                if ((fieldFlags & Flags.STATIC) != 0) continue;
                // Skip initialized final fields.
                if (((fieldFlags & Flags.FINAL) != 0) && childDecl.init != null) continue;
                if (child.get() == field.get()) {
                    args.append(maker.Ident(fieldDecl.name));
                } else {
                    args.append(createFieldAccessor(maker, child, FieldAccess.ALWAYS_FIELD));
                }
            }

            JCNewClass newClass = maker.NewClass(null, List.<JCExpression>nil(), selfType, args.toList(), null);
            JCExpression identityCheck = maker.Binary(CTC_EQUAL, createFieldAccessor(maker, field, FieldAccess.ALWAYS_FIELD), maker.Ident(fieldDecl.name));
            JCConditional conditional = maker.Conditional(identityCheck, maker.Ident(field.toName("this")), newClass);
            JCReturn returnStatement = maker.Return(conditional);

            if (!hasNonNullAnnotations(field)) {
                statements.append(returnStatement);
            } else {
                JCStatement nullCheck = generateNullCheck(maker, field, source);
                if (nullCheck != null) statements.append(nullCheck);
                statements.append(returnStatement);
            }

            methodBody = maker.Block(0, statements.toList());
        }
        List<JCTypeParameter> methodGenericParams = List.nil();
        List<JCVariableDecl> parameters = List.of(param);
        List<JCExpression> throwsClauses = List.nil();
        JCExpression annotationMethodDefaultValue = null;

        List<JCAnnotation> annsOnMethod = copyAnnotations(onMethod);
        CheckerFrameworkVersion checkerFramework = getCheckerFrameworkVersion(source);
        if (checkerFramework.generateSideEffectFree())
            annsOnMethod = annsOnMethod.prepend(maker.Annotation(genTypeRef(source, CheckerFrameworkVersion.NAME__SIDE_EFFECT_FREE), List.<JCExpression>nil()));

        if (isFieldDeprecated(field))
            annsOnMethod = annsOnMethod.prepend(maker.Annotation(genJavaLangTypeRef(field, "Deprecated"), List.<JCExpression>nil()));

        if (makeAbstract) access |= Flags.ABSTRACT;
        AnnotationValues<Accessors> accessors = JavacHandlerUtil.getAccessorsForField(field);
        boolean makeFinal = shouldMakeFinal(field, accessors);
        if (makeFinal) access |= Flags.FINAL;
        JCMethodDecl decl = recursiveSetGeneratedBy(maker.MethodDef(maker.Modifiers(access, annsOnMethod), methodName, returnType,
                methodGenericParams, parameters, throwsClauses, methodBody, annotationMethodDefaultValue), source);
        copyJavadoc(field, decl, CopyJavadoc.WITH);
        return decl;
    }
}
