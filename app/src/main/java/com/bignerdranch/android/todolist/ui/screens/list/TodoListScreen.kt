package com.bignerdranch.android.todolist.ui.screens.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bignerdranch.android.todolist.R
import com.bignerdranch.android.todolist.app.utils.DateUtils
import com.bignerdranch.android.todolist.data.database.Todo
import com.bignerdranch.android.todolist.data.database.TodoPriority
import com.bignerdranch.android.todolist.ui.screens.detail.TodoDetailBottomSheet
import com.bignerdranch.android.todolist.ui.screens.detail.TodoDetailViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

const val LIST_SCREEN_NAV = "TodoListScreen"
val COLOR_ORANGE = Color(0xFFFFA500)
val COLOR_DARK_GREEN = Color(0xff486856)
val COLOR_LIGHT_GREEN = Color(0xff90b493)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoListScreen(
    factory: TodoDetailViewModel.Factory,
    onEditTodoClicked: (UUID) -> Unit,
    viewModel: TodoListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    Column {
        Box {
            OutlinedButton(onClick = { viewModel.toggleDropDown() }) {
                Text(text = "Sort by: ${uiState.todoSort}")
            }
            DropdownMenu(expanded = uiState.sortExpanded,
                onDismissRequest = { viewModel.toggleDropDown() }) {
                TodoSort.entries.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.name) },
                        onClick = { viewModel.sortBy(it) })
                }
            }
        }
        LazyColumn {
            items(uiState.todos) { todo ->
                TodoItem(todo = todo,
                    onEditTodoClicked = { uuid ->
                        showBottomSheet = true
                        viewModel.todoSelected(uuid)
                    },
                    onCheckClicked = {
                        coroutineScope.launch {
                            viewModel.onCheckChanged(todo)
                        }
                    })
            }
        }
        AddTaskCard(onClick = { showBottomSheet = true })
        if (showBottomSheet) {
            TodoDetailBottomSheet(
                id = uiState.selectedTodo,
                onSubmitEditAdd = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                },
                todoDetailViewModelFactory = factory,
                onDismissBottomSheet = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                    viewModel.clearTodoId()
                }
            )
        }
    }
}

@Composable
fun TodoItem(todo: Todo, onEditTodoClicked: (UUID) -> Unit, onCheckClicked: (Boolean) -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 5.dp, bottom = 5.dp)
            .fillMaxWidth()
            .clickable { onEditTodoClicked(todo.id) },
        colors = CardDefaults.outlinedCardColors(todo.priority.toColor()),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 1.dp
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularCheckBox(onCheckClicked, todo.isDone, modifier = Modifier.size(20.dp))
            //Text(text = todo.priority.toString())
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = todo.title,
                fontSize = 16.sp,
                style = if (todo.isDone) {
                    TextStyle(textDecoration = TextDecoration.LineThrough)
                } else {
                    TextStyle.Default
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCircularCheckbox() {
    CircularCheckBox({}, false)
}

@Preview(showBackground = true)
@Composable
fun PreviewCircularCheckboxChecked() {
    CircularCheckBox({}, true)
}

@Composable
fun CircularCheckBox(onCheckClicked: (Boolean) -> Unit, checked: Boolean, modifier: Modifier = Modifier) {
    IconButton(onClick = { onCheckClicked(checked) }) {
        Icon(
            painter = painterResource(id = if (checked) R.drawable.round_checkbox_filled else R.drawable.round_checkbox),
            contentDescription = "",
            tint = if (checked) Color.Black else Color.White,
            modifier = modifier
                .border(
                    border = BorderStroke(1.dp, Color.DarkGray),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun AddTaskCard(onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 5.dp, bottom = 5.dp)
            .fillMaxWidth(),
        colors = cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp, pressedElevation = 1.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
            onClick()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                tint = Color(0xFF006400),
                contentDescription = "add"
            )
            Text(text = "Add Task")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewTodoItem() {
    TodoItem(
        Todo(
            title = "foo",
            content = "this is a foo with some bar",
            isDone = false,
            id = UUID.randomUUID(),
            priority = TodoPriority.P0,
            dateCreated = DateUtils.asDate(LocalDateTime.now()),
            dateDue = DateUtils.asDate(LocalDateTime.now())
        ),
        onEditTodoClicked = {},
        onCheckClicked = {}
    )
}