package com.codingwithmitch.processor

import javax.lang.model.element.ExecutableElement

data class ProvidesElementWithArgs(
    val element: ExecutableElement,
    val factoryClassName: String,
    val argumentName: String,
)
