package com.codingwithmitch.processor

import com.codingwithmitch.annotation.Provide
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(AutoMapProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class AutoMapProcessor : AbstractProcessor() {

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

    private fun generateObject(functionElement: Element,  packageOfMethod: String) {
        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        if(generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.errormessage { "Can't find the target directory for generated Kotlin files." }
            return
        }

        val map: MutableMap<String, Any> = mutableMapOf() // <Field name, Field value>
        (functionElement as ExecutableElement).enclosedElements.forEach { element ->
            val typeArgs = if(element.asType() is DeclaredType){
                (element.asType() as DeclaredType).typeArguments
            }
            else{
                emptyList<TypeMirror>()
            }
            map[element.asType().asTypeName().toString()] = typeArgs.single()
        }
        val file = File(generatedSourcesRoot)
        file.mkdir()
        val fileSpec = FileSpec.builder(packageOfMethod, "PersonGenerated")
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
        fileSpec.build().writeTo(file)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Provide::class.java.canonicalName)
    }
}














