package com.example.fitnessapp.workout_tasks_list

import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.R
import com.example.fitnessapp.popup_window.PopupWindow
import android.database.sqlite.SQLiteDatabase
import com.example.fitnessapp.sql.SQLUpdater

class WorkoutTasksListPopupEditWindow(root: View) : PopupWindow(root) {

    var editWorkoutTaskNameEditText: EditText = root.findViewById(R.id.editWorkoutTaskName)

    override var popupWindow: ConstraintLayout = root.findViewById(R.id.workoutTaskPopupEditWindow)
        get() = field
        set(value) { field = value }

    override var acceptButton: Button = root.findViewById(R.id.workoutTaskPopupEditWindowAcceptButton)
        get() = field
        set(value) { field = value }

    override var cancelButton: Button = root.findViewById(R.id.workoutTaskPopupEditWindowCancelButton)
        get() = field
        set(value) { field = value }

    fun setInputFieldValue(value: String) {
        this.editWorkoutTaskNameEditText.setText(value)
    }

    fun updateWorkoutTask(db: SQLiteDatabase, workoutTaskId: Int) {
        SQLUpdater(db).updateWorkoutTask(workoutTaskId, this.editWorkoutTaskNameEditText.text.toString())
    }
}