package com.example.fitnessapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutTasksAdapter(var values: ArrayList<WorkoutTaskItem>,
                         var context: Context?,
                         var db: SQLiteDatabase
): RecyclerView.Adapter<WorkoutTasksAdapter.WorkoutTasksViewHolder>() {
    override fun getItemCount(): Int {
        return values.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutTasksAdapter.WorkoutTasksViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.workout_tasks_item, parent, false)
        return WorkoutTasksViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutTasksAdapter.WorkoutTasksViewHolder, position: Int) {
        var item: WorkoutTaskItem = values.get(position)
        holder.workoutTaskName!!.setText("Task name: " + item.workoutTaskName)
        holder.workoutTaskSets!!.setText("Sets: " + item.workoutTaskSets.toString())
        holder.workoutTaskRepetitions!!.setText("Repetitions: " + item.workoutTaskRepetitions.toString())
        holder.workoutTaskRest!!.setText("Rest: " + item.workoutTaskRest.toString())

        holder.itemView.setOnClickListener {

        }
        holder.itemView.setOnLongClickListener {

            return@setOnLongClickListener true
        }
    }

    fun swapItems(fromPosition: Int, toPosition: Int): ArrayList<WorkoutTaskItem> {
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
        db.execSQL("DELETE FROM WorkoutTasks WHERE workoutTaskId = ${values.get(position).workoutTaskId}")
        values.removeAt(position)
        notifyItemRemoved(position)
    }

    class WorkoutTasksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var workoutTaskName: TextView? = null
        var workoutTaskSets: TextView? = null
        var workoutTaskRepetitions: TextView? = null
        var workoutTaskRest: TextView? = null
        init {
            workoutTaskName = itemView.findViewById(R.id.doneWorkoutName)
            workoutTaskSets = itemView.findViewById(R.id.workoutTaskSets)
            workoutTaskRepetitions = itemView.findViewById(R.id.workoutTaskRepetitions)
            workoutTaskRest = itemView.findViewById(R.id.workoutTaskRest)
        }
    }
}
