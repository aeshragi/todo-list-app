package com.bignerdranch.android.todolist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.todolist.data.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoListViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ListUiState> = MutableStateFlow(ListUiState(todos = emptyList()))
        init {
            viewModelScope.launch {
                TodoRepository.get().getTodos().stateIn(viewModelScope).collect {
                    // TODO add a cache for sorting and completed preferences
                    _uiState.update { uiState -> uiState.copy(todos = it) }
                }
            }
    }

    fun toggleDropDown() {
        _uiState.update { oldState ->
            val oldExpanded = oldState.sortExpanded
            oldState.copy(sortExpanded = !oldExpanded)
        }
    }

    fun sortBy(sort: TodoSort) {
        _uiState.update { oldState ->
            oldState.copy(
                todos = when (sort) {
                    TodoSort.PRIORITY -> { oldState.todos.sortedBy { it.priority } }
                    TodoSort.DUE_DATE -> { oldState.todos.sortedBy { it.dateDue } }
                    TodoSort.DATE_CREATED -> { oldState.todos.sortedBy { it.dateCreated } }
                }
            )
        }
    }

    val uiState: StateFlow<ListUiState> get() = _uiState
}