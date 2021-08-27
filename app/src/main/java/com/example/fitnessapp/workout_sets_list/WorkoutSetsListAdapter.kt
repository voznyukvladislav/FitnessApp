package com.example.fitnessapp.workout_sets_list

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.KeyboardHider
import com.example.fitnessapp.R

class WorkoutSetsListAdapter(var db: SQLiteDatabase,
                             var values: ArrayList<WorkoutSetsListItem>,
                             var context: Context,
                             var root: View): RecyclerView.Adapter<WorkoutSetsListAdapter.workoutSetsListViewHolder>() {

    override fun getItemCount(): Int {
        return values.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): workoutSetsListViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.workout_set_list_item, parent, false)
        return workoutSetsListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: workoutSetsListViewHolder, position: Int) {
        val item = values[position]

        holder.workoutSetRepetitions?.setText("Repetitions: ${item.workoutSetRepetitions}")
        holder.workoutSetRest?.setText("Rest: ${item.workoutSetRest}")
        holder.workoutSetWeight?.setText("Weight: ${item.workoutSetWeight}")

        holder.editWorkoutSetListItem!!.setOnClickListener {
            val workoutSetsListPopupEditWindow = WorkoutSetsListPopupEditWindow(root)
            workoutSetsListPopupEditWindow.setInputFieldValue(item.workoutSetRepetitions,
                item.workoutSetRest,
                item.workoutSetWeight)
            workoutSetsListPopupEditWindow.show()

            workoutSetsListPopupEditWindow.acceptButton.setOnClickListener {
                if(!workoutSetsListPopupEditWindow.setRepetitionsEditText.text.toString().isNullOrBlank()
                    and !workoutSetsListPopupEditWindow.setRestNumEditText.text.toString().isNullOrBlank()) {
                    workoutSetsListPopupEditWindow.updateWorkoutSet(db, item.workoutSetId)
                    workoutSetsListPopupEditWindow.hide()
                    KeyboardHider(root).hideKeyboard()

                    item.workoutSetRepetitions = workoutSetsListPopupEditWindow.setRepetitionsEditText.text.toString().toInt()
                    item.workoutSetRest = workoutSetsListPopupEditWindow.setRestNumEditText.text.toString().toInt()
                    item.workoutSetWeight = 0
                    if(!workoutSetsListPopupEditWindow.setWeightEditText.text.toString().isNullOrBlank()) {
                        item.workoutSetWeight = workoutSetsListPopupEditWindow.setWeightEditText.text.toString().toInt()
                    }

                    // Finding current element position (in case if it was moved)
                    var curPos = 0
                    for(i in 0 until values.size) {
                        if(values[i].workoutSetId == item.workoutSetId) {
                            curPos = i
                            break
                        }
                    }

                    this.notifyItemChanged(curPos)
                }
            }

            workoutSetsListPopupEditWindow.cancelButton.setOnClickListener {
                workoutSetsListPopupEditWindow.hide()
                KeyboardHider(root).hideKeyboard()
            }
        }

    }

    fun swapItems(fromPosition: Int, toPosition: Int): ArrayList<WorkoutSetsListItem> {
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
        db.execSQL("DELETE FROM workoutSets WHERE workoutSetId = ${values[position].workoutSetId}")
        values.removeAt(position)
        notifyItemRemoved(position)
    }

    class workoutSetsListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var workoutSetRepetitions: TextView? = null
        var workoutSetRest: TextView? = null
        var workoutSetWeight: TextView? = null

        var editWorkoutSetListItem: Button? = null

        init {
            workoutSetRepetitions = itemView.findViewById(R.id.doneWorkoutSetRepetitionsNumber)
            workoutSetRest = itemView.findViewById(R.id.doneWorkoutSetRestNumber)
            workoutSetWeight = itemView.findViewById(R.id.doneWorkoutSetWeightNumber)

            editWorkoutSetListItem = itemView.findViewById(R.id.workoutSetsListItemEditButton)
        }
    }
}