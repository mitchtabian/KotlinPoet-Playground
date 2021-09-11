package com.codingwithmitch.processor

import com.codingwithmitch.annotation.Module
import com.codingwithmitch.annotation.Provides
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(MyProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class MyProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        // Build Factory<T> interface
       generateFactory()

        // Build Factories for producing @Provides objects
        generateProvidesFactories(filterModules(roundEnv))

        return false
    }

    private fun generateProvidesFactories(modulesWithProvides: List<ModuleWithProvides>){
        // Loop through modules
        modulesWithProvides.forEach { moduleWithProvides ->
            // list of factory class names and the name of argument they provide
            val factories: MutableList<ProvidesElementWithArgs> = mutableListOf()
            // Build Factories
            moduleWithProvides.providesFunctions.forEach { providesFn ->
                val providesType = providesFn.returnType
                val argumentName = providesFn.getAnnotation(Provides::class.java).argumentName
                checkDuplicateArgumentNames(factories.map { it.argumentName })
                val className = "${moduleWithProvides.module.enclosingElement.simpleName}_Provide${argumentName}Factory"
                val providesElementWithArgs = ProvidesElementWithArgs(
                    element = providesFn,
                    factoryClassName = className,
                    argumentName = argumentName,
                )
                factories.add(providesElementWithArgs)
                val modulePackage = processingEnv.elementUtils.getPackageOf(moduleWithProvides.module).toString()
                val fileSpec = FileSpec.builder(
                    modulePackage,
                    className
                )

                val factoryClass = ClassName(
                    packageName = modulePackage,
                    className
                )

                // Companion object
                val companion = TypeSpec.companionObjectBuilder()
                    .addProperty(
                        PropertySpec.builder("INSTANCE", factoryClass)
                            .initializer(
                                CodeBlock.builder()
                                    .add("$className()")
                                    .build()
                            )
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder(providesFn.simpleName.toString())
                            .returns(providesFn.returnType.asTypeName())
                            .addStatement("return %T.${providesFn.simpleName}()", moduleWithProvides.module)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("create")
                            .returns(factoryClass)
                            .addStatement("return INSTANCE")
                            .build()
                    )
                    .build()

                val classBuilder = TypeSpec.classBuilder(factoryClass)
                    .addSuperinterface(
                        ClassName(
                            "utility",
                            "Factory"
                        ).parameterizedBy(providesType.asTypeName())
                    )
                    .addFunction(
                        FunSpec.builder("get")
                            .addModifiers(KModifier.OVERRIDE)
                            .addStatement("return ${providesFn.simpleName}()")
                            .returns(providesType.asTypeName())
                            .build()
                    )
                    .addType(companion)
                    .build()

                fileSpec
                    .addType(classBuilder)

                val file = getRootFile()
                fileSpec.build().writeTo(file)
            }
            // Add to Dependencies
            generateDependencyHolder(moduleWithProvides.module, factories)
        }
    }

    /**
     * @param moduleElement: The object annotated with @Module
     * @param factories: List of the factory class names that were generated
     */
    private fun generateDependencyHolder(moduleElement: Element, factories: List<ProvidesElementWithArgs>){
        val className = "${moduleElement.enclosingElement.simpleName}Dependencies"
        val modulePackage = processingEnv.elementUtils.getPackageOf(moduleElement).toString()
        val fileSpec = FileSpec.builder(modulePackage, className)
        val classBuilder = TypeSpec.classBuilder("${moduleElement.enclosingElement.simpleName}Dependencies")
        factories.forEach { providesElemWithArgs ->
            val factoryClass = ClassName(
                packageName = modulePackage,
                providesElemWithArgs.factoryClassName
            )
            classBuilder.addFunction(
                FunSpec.builder(providesElemWithArgs.argumentName)
                    .addStatement("return ${factoryClass.simpleName}.create().get()")
                    .build()
            )
        }
        fileSpec.addType(classBuilder.build())
        val file = getRootFile()
        fileSpec.build().writeTo(file)
    }

    private fun filterModules(roundEnv: RoundEnvironment): List<ModuleWithProvides>{
        val modulesWithProvides: MutableList<ModuleWithProvides> = mutableListOf()
        roundEnv.getElementsAnnotatedWith(Module::class.java).forEach { moduleElement ->
            var moduleInstance: Element? = null
            val providesFunctions: MutableList<ExecutableElement> = mutableListOf()
            moduleElement.enclosedElements.forEachIndexed { index, element ->
                if(index == 0){
                    processingEnv.messager.noteMessage { "${element.simpleName}" }
                    moduleInstance = element
                }
                else if(index > 1){
                    processingEnv.messager.warningMessage { "${element.simpleName}" }
                    if(element is ExecutableElement){
                        providesFunctions.add(element)
                    }
                }
            }
            moduleInstance?.let { instance ->
                modulesWithProvides.add(
                    ModuleWithProvides(
                        module = instance,
                        providesFunctions = providesFunctions,
                    )
                )
            }
        }
        return modulesWithProvides
    }

    private fun getRootFile(): File {
        val file = File(getGeneratedSourcesRoot())
        file.mkdir()
        return file
    }

    private fun generateFactory() {
        // Build factory interface
        val t = TypeVariableName("T")
        val factoryInterfaceName = "Factory"
        val factoryInterface = TypeSpec.interfaceBuilder(factoryInterfaceName)
            .addTypeVariable(t)
            .addFunction(
                FunSpec.builder("get")
                    .returns(t)
                    .addModifiers(KModifier.ABSTRACT)
                    .build()
            )
            .build()

        val interfaceFileSpec = FileSpec.builder(
            "utility",
            factoryInterfaceName
        )
        interfaceFileSpec.addType(factoryInterface)
        val file = getRootFile()
        interfaceFileSpec.build().writeTo(file)
    }

    private fun checkDuplicateArgumentNames(argumentNames: List<String>) {
        if(argumentNames.groupingBy { it }.eachCount().any { it.value > 1 }){
            processingEnv.messager.errormessage { "Cannot have duplicate argument names." }
        }
    }

    private fun getGeneratedSourcesRoot(): String{
        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        if(generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.errormessage { "Can't find the target directory for generated Kotlin files." }
        }
        return generatedSourcesRoot
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Provides::class.java.canonicalName)
    }
}














