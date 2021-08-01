package com.example.fitnessapp

private val TYPE_HEADER = 1
private val TYPE_ITEM = 2
private val TYPE_SAVE_BUTTON = 3

class FitnessListItem {
    var workoutTaskName: String = ""
        get() = field
        private set(value) { field = value }

    var workoutRepetitions: Int = 0
        get() = field
        private set(value) { field = value }

    var workoutRest: Int = 0
        get() = field
        private set(value) { field = value }

    var workoutSetWeight: Int = 0
        get() = field
        private set(value) { field = value }

    var inListType: Int = 0
        get() = field
        private set(value) { field = value }

    // If inputting ony name in list type will be TYPE_HEADER
    constructor(workoutTaskName: String) {
        this.workoutTaskName = workoutTaskName
        this.inListType = TYPE_HEADER
    }

    // If inputting few parameters in list type will be TYPE_ITEM
    constructor(workoutRepetitions: Int, workoutRest: Int, workoutSetWeight: Int) {
        this.workoutRepetitions = workoutRepetitions
        this.workoutRest = workoutRest
        this.workoutSetWeight = workoutSetWeight
        this.inListType = TYPE_ITEM
    }

    // Constructor without arguments for button
    constructor() {
        this.inListType = TYPE_SAVE_BUTTON
    }
}