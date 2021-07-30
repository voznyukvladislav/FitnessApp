package com.example.fitnessapp

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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WorkoutSetsList : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_workout_sets_list, container, false)

        var db: SQLiteDatabase = this.requireContext().openOrCreateDatabase("workout.db", Context.MODE_PRIVATE, null)

        val workoutId = arguments?.getInt("workoutId")
        val workoutTaskId = arguments?.getInt("workoutTaskId")
    
        // Setup recycler view
        val workoutSetsList: RecyclerView = root.findViewById(R.id.workoutSetsList)

        val manager = LinearLayoutManager(this.requireContext())
        workoutSetsList.layoutManager = manager

        var dataSet = getWorkoutSetsList(db, workoutTaskId!!)

        val adapter = WorkoutSetsListAdapter(db, dataSet, this.requireContext())
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
        val workoutsCursor = db.rawQuery("SELECT workoutName FROM Workouts WHERE workoutId = ${workoutId} AND isDeleted = 0", null)
        var workoutName = ""
        while(workoutsCursor.moveToNext()) {
            workoutName = workoutsCursor.getString(0)
        }
        workoutsCursor.close()

        val workoutTasksCursor = db.rawQuery("SELECT workoutTaskName FROM workoutTasks WHERE workoutTaskId = ${workoutTaskId} AND isDeleted = 0", null)
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

        // Animations
        val animationShow: Animation = AnimationUtils.loadAnimation(root.context, R.anim.popup_show)
        val animationHide: Animation = AnimationUtils.loadAnimation(root.context, R.anim.popup_hide)

        // Popup window's related listeners
        val addWorkoutSet: FloatingActionButton = root.findViewById(R.id.addWorkoutSetButton)
        val workoutSetsPopupWindow: ConstraintLayout = root.findViewById(R.id.workoutSetsPopupWindow)
        var isOpenedPopupWindow: Boolean = false
        addWorkoutSet.setOnClickListener {
            if(!isOpenedPopupWindow) {
                workoutSetsPopupWindow.visibility = View.VISIBLE
                workoutSetsPopupWindow.startAnimation(animationShow)
            }
        }

        val workoutSetPopupWindowAcceptButton: Button = root.findViewById(R.id.workoutSetPopupWindowAcceptButton)
        val newSetRepetitionsNumEditText: EditText = root.findViewById(R.id.newSetRepetitionsNum)
        val newSetRestNumEditText: EditText = root.findViewById(R.id.newSetRestNum)
        workoutSetPopupWindowAcceptButton.setOnClickListener {
            var newSetRepetitionsNum = newSetRepetitionsNumEditText.text.toString().toInt()
            var newSetRestNum = newSetRestNumEditText.text.toString().toInt()
            dataSet.add(insertSet(db, workoutTaskId, newSetRepetitionsNum, newSetRestNum))
            workoutSetsList.adapter!!.notifyItemChanged(dataSet.size - 1)
            workoutSetsPopupWindow.startAnimation(animationHide)
        }

        val workoutSetPopupWindowCancelButton: Button = root.findViewById(R.id.workoutSetPopupWindowCancelButton)
        workoutSetPopupWindowCancelButton.setOnClickListener {
            isOpenedPopupWindow = false
            workoutSetsPopupWindow.startAnimation(animationHide)
        }
        // Popup's window related listeners end

        animationShow.setAnimationListener(object: Animation.AnimationListener{
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
                workoutSetsPopupWindow.visibility = View.GONE
                isOpenedPopupWindow = false
                hideKeyboard()
            }

            override fun onAnimationEnd(animation: Animation?) {
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

        return root
    }

    private fun getWorkoutSetsList(db: SQLiteDatabase, workoutTaskId: Int): ArrayList<WorkoutSetsListItem> {
        var dataSet: ArrayList<WorkoutSetsListItem> = arrayListOf()

        val cursor = db.rawQuery("SELECT workoutSetId, workoutSetRepetitions, workoutSetRest, workoutSetOrderNum " +
                "FROM workoutSets WHERE workoutTaskId = ${workoutTaskId} AND isDeleted = 0 ORDER BY workoutSetOrderNum", null)

        var workoutSetId: Int = 0
        var workoutSetRepetitions: Int = 0
        var workoutSetRest: Int = 0
        var workoutSetOrderNum: Int = 0
        while(cursor.moveToNext()) {
            workoutSetId = cursor.getInt(0)
            workoutSetRepetitions = cursor.getInt(1)
            workoutSetRest = cursor.getInt(2)
            workoutSetOrderNum = cursor.getInt(3)

            var item = WorkoutSetsListItem(workoutSetId, workoutSetRepetitions, workoutSetRest, workoutSetOrderNum)
            dataSet.add(item)
        }
        cursor.close()

        return dataSet
    }

    private fun insertSet(db: SQLiteDatabase, workoutTaskId: Int, workoutSetRepetitions: Int, workoutSetRest: Int): WorkoutSetsListItem {
        db.execSQL("INSERT INTO workoutSets VALUES (NULL, " +
                "${workoutTaskId}, " +
                "${workoutSetRepetitions}, " +
                "${workoutSetRest}, " +
                "0," +
                "0)")

        var insertedId = 0
        val cursor = db.rawQuery("SELECT MAX(workoutSetId) FROM workoutSets", null)
        while(cursor.moveToNext()) {
            insertedId = cursor.getInt(0)
        }
        cursor.close()

        return WorkoutSetsListItem(insertedId, workoutSetRepetitions, workoutSetRest, 0)
    }

    private fun hideKeyboard() {
        val view: View? = this.view
        if(view != null) {
            val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}