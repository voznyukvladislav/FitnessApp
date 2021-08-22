package com.example.fitnessapp.sql

import android.database.sqlite.SQLiteDatabase

class SQLUpdater(db: SQLiteDatabase) {
    var db: SQLiteDatabase? = db
    get() = field
    private set(value) { field = value }

    fun updateWorkout(workoutId: Int, newWorkoutName: String) {
        db!!.execSQL("UPDATE workouts SET workoutName = '${newWorkoutName}' WHERE workoutId = ${workoutId}")
    }

    fun updateWorkoutTask(workoutTaskId: Int, newWorkoutTaskName: String) {
        db!!.execSQL("UPDATE workoutTasks SET workoutTaskName = '${newWorkoutTaskName}' WHERE workoutTaskId = ${workoutTaskId}")
    }

    fun updateWorkoutSet(workoutSetId: Int,
                         workoutSetRepetitions: Int,
                         workoutSetRest: Int,
                         workoutSetWeight: Int) {
        db!!.execSQL("UPDATE workoutSets SET workoutSetRepetitions = ${workoutSetRepetitions}," +
                " workoutSetRest = ${workoutSetRest}," +
                " workoutSetWeight = ${workoutSetWeight}" +
                " WHERE workoutSetId = ${workoutSetId}")
    }
}