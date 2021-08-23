package com.codingwithmitch.processor

import com.codingwithmitch.annotation.Provide
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(MyProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class MyProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(Provide::class.java).forEach { element ->
            if (element.kind != ElementKind.METHOD) {
                processingEnv.messager.errormessage { "Can only be applied to functions,  element: $element " }
                return false
            }

            generateObject(
                functionElement = element,
                packageOfMethod = processingEnv.elementUtils.getPackageOf(element).toString(),
            )
        }

        return false
    }

    private fun generateObject(functionElement: Element, packageOfMethod: String) {
        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        if(generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.errormessage { "Can't find the target directory for generated Kotlin files." }
            return
        }

        // TODO("How can I get the person params?...")

        val fileSpec = FileSpec.builder(packageOfMethod, "Dependencies")
        val personClass = ClassName("com.codingwithmitch.kotlinpoetplayground", "Person")
        fileSpec.addType(
            TypeSpec.classBuilder("Dependencies")
                .addFunction(
                    FunSpec.builder("providePerson")
                        .addStatement("return %T(%S,%L)", personClass, "Mitch", 200.00)
                        .build()
                )
                .build()
        )

        val file = File(generatedSourcesRoot)
        file.mkdir()
        fileSpec.build().writeTo(file)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Provide::class.java.canonicalName)
    }
}














