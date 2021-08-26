package com.example.fitnessapp.done_workout_tasks_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView

import com.example.fitnessapp.R

class DoneWorkoutTasksListAdapter(var values: ArrayList<DoneWorkoutTasksListItem>, var navController: NavController) : RecyclerView.Adapter<DoneWorkoutTasksListAdapter.DoneWorkoutTasksListItemViewHolder>() {
    override fun getItemCount(): Int {
        return values.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DoneWorkoutTasksListItemViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.done_workout_task_list_item, parent, false)
        return DoneWorkoutTasksListItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DoneWorkoutTasksListItemViewHolder, position: Int) {
        val item = values[position]

        holder.doneWorkoutTaskName!!.setText("Done task name: ${item.doneWorkoutTaskName}")
        holder.doneWorkoutTaskTotalRepetitions!!.setText("Total repetitions: ${item.doneWorkoutTaskTotalRepetitions}")
        holder.doneWorkoutTaskTotalSets!!.setText("Total sets: ${item.doneWorkoutTaskTotalSets}")

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("doneWorkoutTaskId", values[position].doneWorkoutTaskId)
            navController.navigate(R.id.action_done_workout_tasks_list_to_done_workout_sets_list, bundle)
        }
    }

    class DoneWorkoutTasksListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var doneWorkoutTaskName: TextView? = null
        var doneWorkoutTaskTotalSets: TextView? = null
        var doneWorkoutTaskTotalRepetitions: TextView? = null

        init {
            this.doneWorkoutTaskName = itemView.findViewById(R.id.doneWorkoutTaskName)
            this.doneWorkoutTaskTotalSets = itemView.findViewById(R.id.doneWorkoutTaskTotalSets)
            this.doneWorkoutTaskTotalRepetitions = itemView.findViewById(R.id.doneWorkoutTaskTotalRepetitions)
        }
    }
}