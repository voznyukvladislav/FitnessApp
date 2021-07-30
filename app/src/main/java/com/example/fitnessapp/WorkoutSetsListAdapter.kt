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

        init {
            workoutSetRepetitions = itemView.findViewById(R.id.repetitionsNumber)
            workoutSetRest = itemView.findViewById(R.id.restNumber)
        }
    }
}

class WorkoutSetsListItem {
    var workoutSetId: Int = 0
        get() = field
        set(value) { field = value }

    var workoutSetRepetitions: Int = 0
        get() = field
        set(value) { field = value }

    var workoutSetRest: Int = 0
        get() = field
        set(value) { field = value }

    var workoutSetOrderNum: Int = 0
        get() = field
        set(value) { field = value }

    constructor(workoutSetId: Int,
                workoutSetRepetitions: Int,
                workoutSetRest: Int,
                workoutSetOrderNum: Int) {
        this.workoutSetId = workoutSetId
        this.workoutSetRepetitions = workoutSetRepetitions
        this.workoutSetRest = workoutSetRest
        this.workoutSetOrderNum = workoutSetOrderNum
    }
}

class WorkoutSetsListAdapterManager(var adapter: WorkoutSetsListAdapter,
                                     dragDirs: Int,
                                     swipeDirs: Int,
                                     var db: SQLiteDatabase
): ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
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

    fun updateOrderNums(values: ArrayList<WorkoutSetsListItem>) {
        for(i in 0 until values.size) {
            db.execSQL("UPDATE workoutSets SET workoutSetOrderNum = ${i + 1} WHERE workoutSetId = ${values[i].workoutSetId}")
        }
    }
}