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
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

    private val _detailUiState: MutableStateFlow<DetailUIState> = MutableStateFlow(DetailUIState())
    val detailUiState: StateFlow<DetailUIState> = _detailUiState.asStateFlow()

    init {
        viewModelScope.launch {
            todoId?.let {
                todoRepository.getTodo(it).collectLatest { todo ->
                    _detailUiState.update { state -> state.copy(databaseTodo = todo) }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancelChildren()
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
                databaseTodo = oldState.databaseTodo?.copy(
                    dateDue = date
                ), showDueDate = true
            )
        }
    }

    private suspend fun updateDatabaseTodo(todo: Todo) { todoRepository.updateTodo(todo) }

    fun updateTitle(title: String) {
        viewModelScope.launch {
            _detailUiState.update { it.copy(databaseTodo = it.databaseTodo?.copy(title = title)) }
            updateDatabaseTodo(_detailUiState.value.databaseTodo!!)
        }

    }

    fun updatePriority(priority: TodoPriority) {
        viewModelScope.launch {
            _detailUiState.update { it.copy(databaseTodo = it.databaseTodo?.copy(priority = priority)) }
            updateDatabaseTodo(_detailUiState.value.databaseTodo!!)
            togglePriorityMenu()
        }
    }

    fun togglePriorityMenu() {
        _detailUiState.update {
            val previous = it.showPriorityDropDown
            it.copy(showPriorityDropDown = !previous)
        }
    }

    fun updateContent(content: String) {
        viewModelScope.launch {
            _detailUiState.update { it.copy(databaseTodo = it.databaseTodo?.copy(content = content)) }
            updateDatabaseTodo(_detailUiState.value.databaseTodo!!)
        }
    }

    fun updateCheck(check: Boolean) {
        viewModelScope.launch {
            _detailUiState.update { it.copy(databaseTodo = it.databaseTodo?.copy(isDone = check)) }
            updateDatabaseTodo(_detailUiState.value.databaseTodo!!)
        }
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
