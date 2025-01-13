package com.oscargs.savingsapp

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.oscargs.savingsapp.models.Movement
import com.oscargs.savingsapp.ui.theme.SavingsAppTheme
import com.oscargs.savingsapp.utilities.Category
import com.oscargs.savingsapp.utilities.MovementType
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EditMovementScreen(id: Int) {
    SavingsAppTheme {
        EditMovementForm(id = id)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMovementForm(id: Int) {
    // Database
    val db = MainApplication.database

    // Obtén los datos del movimiento
    val movement: Movement = getMovement(id = id)

    // Text variables
    var text by remember { mutableStateOf(movement.description) }
    var amount by remember { mutableStateOf(movement.amount.toString()) }

    // Date
    var pickedDate by remember { mutableStateOf(movement.date) }
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter.ofPattern("dd-MM-yyyy").format(pickedDate)
        }
    }

    // Type
    var expandedType by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(movement.type) }

    // Category
    var expandedCategory by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(movement.category) }

    // Errors
    var descriptionError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var typeError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }

    // Reset category when type changes
    LaunchedEffect(selectedType) {
        selectedCategory = Category.NONE
    }

    // Update states when movement changes
    LaunchedEffect(movement) {
        text = movement.description
        amount = movement.amount.toString()
        pickedDate = movement.date
        selectedType = movement.type
        selectedCategory = movement.category
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.labelAddMovement)) },
                actions = {
                    FilledTonalButton(
                        modifier = Modifier.padding(16.dp),
                        onClick = {
                            // Error handling
                            descriptionError = text.isEmpty()
                            amountError = amount.isEmpty()
                            typeError = selectedType == MovementType.NONE
                            categoryError = selectedCategory == Category.NONE

                            if (descriptionError || amountError || typeError || categoryError) {
                                return@FilledTonalButton
                            }

                            CoroutineScope(Dispatchers.IO).launch {
                                db.movementDAO().addMovement(
                                    Movement(
                                        id = 0,
                                        amount = amount.toDouble(),
                                        description = text,
                                        date = pickedDate,
                                        type = selectedType,
                                        category = selectedCategory,
                                        creationTime = LocalDateTime.now(),
                                        modificationTime = LocalDateTime.now(),
                                    )
                                )
                            }
                        }) {
                        Text(text = stringResource(R.string.save))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
            .padding(8.dp)) {

            // Description text field
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(R.string.labelDescriptionTF)) },
                placeholder = { Text(stringResource(R.string.hintDescriptionTF)) },
                singleLine = true,
                isError = descriptionError
            )

            // Amount text field
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                        amount = newValue
                    }
                },
                label = { Text(stringResource(R.string.labelAmountTF)) },
                placeholder = { Text(stringResource(R.string.hintAmountTF)) },
                singleLine = true,
                isError = amountError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            // Date field
            val dateDialogState = rememberMaterialDialogState()

            FilledTonalButton(onClick = {
                dateDialogState.show()
            }, modifier = Modifier.padding(8.dp)) {
                Text(text = stringResource(R.string.labelDateTF))
            }
            Text(modifier = Modifier.padding(8.dp), text = formattedDate)

            MaterialDialog(
                dialogState = dateDialogState,
                buttons = {
                    positiveButton(stringResource(R.string.labelAccept))
                    negativeButton(stringResource(R.string.labelCancel))
                }
            ) {
                datepicker(
                    initialDate = pickedDate ?: LocalDate.now(),
                    title = R.string.labelSelectDate.toString(),
                ) {
                    pickedDate = it
                }
            }

            // Type Selector
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                TextField(
                    value = stringResource(id = when (selectedType) {
                        MovementType.INCOME -> R.string.labelIncome
                        MovementType.EXPENSE -> R.string.labelExpense
                        MovementType.NONE -> R.string.typeNone
                    }),
                    onValueChange = {},
                    readOnly = true,
                    isError = typeError,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .padding(bottom = 32.dp)
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
                    isError = categoryError,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
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
}

fun getMovement(id: Int): Movement {
    val db = MainApplication.database
    var movement: Movement? = null
    runBlocking {
        movement = withContext(Dispatchers.IO) {
            db.movementDAO().getMovementById(id)
        }
    }
    return movement ?: throw IllegalStateException("Movement not found")
}


@Preview(showBackground = true)
@Composable
fun EditMovementScreenPreview() {
    SavingsAppTheme {
        EditMovementScreen(id = 0)
    }
}