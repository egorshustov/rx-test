package com.egorshustov.rxtest

object DataSource {
    fun createTasksList(): List<Task> {
        val tasksList = mutableListOf<Task>()
        tasksList.add(Task("Take out the trash", true, 3))
        tasksList.add(Task("Walk the dog", false, 2))
        tasksList.add(Task("Make my bed", true, 1))
        tasksList.add(Task("Unload the dishwasher", false, 0))
        tasksList.add(Task("Make dinner", true, 7))
        tasksList.add(Task("Make dinner", true, 8))
        tasksList.add(Task("Make dinner", true, 9))
        tasksList.add(Task("Walk the dog", false, 2))
        return tasksList
    }

    fun createTasksArray(): Array<Task?> {
        val tasksArray = arrayOfNulls<Task>(6)
        tasksArray[0] = Task("Take out the trash", true, 3)
        tasksArray[1] = Task("Walk the dog", false, 2)
        tasksArray[2] = Task("Make my bed", true, 1)
        tasksArray[3] = Task("Unload the dishwasher", false, 0)
        tasksArray[4] = Task("Make dinner", true, 7)
        tasksArray[5] = Task("Make dinner", true, 8)
        tasksArray[6] = Task("Make dinner", true, 9)
        tasksArray[7] = Task("Walk the dog", false, 2)
        return tasksArray
    }
}