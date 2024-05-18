package com.bignerdranch.android.todolist.ui.screens.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.outlinedCardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.todolist.R
import com.bignerdranch.android.todolist.app.utils.DateUtils
import com.bignerdranch.android.todolist.data.database.Todo
import com.bignerdranch.android.todolist.data.database.TodoPriority
import java.time.LocalDateTime
import java.util.UUID

const val LIST_SCREEN_NAV = "TodoListScreen"

@Composable
fun TodoListScreen(
    addTodoClicked: () -> Unit,
    onEditTodoClicked: (UUID) -> Unit,
    viewModel: TodoListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
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
            items(uiState.todos) {
                TodoItem(todo = it, onEditTodoClicked)
            }
        }
        AddTaskCard(onClick = { addTodoClicked() })
    }
}

@Composable
fun TodoItem(todo: Todo, onEditTodoClicked: (UUID) -> Unit) {
    Card(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 5.dp, bottom = 5.dp)
            .fillMaxWidth(),
        colors = cardColors(containerColor = Color.White),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
            onEditTodoClicked(todo.id)
        }) {
//            Checkbox(checked = todo.isDone,
//                modifier = Modifier.clip(CircleShape), onCheckedChange = { })
            CircularCheckBox()
            Text(text = todo.priority.toString())
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = todo.title)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCircularCheckbox() {
    CircularCheckBox()
}

@Composable
fun CircularCheckBox() {
    IconButton(onClick = { /*TODO*/ }) {
        Icon(
            painter = painterResource(id = R.drawable.round_checkbox),
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .size(25.dp)
                .border(
                    border = BorderStroke(2.dp, Color.DarkGray),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun AddTaskCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 5.dp, bottom = 5.dp)
            .fillMaxWidth(),
        colors = cardColors(containerColor = Color.White),
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
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
        onEditTodoClicked = {}
    )
}