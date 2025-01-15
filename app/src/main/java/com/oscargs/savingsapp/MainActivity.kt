package com.oscargs.savingsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.oscargs.savingsapp.ui.theme.SavingsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SavingsAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //MainScreen(modifier = Modifier.padding(innerPadding))
                    Column (modifier = Modifier.padding(innerPadding)) {
                        Button(
                            onClick = {
                                // Handle button click
                                throw RuntimeException("This is a crash")
                        }) {
                            Text(text = "Click me")
                        }

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SavingsAppTheme {
        MainScreen(modifier = Modifier.fillMaxSize())
    }
}