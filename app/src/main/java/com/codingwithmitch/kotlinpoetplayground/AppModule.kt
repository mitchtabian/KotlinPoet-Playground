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

    @Provides(argumentName = "mitch")
    fun providePerson(): Person {
        return Person(
            name = "Mitch",
            weight = 200.00
        )
    }

    @Provides(argumentName = "blake")
    fun provideAnotherPerson(): Person {
        return Person(
            name = "Blake",
            weight = 190.00
        )
    }
}
















