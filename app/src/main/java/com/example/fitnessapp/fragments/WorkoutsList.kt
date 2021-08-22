package com.example.fitnessapp.fragments

import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.*
import com.example.fitnessapp.sql.SQLInserter
import com.example.fitnessapp.sql.SQLListGetter
import com.example.fitnessapp.workout_list.WorkoutListAdapter
import com.example.fitnessapp.workout_list.WorkoutListItem
import com.example.fitnessapp.workout_list.WorkoutListManageAdapter
import com.example.fitnessapp.workout_list.WorkoutsListPopupAddWindow
import com.google.android.material.floatingactionbutton.FloatingActionButton


class WorkoutsList : Fragment() {

    private lateinit var db: SQLiteDatabase

    private lateinit var navController: NavController

    private lateinit var dataSet: ArrayList<WorkoutListItem>
    private lateinit var workoutListRecyclerView: RecyclerView

    private lateinit var add: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_workouts_list, container, false)
        db = root.context.openOrCreateDatabase("workout.db", MODE_PRIVATE, null)
        createDatabaseTables(db)

        val listGetter = SQLListGetter(db)
        dataSet = listGetter.getWorkoutsListWithTotalTasks()

        navController = (activity as MainActivity?)!!.findNavController(R.id.screen)

        // Start recycler view setup
        workoutListRecyclerView = root.findViewById<RecyclerView>(R.id.workoutList)

        val manager = LinearLayoutManager(this.context)
        workoutListRecyclerView.layoutManager = manager

        val adapter = WorkoutListAdapter(dataSet, this.context, db, navController, root)
        workoutListRecyclerView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this.context, manager.orientation)
        workoutListRecyclerView.addItemDecoration(dividerItemDecoration)

        val callback = WorkoutListManageAdapter(adapter, this.requireContext(),
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT), db)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(workoutListRecyclerView)
        // End recycler view setup


        val workoutListPopupAddWindow = WorkoutsListPopupAddWindow(root)
        add = root.findViewById(R.id.addButton)

        // Floating action button click listener
        add.setOnClickListener {
            workoutListPopupAddWindow.acceptButton.setText("Add")
            workoutListPopupAddWindow.show()
        }

        // Adding new workout to database
        workoutListPopupAddWindow.acceptButton.setOnClickListener {
            if(!workoutListPopupAddWindow.workoutNameEditText.text.toString().isNullOrBlank()) {
                workoutListPopupAddWindow.add(db)
                workoutListPopupAddWindow.hide()

                val workoutListItem = SQLInserter(db).getInsertedWorkout()
                dataSet.add(workoutListItem)

                workoutListRecyclerView.adapter!!.notifyItemChanged(dataSet.size - 1)
                KeyboardHider(root).hideKeyboard()
            }
        }

        return root
    }

    private fun createDatabaseTables(db: SQLiteDatabase) {

        // Workout tables
        db.execSQL("CREATE TABLE IF NOT EXISTS workouts " +
                "(workoutId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "workoutName TEXT," +
                "workoutOrderNum INTEGER)")

        db.execSQL("CREATE TABLE IF NOT EXISTS workoutTasks " +
                "(workoutTaskId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "workoutId INTEGER," +
                "workoutTaskName TEXT," +
                "workoutTaskOrderNum INTEGER)")

        db.execSQL("CREATE TABLE IF NOT EXISTS workoutSets " +
                "(workoutSetId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "workoutTaskId INTEGER," +
                "workoutSetRepetitions INTEGER," +
                "workoutSetRest INTEGER," +
                "workoutSetOrderNum INTEGER," +
                "workoutSetWeight)")

        // Done workout tables
        db.execSQL("CREATE TABLE IF NOT EXISTS doneWorkouts " +
                "(doneWorkoutId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "doneWorkoutName TEXT)")

        db.execSQL("CREATE TABLE IF NOT EXISTS doneWorkoutTasks " +
                "(doneWorkoutTaskId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "doneWorkoutId INTEGER, " +
                "doneWorkoutTaskName TEXT)")

        db.execSQL("CREATE TABLE IF NOT EXISTS doneWorkoutSets " +
                "(doneWorkoutSetId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "doneWorkoutTaskId INTEGER, " +
                "doneWorkoutSetRepetitions INTEGER, " +
                "doneWorkoutSetRest INTEGER, " +
                "doneWorkoutSetWeight INTEGER)")

        // Calendar table
        db.execSQL("CREATE TABLE IF NOT EXISTS calendarDates " +
                "(calendarDateId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "calendarDateDay TEXT," +
                "calendarDateMonth TEXT," +
                "calendarDateYear TEXT)")

        // Table which connects done workout to some calendar date
        db.execSQL("CREATE TABLE IF NOT EXISTS doneWorkoutsCalendar " +
                "(doneWorkoutRecordId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "doneWorkoutId INTEGER," +
                "workoutDateId INTEGER)")
    }
}