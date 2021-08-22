package com.example.fitnessapp.workout_tasks_list

import android.database.sqlite.SQLiteDatabase
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutTasksListAdapterManager(var adapter: WorkoutTasksListAdapter,
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

    fun updateOrderNums(values: ArrayList<WorkoutTasksListItem>) {
        for(i in 0 until values.size) {
            db.execSQL("UPDATE workoutTasks SET workoutTaskOrderNum = ${i + 1} WHERE workoutTaskId = ${values[i].workoutTasksListItemId}")
        }
    }
}