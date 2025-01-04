package com.oscargs.savingsapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oscargs.savingsapp.ui.theme.SavingsAppTheme
import com.oscargs.savingsapp.utilities.MovementType
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
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

    // Date
    val pickedDate by remember { mutableStateOf(LocalDate.now()) }
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter.ofPattern("dd-MM-yyyy").format(pickedDate)
        }
    }

    // Type
    var isExpanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(MovementType.Income) }

    // Error variables

    Column {
        // Description text field
        TextField(
            modifier = Modifier.padding(8.dp),
            value = text,
            onValueChange = { text = it },
            label = { stringResource(R.string.labelDescriptionTF) },
            placeholder = { stringResource(R.string.hintDescriptionTF) },
            singleLine = true,
            isError = false
        )

        // Amount text field
        TextField(
            modifier = Modifier.padding(8.dp),
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

        // Date field
        DateCalendarPicker(formattedDate, pickedDate)

        //Type Selector
        TypeSelector(isExpanded, selectedType)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TypeSelector(
    isExpanded: Boolean,
    selectedType: MovementType
) {
    var expanded = isExpanded
    var type = selectedType
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.padding(8.dp)
    ) {
        TextField(
            value = type.toString(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { R.string.labelIncome.toString() },
                onClick = {
                    type = MovementType.Income
                    expanded = false
                })
            DropdownMenuItem(
                text = { R.string.labelExpense.toString() },
                onClick = {
                    type = MovementType.Expense
                    expanded = false
                })
        }
    }
}

@Composable
private fun DateCalendarPicker(formattedDate: String, pickedDate: LocalDate?) {
    var pickedDate1 = pickedDate
    val dateDialogState = rememberMaterialDialogState()

    Button(onClick = {
        dateDialogState.show()
    }, modifier = Modifier.padding(8.dp)) {
        Text(text = stringResource(R.string.labelDateTF))
    }
    Text(modifier = Modifier.padding(8.dp), text = formattedDate)

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(R.string.labelAccept.toString())
            negativeButton(R.string.labelCancel.toString())
        }
    ) {
        datepicker(
            initialDate = pickedDate1 ?: LocalDate.now(),
            title = R.string.labelSelectDate.toString(),
        ) {
            pickedDate1 = it
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddMovementScreenPreview() {
    SavingsAppTheme {
        AddMovementScreen()
    }
}