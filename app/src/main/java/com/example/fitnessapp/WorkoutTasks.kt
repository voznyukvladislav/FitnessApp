package com.example.fitnessapp

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WorkoutTasks : Fragment() {

    private lateinit var workoutName: TextView // Header

    private lateinit var db: SQLiteDatabase // Database

    private lateinit var workoutTasksRecyclerView: RecyclerView // Workout tasks list

    private lateinit var dataSet: ArrayList<WorkoutTaskItem> // Data for workout tasks list

    private lateinit var addWorkoutTask: FloatingActionButton // Button to open adding workout task window

    private lateinit var addWorkoutTaskWindow: ConstraintLayout // Adding workout task window

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_workout_tasks, container, false)

        //var activity = root.getRootView()
        //var bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottomMenu)

        db = root.context.openOrCreateDatabase("workout.db", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS WorkoutTasks (" +
                "workoutTaskId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "workoutId INTEGER, " +
                "workoutTaskName TEXT, " +
                "workoutTaskSets INTEGER, " +
                "workoutTaskRepetitions INTEGER, " +
                "workoutTaskRest INTEGER, " +
                "workoutTaskOrderNum INTEGER)")

        val workoutId = arguments?.getInt("workoutId")!!
        dataSet = fillDataSet(db, workoutId)

        // Start recycler view setup
        workoutTasksRecyclerView = root.findViewById(R.id.workoutTasksRecyclerView)
        val manager = LinearLayoutManager(this.context)
        workoutTasksRecyclerView.layoutManager = manager

        val adapter = WorkoutTasksAdapter(dataSet, this.context, db)
        workoutTasksRecyclerView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this.context, manager.orientation)
        workoutTasksRecyclerView.addItemDecoration(dividerItemDecoration)

        val callback = WorkoutTasksManageAdapter(adapter, this.requireContext(),
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT), db)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(workoutTasksRecyclerView)
        // End recycler view setup

        addWorkoutTaskWindow = root.findViewById(R.id.addWorkoutTaskWindow)
        addWorkoutTask = root.findViewById(R.id.addWorkoutTask)

        addWorkoutTaskWindow.setOnClickListener {
            hideKeyboard()
        }

        var isOpenedAddWindow: Boolean = false
        val animationShow = AnimationUtils.loadAnimation(root.context, R.anim.popup_show)
        val animationHide = AnimationUtils.loadAnimation(root.context, R.anim.popup_hide)
        addWorkoutTask.setOnClickListener {
            if(!isOpenedAddWindow) {
                addWorkoutTaskWindow.visibility = View.VISIBLE
                addWorkoutTaskWindow.startAnimation(animationShow)
                isOpenedAddWindow = true
            }
        }

        val addWorkoutTaskButton: Button = root.findViewById(R.id.addWorkoutTaskButton)
        val cancelButton: Button = root.findViewById(R.id.cancelWorkoutTaskButton)

        addWorkoutTaskButton.setOnClickListener {
            val workoutTaskNameTextEdit: EditText = root.findViewById(R.id.workoutTaskNameEditText)
            val setsEditText: EditText = root.findViewById(R.id.setsEditText)
            val repetitionEditText: EditText = root.findViewById(R.id.repetitionsEditText)
            val restEditText: EditText = root.findViewById(R.id.restEditText)
            db.execSQL("INSERT INTO WorkoutTasks VALUES" +
                    "(NULL, ${workoutId}, '${workoutTaskNameTextEdit.text.toString()}', ${setsEditText.text.toString()}, " +
                    "${repetitionEditText.text.toString()}, ${restEditText.text.toString()}, " +
                    "(SELECT MAX(workoutTaskOrderNum) FROM WorkoutTasks WHERE workoutId = ${workoutId}) + 1)")
            val cursor = db.rawQuery("SELECT MAX(workoutTaskId) FROM WorkoutTasks", null)
            var workoutTaskId: Int = 0
            if(cursor.moveToFirst()) {
                workoutTaskId = cursor.getInt(0)
            }
            cursor.close()
            val workoutTaskItem = WorkoutTaskItem(workoutTaskId, workoutId, workoutTaskNameTextEdit.text.toString(),
                    setsEditText.text.toString().toInt(), repetitionEditText.text.toString().toInt(),
                    restEditText.text.toString().toInt(), 1) // TODO rework order num
            dataSet.add(workoutTaskItem)

            workoutTasksRecyclerView.adapter!!.notifyItemChanged(dataSet.size - 1)

            addWorkoutTaskWindow.startAnimation(animationHide)

        }

        cancelButton.setOnClickListener {
            if(isOpenedAddWindow) {
                addWorkoutTaskWindow.startAnimation(animationHide)
            }
        }

        val workoutTasksScreen: ConstraintLayout = root.findViewById(R.id.workoutTasksScreen)
        workoutTasksScreen.setOnClickListener {
            if(isOpenedAddWindow) {
                addWorkoutTaskWindow.startAnimation(animationHide)
            }
        }

        /*workoutTasksRecyclerView.setOnClickListener {
            if(isOpenedAddWindow) {
                addWorkoutTaskWindow.startAnimation(animationHide)
            }
        }*/

        // Show animation listener
        animationShow.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }
            override fun onAnimationRepeat(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {
                isOpenedAddWindow = true
                addWorkoutTaskWindow.visibility = View.VISIBLE
            }
        })

        // Hide animation listener
        animationHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                hideKeyboard()
            }
            override fun onAnimationRepeat(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {
                isOpenedAddWindow = false
                addWorkoutTaskWindow.visibility = View.GONE
                clearFields(root)
            }
        })

        return root
    }

    // Filling dataset from db
    fun fillDataSet(db: SQLiteDatabase, workoutId: Int): ArrayList<WorkoutTaskItem> {
        val cursor: Cursor = db.rawQuery("SELECT * FROM WorkoutTasks WHERE workoutId = ${workoutId} ORDER BY workoutTaskOrderNum", null)
        val dataSet: ArrayList<WorkoutTaskItem> = arrayListOf()
        var item: WorkoutTaskItem
        if(cursor.moveToFirst()) {
            var workoutTaskId: Int
            var workoutTaskName: String
            var workoutTaskSets: Int
            var workoutTaskRepetitions: Int
            var workoutTaskRest: Int
            var workoutTaskOrderNum: Int
            while(!cursor.isAfterLast) {
                workoutTaskId = cursor.getInt(0)

                workoutTaskName = cursor.getString(2)
                workoutTaskSets = cursor.getInt(3)
                workoutTaskRepetitions = cursor.getInt(4)
                workoutTaskRest = cursor.getInt(5)
                workoutTaskOrderNum = cursor.getInt(6)

                item = WorkoutTaskItem(workoutTaskId,
                        workoutId,
                        workoutTaskName,
                        workoutTaskSets,
                        workoutTaskRepetitions,
                        workoutTaskRest,
                        workoutTaskOrderNum)
                dataSet.add(item)
                cursor.moveToNext()
            }
            cursor.close()
            return dataSet
        }
        return dataSet
    }



    // Hiding keyboard
    private fun hideKeyboard() {
        val view: View? = this.view
        if(view != null) {
            val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    // Clearing fields inside adding window
    private fun clearFields(root: View) {
        val workoutTaskNameTextEdit: EditText = root.findViewById(R.id.workoutTaskNameEditText)
        val setsEditText: EditText = root.findViewById(R.id.setsEditText)
        val repetitionEditText: EditText = root.findViewById(R.id.repetitionsEditText)
        val restEditText: EditText = root.findViewById(R.id.restEditText)

        workoutTaskNameTextEdit.setText("")
        setsEditText.setText("")
        repetitionEditText.setText("")
        restEditText.setText("")
    }
}