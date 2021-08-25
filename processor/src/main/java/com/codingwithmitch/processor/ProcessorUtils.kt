package com.codingwithmitch.processor

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement

fun Collection<Element>.filterMethods(): Sequence<ExecutableElement> {
    return asSequence()
        .filter { it.kind == ElementKind.METHOD }
        .map { it as ExecutableElement }
}
