package com.example.todolist.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolist.database.TaskDao
import com.example.todolist.database.TaskDatabase
import kotlin.jvm.Synchronized
import androidx.room.Room
import com.example.todolist.model.Task

//3
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        private const val DATABASE_NAME = "Task.db"
        private var mInstance: TaskDatabase? = null
        @JvmStatic
        @Synchronized
        fun getInstance(mCtx: Context?): TaskDatabase {
            if (mInstance == null) {
                mInstance = Room.databaseBuilder(
                    mCtx!!,
                    TaskDatabase::class.java,
                    DATABASE_NAME
                ) //biến môi trường- class RoomDatabase- Database Name
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return mInstance!!
        }
    }
}