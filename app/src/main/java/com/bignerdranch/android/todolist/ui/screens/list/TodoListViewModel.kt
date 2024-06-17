package com.bignerdranch.android.todolist.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.todolist.data.TodoRepository
import com.bignerdranch.android.todolist.data.database.Todo
import com.bignerdranch.android.todolist.ui.screens.list.TodoSort.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(private val todoRepository: TodoRepository) :
    ViewModel() {
    private val _uiState: MutableStateFlow<ListUiState> =
        MutableStateFlow(ListUiState(todos = emptyList()))

    init {
        viewModelScope.launch {
            todoRepository.getTodos().stateIn(viewModelScope).collectLatest { todos ->
                // TODO add a cache for sorting and completed preferences
                _uiState.update { state -> state.copy(todos = todos.sortBy(uiState.value.todoSort)) }
            }
        }
    }

    fun clearTodoId() {
        _uiState.update { oldState ->
            oldState.copy(selectedTodo = null)
        }
    }

    fun todoSelected(id: UUID) {
        _uiState.update { oldState ->
            oldState.copy(selectedTodo = id)
        }
    }

    fun toggleDropDown() {
        _uiState.update { oldState ->
            val oldExpanded = oldState.sortExpanded
            oldState.copy(sortExpanded = !oldExpanded)
        }
    }

    suspend fun onCheckChanged(todo: Todo) {
        val checked = todo.isDone
        todoRepository.updateTodo(todo.copy(isDone = !checked))
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancelChildren()
    }

    fun List<Todo>.sortBy(sort: TodoSort): List<Todo> {
        return when (sort) {
            PRIORITY -> sortedBy { it.priority }
            DUE_DATE -> sortedBy { it.dateDue }
            DATE_CREATED -> sortedBy { it.dateCreated }
        }
    }

    fun sortBy(sort: TodoSort) {
        _uiState.update { oldState ->
            oldState.copy(
                todos = when (sort) {
                    PRIORITY -> {
                        oldState.todos.sortedBy { it.priority }
                    }

                    DUE_DATE -> {
                        oldState.todos.sortedBy { it.dateDue }
                    }

                    DATE_CREATED -> {
                        oldState.todos.sortedBy { it.dateCreated }
                    }
                },
                todoSort = sort
            )
        }
        toggleDropDown()
    }

    val uiState: StateFlow<ListUiState> get() = _uiState
}