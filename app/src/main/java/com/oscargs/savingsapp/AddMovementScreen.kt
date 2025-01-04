package com.oscargs.savingsapp

import android.util.Log
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
import com.oscargs.savingsapp.utilities.Category
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
    var expandedType by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(MovementType.NONE) }

    // Category
    var expandedCategory by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(Category.NONE) }

    Column {
        // Description text field
        TextField(
            modifier = Modifier.padding(8.dp),
            value = text,
            onValueChange = { text = it },
            label = { Text(stringResource(R.string.labelDescriptionTF)) },
            placeholder = { Text(stringResource(R.string.hintDescriptionTF)) },
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
            label = { Text(stringResource(R.string.labelAmountTF)) },
            placeholder = { Text(stringResource(R.string.hintAmountTF)) },
            singleLine = true,
            isError = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        // Date field
        DateCalendarPicker(formattedDate, pickedDate)

        // Type Selector
        ExposedDropdownMenuBox(
            expanded = expandedType,
            onExpandedChange = { expandedType = it },
            modifier = Modifier.padding(8.dp)
        ) {
            TextField(
                value = stringResource(id = when (selectedType) {
                    MovementType.INCOME -> R.string.labelIncome
                    MovementType.EXPENSE -> R.string.labelExpense
                    MovementType.NONE -> R.string.categoryNone
                }),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedType,
                onDismissRequest = { expandedType = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.labelIncome)) },
                    onClick = {
                        selectedType = MovementType.INCOME
                        expandedType = false
                    })
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.labelExpense)) },
                    onClick = {
                        selectedType = MovementType.EXPENSE
                        expandedType = false
                    })
            }
        }

        // Category Selector
        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { expandedCategory = it },
            modifier = Modifier.padding(8.dp)
        ) {
            TextField(
                value = stringResource(id = when (selectedCategory) {
                    Category.FOOD -> R.string.categoryFood
                    Category.TRANSPORTATION -> R.string.categoryTransportation
                    Category.BILLS -> R.string.categoryBills
                    Category.ENTERTAINMENT -> R.string.categoryEntertainment
                    Category.HEALTH -> R.string.categoryHealth
                    Category.SHOPPING -> R.string.categoryShopping
                    Category.RENT -> R.string.categoryRent
                    Category.OTHER_EXPENSES -> R.string.categoryOtherExpenses
                    Category.SALARY -> R.string.categorySalary
                    Category.CAPITAL_GAINS -> R.string.categoryCapitalGains
                    Category.OTHER_INCOMES -> R.string.categoryOtherIncomes
                    Category.NONE -> R.string.categoryNone
                }),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false }
            ) {
                // Expenses
                if (selectedType == MovementType.EXPENSE) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.categoryFood)) },
                        onClick = {
                            selectedCategory = Category.FOOD
                            expandedCategory = false
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.categoryTransportation)) },
                        onClick = {
                            selectedCategory = Category.TRANSPORTATION
                            expandedCategory = false
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.categoryBills)) },
                        onClick = {
                            selectedCategory = Category.BILLS
                            expandedCategory = false
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.categoryEntertainment)) },
                        onClick = {
                            selectedCategory = Category.ENTERTAINMENT
                            expandedCategory = false
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.categoryHealth)) },
                        onClick = {
                            selectedCategory = Category.HEALTH
                            expandedCategory = false
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.categoryShopping)) },
                        onClick = {
                            selectedCategory = Category.SHOPPING
                            expandedCategory = false
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.categoryRent)) },
                        onClick = {
                            selectedCategory = Category.RENT
                            expandedCategory = false
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.categoryOtherExpenses)) },
                        onClick = {
                            selectedCategory = Category.OTHER_EXPENSES
                            expandedCategory = false
                        })
                }
                // Incomes
                else {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.categorySalary)) },
                        onClick = {
                            selectedCategory = Category.SALARY
                            expandedCategory = false
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.categoryCapitalGains)) },
                        onClick = {
                            selectedCategory = Category.CAPITAL_GAINS
                            expandedCategory = false
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.categoryOtherIncomes)) },
                        onClick = {
                            selectedCategory = Category.OTHER_INCOMES
                            expandedCategory = false
                        })
                }
            }
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