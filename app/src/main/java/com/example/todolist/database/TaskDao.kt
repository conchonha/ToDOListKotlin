package com.example.todolist.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolist.model.Task

//2
@Dao
interface TaskDao {
    @get:Query("SELECT * FROM Task")
    val allTasksList: List<Task>

    //    @Query("DELETE FROM Task")
    //    void truncateTheList();
    @Insert
    fun insertDataIntoTaskList(task: Task?)

    @Query("DELETE FROM Task WHERE taskId = :taskId")
    fun deleteTaskFromId(taskId: Int)

    @Query("SELECT * FROM Task WHERE taskId = :taskId")
    fun selectDataFromAnId(taskId: Int): Task

    @Query(
        "UPDATE Task SET taskTitle = :taskTitle, taskDescription = :taskDescription, date = :taskDate, " +
                "lastAlarm = :taskTime WHERE taskId = :taskId"
    )
    fun updateAnExistingRow(
        taskId: Int,
        taskTitle: String?,
        taskDescription: String?,
        taskDate: String?,
        taskTime: String?
    )

    @Query("UPDATE Task SET isComplete = :taskStatus WHERE taskId = :taskId")
    fun updateStatusRow(taskId: Int, taskStatus: Int)
}