package com.example.fitnessapp.done_workout_sets_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R

class DoneWorkoutSetsListAdapter(var values: ArrayList<DoneWorkoutSetsListItem>) : RecyclerView.Adapter<DoneWorkoutSetsListAdapter.DoneWorkoutSetsListViewHolder>() {
    override fun getItemCount(): Int {
        return values.size
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DoneWorkoutSetsListViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.done_workout_set_list_item, parent,false)
        return DoneWorkoutSetsListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DoneWorkoutSetsListViewHolder, position: Int) {
        holder.doneWorkoutSetsListItemRepetitions!!.setText("Repetitions: ${values[position].doneWorkoutSetsListItemRepetitions}")
        holder.doneWorkoutSetsListItemRest!!.setText("Rest: ${values[position].doneWorkoutSetsListItemRest}")
        holder.doneWorkoutSetsListItemWeight!!.setText("Weight: ${values[position].doneWorkoutSetsListItemWeight}")
    }

    class DoneWorkoutSetsListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var doneWorkoutSetsListItemRepetitions: TextView? = null
        var doneWorkoutSetsListItemRest: TextView? = null
        var doneWorkoutSetsListItemWeight: TextView? = null

        init {
            this.doneWorkoutSetsListItemRepetitions = itemView.findViewById(R.id.doneWorkoutSetRepetitionsNumber)
            this.doneWorkoutSetsListItemRest = itemView.findViewById(R.id.doneWorkoutSetRestNumber)
            this.doneWorkoutSetsListItemWeight = itemView.findViewById(R.id.doneWorkoutSetWeightNumber)
        }
    }
}

