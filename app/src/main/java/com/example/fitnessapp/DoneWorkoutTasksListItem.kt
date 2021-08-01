package com.example.fitnessapp

class DoneWorkoutTasksListItem {
    var doneWorkoutTaskListItemId: Int = 0
    get() = field
    private set(value) { field = value }

    var doneWorkoutTaskListItemName: String = ""
    get() = field
    private set(value ) { field = value }

    constructor()

    constructor(doneWorkoutTaskListItemId: Int, doneWorkoutTaskListItemName: String) {
        this.doneWorkoutTaskListItemId = doneWorkoutTaskListItemId
        this.doneWorkoutTaskListItemName = doneWorkoutTaskListItemName
    }
}