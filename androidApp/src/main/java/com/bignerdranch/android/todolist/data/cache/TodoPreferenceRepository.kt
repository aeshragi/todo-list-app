package com.bignerdranch.android.todolist.data.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bignerdranch.android.todolist.ui.screens.list.TodoSort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoPreferenceRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {
    val storedQuery: Flow<TodoSort> = dataStore.data.map {
        it[SORT_METHOD]?.let { sortMethod -> TodoSort.entries.firstOrNull { entry -> entry.name == sortMethod } }
            ?: TodoSort.DUE_DATE
    }.distinctUntilChanged()

    suspend fun setStoredQuery(todoSort: TodoSort) {
        dataStore.edit {
            it[SORT_METHOD] = todoSort.name
        }
    }

    companion object {
        private val SORT_METHOD = stringPreferencesKey("sort_method")
    }
}