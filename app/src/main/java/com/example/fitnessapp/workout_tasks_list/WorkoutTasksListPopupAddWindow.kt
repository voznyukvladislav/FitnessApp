package com.example.fitnessapp.workout_tasks_list

import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.popup_window.PopupWindow
import com.example.fitnessapp.R
import com.example.fitnessapp.sql.SQLInserter

class WorkoutTasksListPopupAddWindow(root: View) : PopupWindow(root) {

    var newWorkoutTaskName: EditText = root.findViewById(R.id.newWorkoutTaskName)

    override var popupWindow: ConstraintLayout = root.findViewById(R.id.workoutTaskPopupAddWindow)
        get() = field
        set(value) { field = value }

    override var acceptButton: Button = root.findViewById(R.id.workoutTaskPopupAddWindowAcceptButton)
        get() = field
        set(value) { field = value }

    override var cancelButton: Button = root.findViewById(R.id.workoutTaskPopupAddWindowCancelButton)
        get() = field
        set(value) { field = value }

    fun addWorkoutListItem(db: SQLiteDatabase, workoutId: Int) {
        SQLInserter(db).insertWorkoutTask(workoutId, this.newWorkoutTaskName.text.toString())
    }

    fun clearInputFields() {
        this.newWorkoutTaskName.setText("")
    }
}