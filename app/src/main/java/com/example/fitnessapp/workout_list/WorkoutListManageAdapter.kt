package com.example.fitnessapp.workout_list

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutListManageAdapter(adapter: WorkoutListAdapter, context: Context, dragDirs: Int, swipeDirs: Int, var db: SQLiteDatabase) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    var workoutTasksAdapter = adapter
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val values = workoutTasksAdapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
        CoroutineScope(Dispatchers.IO).launch {
            UpdateOrderNums(values)
        }
        return true
    }

    private suspend fun UpdateOrderNums(values: ArrayList<WorkoutListItem>) {
        for(i in 0 until values.size) {
            db.execSQL("UPDATE workouts SET workoutOrderNum = ${i + 1} WHERE workoutId = ${values.get(i).getId()}")
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        workoutTasksAdapter.onItemDismiss(viewHolder.adapterPosition)
    }
}