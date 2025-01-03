package com.oscargs.savingsapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.oscargs.savingsapp.ui.theme.SavingsAppTheme


@Composable
fun FloatingButton(){
    FloatingActionButton(
        onClick = {  }
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add movement button")
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SavingsAppTheme {
        FloatingButton()
    }
}