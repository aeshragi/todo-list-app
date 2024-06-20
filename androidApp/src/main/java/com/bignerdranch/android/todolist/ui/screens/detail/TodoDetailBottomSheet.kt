package com.bignerdranch.android.todolist.ui.screens.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bignerdranch.android.todolist.R
import com.bignerdranch.android.todolist.app.utils.DateUtils
import com.bignerdranch.android.todolist.app.utils.DateUtils.asDateEx
import com.bignerdranch.android.todolist.app.utils.DateUtils.asLocalDate
import com.bignerdranch.android.todolist.data.database.Todo
import com.bignerdranch.android.todolist.data.database.TodoPriority
import com.bignerdranch.android.todolist.ui.screens.list.COLOR_DARK_GREEN
import com.bignerdranch.android.todolist.ui.screens.list.COLOR_LIGHT_GREEN
import com.bignerdranch.android.todolist.ui.screens.list.CircularCheckBox
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID

const val TODO_DETAIL_SCREEN_NAV = "TodoDetailScreen"

val DIALOG_COLOR = Color(0xfff1f1f1)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoDetailBottomSheet(
    id: UUID?,
    todoDetailViewModelFactory: TodoDetailViewModel.Factory,
    onDismissBottomSheet: () -> Unit,
    onSubmitEditAdd: () -> Unit
) {
    val viewModel: TodoDetailViewModel = viewModel(
        factory = TodoDetailViewModel.provideFactory(
            todoDetailViewModelFactory,
            id
        ),
        key = id.toString()
    )
    val uiState: DetailUIState by viewModel.detailUiState.collectAsState()
    ModalBottomSheet(
        onDismissRequest = onDismissBottomSheet,
        modifier = Modifier.fillMaxHeight(),
        containerColor = DIALOG_COLOR
    ) {
        uiState.databaseTodo?.let {
            ExistingTodo(
                todo = it,
                onCheckChanged = { check -> viewModel.updateCheck(!check) },
                onTitleChanged = { title ->
                    viewModel.updateTitle(title)
                },
                onContentChanged = { content ->
                    viewModel.updateContent(content)
                },
                onDatePickerToggled = { viewModel.toggleDatePicker() },
                showDatePicker = uiState.showDatePicker,
                onDatePickerDismissed = { viewModel.toggleDatePicker() },
                onUpdateDateDue = {
                    viewModel.updateDueDate(
                        DateUtils.convertMillisToLocalDate(it).asDateEx()
                    )
                },
                togglePriority = { viewModel.togglePriorityMenu() },
                showPriorityDropDown = uiState.showPriorityDropDown,
                updatePriority = { priority -> viewModel.updatePriority(priority) },
                updateTodo = { viewModel.upsert(it) }
            )
        } ?: NewTodo(
            onUpdateDateDue = {
                viewModel.updateDueDate(
                    DateUtils.convertMillisToLocalDate(it).asDateEx()
                )
            },
            togglePriority = { viewModel.togglePriorityMenu() },
            showPriorityDropDown = uiState.showPriorityDropDown,
            updateTodo = { todo -> viewModel.upsert(todo) },
            dismissSheet = onSubmitEditAdd
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun ExistingTodo(
    todo: Todo, onCheckChanged: (Boolean) -> Unit, onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit, onDatePickerToggled: () -> Unit, showDatePicker: Boolean,
    onDatePickerDismissed: () -> Unit, onUpdateDateDue: (Long) -> Unit, togglePriority: () -> Unit,
    showPriorityDropDown: Boolean, updatePriority: (TodoPriority) -> Unit, updateTodo: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(10.dp)) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {

        }) {
            CircularCheckBox(
                checked = todo.isDone,
                onCheckClicked = { onCheckChanged(it) },
                modifier = Modifier.size(25.dp)
            )
            TodoTextField(
                text = todo.title,
                onValueChanged = { onTitleChanged(it) },
                textStyle = MaterialTheme.typography.titleLarge,
                placeHolder = { Text(text = "Task Name", fontSize = 20.sp) }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        //modifier = Modifier.padding(start = 40.dp)
        Row {
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_document),
                    contentDescription = "edit todo",
                    tint = Color.DarkGray
                )
            }
            TodoTextField(
                text = todo.content,
                onValueChanged = { onContentChanged(it) },
                textStyle = MaterialTheme.typography.bodyMedium,
                placeHolder = { Text(text = "Description") }
            )
        }
    }

    TodoDetailActions(
        showPriorityDropDown = showPriorityDropDown,
        togglePriority = togglePriority, todo = todo,
        updatePriority = updatePriority
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ExitingTodoPreview() {
    ExistingTodo(
        todo = Todo(
            id = UUID.randomUUID(),
            title = "task title",
            content = "This is a description",
            isDone = false
        ),
        onCheckChanged = {},
        onTitleChanged = {},
        onContentChanged = {},
        onDatePickerToggled = { },
        showDatePicker = false,
        onDatePickerDismissed = { },
        onUpdateDateDue = { },
        togglePriority = { },
        showPriorityDropDown = false,
        updatePriority = {}
    ) {

    }
}

@Composable
fun TodoTextField(
    text: String,
    onValueChanged: (String) -> Unit,
    textStyle: androidx.compose.ui.text.TextStyle,
    placeHolder: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(value = text,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DIALOG_COLOR,
            unfocusedContainerColor = DIALOG_COLOR,
            focusedIndicatorColor = DIALOG_COLOR,
            unfocusedIndicatorColor = DIALOG_COLOR
        ),
        textStyle = MaterialTheme.typography.titleLarge,
        placeholder = { placeHolder() },
        modifier = modifier,
        onValueChange = { onValueChanged(it) })
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun NewTodo(
    onUpdateDateDue: (Long) -> Unit,
    togglePriority: () -> Unit,
    showPriorityDropDown: Boolean,
    updateTodo: (Todo) -> Unit,
    dismissSheet: () -> Unit
) {
    //var datePickerState = rememberDatePickerState()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var dueDate: Date? by remember { mutableStateOf(null) }
    var priority: TodoPriority by remember { mutableStateOf(TodoPriority.P4) }
    //var showDatePicker: Boolean by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    Column(horizontalAlignment = Alignment.Start) {
        TodoTextField(
            text = title,
            onValueChanged = { title = it },
            textStyle = MaterialTheme.typography.titleLarge,
            modifier = Modifier.focusRequester(focusRequester),
            placeHolder = { Text(text = "Task Name", fontSize = 20.sp) }
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        TodoTextField(
            text = content,
            onValueChanged = { content = it },
            textStyle = MaterialTheme.typography.bodyMedium,
            placeHolder = { Text(text = "Description") }
        )

        TodoDetailActions(
            showPriorityDropDown = showPriorityDropDown,
            togglePriority = togglePriority
        )
        FloatingActionButton(
            containerColor = if (title.isNotBlank()) COLOR_DARK_GREEN else COLOR_LIGHT_GREEN,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
            onClick = {
                if (title.isNotBlank()) {
                    updateTodo(
                        Todo(
                            id = UUID.randomUUID(),
                            title = title,
                            content = content,
                            isDone = false,
                            priority = priority,
                            dateDue = dueDate
                        )
                    )
                    dismissSheet()
                }
            },
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoDetailActions(
    togglePriority: () -> Unit,
    showPriorityDropDown: Boolean,
    todo: Todo? = null,
    updatePriority: ((TodoPriority) -> Unit)? = null
) {
    var showDatePicker: Boolean by remember { mutableStateOf(false) }
    var dueDate: Date? by remember { mutableStateOf(todo?.dateDue) }
    val datePickerState = rememberDatePickerState()
    var priority: TodoPriority by remember { mutableStateOf(todo?.priority ?: TodoPriority.P4) }

    Row(modifier = Modifier.padding(start = 10.dp)) {
        OutlinedButton(onClick = { showDatePicker = true }, shape = RoundedCornerShape(10.dp)) {
            val dateFormatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
            Text(
                text = if (dueDate != null) {
                    "Due date: " + asLocalDate(dueDate!!).format(dateFormatter)
                } else {
                    "Due date"
                },
                color = Color.DarkGray
            )
            if (showDatePicker) {
                val confirmEnabled = remember {
                    derivedStateOf { datePickerState.selectedDateMillis != null }
                }
                DatePickerDialog(
                    onDismissRequest = {
                        showDatePicker = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDatePicker = false
                                datePickerState.selectedDateMillis?.let {
                                    dueDate =
                                        Date.from(Instant.ofEpochMilli(it + 4 * 60 * 60 * 1000))
                                }
                            },
                            enabled = confirmEnabled.value
                        ) { Text(text = "OK") }
                    }) { DatePicker(state = datePickerState) }
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box {
            OutlinedButton(onClick = { togglePriority() }, shape = RoundedCornerShape(10.dp)) {
                val prio = todo?.priority ?: priority
                Text("$prio", color = prio.toColor())
            }
            DropdownMenu(expanded = showPriorityDropDown, onDismissRequest = {
                togglePriority()
            }) {
                TodoPriority.entries.forEach {
                    DropdownMenuItem(text = { Text(text = it.name) }, onClick = {
                        if (updatePriority != null) {
                            updatePriority(it)
                        } else {
                            priority = it
                        }
                    })
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewNewTodo() {
    NewTodo(
        onUpdateDateDue = { },
        togglePriority = { },
        showPriorityDropDown = false,
        updateTodo = { },
        dismissSheet = {})
}