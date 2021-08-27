package com.example.fitnessapp.fragments

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
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
import com.example.fitnessapp.workout_sets_list.WorkoutSetsListAdapter
import com.example.fitnessapp.workout_sets_list.WorkoutSetsListAdapterManager
import com.example.fitnessapp.workout_sets_list.WorkoutSetsListPopupAddWindow
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WorkoutSetsList : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_workout_sets_list, container, false)

        val db: SQLiteDatabase = this.requireContext().openOrCreateDatabase("workout.db", Context.MODE_PRIVATE, null)

        val workoutId = arguments?.getInt("workoutId")
        val workoutTaskId = arguments?.getInt("workoutTaskId")
    
        // Setup recycler view
        val workoutSetsList: RecyclerView = root.findViewById(R.id.workoutSetsList)

        val manager = LinearLayoutManager(this.requireContext())
        workoutSetsList.layoutManager = manager

        val listGetter = SQLListGetter(db)
        val dataSet = listGetter.getWorkoutSetsList(workoutTaskId!!)

        val adapter = WorkoutSetsListAdapter(db, dataSet, this.requireContext(), root)
        workoutSetsList.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this.context, manager.orientation)
        workoutSetsList.addItemDecoration(dividerItemDecoration)

        val callback = WorkoutSetsListAdapterManager(adapter,
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
            ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT), db)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(workoutSetsList)
        // End setup recycler view

        // Navigation bar start
        val workoutsCursor = db.rawQuery("SELECT workoutName FROM Workouts WHERE workoutId = ${workoutId}", null)
        var workoutName = ""
        while(workoutsCursor.moveToNext()) {
            workoutName = workoutsCursor.getString(0)
        }
        workoutsCursor.close()

        val workoutTasksCursor = db.rawQuery("SELECT workoutTaskName FROM workoutTasks WHERE workoutTaskId = ${workoutTaskId}", null)
        var workoutTaskName = ""
        while(workoutTasksCursor.moveToNext()) {
            workoutTaskName = workoutTasksCursor.getString(0)
        }
        workoutTasksCursor.close()

        val workoutNameInWorkoutSetsNavigationBar: TextView = root.findViewById(R.id.workoutNameInWorkoutSetsNavigationBar)
        val workoutNameUnderlined = SpannableString("${workoutName} / ")
        workoutNameUnderlined.setSpan(UnderlineSpan(), 0, workoutNameUnderlined.length, 0)
        workoutNameInWorkoutSetsNavigationBar.text = workoutNameUnderlined

        val workoutTaskNameInWorkoutSetsNavigationBar: TextView = root.findViewById(R.id.workoutTaskNameInWorkoutSetsNavigationBar)
        val workoutTaskNameUnderlined = SpannableString("${workoutTaskName}")
        workoutTaskNameUnderlined.setSpan(UnderlineSpan(), 0, workoutTaskNameUnderlined.length, 0)
        workoutTaskNameInWorkoutSetsNavigationBar.text = workoutTaskNameUnderlined


        val navController = (activity as MainActivity?)!!.findNavController(R.id.screen)
        // Navigation bar action to workouts list
        workoutNameInWorkoutSetsNavigationBar.setOnClickListener {
            navController.navigate(R.id.action_workoutSetsList_to_training_list)
        }

        // Navigation bar action to workout tasks list
        val bundle: Bundle = Bundle()
        bundle.putInt("workoutId", workoutId!!)
        workoutTaskNameInWorkoutSetsNavigationBar.setOnClickListener {
            navController.navigate(R.id.action_workoutSetsList_to_workoutTasksList, bundle)
        }
        // Navigation bar end



        // Popup window's related listeners
        val addWorkoutSet: FloatingActionButton = root.findViewById(R.id.addWorkoutSetButton)
        val workoutSetsListPopupAddWindow = WorkoutSetsListPopupAddWindow(root)
        addWorkoutSet.setOnClickListener {
            workoutSetsListPopupAddWindow.show()
        }

        workoutSetsListPopupAddWindow.acceptButton.setOnClickListener {
            val newSetRepetitionsNum = workoutSetsListPopupAddWindow.setRepetitionsEditText.text.toString()
            val newSetRestNum = workoutSetsListPopupAddWindow.setRestNumEditText.text.toString()
            if(!newSetRepetitionsNum.isNullOrBlank() and !newSetRestNum.isNullOrBlank()) {

                workoutSetsListPopupAddWindow.addWorkoutSet(db, workoutTaskId)
                workoutSetsListPopupAddWindow.hide()
                workoutSetsListPopupAddWindow.clearInputFields()
                KeyboardHider(root).hideKeyboard()

                val workoutSetListItem = SQLInserter(db).getInsertedWorkoutSet()
                dataSet.add(workoutSetListItem)
                workoutSetsList.adapter!!.notifyItemChanged(dataSet.size - 1)
            }
        }

        workoutSetsListPopupAddWindow.cancelButton.setOnClickListener {
            workoutSetsListPopupAddWindow.hide()
            KeyboardHider(root).hideKeyboard()
        }

        return root
    }
}