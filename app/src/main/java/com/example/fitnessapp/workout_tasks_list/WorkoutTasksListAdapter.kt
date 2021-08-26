package com.example.fitnessapp.workout_tasks_list

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.KeyboardHider
import com.example.fitnessapp.R

class WorkoutTasksListAdapter(var values: ArrayList<WorkoutTasksListItem>,
                              var db: SQLiteDatabase,
                              var workoutId: Int,
                              var navController: NavController,
                              var root: View): RecyclerView.Adapter<WorkoutTasksListAdapter.WorkoutTasksListViewHolder>() {
    override fun getItemCount(): Int {
        return values.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutTasksListViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.workout_task_list_item, parent, false)
        return WorkoutTasksListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutTasksListViewHolder, position: Int) {
        var item = values[position]
        holder.workoutTasksListItemName?.setText("Task name: ${item.workoutTasksListItemName}")
        holder.workoutTasksListItemTotalSets?.setText("Total sets: ${item.workoutTasksListItemTotalSets}")
        holder.workoutTasksListItemTotalRepetitions?.setText("Total repetitions: ${item.workoutTasksListItemTotalRepetitions}")

        holder.itemView.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putInt("workoutId", workoutId)
            bundle.putInt("workoutTaskId", item.workoutTasksListItemId)
            navController.navigate(R.id.action_workoutTasksList_to_workoutSetsList, bundle)
        }

        holder.editWorkoutTasksListItem!!.setOnClickListener {
            val workoutTasksListPopupEditWindow = WorkoutTasksListPopupEditWindow(root)
            workoutTasksListPopupEditWindow.setInputFieldValue(values[position].workoutTasksListItemName)
            workoutTasksListPopupEditWindow.show()

            workoutTasksListPopupEditWindow.acceptButton.setOnClickListener {
                if(!workoutTasksListPopupEditWindow.editWorkoutTaskNameEditText.text.toString().isNullOrBlank()) {
                    workoutTasksListPopupEditWindow.updateWorkoutTask(db, values[position].workoutTasksListItemId)
                    workoutTasksListPopupEditWindow.hide()
                    KeyboardHider(root).hideKeyboard()

                    values[position].workoutTasksListItemName = workoutTasksListPopupEditWindow.editWorkoutTaskNameEditText.text.toString()
                    this.notifyItemChanged(position)
                }
            }

            workoutTasksListPopupEditWindow.cancelButton.setOnClickListener {
                workoutTasksListPopupEditWindow.hide()
                KeyboardHider(root).hideKeyboard()
            }
        }
    }

    fun swapItems(fromPosition: Int, toPosition: Int): ArrayList<WorkoutTasksListItem> {
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
        db.execSQL("DELETE FROM workoutTasks WHERE workoutTaskId = ${values[position].workoutTasksListItemId}")
        values.removeAt(position)
        notifyItemRemoved(position)
    }

    class WorkoutTasksListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var workoutTasksListItemName: TextView? = null
        var workoutTasksListItemTotalSets: TextView? = null
        var workoutTasksListItemTotalRepetitions: TextView? = null

        var editWorkoutTasksListItem: Button? = null

        init {
            workoutTasksListItemName = itemView.findViewById(R.id.doneWorkoutTaskName)
            workoutTasksListItemTotalSets = itemView.findViewById(R.id.doneWorkoutTaskTotalSets)
            workoutTasksListItemTotalRepetitions = itemView.findViewById(R.id.doneWorkoutTaskTotalRepetitions)

            editWorkoutTasksListItem = itemView.findViewById(R.id.editWorkoutTaskListItem)
        }
    }
}