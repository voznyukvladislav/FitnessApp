package com.example.fitnessapp.workout_list

class WorkoutListItem {
    private var id: Int = 0
    private var workoutName: String = ""
    private var workoutTotalTasks = 0
    private var color: String = "" // Will be added in future


    // Constructor to get data from db
    constructor(id: Int, workoutName: String) {
        this.id = id
        this.workoutName = workoutName
    }

    constructor(id: Int, workoutName: String, workoutTotalTasks: Int) {
        this.id = id
        this.workoutName = workoutName
        this.workoutTotalTasks = workoutTotalTasks
    }

    // Getters
    fun getId(): Int {
        return this.id
    }

    fun getName(): String {
        return this.workoutName
    }
    fun getTotalTasks(): Int {
        return this.workoutTotalTasks
    }
    fun getColor(): String {
        return this.color
    }


    // Setters
    fun setId(newId: Int) {
        this.id = newId
    }
    fun setName(newName: String) {
        this.workoutName = newName
    }
    fun setColor(newColor: String) {
        this.color = newColor
    }
}