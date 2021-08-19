package com.codingwithmitch.processor

import com.codingwithmitch.annotation.AutoMap
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

//data class Person(
//    val name: String,
//    val weight: Double,
//)
//
//fun Person.toMap(): Map<String, Any> {
//    return mapOf(
//        "name" to name,
//        "weight" to weight
//    )
//}

@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(AutoMapProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class AutoMapProcessor: AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        roundEnv?.getElementsAnnotatedWith(AutoMap::class.java)?.forEach { classElement ->
            if (classElement.kind != ElementKind.CLASS) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can only be applied to class's,  element: $classElement ")
                return false
            }
            val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
            if(generatedSourcesRoot.isEmpty()) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
                return false
            }

            val stringType = TypeVariableName("String")
            val anyType = TypeVariableName("Any")
            val mapType = Map::class.asClassName()
                .parameterizedBy(stringType, anyType)
            classElement.enclosedElements.forEach{ element ->
                val function = FunSpec.builder("toMap")
                    .receiver(element::class)
                    .returns(mapType)
                    .addStatement("return mapOf(" +
                            "name to name," +
                            "weight to weight," +
                            ")")
                    .build()

                val file = File(generatedSourcesRoot).apply { mkdir() }
                FileSpec.builder("com.codingwithmitch.kotlinpoet.generated", "ToMapGenerated")
                    .addFunction(function)
                    .build()
                    .writeTo(file)
            }
        }
        return true
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

}














