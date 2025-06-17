package com.example.zt_datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


/**
 * @author: zeting
 * @date: 2025/6/16
 *
 */
// 定义 DataStore 名称
private const val USER_PREFERENCES_NAME = "user_preferences"

// 定义旧的 SharedPreferences 文件名 (如果存在)
private const val OLD_SHARED_PREFS_NAME = "my_old_shared_prefs" // 替换为你旧的 SharedPreferences 文件名

// 通过属性委托创建 DataStore 实例，并添加迁移支持
val Context.stringDataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME,
    // 添加 CorruptionHandler 来处理文件损坏
    corruptionHandler = ReplaceFileCorruptionHandler(
        produceNewData = {
            // 当文件损坏时，这里会创建一个新的空 Preferences 对象
            // 相当于清空了 DataStore
            // 你也可以在这里提供一些默认值
            emptyPreferences()
        }
    )
)

class StringDataStoreManager(private val context: Context) {
    /**
     * 保存一个 String 值到 DataStore
     * @param key 用于存储的 Preferences Key
     * @param value 要保存的 String 值
     */
    suspend fun saveString(key: Preferences.Key<String>, value: String) {
        context.stringDataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    /**
     * 从 DataStore 中读取一个 String 值
     * 返回一个 Flow，它会发出最新的值。当 DataStore 中的值发生变化时，Flow 会自动更新。
     * @param key 要读取的 Preferences Key
     * @param defaultValue 如果找不到该 key 对应的值，则返回的默认值
     */
    fun getString(key: Preferences.Key<String>, defaultValue: String = ""): Flow<String> {
        return context.stringDataStore.data
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    /**
     * 移除一个 String 值
     * @param key 要移除的 Preferences Key
     */
    suspend fun removeString(key: Preferences.Key<String>) {
        context.stringDataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}