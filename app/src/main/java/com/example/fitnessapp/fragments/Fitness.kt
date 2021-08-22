package com.example.fitnessapp.fragments

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
import com.example.fitnessapp.FitnessListAdapter
import com.example.fitnessapp.fitness_list.FitnessListItem
import com.example.fitnessapp.R
import com.example.fitnessapp.sql.SQLListGetter
import com.example.fitnessapp.workout_list.WorkoutListItem


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

        val listGetter = SQLListGetter(db)
        val workoutsList = listGetter.getWorkoutsList()
        val workoutNames = getNamesAndNumbers(workoutsList)

        val workoutsSpinner: Spinner = root.findViewById(R.id.workoutsSpinner)

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(this.requireContext(),
            R.layout.workouts_spinner_item, workoutNames)
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
                val fitnessListRecyclerView: RecyclerView = root.findViewById(R.id.fitnessRecyclerView)
                if(isListShown == false || fitnessListRecyclerView.visibility == View.GONE) {
                    val fitnessList = getFitnessList(db, workoutsList, selectedItemNum)

                    val manager = LinearLayoutManager(this.requireContext())
                    fitnessListRecyclerView.layoutManager = manager

                    val adapter = FitnessListAdapter(root, fitnessList, workoutsList[selectedItemNum].getId())
                    fitnessListRecyclerView.adapter = adapter

                    val animationShow = AnimationUtils.loadAnimation(root.context,
                        R.anim.popup_show
                    )
                    fitnessListRecyclerView.visibility = View.VISIBLE
                    fitnessListRecyclerView.startAnimation(animationShow)
                    isListShown = true
                } else {
                    val fitnessListRecyclerView: RecyclerView = root.findViewById(R.id.fitnessRecyclerView)

                    val animationShow = AnimationUtils.loadAnimation(root.context,
                        R.anim.popup_show
                    )
                    val animationHide = AnimationUtils.loadAnimation(root.context,
                        R.anim.popup_hide
                    )

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
        val listGetter = SQLListGetter(db)
        val workoutTasks = listGetter.getWorkoutTasksList(workoutsList[selectedItemNum].getId())
        val fitnessList: ArrayList<FitnessListItem> = arrayListOf()

        for(i in 0 until workoutTasks.size) {
            fitnessList.add(FitnessListItem(workoutTasks[i].workoutTasksListItemName))
            val workoutSets = listGetter.getWorkoutSetsList(workoutTasks[i].workoutTasksListItemId)
            for(j in 0 until workoutSets.size) {
                fitnessList.add(FitnessListItem(workoutSets[j].workoutSetRepetitions, workoutSets[j].workoutSetRest, workoutSets[j].workoutSetWeight))
            }
        }
        fitnessList.add(FitnessListItem())
        return fitnessList
    }

    fun getNamesAndNumbers(arr: ArrayList<WorkoutListItem>): ArrayList<String> { // Returns array with names and numeration of workouts
        val names = arrayListOf<String>()
        for(i in 0 until arr.size) {
            names.add( "${i + 1}. ${arr[i].getName()}")
        }
        return names
    }
}