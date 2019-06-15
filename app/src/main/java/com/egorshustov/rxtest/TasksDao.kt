package com.egorshustov.rxtest

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TasksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllTasks(userList: List<Task>)

    @Query("select * from tasks")
    fun getAllTasks(): List<Task>
}