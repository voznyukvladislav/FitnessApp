package com.example.fitnessapp

class WorkoutSetsListItem {
    var workoutSetId: Int = 0
        get() = field
        set(value) { field = value }

    var workoutSetRepetitions: Int = 0
        get() = field
        set(value) { field = value }

    var workoutSetRest: Int = 0
        get() = field
        set(value) { field = value }

    var workoutSetOrderNum: Int = 0
        get() = field
        set(value) { field = value }

    var workoutSetWeight: Int = 0
        get() = field
        set(value) { field = value }

    constructor(workoutSetId: Int,
                workoutSetRepetitions: Int,
                workoutSetRest: Int,
                workoutSetOrderNum: Int,
                workoutSetWeight: Int) {
        this.workoutSetId = workoutSetId
        this.workoutSetRepetitions = workoutSetRepetitions
        this.workoutSetRest = workoutSetRest
        this.workoutSetOrderNum = workoutSetOrderNum
        this.workoutSetWeight = workoutSetWeight
    }

    constructor()
}