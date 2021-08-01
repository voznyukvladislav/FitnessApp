package com.example.fitnessapp

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
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
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class WorkoutTasksList : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_workout_tasks_list, container, false)

        val db = root.context.openOrCreateDatabase("workout.db", Context.MODE_PRIVATE, null)

        // Workout tasks list setup start
        val workoutTasksList: RecyclerView = root.findViewById(R.id.workoutTasksList)

        val manager = LinearLayoutManager(this.requireContext())
        workoutTasksList.layoutManager = manager

        val workoutId = arguments?.getInt("workoutId")!!
        val listGetter = ListGetter(db)
        val dataSet: ArrayList<WorkoutTasksListItem> = listGetter.getWorkoutTasksList(workoutId)

        val navController = (activity as MainActivity?)!!.findNavController(R.id.screen)
        val adapter = WorkoutTasksListAdapter(dataSet, db, workoutId, navController)
        workoutTasksList.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this.context, manager.orientation)
        workoutTasksList.addItemDecoration(dividerItemDecoration)

        val callback = WorkoutTasksListAdapterManager(adapter,
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
            ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT), db)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(workoutTasksList)
        // Workout tasks list setup end

        // Navigation bar
        // Getting related workout name
        val workoutsCursor = db.rawQuery("SELECT workoutName FROM Workouts WHERE workoutId = ${workoutId} AND isDeleted = 0", null)
        var workoutName = ""
        while(workoutsCursor.moveToNext()) {
            workoutName = workoutsCursor.getString(0)
        }
        workoutsCursor.close()

        val workoutNameInWorkoutTasksNavigationBar: TextView = root.findViewById(R.id.workoutNameInWorkoutTasksNavigationBar)

        val content = SpannableString("${workoutName}")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        workoutNameInWorkoutTasksNavigationBar.text = content

        // Navigation bar action to workouts list
        workoutNameInWorkoutTasksNavigationBar.setOnClickListener {
            navController.navigate(R.id.action_workoutTasksList_to_training_list)
        }
        // Navigation bar end

        // Animations
        val animationShow: Animation = AnimationUtils.loadAnimation(root.context, R.anim.popup_show)
        val animationHide: Animation = AnimationUtils.loadAnimation(root.context, R.anim.popup_hide)

        val addWorkoutTaskListItemButton: FloatingActionButton = root.findViewById(R.id.addWorkoutTaskButton)
        var isOpenedPopupWindow: Boolean = false
        val workoutTaskPopupWindow: ConstraintLayout = root.findViewById(R.id.workoutTaskPopupWindow)
        addWorkoutTaskListItemButton.setOnClickListener {
            if(!isOpenedPopupWindow) {
                workoutTaskPopupWindow.visibility = View.VISIBLE
                workoutTaskPopupWindow.startAnimation(animationShow)

            }
        }

        val newWorkoutTaskName: EditText = root.findViewById(R.id.newWorkoutTaskName)
        val workoutTaskPopupWindowAcceptButton: Button = root.findViewById(R.id.workoutTaskPopupWindowAcceptButton)
        workoutTaskPopupWindowAcceptButton.setOnClickListener {
            val newName = newWorkoutTaskName.text.toString()
            if(!newName.isNullOrBlank()) {
                val insertedId = insertWorkoutTask(db, newName, workoutId)
                val item = WorkoutTasksListItem(insertedId, newName)
                dataSet.add(item)
                workoutTasksList.adapter!!.notifyItemChanged(dataSet.size - 1)
                workoutTaskPopupWindow.startAnimation(animationHide)
            }
        }

        val workoutTaskPopupWindowCancelButton: Button = root.findViewById(R.id.workoutTaskPopupWindowCancelButton)
        workoutTaskPopupWindowCancelButton.setOnClickListener {
            workoutTaskPopupWindow.startAnimation(animationHide)
        }

        animationShow.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                isOpenedPopupWindow = true
            }
            override fun onAnimationEnd(animation: Animation?) {
            }
            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

        animationHide.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                workoutTaskPopupWindow.visibility = View.GONE
                isOpenedPopupWindow = false
                hideKeyboard()
            }
            override fun onAnimationEnd(animation: Animation?) {
                clearInput(newWorkoutTaskName)
            }
            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

        return root
    }

    // Inserts new row in db and returns its primary key
    private fun insertWorkoutTask(db: SQLiteDatabase, workoutTaskName: String, workoutId: Int): Int {
        db.execSQL("INSERT INTO workoutTasks VALUES (NULL, ${workoutId}, '${workoutTaskName}', 0, 0)")
        var workoutTaskId = 0
        val cursor = db.rawQuery("SELECT MAX(workoutTaskId) FROM workoutTasks", null)
        while(cursor.moveToNext()) {
            workoutTaskId = cursor.getInt(0)
        }
        return workoutTaskId
    }

    private fun clearInput(input: EditText) {
        input.setText("")
    }

    private fun hideKeyboard() {
        val view: View? = this.view
        if(view != null) {
            val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}

