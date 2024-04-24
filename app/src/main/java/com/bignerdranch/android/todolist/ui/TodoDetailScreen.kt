package com.bignerdranch.android.todolist.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bignerdranch.android.todolist.app.DateUtils
import com.bignerdranch.android.todolist.app.DateUtils.asDateEx
import com.bignerdranch.android.todolist.data.TodoRepository
import com.bignerdranch.android.todolist.data.database.TodoPriority
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoDetailScreen(
    id: UUID?, onSubmitEditAdd: () -> Unit,
    todoDetailViewModelFactory: TodoDetailViewModel.Factory
) {
    val viewModel: TodoDetailViewModel = viewModel(factory = TodoDetailViewModel.provideFactory(todoDetailViewModelFactory, id))
    val uiState: DetailUIState by viewModel.detailUiState.collectAsState()
    val datePickerState = rememberDatePickerState()
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {

        }) {
            Checkbox(checked = uiState.databaseTodo.isDone, onCheckedChange = {
                viewModel.updateCheck(it)
            })
            TextField(value = uiState.databaseTodo.title,
                modifier = Modifier.background(Color.White),
                textStyle = MaterialTheme.typography.titleMedium,
                onValueChange = {
                    viewModel.updateTitle(it)
                })
        }
        Spacer(modifier = Modifier.height(10.dp))
        TextField(value = uiState.databaseTodo.content,
            modifier = Modifier.padding(start = 45.dp, end = 15.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            onValueChange = {
                viewModel.updateContent(it)
            })
        OutlinedButton(onClick = { viewModel.toggleDatePicker() }) {
            Text(
                text = if (uiState.showDueDate) {
                    "due date: " + uiState.databaseTodo.dateDue.toString()
                } else {
                    "due date"
                }
            )
        }
        if (uiState.showDatePicker) {
            DatePickerDialog(onDismissRequest = { viewModel.toggleDatePicker() },
                confirmButton = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.updateDueDate(DateUtils.convertMillisToLocalDate(it).asDateEx())
                    }
                }) {
                DatePicker(state = datePickerState)
            }
        }
        Box {
            OutlinedButton(onClick = { viewModel.togglePriorityMenu() }) {
                Text("Priority: ${uiState.databaseTodo.priority}")
            }
            DropdownMenu(expanded = uiState.showPriorityDropDown, onDismissRequest = {
                viewModel.togglePriorityMenu()
            }) {
                TodoPriority.entries.forEach {
                    DropdownMenuItem(text = { Text(text = it.name) }, onClick = {
                        viewModel.updatePriority(it)
                    })
                }
            }
        }
        OutlinedButton(
            onClick = {
                viewModel.upsert(uiState.databaseTodo)
                onSubmitEditAdd()
            }) {
            Text("add/edit todo")
        }
    }
}