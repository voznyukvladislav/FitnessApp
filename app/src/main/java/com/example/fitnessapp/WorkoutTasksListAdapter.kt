package com.example.fitnessapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.media.MediaRouter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutTasksListAdapter(var values: ArrayList<WorkoutTasksListItem>,
                              var db: SQLiteDatabase,
                              var workoutId: Int,
                              var navController: NavController): RecyclerView.Adapter<WorkoutTasksListAdapter.WorkoutTasksListViewHolder>() {
    override fun getItemCount(): Int {
        return values.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutTasksListViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.workout_task_list_item, parent, false)
        return WorkoutTasksListAdapter.WorkoutTasksListViewHolder(itemView)
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
        init {
            workoutTasksListItemName = itemView.findViewById(R.id.workoutTaskName)
            workoutTasksListItemTotalSets = itemView.findViewById(R.id.workoutTotalSets)
            workoutTasksListItemTotalRepetitions = itemView.findViewById(R.id.workoutTotalRepetitions)
        }
    }
}

class WorkoutTasksListItem {
    var workoutTasksListItemId: Int = 0
        get() = field
        set(value) { field = value }
    var workoutTasksListItemName: String = ""
        get() = field
        set(value) { field = value }

    var workoutTasksListItemTotalSets: Int = 0
        get() = field
        set(value) { field = value }

    var workoutTasksListItemTotalRepetitions: Int = 0
        get() = field
        set(value) { field = value }

    constructor(workoutTasksListItemId: Int,
                workoutTasksListItemName: String,
                workoutTasksListItemTotalSets: Int,
                workoutTasksListItemTotalRepetitions: Int) {
        this.workoutTasksListItemId = workoutTasksListItemId
        this.workoutTasksListItemName = workoutTasksListItemName
        this.workoutTasksListItemTotalSets = workoutTasksListItemTotalSets
        this.workoutTasksListItemTotalRepetitions = workoutTasksListItemTotalRepetitions
    }

    constructor(workoutTasksListItemId: Int, workoutTasksListItemName: String) {
        this.workoutTasksListItemId = workoutTasksListItemId
        this.workoutTasksListItemName = workoutTasksListItemName
    }
}

class WorkoutTasksListAdapterManager(var adapter: WorkoutTasksListAdapter,
                                     dragDirs: Int,
                                     swipeDirs: Int,
                                     var db: SQLiteDatabase): ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        var values = adapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
        CoroutineScope(Dispatchers.IO).launch {
            updateOrderNums(values)
        }
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    fun updateOrderNums(values: ArrayList<WorkoutTasksListItem>) {
        for(i in 0 until values.size) {
            db.execSQL("UPDATE workoutTasks SET workoutTaskOrderNum = ${i + 1} WHERE workoutTaskId = ${values[i].workoutTasksListItemId}")
        }
    }
}