package com.codingwithmitch.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import jdk.internal.reflect.Reflection.filterFields
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.*
import javax.lang.model.element.ElementKind.CLASS
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind.DECLARED
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic.Kind.ERROR
import javax.lang.model.type.TypeKind.VOID

fun ProcessingEnvironment.error(message: String) {
    messager.printMessage(ERROR, message)
}

fun Element.getPackage(env: ProcessingEnvironment): String {
    return env.elementUtils.getPackageOf(this)
        .toString()
}

fun Element.getFullClassName(
    env: ProcessingEnvironment,
    pkg: String
): ClassName {
    val simpleClassName = this.simpleName.toString()
    val superClass = getSuperClass(env)
    val fullClassName = if (superClass != null) {
        "${superClass.asTypeName().toString().lastComponent()}.$simpleClassName"
    } else {
        simpleClassName
    }
    return ClassName(pkg, fullClassName)
}

private fun Element.getSuperClass(env: ProcessingEnvironment): TypeMirror? {
    return env.typeUtils.directSupertypes(this.asType())
        .asSequence()
        .filter { it.kind == DECLARED }
        .filterNot { it.isObject() }
        .filter { it.asTypeElement().kind == CLASS }
        .singleOrNull()
}

private fun String.lastComponent(): String = substring(lastIndexOf('.') + 1)

private fun TypeMirror?.isObject(): Boolean {
    if (this == null) {
        return false
    }
    return toString() == "java.lang.Object"
}

fun TypeMirror.asTypeElement(): TypeElement {
    return (this as DeclaredType).asElement() as TypeElement
}

inline fun <reified T : Any> Element.getAnnotationMirror(): AnnotationMirror? {
    return annotationMirrors.firstOrNull { ann ->
        ann.annotationType.toString() == T::class.java.name
    }
}

fun ClassName.asFileName(suffix: String): String {
    return "${simpleName.replace(".", "_")}$suffix"
}

@Suppress("UNCHECKED_CAST")
fun <T> AnnotationMirror.getParameter(name: String): T? {
    return elementValues.entries
        .singleOrNull { it.key.simpleName.toString() == name }
        ?.value?.value as? T
}

fun Collection<Element>.filterMethods(): Sequence<ExecutableElement> {
    return asSequence()
        .filter { it.kind == ElementKind.METHOD }
        .map { it as ExecutableElement }
}

fun TypeMirror?.isVoid(): Boolean {
    if (this == null) {
        return false
    }
    return kind == VOID
}

fun Collection<Element>.filterClassesAndInterfaces(): Sequence<Element> {
    return asSequence().filter { it.kind == CLASS || it.kind == ElementKind.INTERFACE }
}

//fun Collection<Element>.injectingFields(
//    env: ProcessingEnvironment
//): Sequence<InjectingField> {
//    return filterFields()
//        .filter { it.hasAnnotationMirror<Inject>() }
//        .map { it.asInjectField(env) }
//}












