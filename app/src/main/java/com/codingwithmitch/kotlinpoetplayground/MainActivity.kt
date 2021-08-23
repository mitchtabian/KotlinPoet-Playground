package com.codingwithmitch.kotlinpoetplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.codingwithmitch.annotation.Provide
import com.codingwithmitch.kotlinpoetplayground.ui.theme.KotlinPoetPlaygroundTheme

@Provide
fun providePerson(): Person {
    return Person(
        name = "Mitch",
        weight = 200.00
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        println("gdfnkgnfd: ${person}")
        val dependencies = Dependencies()
        setContent {
            KotlinPoetPlaygroundTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Greeting(dependencies.providePerson().name)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KotlinPoetPlaygroundTheme {
        Greeting("Android")
    }
}