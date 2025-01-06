package com.oscargs.savingsapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.oscargs.savingsapp.ui.theme.SavingsAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        showBottomSheet = true
                    }
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add movement button")
            }
        }
    ) { innerPadding ->
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                modifier = Modifier.padding(innerPadding)
            ) {
                AddMovementScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SavingsAppTheme {
        MainScreen()
    }
}