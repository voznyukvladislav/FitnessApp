package com.example.fitnessapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutSetsListAdapter(var db: SQLiteDatabase,
                             var values: ArrayList<WorkoutSetsListItem>,
                             var context: Context): RecyclerView.Adapter<WorkoutSetsListAdapter.workoutSetsListViewHolder>() {

    override fun getItemCount(): Int {
        return values.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): workoutSetsListViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.workout_set_list_item, parent, false)
        return WorkoutSetsListAdapter.workoutSetsListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: workoutSetsListViewHolder, position: Int) {
        val item = values[position]

        holder.workoutSetRepetitions?.setText("Repetitions: ${item.workoutSetRepetitions}")
        holder.workoutSetRest?.setText("Rest: ${item.workoutSetRest}")
        holder.workoutSetWeight?.setText("Weight: ${item.workoutSetWeight}")
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
        db.execSQL("UPDATE workoutSets SET isDeleted = 1 WHERE workoutSetId = ${values[position].workoutSetId}")
        values.removeAt(position)
        notifyItemRemoved(position)
    }

    class workoutSetsListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var workoutSetRepetitions: TextView? = null
        var workoutSetRest: TextView? = null
        var workoutSetWeight: TextView? = null

        init {
            workoutSetRepetitions = itemView.findViewById(R.id.repetitionsNumber)
            workoutSetRest = itemView.findViewById(R.id.restNumber)
            workoutSetWeight = itemView.findViewById(R.id.weightNumber)
        }
    }
}