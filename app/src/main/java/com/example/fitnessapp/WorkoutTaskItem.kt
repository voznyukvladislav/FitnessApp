package com.example.fitnessapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

open class WorkoutTaskItem {
    var workoutTaskId: Int = 0 // id in db
        get() = field
        set(value) { field = value }

    var workoutId: Int = 0 // id of workout
        get() = field
        set(value) { field = value }

    var workoutTaskName: String = "" // task name for example "pull ups"
        get() = field
        set(value) { field = value }

    var workoutTaskSets: Int = 0 // number of sets
        get() = field
        set(value) { field = value }

    var workoutTaskRepetitions: Int = 0 // number of repetitions in set
        get() = field
        set(value) { field = value }

    var workoutTaskRest: Int = 0 // time of rest (in seconds)
        get() = field
        set(value) { field = value }

    var workoutTaskOrderNum: Int = 0 // order in list
        get() = field
        set(value) { field = value }

    // Main constructor
    constructor(workoutTaskId: Int,
                workoutId: Int,
                workoutTaskName: String,
                workoutTaskSets: Int,
                workoutTaskRepetitions: Int,
                workoutTaskRest: Int,
                workoutTaskOrderNum: Int) {
        this.workoutTaskId = workoutTaskId
        this.workoutId = workoutId
        this.workoutTaskName = workoutTaskName
        this.workoutTaskSets = workoutTaskSets
        this.workoutTaskRepetitions = workoutTaskRepetitions
        this.workoutTaskRest = workoutTaskRest
        this.workoutTaskOrderNum = workoutTaskOrderNum
    }

    constructor() {
        workoutId = 0
        workoutTaskId = 0
        workoutTaskName = ""
        workoutTaskSets = 0
        workoutTaskRepetitions = 0
        workoutTaskRest = 0
        workoutTaskOrderNum = 0
    }

    // Getters
    /*fun getId(): Int {
        return this.workoutTaskId
    }
    fun getWorkoutId(): Int {
        return this.workoutId
    }
    fun getWorkoutTaskName(): String {
        return this.workoutTaskName
    }
    fun getWorkoutTaskSets(): Int {
        return this.workoutTaskSets
    }
    fun getWorkoutTaskRepetitions(): Int {
        return this.workoutTaskRepetitions
    }
    fun getWorkoutTaskRest(): Int {
        return this.workoutTaskRest
    }
    fun getWorkoutTaskOrderNum(): Int {
        return this.workoutTaskOrderNum
    }*/


}