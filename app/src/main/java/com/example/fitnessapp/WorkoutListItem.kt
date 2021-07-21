package com.example.fitnessapp

class WorkoutListItem {
    private var id: Int = 0
    private var workoutName: String = ""
    private var color: String = "" // Will be added in future


    // Constructor to get data from db
    constructor(id: Int, workoutName: String) {
        this.id = id
        this.workoutName = workoutName
    }

    // Getters
    fun getId(): Int {
        return this.id
    }
    /*fun getId(db: SQLiteDatabase): Int {
        var cursor: Cursor = db.rawQuery("SELECT workoutId FROM Workouts WHERE name = '${this.getName()}'", null)
        if(cursor.moveToFirst()) {
            return cursor.getString(0).toInt()
        }
        cursor.close()
        return 0
    }*/
    fun getName(): String {
        return this.workoutName
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