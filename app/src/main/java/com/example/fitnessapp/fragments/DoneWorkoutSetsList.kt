package com.example.fitnessapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnessapp.R
import com.example.fitnessapp.sql.SQLListGetter
import android.database.sqlite.SQLiteDatabase
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.done_workout_sets_list.DoneWorkoutSetsListAdapter


class DoneWorkoutSetsList : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_done_workout_sets_list, container, false)
        val db = root.context.openOrCreateDatabase("workout.db", Context.MODE_PRIVATE, null)

        val doneWorkoutTaskId = arguments?.getInt("doneWorkoutTaskId")
        val dataSet = SQLListGetter(db).getDoneWorkoutSets(doneWorkoutTaskId!!)

        val doneWorkoutSetsListRecyclerView: RecyclerView = root.findViewById(R.id.doneWorkoutSetsList)
        val manager = LinearLayoutManager(root.context)
        doneWorkoutSetsListRecyclerView.layoutManager = manager

        val adapter = DoneWorkoutSetsListAdapter(dataSet)
        doneWorkoutSetsListRecyclerView.adapter = adapter

        return root
    }
}