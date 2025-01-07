package com.oscargs.savingsapp

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.oscargs.savingsapp.models.Movement
import com.oscargs.savingsapp.ui.theme.SavingsAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // Bottom sheet
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    // DB
    val movements: LiveData<List<Movement>> = loadMovements()
    val movementList by movements.observeAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
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
        },
    ) { innerPadding ->
        // Movement list
        MovementList(modifier = Modifier.padding(innerPadding), movements = movementList)

        // Bottom sheet
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

@Composable
fun MovementList(modifier: Modifier, movements: List<Movement>) {
    LazyColumn(modifier = modifier) {
        items(movements) { movement ->
            //TODO: Make a better item display
            Text(text = movement.toString(), modifier = Modifier.padding(8.dp))
        }
    }
}

fun loadMovements(): LiveData<List<Movement>> = liveData(Dispatchers.IO) {
    val db = MainApplication.database
    val movements = db.movementDAO().getAllMovements()
    emitSource(movements)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SavingsAppTheme {
        MainScreen()
    }
}