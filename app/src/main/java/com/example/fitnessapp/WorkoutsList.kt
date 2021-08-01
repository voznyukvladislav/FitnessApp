package com.example.fitnessapp

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class WorkoutsList : Fragment() {

    private lateinit var db: SQLiteDatabase

    private lateinit var navController: NavController

    private lateinit var dataSet: ArrayList<WorkoutListItem>
    private lateinit var workoutListRecyclerView: RecyclerView

    private lateinit var mainScreen: ConstraintLayout
    private lateinit var addWindow: ConstraintLayout

    private lateinit var newWorkoutName: EditText

    private lateinit var addWorkout: Button
    private lateinit var cancel: Button
    private lateinit var add: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_training_list, container, false)
        db = root.context.openOrCreateDatabase("workout.db", MODE_PRIVATE, null)
        createDatabaseTables(db)

        val listGetter = ListGetter(db)
        dataSet = listGetter.getWorkoutsList()

        navController = (activity as MainActivity?)!!.findNavController(R.id.screen)

        // TextEdit new workout name
        newWorkoutName = root.findViewById(R.id.newWorkoutName)

        // Start recycler view setup
        workoutListRecyclerView = root.findViewById<RecyclerView>(R.id.workoutList)

        val manager = LinearLayoutManager(this.context)
        workoutListRecyclerView.layoutManager = manager

        val adapter = WorkoutListAdapter(dataSet, this.context, db, navController)
        workoutListRecyclerView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this.context, manager.orientation)
        workoutListRecyclerView.addItemDecoration(dividerItemDecoration)

        val callback = WorkoutListManageAdapter(adapter, this.requireContext(),
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT), db)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(workoutListRecyclerView)
        // End recycler view setup

        var isOpenedAddWindow: Boolean = false

        // Windows declaration
        mainScreen = root.findViewById(R.id.trainingListMainScreen)
        addWindow = root.findViewById(R.id.workoutTasksListPopupWindow)

        // Buttons declaration
        addWorkout = root.findViewById(R.id.addWorkout)
        cancel = root.findViewById(R.id.cancel)
        add = root.findViewById(R.id.addButton)

        // Animations declaration
        val animationHide: Animation = AnimationUtils.loadAnimation(root.context, R.anim.popup_hide)
        val animationShow: Animation = AnimationUtils.loadAnimation(root.context, R.anim.popup_show)

        // Main screen listener
        mainScreen.setOnClickListener {
            if(isOpenedAddWindow == true) {
                isOpenedAddWindow = false
                addWindow.startAnimation(animationHide)
                //addWindow.visibility = View.GONE
            }
        }

        // Add window listener
        addWindow.setOnClickListener {
            hideKeyboard()
        }

        // Hide animation listener
        animationHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                hideKeyboard()
            }
            override fun onAnimationRepeat(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {
                newWorkoutName.setText("")
                addWindow.visibility = View.GONE
            }
        })

        // Floating action button click listener
        add.setOnClickListener {
            if(isOpenedAddWindow == false) {
                isOpenedAddWindow = true
                addWindow.visibility = View.VISIBLE
                addWindow.startAnimation(animationShow)
            }
        }

        // Adding new workout to database
        addWorkout.setOnClickListener {
            if(!newWorkoutName.text.toString().isNullOrBlank()) {
                isOpenedAddWindow = false

                db.execSQL("INSERT INTO workouts VALUES(NULL, '${newWorkoutName.text.toString()}', (SELECT MAX(workoutOrderNum) + 1 FROM Workouts), 0)")

                val cursor = db.rawQuery("SELECT MAX(workoutId) FROM Workouts", null)
                var workoutId: Int = 0
                if(cursor.moveToFirst()) {
                    workoutId = cursor.getInt(0)
                }
                cursor.close()
                val workoutListItem = WorkoutListItem(workoutId, newWorkoutName.text.toString()) // TODO rework order num
                dataSet.add(workoutListItem)

                workoutListRecyclerView.adapter!!.notifyItemChanged(dataSet.size - 1)
                addWindow.startAnimation(animationHide)
            }
        }

        // Cancel click listener
        cancel.setOnClickListener {
            isOpenedAddWindow = false
            addWindow.startAnimation(animationHide)
        }

        return root
    }

    private fun createDatabaseTables(db: SQLiteDatabase) {

        // Workout tables
        db.execSQL("CREATE TABLE IF NOT EXISTS workouts " +
                "(workoutId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "workoutName TEXT," +
                "workoutOrderNum INTEGER," +
                "isDeleted INTEGER)")

        db.execSQL("CREATE TABLE IF NOT EXISTS workoutTasks " +
                "(workoutTaskId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "workoutId INTEGER," +
                "workoutTaskName TEXT," +
                "workoutTaskOrderNum INTEGER," +
                "isDeleted INTEGER)")

        db.execSQL("CREATE TABLE IF NOT EXISTS workoutSets " +
                "(workoutSetId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "workoutTaskId INTEGER," +
                "workoutSetRepetitions INTEGER," +
                "workoutSetRest INTEGER," +
                "workoutSetOrderNum INTEGER," +
                "workoutSetWeight," +
                "isDeleted INTEGER)")

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

    private fun hideKeyboard() {
        val view: View? = this.view
        if(view != null) {
            val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}