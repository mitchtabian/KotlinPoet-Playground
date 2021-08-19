package com.codingwithmitch.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Greeting(
    val text: String = "",
)