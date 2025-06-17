package com.example.zt_datastore

import android.app.Application
import android.content.Context

/**
 * @author: zeting
 * @date: 2025/6/16
 *
 */
class MyApplication : Application() {

    // 伴生对象，用于存放静态成员（属性和方法）
    companion object {
        // 静态属性，通常用于存储Application实例，方便全局访问
        private var instance: MyApplication? = null

        // 静态方法，用于获取Application实例
        fun getInstance(): MyApplication {
            return instance as MyApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this // 在onCreate中初始化静态实例
    }
}