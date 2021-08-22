package com.example.fitnessapp.workout_tasks_list

class WorkoutTasksListItem {
    var workoutTasksListItemId: Int = 0
        get() = field
        set(value) { field = value }

    var workoutTasksListItemName: String = ""
        get() = field
        set(value) { field = value }

    var workoutTasksListItemTotalSets: Int = 0
        get() = field
        set(value) { field = value }

    var workoutTasksListItemTotalRepetitions: Int = 0
        get() = field
        set(value) { field = value }

    constructor(workoutTasksListItemId: Int,
                workoutTasksListItemName: String,
                workoutTasksListItemTotalSets: Int,
                workoutTasksListItemTotalRepetitions: Int) {
        this.workoutTasksListItemId = workoutTasksListItemId
        this.workoutTasksListItemName = workoutTasksListItemName
        this.workoutTasksListItemTotalSets = workoutTasksListItemTotalSets
        this.workoutTasksListItemTotalRepetitions = workoutTasksListItemTotalRepetitions
    }

    constructor(workoutTasksListItemId: Int, workoutTasksListItemName: String) {
        this.workoutTasksListItemId = workoutTasksListItemId
        this.workoutTasksListItemName = workoutTasksListItemName
    }

    constructor()
}