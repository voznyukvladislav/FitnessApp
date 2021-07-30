package com.example.fitnessapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class Fitness : Fragment() {

    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_fitness, container, false)
        db = root.context.openOrCreateDatabase("workout.db", Context.MODE_PRIVATE, null)

        val workoutsList = getWorkouts(db)
        val workoutNames = getNamesAndNumbers(workoutsList)

        val workoutsSpinner: Spinner = root.findViewById(R.id.workoutsSpinner)

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(this.requireContext(), R.layout.workouts_spinner_item, workoutNames)
        spinnerAdapter.setDropDownViewResource(R.layout.workouts_spinner_item)
        workoutsSpinner.adapter = spinnerAdapter

        var selectedItemNum: Int = -10 // Special identifier for unselected choice
        workoutsSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View,
                pos: Int, id: Long
            ) {
                selectedItemNum = pos
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {
            }
        }

        val startWorkoutButton: Button = root.findViewById(R.id.startWorkoutButton)
        var isListShown: Boolean = false
        startWorkoutButton.setOnClickListener {
            if(selectedItemNum != -10) {
                if(isListShown == false) {
                    val fitnessList = getFitnessList(db, workoutsList, selectedItemNum)

                    val fitnessListRecyclerView: RecyclerView = root.findViewById(R.id.fitnessRecyclerView)

                    val manager = LinearLayoutManager(this.requireContext())
                    fitnessListRecyclerView.layoutManager = manager

                    val adapter = FitnessListAdapter(root, fitnessList, workoutsList[selectedItemNum].getId())
                    fitnessListRecyclerView.adapter = adapter

                    val animationShow = AnimationUtils.loadAnimation(root.context, R.anim.popup_show)
                    fitnessListRecyclerView.visibility = View.VISIBLE
                    fitnessListRecyclerView.startAnimation(animationShow)
                    isListShown = true
                } else {
                    val fitnessListRecyclerView: RecyclerView = root.findViewById(R.id.fitnessRecyclerView)

                    val animationShow = AnimationUtils.loadAnimation(root.context, R.anim.popup_show)
                    val animationHide = AnimationUtils.loadAnimation(root.context, R.anim.popup_hide)

                    fitnessListRecyclerView.startAnimation(animationHide)

                    val context = this.requireContext()

                    animationHide.setAnimationListener(object: Animation.AnimationListener{
                        override fun onAnimationStart(animation: Animation?) {
                            fitnessListRecyclerView.visibility = View.GONE
                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            val manager = LinearLayoutManager(context)
                            fitnessListRecyclerView.layoutManager = manager

                            val fitnessList = getFitnessList(db, workoutsList, selectedItemNum)

                            val adapter = FitnessListAdapter(root, fitnessList, workoutsList[selectedItemNum].getId())
                            fitnessListRecyclerView.adapter = adapter

                            fitnessListRecyclerView.visibility = View.VISIBLE
                            fitnessListRecyclerView.startAnimation(animationShow)
                        }

                        override fun onAnimationRepeat(animation: Animation?) {
                        }
                    })
                }
            }
        }

        return root
    }

    fun getFitnessList(db: SQLiteDatabase, workoutsList: ArrayList<WorkoutListItem>, selectedItemNum: Int): ArrayList<FitnessListItem> {
        val workoutTasks = getWorkoutTasks(db, workoutsList[selectedItemNum].getId())
        val fitnessList: ArrayList<FitnessListItem> = arrayListOf()

        for(i in 0 until workoutTasks.size) {
            fitnessList.add(FitnessListItem(workoutTasks[i].workoutTasksListItemName))

            var workoutSets = getWorkoutSets(db, workoutTasks[i].workoutTasksListItemId)
            for(j in 0 until workoutSets.size) {
                fitnessList.add(FitnessListItem(workoutSets[j].workoutSetRepetitions, workoutSets[j].workoutSetRest))
            }
        }
        fitnessList.add(FitnessListItem())
        return fitnessList
    }

    fun getWorkouts(db: SQLiteDatabase): ArrayList<WorkoutListItem> { // Returns array with information about workouts from db
        val cursor = db.rawQuery("SELECT workoutId, workoutName FROM Workouts WHERE isDeleted = 0 ORDER BY workoutOrderNum", null)
        val workoutsArray = arrayListOf<WorkoutListItem>()

        while(cursor.moveToNext()) {
            workoutsArray.add(WorkoutListItem(cursor.getInt(0), cursor.getString(1)))
        }
        cursor.close()
        return workoutsArray
    }

    fun getWorkoutTasks(db: SQLiteDatabase, workoutId: Int): ArrayList<WorkoutTasksListItem> {
        val workoutTasks = arrayListOf<WorkoutTasksListItem>()
        val cursor = db.rawQuery("SELECT workoutTaskId, workoutTaskName FROM WorkoutTasks WHERE workoutId = ${workoutId} AND isDeleted = 0 ORDER BY workoutTaskOrderNum", null)

        var workoutTaskId = 0
        var workoutTaskName = ""
        while(cursor.moveToNext()) {
            workoutTaskId = cursor.getInt(0)
            workoutTaskName = cursor.getString(1)

            var item = WorkoutTasksListItem(workoutTaskId, workoutTaskName)
            workoutTasks.add(item)
        }
        cursor.close()

        return workoutTasks
    }

    fun getWorkoutSets(db: SQLiteDatabase, workoutTaskId: Int): ArrayList<WorkoutSetsListItem> {
        var dataSet: ArrayList<WorkoutSetsListItem> = arrayListOf()
        var cursor = db.rawQuery("SELECT workoutSetId, workoutSetRepetitions, workoutSetRest, workoutSetOrderNum FROM workoutSets WHERE workoutTaskId = ${workoutTaskId} AND isDeleted = 0", null)

        var workoutSetId = 0
        var workoutSetRepetitions = 0
        var workoutSetRest = 0
        var workoutSetOrderNum = 0
        while(cursor.moveToNext()) {
            workoutSetId = cursor.getInt(0)
            workoutSetRepetitions = cursor.getInt(1)
            workoutSetRest = cursor.getInt(2)
            workoutSetOrderNum = cursor.getInt(3)

            var item = WorkoutSetsListItem(workoutSetId,
                workoutSetRepetitions,
                workoutSetRest,
                workoutSetOrderNum)
            dataSet.add(item)
        }
        cursor.close()
        return dataSet
    }

    fun getNamesAndNumbers(arr: ArrayList<WorkoutListItem>): ArrayList<String> { // Returns array with names and numeration of workouts
        val names = arrayListOf<String>()
        for(i in 0 until arr.size) {
            names.add( "${i + 1}. ${arr[i].getName()}")
        }
        return names
    }
}