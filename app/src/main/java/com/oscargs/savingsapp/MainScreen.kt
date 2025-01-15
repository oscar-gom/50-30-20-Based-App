package com.oscargs.savingsapp

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier) {
    // Bottom sheet states
    val addSheetState = rememberModalBottomSheetState()
    val editSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showAddBottomSheet by remember { mutableStateOf(false) }
    var showEditBottomSheet by remember { mutableStateOf(false) }
    var selectedMovementId by remember { mutableStateOf<Int?>(null) }

    // DB
    val movements: LiveData<List<Movement>> = loadMovements(YearMonth.now())
    val movementList by movements.observeAsState(initial = emptyList())

    // Values for the top bar
    var totalNecessary = 0.0
    var totalUnnecessary = 0.0
    var totalIncome = 0.0

    var isErrorNecessary by remember { mutableStateOf(false) }
    var isErrorUnnecessary by remember { mutableStateOf(false) }
    var isErrorSavings by remember { mutableStateOf(false) }



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
                    showAddBottomSheet = true
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

                // Calculate errors
                isErrorNecessary = necessaryPercentage > 50
                isErrorUnnecessary = unnecessaryPercentage > 30
                isErrorSavings = savingsPercentage < 20

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
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center,
                            color = if (isErrorNecessary) Color(0xFF861d1d) else Color(0xFF0b6730)
                        ),
                        readOnly = true,
                        isError = isErrorNecessary,
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
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center,
                            color = if (isErrorUnnecessary) Color(0xFF861d1d) else Color(0xFF0b6730)
                        ),
                        isError = isErrorUnnecessary
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
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center,
                            color = if (isErrorSavings) Color(0xFF861d1d) else Color(0xFF0b6730)
                        ),
                        isError = isErrorSavings
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
                        "%.2f €", totalIncome
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp
                )
                Text(
                    text = stringResource(R.string.totalSpendings) + String.format(
                        "%.2f €", (totalNecessary + totalUnnecessary)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp
                )
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            // Month selector
            var currentMonth by remember { mutableStateOf(YearMonth.now()) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        currentMonth = currentMonth.minusMonths(1)
                        loadMovements(currentMonth)
                    }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
                }
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(
                    onClick = {
                        currentMonth = currentMonth.plusMonths(1)
                        loadMovements(currentMonth)
                    }
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
                }
            }

            // Movement list
            MovementList(
                modifier = Modifier.padding(8.dp),
                movements = movementList,
                onItemClick = { movementId ->
                    scope.launch {
                        selectedMovementId = movementId
                        showEditBottomSheet = true
                    }
                }
            )
        }

        // Add Bottom sheet
        if (showAddBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showAddBottomSheet = false
                }, sheetState = addSheetState, modifier = Modifier.padding(innerPadding)
            ) {
                AddMovementScreen()
            }
        }

        // Edit Bottom sheet
        if (showEditBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showEditBottomSheet = false
                }, sheetState = editSheetState, modifier = Modifier.padding(innerPadding)
            ) {
                EditMovementScreen(id = selectedMovementId!!)
            }
        }
    }
}


@Composable
fun MovementList(modifier: Modifier, movements: List<Movement>, onItemClick: (Int) -> Unit) {
    LazyColumn(modifier = modifier) {
        items(movements) { movement ->
            ItemDisplay(movement, onItemClick)
        }
    }
}

@Composable
fun ItemDisplay(movement: Movement, onItemClick: (Int) -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
            .clickable { onItemClick(movement.id) }
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
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    showDeleteDialog = true
                }
            ) {
                Icon(Icons.Filled.Delete, contentDescription = stringResource(id = R.string.deleteMovement))
            }
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(text = stringResource(id = R.string.deleteMovement))
                    },
                    text = {
                        Text(text = stringResource(id = R.string.deleteMovementConfirmation))
                    },
                    confirmButton = {
                        FilledTonalButton(
                            onClick = {
                                deleteMovement(movement)
                                showDeleteDialog = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.delete))
                        }
                    },
                    dismissButton = {
                        FilledTonalButton(
                            onClick = {
                                showDeleteDialog = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}

fun deleteMovement(movement: Movement) {
    CoroutineScope(Dispatchers.IO).launch {
        MainApplication.database.movementDAO().deleteMovement(movement)
    }
}


fun loadMovements(date: YearMonth): LiveData<List<Movement>> = liveData(Dispatchers.IO) {
    val db = MainApplication.database
    val movements = db.movementDAO().getMovementByMonthYear(
        date.monthValue.toString(),
        date.year.toString()
    )
    emitSource(movements)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SavingsAppTheme {
        MainScreen(Modifier.fillMaxWidth())
    }
}