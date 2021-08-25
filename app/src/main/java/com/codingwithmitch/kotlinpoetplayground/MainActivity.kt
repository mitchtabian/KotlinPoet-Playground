package com.codingwithmitch.kotlinpoetplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.codingwithmitch.kotlinpoetplayground.ui.theme.KotlinPoetPlaygroundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dependencies = AppModuleDependencies()
        setContent {
            KotlinPoetPlaygroundTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Greeting(dependencies.mitch().name)
                    Greeting(dependencies.blake().name)
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