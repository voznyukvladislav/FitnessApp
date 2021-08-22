package com.example.fitnessapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

private val TYPE_DONE_WORKOUT = 1
private val TYPE_DATE = 2

class DoneWorkoutsListAdapter(val values: ArrayList<DoneWorkoutsRecyclerViewItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return values.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var itemView: View
        if(viewType == TYPE_DONE_WORKOUT) {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.done_workout_list_item, parent, false)
            return DoneWorkoutsListItemViewHolder(itemView)
        } else {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.done_workout_list_date, parent, false)
            return DoneWorkoutsListDateViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == TYPE_DONE_WORKOUT) {
            (holder as DoneWorkoutsListItemViewHolder).doneWorkoutName!!.text = "Done workout name: ${values[position].doneWorkoutName}"
        } else {
            (holder as DoneWorkoutsListDateViewHolder).doneWorkoutDate!!.text = "${values[position].doneWorkoutDateDay}.${values[position].doneWorkoutDateMonth}.${values[position].doneWorkoutDateYear}"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return values[position].inListType
    }
}

class DoneWorkoutsListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var doneWorkoutName: TextView? = null

    init {
        doneWorkoutName = itemView.findViewById(R.id.doneWorkoutName)
    }
}

class DoneWorkoutsListDateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var doneWorkoutDate: TextView? = null

    init {
        doneWorkoutDate = itemView.findViewById(R.id.doneWorkoutDate)
    }
}