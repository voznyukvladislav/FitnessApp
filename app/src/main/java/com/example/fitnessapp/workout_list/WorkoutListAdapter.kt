package com.example.fitnessapp.workout_list

import android.content.Context
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

class WorkoutListAdapter(var values: ArrayList<WorkoutListItem>,
                         var context: Context?,
                         var db: SQLiteDatabase,
                         var navController: NavController,
                         var root: View
): RecyclerView.Adapter<WorkoutListAdapter.WorkoutListViewHolder>() {
    override fun getItemCount(): Int {
        return values.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutListViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.workout_list_item, parent, false)
        return WorkoutListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutListViewHolder, position: Int) {
        val item: WorkoutListItem = values[position]

        holder.workoutName!!.setText("Workout name: ${item.getName()}")
        holder.workoutTotalTasks!!.setText("Total tasks: ${item.getTotalTasks()}")

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("workoutId", item.getId())
            navController.navigate(R.id.action_training_list_to_workoutTasksList, bundle)
        }

        holder.workoutEditButton!!.setOnClickListener {
            val workoutPopupEditWindow = WorkoutListPopupEditWindow(root)
            workoutPopupEditWindow.setInputFieldValue(item.getName())
            workoutPopupEditWindow.show()



            workoutPopupEditWindow.acceptButton.setOnClickListener {
                val newName = workoutPopupEditWindow.workoutNameEditText.text.toString()
                if(!newName.isNullOrBlank()) {
                    workoutPopupEditWindow.updateWorkoutListItem(db, item.getId())
                    workoutPopupEditWindow.hide()
                    KeyboardHider(root).hideKeyboard()

                    item.setName(newName)

                    // Finding current element position (in case if it was moved)
                    var curPos = 0
                    for(i in 0 until values.size) {
                        if(values[i].getId() == item.getId()) {
                            curPos = i
                            break
                        }
                    }

                    this.notifyItemChanged(curPos)
                }
            }
        }
    }

    fun swapItems(fromPosition: Int, toPosition: Int): ArrayList<WorkoutListItem> {
        if(fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                values[i] = values.set(i + 1, values[i])
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                values[i] = values.set(i - 1, values[i])
            }
        }

        notifyItemMoved(fromPosition, toPosition)
        return values
    }

    fun onItemDismiss(position: Int) {
        db.execSQL("DELETE FROM workouts WHERE workoutId = ${values[position].getId()}")
        values.removeAt(position)
        notifyItemRemoved(position)
    }

    class WorkoutListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var workoutName: TextView? = null
        var workoutTotalTasks: TextView? = null
        var workoutEditButton: Button? = null
        init {
            workoutName = itemView.findViewById(R.id.doneWorkoutTaskName)
            workoutTotalTasks = itemView.findViewById(R.id.workoutTotalTasks)
            workoutEditButton = itemView.findViewById(R.id.editWorkout)
        }
    }
}