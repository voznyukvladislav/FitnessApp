package com.example.fitnessapp.fragments

import android.content.Context
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
import com.example.fitnessapp.KeyboardHider
import com.example.fitnessapp.MainActivity
import com.example.fitnessapp.R
import com.example.fitnessapp.sql.SQLInserter
import com.example.fitnessapp.sql.SQLListGetter
import com.example.fitnessapp.workout_tasks_list.WorkoutTasksListAdapter
import com.example.fitnessapp.workout_tasks_list.WorkoutTasksListAdapterManager
import com.example.fitnessapp.workout_tasks_list.WorkoutTasksListItem
import com.example.fitnessapp.workout_tasks_list.WorkoutTasksListPopupAddWindow
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
        val listGetter = SQLListGetter(db)
        val dataSet: ArrayList<WorkoutTasksListItem> = listGetter.getWorkoutTasksList(workoutId)

        val navController = (activity as MainActivity?)!!.findNavController(R.id.screen)
        val adapter = WorkoutTasksListAdapter(dataSet, db, workoutId, navController, root)
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
        val workoutsCursor = db.rawQuery("SELECT workoutName FROM Workouts WHERE workoutId = ${workoutId}", null)
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


        // Popup window
        val workoutTasksListPopupAddWindow = WorkoutTasksListPopupAddWindow(root)
        val addWorkoutTaskListItemButton: FloatingActionButton = root.findViewById(R.id.addWorkoutTaskButton)
        addWorkoutTaskListItemButton.setOnClickListener {
            workoutTasksListPopupAddWindow.show()
        }

        workoutTasksListPopupAddWindow.acceptButton.setOnClickListener {
            val newName = workoutTasksListPopupAddWindow.newWorkoutTaskName.text.toString()
            if(!newName.isNullOrBlank()) {
                workoutTasksListPopupAddWindow.addWorkoutListItem(db, workoutId)
                val item = SQLInserter(db).getInsertedWorkoutTask()
                dataSet.add(item)
                workoutTasksList.adapter!!.notifyItemChanged(dataSet.size - 1)
                workoutTasksListPopupAddWindow.hide()
                workoutTasksListPopupAddWindow.clearInputFields()
                KeyboardHider(root).hideKeyboard()
            }
        }

        workoutTasksListPopupAddWindow.cancelButton.setOnClickListener {
            workoutTasksListPopupAddWindow.hide()
        }

        return root
    }
}

