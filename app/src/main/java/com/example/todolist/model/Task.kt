package com.example.todolist.model

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

//1
@Entity
class Task //constructor
    : Serializable {
    @PrimaryKey(autoGenerate = true)
    var taskId//trường TaskId là khóa chính và tự động tăng
            = 0

    @ColumnInfo(name = "taskTitle")
    var taskTitle: //khai báo tên cột
            String = ""

    @ColumnInfo(name = "date")
    var date: String = " "

    @ColumnInfo(name = "taskDescription")
    var taskDescrption: String? = " "

    @ColumnInfo(name = "isComplete")
    var isComplete = 0

    @ColumnInfo(name = "lastAlarm")
    var lastAlarm: String = " "
}