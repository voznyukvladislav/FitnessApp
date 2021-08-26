package com.example.fitnessapp.done_workout_tasks_list

class DoneWorkoutTasksListItem {
    var doneWorkoutTaskId: Int = 0
        get() = field
        set(value) { field = value }

    var doneWorkoutTaskName: String = ""
        get() = field
        set(value) { field = value }

    var doneWorkoutTaskTotalRepetitions: Int = 0
        get() = field
        set(value) { field = value }

    var doneWorkoutTaskTotalSets: Int = 0
        get() = field
        set(value) { field = value }

    constructor(doneWorkoutTaskId: Int,
                doneWorkoutTaskName: String,
                doneWorkoutTaskTotalRepetitions: Int,
                doneWorkoutTaskTotalSets: Int) {
        this.doneWorkoutTaskId = doneWorkoutTaskId
        this.doneWorkoutTaskName = doneWorkoutTaskName
        this.doneWorkoutTaskTotalRepetitions = doneWorkoutTaskTotalRepetitions
        this.doneWorkoutTaskTotalSets = doneWorkoutTaskTotalSets
    }

    constructor(doneWorkoutTaskId: Int, doneWorkoutTaskName: String) {
        this.doneWorkoutTaskId = doneWorkoutTaskId
        this.doneWorkoutTaskName = doneWorkoutTaskName
    }
}