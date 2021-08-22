package com.example.fitnessapp.sql

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.fitnessapp.DoneWorkoutListItem
import com.example.fitnessapp.workout_list.WorkoutListItem
import com.example.fitnessapp.workout_sets_list.WorkoutSetsListItem
import com.example.fitnessapp.workout_tasks_list.WorkoutTasksListItem

class SQLListGetter {
    var db: SQLiteDatabase? = null
    get() = field
    private set(value) { field = value }

    constructor(db: SQLiteDatabase) {
        this.db = db
    }

    fun getWorkoutsList(): ArrayList<WorkoutListItem> {
        val dataSet: ArrayList<WorkoutListItem> = arrayListOf()
        val query = (this.db!!).rawQuery("SELECT * FROM workouts ORDER BY workoutOrderNum", null)

        while(query.moveToNext()) {
            // Creates new WorkoutListItem object with id and name
            dataSet.add(WorkoutListItem(query.getString(0).toString().toInt(), query.getString(1).toString()))
        }
        query.close()
        return dataSet
    }

    fun getWorkoutsListWithTotalTasks(): ArrayList<WorkoutListItem> {
        val dataSet: ArrayList<WorkoutListItem> = arrayListOf()
        val workoutCursor = (this.db!!).rawQuery("SELECT * FROM workouts ORDER BY workoutOrderNum", null)

        while(workoutCursor.moveToNext()) {
            val workoutId = workoutCursor.getInt(0)
            val workoutName = workoutCursor.getString(1)

            val workoutTasksCursor = (this.db)!!.rawQuery("SELECT COUNT(workoutTaskId) FROM workoutTasks WHERE workoutId = ${workoutId}", null)
            var workoutTotalTasks = 0
            while(workoutTasksCursor.moveToNext()) {
                workoutTotalTasks = workoutTasksCursor.getInt(0)
            }
            workoutTasksCursor.close()

            dataSet.add(WorkoutListItem(workoutId, workoutName, workoutTotalTasks))
        }
        workoutCursor.close()
        return dataSet
    }

    fun getWorkoutTasksList(workoutId: Int): ArrayList<WorkoutTasksListItem> {
        val dataSet: ArrayList<WorkoutTasksListItem> = arrayListOf()

        val workoutTasksCursor = this.db!!.rawQuery("SELECT workoutTaskId, workoutTaskName FROM workoutTasks WHERE workoutId = ${workoutId} ORDER BY workoutTaskOrderNum", null)

        var workoutTaskId = 0
        var workoutTaskName = ""
        var workoutSetsQuantity = 0
        var workoutTaskRepetitionsQuantity = 0

        var workoutSetsCursor: Cursor

        while(workoutTasksCursor.moveToNext()) {
            workoutTaskId = workoutTasksCursor.getInt(0)
            workoutTaskName = workoutTasksCursor.getString(1)

            workoutSetsCursor = this.db!!.rawQuery("SELECT COUNT(workoutSetId) FROM workoutSets WHERE workoutTaskId = ${workoutTaskId}", null)
            while(workoutSetsCursor.moveToNext()) {
                workoutSetsQuantity = workoutSetsCursor.getInt(0)
            }

            workoutSetsCursor = this.db!!.rawQuery("SELECT SUM(workoutSetRepetitions) FROM workoutSets WHERE workoutTaskId = ${workoutTaskId}", null)
            while(workoutSetsCursor.moveToNext()) {
                workoutTaskRepetitionsQuantity = workoutSetsCursor.getInt(0)
            }
            workoutSetsCursor.close()

            dataSet.add(WorkoutTasksListItem(workoutTaskId, workoutTaskName, workoutSetsQuantity, workoutTaskRepetitionsQuantity))
        }
        workoutTasksCursor.close()

        return dataSet
    }

    fun getWorkoutSetsList(workoutTaskId: Int): ArrayList<WorkoutSetsListItem> {
        val dataSet: ArrayList<WorkoutSetsListItem> = arrayListOf()

        val cursor = this.db!!.rawQuery("SELECT workoutSetId, workoutSetRepetitions, workoutSetRest, workoutSetOrderNum, workoutSetWeight " +
                "FROM workoutSets WHERE workoutTaskId = ${workoutTaskId} ORDER BY workoutSetOrderNum", null)

        var workoutSetId: Int = 0
        var workoutSetRepetitions: Int = 0
        var workoutSetRest: Int = 0
        var workoutSetWeight: Int = 0
        while(cursor.moveToNext()) {
            workoutSetId = cursor.getInt(0)
            workoutSetRepetitions = cursor.getInt(1)
            workoutSetRest = cursor.getInt(2)
            workoutSetWeight = cursor.getInt(3)

            val item = WorkoutSetsListItem(workoutSetId, workoutSetRepetitions, workoutSetRest, workoutSetWeight)
            dataSet.add(item)
        }
        cursor.close()

        return dataSet
    }

    fun getDoneWorkoutsFromDate(dateId: Int): ArrayList<DoneWorkoutListItem> {
        val doneWorkoutIdArrayList: ArrayList<Int> = arrayListOf()

        // Id's of related to chosen date done workouts
        val doneWorkoutIdCursor = this.db!!.rawQuery("SELECT doneWorkoutId FROM doneWorkoutsCalendar WHERE workoutDateId = ${dateId}", null)
        while(doneWorkoutIdCursor.moveToNext()) {
            doneWorkoutIdArrayList.add(doneWorkoutIdCursor.getInt(0))
        }
        doneWorkoutIdCursor.close()

        val doneWorkoutsArrayList: ArrayList<DoneWorkoutListItem> = arrayListOf()
        for(i in 0 until doneWorkoutIdArrayList.size) {
            val doneWorkoutsCursor = this.db!!.rawQuery("SELECT * FROM doneWorkouts WHERE doneWorkoutId = ${doneWorkoutIdArrayList[i]}", null)
            while(doneWorkoutsCursor.moveToNext()) {
                val doneWorkouItem = DoneWorkoutListItem(doneWorkoutsCursor.getInt(0),
                    doneWorkoutsCursor.getString(1))
                doneWorkoutsArrayList.add(doneWorkouItem)
            }
            doneWorkoutsCursor.close()
        }

        return doneWorkoutsArrayList
    }
}