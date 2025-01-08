package com.oscargs.savingsapp

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.oscargs.savingsapp.models.Movement
import com.oscargs.savingsapp.ui.theme.SavingsAppTheme
import com.oscargs.savingsapp.utilities.Category
import com.oscargs.savingsapp.utilities.MovementType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier) {
    // Bottom sheet
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    // DB
    val movements: LiveData<List<Movement>> = loadMovements()
    val movementList by movements.observeAsState(initial = emptyList())

    var totalNecessary = 0.0
    var totalUnnecessary = 0.0
    var totalIncome = 0.0

    for (movement in movementList) {
        when (movement.category) {
            Category.FOOD, Category.TRANSPORTATION, Category.BILLS, Category.HEALTH, Category.RENT -> {
                totalNecessary += movement.amount
            }

            Category.SHOPPING, Category.ENTERTAINMENT, Category.OTHER_EXPENSES -> {
                totalUnnecessary += movement.amount
            }

            Category.SALARY, Category.CAPITAL_GAINS, Category.OTHER_INCOMES -> {
                totalIncome += movement.amount
            }

            Category.NONE -> {}
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    showBottomSheet = true
                }
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add movement button")
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val necessaryPercentage =
                    if (totalIncome > 0) (totalNecessary / totalIncome) * 100 else 0.0
                val unnecessaryPercentage =
                    if (totalIncome > 0) (totalUnnecessary / totalIncome) * 100 else 0.0
                val savingsPercentage =
                    if (totalIncome > 0) ((totalIncome - totalNecessary - totalUnnecessary) / totalIncome) * 100 else 0.0

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.necessary), textAlign = TextAlign.Center)
                    TextField(
                        value = String.format("%.2f%%", necessaryPercentage),
                        onValueChange = { },
                        readOnly = true,
                        textStyle = TextStyle(textAlign = TextAlign.Center)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.unnecessary), textAlign = TextAlign.Center)
                    TextField(
                        value = String.format("%.2f%%", unnecessaryPercentage),
                        onValueChange = { },
                        readOnly = true,
                        textStyle = TextStyle(textAlign = TextAlign.Center)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.savings), textAlign = TextAlign.Center)
                    TextField(
                        value = String.format("%.2f%%", savingsPercentage),
                        onValueChange = { },
                        readOnly = true,
                        textStyle = TextStyle(textAlign = TextAlign.Center)
                    )
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = stringResource(R.string.totalIncome) + String.format(
                        "%.2f €",
                        totalIncome
                    ), textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.totalSpendings) + String.format(
                        "%.2f €",
                        (totalNecessary + totalUnnecessary)
                    ), textAlign = TextAlign.Center
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Movement list
            MovementList(modifier = Modifier.padding(8.dp), movements = movementList)
        }


        // Bottom sheet
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                }, sheetState = sheetState, modifier = Modifier.padding(innerPadding)
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
            ItemDisplay(movement)
        }
    }
}

@Composable
fun ItemDisplay(movement: Movement) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row {
                Text(
                    text = movement.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = movement.amount.toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = movement.date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        .toString()
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row {
                Text(
                    text = when (movement.type) {
                        MovementType.INCOME -> stringResource(id = R.string.labelIncome)
                        MovementType.EXPENSE -> stringResource(id = R.string.labelExpense)
                        MovementType.NONE -> stringResource(id = R.string.typeNone)
                    }, fontSize = 12.sp, color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (movement.category) {
                        Category.FOOD -> stringResource(id = R.string.categoryFood)
                        Category.TRANSPORTATION -> stringResource(id = R.string.categoryTransportation)
                        Category.BILLS -> stringResource(id = R.string.categoryBills)
                        Category.ENTERTAINMENT -> stringResource(id = R.string.categoryEntertainment)
                        Category.HEALTH -> stringResource(id = R.string.categoryHealth)
                        Category.SHOPPING -> stringResource(id = R.string.categoryShopping)
                        Category.RENT -> stringResource(id = R.string.categoryRent)
                        Category.OTHER_EXPENSES -> stringResource(id = R.string.categoryOtherExpenses)
                        Category.SALARY -> stringResource(id = R.string.categorySalary)
                        Category.CAPITAL_GAINS -> stringResource(id = R.string.categoryCapitalGains)
                        Category.OTHER_INCOMES -> stringResource(id = R.string.categoryOtherIncomes)
                        Category.NONE -> stringResource(id = R.string.categoryNone)
                    }, fontSize = 12.sp, color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (movement.category) {
                        Category.FOOD, Category.TRANSPORTATION, Category.BILLS, Category.HEALTH, Category.RENT -> stringResource(
                            id = R.string.necessary
                        )

                        Category.SHOPPING, Category.ENTERTAINMENT, Category.OTHER_EXPENSES -> stringResource(
                            id = R.string.unnecessary
                        )

                        else -> ""
                    }, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1f)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        color = when (movement.type) {
                            MovementType.INCOME -> Color.Green
                            MovementType.EXPENSE -> Color.Red
                            else -> Color.Transparent
                        }
                    )
            )
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
        MainScreen(Modifier.fillMaxWidth())
    }
}