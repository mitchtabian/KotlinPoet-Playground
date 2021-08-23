package com.codingwithmitch.processor

import com.codingwithmitch.annotation.Description
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(AutoMapProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class AutoMapProcessor: AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        roundEnv?.getElementsAnnotatedWith(Description::class.java)?.forEach { classElement ->
            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "YOOOO STARTING....")
            if (classElement.kind != ElementKind.CLASS) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can only be applied to class's,  element: $classElement ")
                return false
            }
            val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
            if(generatedSourcesRoot.isEmpty()) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
                return false
            }
            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "PROCEEDING...!!")

            var count = 0
            // loop through the fields, methods, constructors, and member types
            classElement.enclosedElements.forEach { element ->

                // Found a field
                if(element.kind == ElementKind.FIELD){
                    count++
                }
            }

            val function = FunSpec.builder("description")
                .receiver(classElement::class)
                .addStatement("""
                    return 'this class has count fields'
                """)
                .build()
//            val file = File(generatedSourcesRoot).apply { mkdir() }
            FileSpec.builder(processingEnv.elementUtils.getPackageOf(classElement).toString(), "DescriptionGenerated")
                .addFunction(function)
                .build()
                .writeTo(processingEnv.filer)

//            val stringType = TypeVariableName("String")
//            val anyType = TypeVariableName("Any")
//            val mapType = Map::class.asClassName()
//                .parameterizedBy(stringType, anyType)
//            classElement.enclosedElements.forEach { element ->
//                val function = FunSpec.builder("toMap")
//                    .receiver(element::class)
//                    .returns(mapType)
//                    .addStatement("return mapOf(" +
//                            "name to name," +
//                            "weight to weight," +
//                            ")")
//                    .build()
//
//                val file = File(generatedSourcesRoot).apply { mkdir() }
//                FileSpec.builder(processingEnv.elementUtils.getPackageOf(classElement).toString(), "ToMapGenerated")
//                    .addFunction(function)
//                    .build()
//                    .writeTo(file)
//            }
        }
        return false
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

}














