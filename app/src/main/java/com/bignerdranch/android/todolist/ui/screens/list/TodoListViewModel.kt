package com.bignerdranch.android.todolist.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.todolist.data.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(private val todoRepository: TodoRepository): ViewModel() {
    private val _uiState: MutableStateFlow<ListUiState> = MutableStateFlow(ListUiState(todos = emptyList()))
        init {
            viewModelScope.launch {
                todoRepository.getTodos().stateIn(viewModelScope).collect {
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

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancelChildren()
    }

    fun sortBy(sort: TodoSort) {
        _uiState.update { oldState ->
            oldState.copy(
                todos = when (sort) {
                    TodoSort.PRIORITY -> { oldState.todos.sortedBy { it.priority } }
                    TodoSort.DUE_DATE -> { oldState.todos.sortedBy { it.dateDue } }
                    TodoSort.DATE_CREATED -> { oldState.todos.sortedBy { it.dateCreated } }
                },
                todoSort = sort
            )
        }
        toggleDropDown()
    }

    val uiState: StateFlow<ListUiState> get() = _uiState
}