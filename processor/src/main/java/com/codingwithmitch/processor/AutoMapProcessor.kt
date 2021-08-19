package com.codingwithmitch.processor

import com.codingwithmitch.annotation.AutoMap
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement


class AutoMapProcessor: AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        if(annotations?.isEmpty() == true){
            return false
        }
        val elements = roundEnv?.getElementsAnnotatedWith(AutoMap::class.java)
        if(elements?.isNotEmpty() == true){
            for(element in elements){
                if(element !is TypeElement){
                    // log error?
                    println("Element is not a TypeElement")
                    continue
                }
                val variables = element.enclosedElements
                    .filterIsInstance<VariableElement>()
                    .map { variable ->
                        val type: ElementKind = variable.kind
                        val hasAnnotation = variable.getAnnotation(AutoMap::class.java) != null
                        variable.simpleName.toString() to type.takeIf { hasAnnotation }
                    }
                generateCode(element, variables)
            }
        }

        return true
    }

}