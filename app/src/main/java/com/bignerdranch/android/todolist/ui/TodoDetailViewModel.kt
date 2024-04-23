package com.bignerdranch.android.todolist.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.todolist.app.DateUtils
import com.bignerdranch.android.todolist.data.TodoRepository
import com.bignerdranch.android.todolist.data.database.Todo
import com.bignerdranch.android.todolist.data.database.TodoPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
class TodoDetailViewModel(todoId: UUID?) : ViewModel() {

    private val _detailUiState: MutableStateFlow<DetailUIState> = MutableStateFlow(
        DetailUIState(
            databaseTodo = Todo(
                id = UUID.randomUUID(), title = "", content = "", isDone = false,
            )
        )
    )
    val detailUiState: StateFlow<DetailUIState> = _detailUiState.asStateFlow()

    init {
        viewModelScope.launch {
            todoId?.let {
                TodoRepository.get().getTodo(it).collect { todo ->
                    _detailUiState.update { state -> state.copy(databaseTodo = todo) }
                }
            }
        }
    }

    fun toggleDatePicker() {
        _detailUiState.update {oldState ->
            val prevDatePicker = oldState.showDatePicker
            oldState.copy(showDatePicker = !prevDatePicker)
        }
    }

    fun upsert(todo: Todo) {
        val updatedTodo = todo.copy(dateCreated = DateUtils.asDate(LocalDateTime.now()))
        viewModelScope.launch {
            val existingTodo = TodoRepository.get().getTodo(todo.id).firstOrNull()
            if (existingTodo != null) {
                TodoRepository.get().updateTodo(updatedTodo)
            } else {
                TodoRepository.get().addTodo(updatedTodo)
            }
        }
    }

    fun updateDueDate(date: Date) {
        _detailUiState.update { oldState -> oldState.copy(databaseTodo = oldState.databaseTodo.copy(dateDue = date), showDueDate = true)}
    }

    fun updateTitle(title: String) {
        _detailUiState.update { it.copy(databaseTodo = it.databaseTodo.copy(title = title)) }
    }

    fun updatePriority(priority: TodoPriority) {
        _detailUiState.update { it.copy(databaseTodo = it.databaseTodo.copy(priority = priority)) }
        togglePriorityMenu()
    }

    fun togglePriorityMenu() {
        _detailUiState.update {
            val previous = it.showPriorityDropDown
            it.copy(showPriorityDropDown = !previous)
        }
    }

    fun updateContent(content: String) {
        _detailUiState.update { it.copy(databaseTodo = it.databaseTodo.copy(content = content)) }
    }

    fun updateCheck(check: Boolean) {
        _detailUiState.update { it.copy(databaseTodo = it.databaseTodo.copy(isDone = check)) }
    }
}

class TodoDetailViewModelFactory(private val uuid: UUID?) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TodoDetailViewModel(uuid) as T
    }
}