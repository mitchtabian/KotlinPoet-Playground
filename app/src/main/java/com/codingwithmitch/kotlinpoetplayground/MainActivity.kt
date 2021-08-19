package com.codingwithmitch.kotlinpoetplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.codingwithmitch.annotation.AutoMap
import com.codingwithmitch.kotlinpoetplayground.ui.theme.KotlinPoetPlaygroundTheme

data class Person(
    val name: String,
    val weight: Double,
)

@AutoMap
val person = Person(
    name = "Mitch",
    weight = 200.00
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("gdfnkgnfd: ${person}")
        setContent {
            KotlinPoetPlaygroundTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
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