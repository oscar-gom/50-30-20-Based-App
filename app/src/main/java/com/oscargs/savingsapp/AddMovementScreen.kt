package com.oscargs.savingsapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.oscargs.savingsapp.ui.theme.SavingsAppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun AddMovementScreen() {
    SavingsAppTheme {
        MovementForm()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementForm() {
    // Text variables
    var text by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Date formatter
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    // Error variables

    Column {
        // Description text field
        TextField(
            modifier = Modifier.padding(16.dp),
            value = text,
            onValueChange = { text = it },
            label = { stringResource(R.string.labelDescriptionTF) },
            placeholder = { stringResource(R.string.hintDescriptionTF) },
            singleLine = true,
            isError = false
        )

        // Amount text field
        TextField(
            modifier = Modifier.padding(16.dp),
            value = amount,
            onValueChange = { newValue ->
                if (newValue.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                    amount = newValue
                }
            },
            label = { stringResource(R.string.labelAmountTF) },
            placeholder = { stringResource(R.string.hintAmountTF) },
            singleLine = true,
            isError = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        // Date picker


    }
}




@Preview(showBackground = true)
@Composable
fun AddMomventScreenPreview() {
    SavingsAppTheme {
        AddMovementScreen()
    }
}