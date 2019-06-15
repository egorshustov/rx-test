package com.egorshustov.rxtest

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    var description: String,
    @ColumnInfo(name = "is_complete")
    var isComplete: Boolean,
    var priority: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}