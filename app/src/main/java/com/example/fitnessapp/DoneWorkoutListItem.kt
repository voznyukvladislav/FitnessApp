package com.example.fitnessapp

open class DoneWorkoutListItem {
    var doneWorkoutId: Int = 0
    get() = field
    private set(value) { field = value }

    var doneWorkoutName: String = ""
    get() = field
    private set(value) { field = value }

    constructor()

    constructor(doneWorkoutId: Int, doneWorkoutName: String) {
        this.doneWorkoutId = doneWorkoutId
        this.doneWorkoutName = doneWorkoutName
    }
}