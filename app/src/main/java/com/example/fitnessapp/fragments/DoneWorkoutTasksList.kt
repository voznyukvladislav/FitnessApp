package com.example.fitnessapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.done_workout_tasks_list.DoneWorkoutTasksListAdapter
import com.example.fitnessapp.done_workout_tasks_list.DoneWorkoutTasksListItem
import com.example.fitnessapp.sql.SQLListGetter
import android.database.sqlite.SQLiteDatabase
import androidx.navigation.findNavController
import com.example.fitnessapp.MainActivity

class DoneWorkoutTasksList : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_done_workout_tasks_list, container, false)

        val doneWorkoutTasksListRecyclerView: RecyclerView = root.findViewById(R.id.doneWorkoutTasksList)
        val manager = LinearLayoutManager(root.context)
        doneWorkoutTasksListRecyclerView.layoutManager = manager

        val db: SQLiteDatabase = root.context.openOrCreateDatabase("workout.db", Context.MODE_PRIVATE, null)

        val doneWorkoutId = arguments?.getInt("doneWorkoutId")
        val dataSet: ArrayList<DoneWorkoutTasksListItem> = SQLListGetter(db).getDoneWorkoutTasks(doneWorkoutId!!)

        val navController = (activity as MainActivity?)!!.findNavController(R.id.screen)
        val adapter = DoneWorkoutTasksListAdapter(dataSet, navController)
        doneWorkoutTasksListRecyclerView.adapter = adapter

        return root
    }
}