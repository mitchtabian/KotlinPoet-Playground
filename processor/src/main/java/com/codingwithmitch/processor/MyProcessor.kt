package com.codingwithmitch.processor

import com.codingwithmitch.annotation.Greeting
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class MyProcessor : AbstractProcessor() {

    private val greetingBuilder: GreetingBuilder by lazy {
        GreetingBuilder(
            environment = processingEnv,
        )
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        // Retrieve all greetings, this sequence is re-used twice
        val allGreetings = roundEnv
            .getElementsAnnotatedWith(Greeting::class.java)
            .filterClassesAndInterfaces()

        allGreetings.generateGreetings()

        return true
    }

    private fun Sequence<Element>.generateGreetings() = forEach {
        greetingBuilder.generate(it)
    }
}















