package com.codingwithmitch.kotlinpoetplayground

import com.codingwithmitch.annotation.Module
import com.codingwithmitch.annotation.Provides

/**
 * enclosedElements yields:
 * 1. INSTANCE
 * 2. init fn
 * 3. everything else (ex: providePerson fn)
 */
@Module
object AppModule {

    @Provides(argumentName = "person")
    fun providePerson(): Person {
        return Person(
            name = "Mitch",
            weight = 200.00
        )
    }
}
















