package com.example.fitnessapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutTasksManageAdapter(adapter: WorkoutTasksAdapter, context: Context, dragDirs: Int, swipeDirs: Int, var db: SQLiteDatabase) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    var workoutTasksAdapter = adapter
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        var values = workoutTasksAdapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
        CoroutineScope(Dispatchers.IO).launch {
            UpdateOrderNums(values)
        }
        return true
    }

    private suspend fun UpdateOrderNums(values: ArrayList<WorkoutTaskItem>) {
        for(i in 0 until values.size) {
            db.execSQL("UPDATE WorkoutTasks SET workoutTaskOrderNum = ${i + 1} WHERE workoutTaskId = ${values.get(i).workoutTaskId}")
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        workoutTasksAdapter.onItemDismiss(viewHolder.adapterPosition)
    }
}