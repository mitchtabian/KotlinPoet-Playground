package com.codingwithmitch.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Provides(
    val argumentName: String // Name of argument as appears in Dependencies
)
