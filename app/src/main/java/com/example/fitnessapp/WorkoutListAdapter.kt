package com.example.fitnessapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView

class WorkoutListAdapter(var values: ArrayList<WorkoutListItem>,
                         var context: Context?,
                         var db: SQLiteDatabase,
                         var navController: NavController
): RecyclerView.Adapter<WorkoutListAdapter.WorkoutListViewHolder>() {
    override fun getItemCount(): Int {
        return values.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutListAdapter.WorkoutListViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.workout_list_item, parent, false)
        return WorkoutListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutListAdapter.WorkoutListViewHolder, position: Int) {
        val item: WorkoutListItem = values.get(position)
        holder.workoutName!!.setText("Workout name: ${item.getName()}")

        holder.itemView.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putInt("workoutId", item.getId())
            navController.navigate(R.id.action_training_list_to_workoutTasksList, bundle)
        }
    }

    fun swapItems(fromPosition: Int, toPosition: Int): ArrayList<WorkoutListItem> {
        if(fromPosition < toPosition) {
            for (i in fromPosition..toPosition - 1) {
                values.set(i, values.set(i + 1, values.get(i)));
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                values.set(i, values.set(i - 1, values.get(i)));
            }
        }

        notifyItemMoved(fromPosition, toPosition)
        return values
    }

    fun onItemDismiss(position: Int) {
        db.execSQL("DELETE FROM Workouts WHERE workoutId = ${values.get(position).getId()}")
        values.removeAt(position)
        notifyItemRemoved(position)
    }

    class WorkoutListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var workoutName: TextView? = null
        init {
            workoutName = itemView.findViewById(R.id.workoutTaskName)
        }
    }
}