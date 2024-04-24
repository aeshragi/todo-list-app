package com.bignerdranch.android.todolist.ui.screens.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.todolist.app.utils.DateUtils
import com.bignerdranch.android.todolist.data.TodoRepository
import com.bignerdranch.android.todolist.data.database.Todo
import com.bignerdranch.android.todolist.data.database.TodoPriority
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
class TodoDetailViewModel @AssistedInject constructor(
    @Assisted todoId: UUID?,
    private val todoRepository: TodoRepository
) : ViewModel() {

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
                todoRepository.getTodo(it).collect { todo ->
                    _detailUiState.update { state -> state.copy(databaseTodo = todo) }
                }
            }
        }
    }

    fun toggleDatePicker() {
        _detailUiState.update { oldState ->
            val prevDatePicker = oldState.showDatePicker
            oldState.copy(showDatePicker = !prevDatePicker)
        }
    }

    fun upsert(todo: Todo) {
        val updatedTodo = todo.copy(dateCreated = DateUtils.asDate(LocalDateTime.now()))
        viewModelScope.launch {
            val existingTodo = todoRepository.getTodo(todo.id).firstOrNull()
            if (existingTodo != null) {
                todoRepository.updateTodo(updatedTodo)
            } else {
                todoRepository.addTodo(updatedTodo)
            }
        }
    }

    fun updateDueDate(date: Date) {
        _detailUiState.update { oldState ->
            oldState.copy(
                databaseTodo = oldState.databaseTodo.copy(
                    dateDue = date
                ), showDueDate = true
            )
        }
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

    @AssistedFactory
    interface Factory {
        fun create(uuid: UUID?): TodoDetailViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            uuid: UUID?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(uuid) as T
            }
        }
    }
}
