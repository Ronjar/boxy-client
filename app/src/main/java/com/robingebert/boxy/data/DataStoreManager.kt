package com.robingebert.boxy.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class DataStoreManager(context: Context) {

    //region Boilerplate
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "preferences")
    }

    // DataStore-Instanz
    private val dataStore = context.dataStore

    inner class Preference<T>(
        keyName: String,
        defaultValue: T,
    ) : ReadOnlyProperty<DataStoreManager, PreferenceData<T>> {

        private val key: Preferences.Key<T> = when (defaultValue) {
            is String -> stringPreferencesKey(keyName)
            is Int -> intPreferencesKey(keyName)
            is Boolean -> booleanPreferencesKey(keyName)
            is Float -> floatPreferencesKey(keyName)
            is Long -> longPreferencesKey(keyName)
            else -> throw IllegalArgumentException("Unsupported type")
        } as Preferences.Key<T>

        private val preferenceData = PreferenceData(
            dataStore = dataStore,
            key = key,
            defaultValue = defaultValue,
            scope = CoroutineScope(Dispatchers.IO)
        )

        override fun getValue(thisRef: DataStoreManager, property: KProperty<*>): PreferenceData<T> {
            return preferenceData
        }
    }

    class PreferenceData<T>(
        private val dataStore: DataStore<Preferences>,
        private val key: Preferences.Key<T>,
        private val defaultValue: T,
        private val scope: CoroutineScope
    ) {
        val flow: StateFlow<T> = dataStore.data
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
            .stateIn(
                scope = scope,
                started = SharingStarted.Companion.Eagerly,
                initialValue = defaultValue
            )

        fun set(newValue: T) {
            scope.launch {
                dataStore.edit { preferences ->
                    preferences[key] = newValue
                }
            }
        }
    }
    //endregion

    //region Preferences
    val url by Preference("url", "")
    val username by Preference("username", "")
    val password by Preference("password", "")
    val isFirstRun by Preference("is_first_run", true)
    //endregion
}