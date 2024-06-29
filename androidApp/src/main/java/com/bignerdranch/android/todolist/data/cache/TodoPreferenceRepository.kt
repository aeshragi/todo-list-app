package com.bignerdranch.android.todolist.data.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bignerdranch.android.todolist.ui.screens.list.TodoSort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoPreferenceRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {
    val storedQuery: Flow<TodoSort> = dataStore.data.map {
        it[SORT_METHOD]?.let { sortMethod -> TodoSort.entries.firstOrNull { entry -> entry.name == sortMethod } }
            ?: TodoSort.DUE_DATE
    }

    val showCompleted: Flow<Boolean> = dataStore.data.map { it[SHOW_COMPLETED] ?: false }

    suspend fun setStoredQuery(todoSort: TodoSort) {
        dataStore.edit {
            it[SORT_METHOD] = todoSort.name
        }
    }

    suspend fun setShowCompleted(showCompleted: Boolean) {
        dataStore.edit {
            it[SHOW_COMPLETED] = showCompleted
        }
    }

    companion object {
        private val SORT_METHOD = stringPreferencesKey("sort_method")
        private val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
    }
}