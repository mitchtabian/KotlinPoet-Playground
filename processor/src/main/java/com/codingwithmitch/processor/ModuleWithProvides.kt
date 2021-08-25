package com.codingwithmitch.processor

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

data class ModuleWithProvides(
    val module: Element,
    val providesFunctions: List<ExecutableElement> = listOf()
)

