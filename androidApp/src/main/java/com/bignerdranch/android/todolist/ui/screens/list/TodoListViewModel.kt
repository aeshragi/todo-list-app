package com.bignerdranch.android.todolist.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.todolist.data.TodoRepository
import com.bignerdranch.android.todolist.data.cache.TodoPreferenceRepository
import com.bignerdranch.android.todolist.data.database.Todo
import com.bignerdranch.android.todolist.ui.screens.list.TodoSort.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val todoPreferenceRepository: TodoPreferenceRepository
) :
    ViewModel() {
    private val _uiState: MutableStateFlow<ListUiState> =
        MutableStateFlow(ListUiState(todos = emptyList()))

    init {
        viewModelScope.launch {
            combine(
                todoRepository.getTodos(),
                todoPreferenceRepository.storedQuery,
                todoPreferenceRepository.showCompleted
            ) { todos, query, showCompleted ->
                _uiState.update { state ->
                    state.copy(
                        todos = todos.sortBy(query).filter {
                            if (!showCompleted) {
                                !it.isDone
                            } else {
                                true
                            }
                        },
                        todoSort = query,
                        showCompleted = showCompleted
                    )
                }
            }.collect()
        }
    }

    fun toggleShowCompleted() {
        _uiState.update { old ->
            old.copy(showCompleted = !old.showCompleted)
        }
        viewModelScope.launch {
            todoPreferenceRepository.setShowCompleted(_uiState.value.showCompleted)
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

    private fun List<Todo>.sortBy(sort: TodoSort): List<Todo> {
        val doneList = filter { it.isDone }
        val unDone = filter { !it.isDone }

        return when (sort) {
            PRIORITY -> unDone.sortedBy { it.priority } + doneList.sortedBy { it.priority }
            DUE_DATE -> unDone.sortedBy { it.dateDue } + doneList.sortedBy { it.dateDue }
            DATE_CREATED -> unDone.sortedBy { it.dateCreated } + doneList.sortedBy { it.dateCreated }
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
        viewModelScope.launch {
            todoPreferenceRepository.setStoredQuery(sort)
        }
        toggleDropDown()
    }

    val uiState: StateFlow<ListUiState> get() = _uiState
}