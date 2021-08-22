package com.example.fitnessapp.sql

import android.database.sqlite.SQLiteDatabase
import com.example.fitnessapp.DoneWorkoutListItem
import com.example.fitnessapp.DoneWorkoutSetsListItem
import com.example.fitnessapp.DoneWorkoutTasksListItem
import com.example.fitnessapp.workout_list.WorkoutListItem
import com.example.fitnessapp.workout_sets_list.WorkoutSetsListItem
import com.example.fitnessapp.workout_tasks_list.WorkoutTasksListItem

class SQLInserter {
    var db: SQLiteDatabase? = null
    get() = field
    private set(value) { field = value }

    constructor(db: SQLiteDatabase) {
        this.db = db
    }

    // Insertion into db
    // Inserts new workout
    fun insertWorkout(workoutName: String) {
        db!!.execSQL("INSERT INTO workouts VALUES(NULL, '${workoutName}', (SELECT MAX(workoutOrderNum) + 1 FROM Workouts), 0)")
    }

    // Inserts new workout task
    fun insertWorkoutTask(workoutId: Int, workoutTaskName: String) {
        db!!.execSQL("INSERT INTO workoutTasks VALUES (NULL, ${workoutId}, '${workoutTaskName}', 0, 0)")
    }

    // Inserts new workout set
    fun insertWorkoutSet(workoutTaskId: Int,
                         workoutSetRepetitions: Int,
                         workoutSetRest: Int,
                         workoutSetWeight: Int) {
        db!!.execSQL("INSERT INTO workoutSets VALUES (NULL, " +
                "${workoutTaskId}, " +
                "${workoutSetRepetitions}, " +
                "${workoutSetRest}, " +
                "0," +
                "${workoutSetWeight}," +
                "0)")
    }

    // Inserts done workout to doneWorkouts table
    fun insertDoneWorkout(db: SQLiteDatabase, doneWorkoutName: String) {
        db.execSQL("INSERT INTO doneWorkouts VALUES (NULL, '${doneWorkoutName}')")
    }

    // Inserts done workout task to doneWorkoutTasks table
    fun insertDoneWorkoutTask(db: SQLiteDatabase, doneWorkoutId: Int, doneWorkoutTaskName: String) {
        db.execSQL("INSERT INTO doneWorkoutTasks VALUES (NULL, ${doneWorkoutId}, '${doneWorkoutTaskName}')")
    }

    // Inserts done workout set to doneWorkoutSets table
    fun insertDoneWorkoutSet(db: SQLiteDatabase,
                                     doneWorkoutTaskId: Int,
                                     doneWorkoutSetRepetitions: Int,
                                     doneWorkoutSetRest: Int,
                                     doneWorkoutSetWeight: Int) {
        db.execSQL("INSERT INTO doneWorkoutSets VALUES(NULL, ${doneWorkoutTaskId}, ${doneWorkoutSetRepetitions}, ${doneWorkoutSetRest}, ${doneWorkoutSetWeight})")
    }


    // Get inserted from db
    fun getInsertedWorkout(): WorkoutListItem {
        var workoutCursor = db!!.rawQuery("SELECT MAX(workoutId) FROM workouts", null)
        var workoutId: Int = 0
        if (workoutCursor.moveToFirst()) {
            workoutId = workoutCursor.getInt(0)
        }
        workoutCursor.close()

        workoutCursor =
            db!!.rawQuery("SELECT workoutName FROM workouts WHERE workoutId = ${workoutId}", null)
        var workoutName = ""
        while (workoutCursor.moveToNext()) {
            workoutName = workoutCursor.getString(0)
        }
        workoutCursor.close()

        return WorkoutListItem(workoutId, workoutName)
    }

    fun getInsertedWorkoutTask(): WorkoutTasksListItem {
        var workoutTaskCursor = db!!.rawQuery("SELECT MAX(workoutTaskId) FROM workoutTasks", null)
        var workoutTaskId: Int = 0
        if (workoutTaskCursor.moveToFirst()) {
            workoutTaskId = workoutTaskCursor.getInt(0)
        }
        workoutTaskCursor.close()

        workoutTaskCursor = db!!.rawQuery(
            "SELECT workoutTaskName FROM workoutTasks WHERE workoutTaskId = ${workoutTaskId}",
            null
        )
        var workoutTaskName = ""
        while (workoutTaskCursor.moveToNext()) {
            workoutTaskName = workoutTaskCursor.getString(0)
        }
        workoutTaskCursor.close()

        return WorkoutTasksListItem(workoutTaskId, workoutTaskName)
    }

    fun getInsertedWorkoutSet(): WorkoutSetsListItem {
        var workoutSetCursor = db!!.rawQuery("SELECT MAX(workoutSetId) FROM workoutSets", null)
        var workoutSetId: Int = 0
        if (workoutSetCursor.moveToFirst()) {
            workoutSetId = workoutSetCursor.getInt(0)
        }
        workoutSetCursor.close()

        workoutSetCursor = db!!.rawQuery("SELECT workoutSetRepetitions, workoutSetRest, workoutSetWeight FROM workoutSets WHERE workoutSetId = ${workoutSetId}", null)
        var workoutSetRepetitions = 0
        var workoutSetRest = 0
        var workoutSetWeight = 0
        while(workoutSetCursor.moveToNext()) {
            workoutSetRepetitions = workoutSetCursor.getInt(0)
            workoutSetRest = workoutSetCursor.getInt(1)
            workoutSetWeight = workoutSetCursor.getInt(2)
        }
        workoutSetCursor.close()

        return WorkoutSetsListItem(workoutSetId, workoutSetRepetitions, workoutSetRest, workoutSetWeight)
    }

    fun getInsertedDoneWorkout(): DoneWorkoutListItem {
        val doneWorkoutsCursor = db!!.rawQuery("SELECT * FROM doneWorkouts WHERE doneWorkoutId = (SELECT MAX(doneWorkoutId) FROM doneWorkouts)", null)
        var doneWorkoutItem = DoneWorkoutListItem()
        while(doneWorkoutsCursor.moveToNext()) {
            doneWorkoutItem = DoneWorkoutListItem(doneWorkoutsCursor.getInt(0), doneWorkoutsCursor.getString(1))
        }
        doneWorkoutsCursor.close()
        return doneWorkoutItem
    }

    fun getInsertedDoneWorkoutTask(): DoneWorkoutTasksListItem {
        val doneWorkoutTasksCursor = db!!.rawQuery("SELECT doneWorkoutTaskId, doneWorkoutTaskName FROM doneWorkoutTasks WHERE doneWorkoutTaskId = " +
                "(SELECT MAX(doneWorkoutTaskId) FROM doneWorkoutTasks)", null)
        var doneWorkoutTask = DoneWorkoutTasksListItem()
        while (doneWorkoutTasksCursor.moveToNext()) {
            doneWorkoutTask = DoneWorkoutTasksListItem(doneWorkoutTasksCursor.getInt(0), doneWorkoutTasksCursor.getString(1))
        }
        doneWorkoutTasksCursor.close()

        return doneWorkoutTask
    }

    fun getInsertedDoneWorkoutSet(): DoneWorkoutSetsListItem {
        val doneWorkoutSetCursor = db!!.rawQuery("SELECT doneWorkoutSetId, doneWorkoutSetRepetitions, doneWorkoutSetRest, doneWorkoutSetWeight FROM doneWorkoutSets " +
                "WHERE doneWorkoutSetId = (SELECT MAX(doneWorkoutSetId) FROM doneWorkoutSets)", null)
        var doneWorkoutSetItem = DoneWorkoutSetsListItem()
        while(doneWorkoutSetCursor.moveToNext()) {
            doneWorkoutSetItem = DoneWorkoutSetsListItem(doneWorkoutSetCursor.getInt(0),
                doneWorkoutSetCursor.getInt(1),
                doneWorkoutSetCursor.getInt(2),
                doneWorkoutSetCursor.getInt(3))
        }
        doneWorkoutSetCursor.close()

        return doneWorkoutSetItem
    }
}