package com.example.fitnessapp.workout_list

import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.*
import com.example.fitnessapp.popup_window.PopupWindow
import com.example.fitnessapp.sql.SQLUpdater

class WorkoutListPopupEditWindow(root: View) : PopupWindow(root) {

    var workoutNameEditText: EditText = root.findViewById(R.id.editWorkoutName)

    override var popupWindow: ConstraintLayout = root.findViewById(R.id.workoutListEditPopupWindow)
        get() = field
        set(value) { field = value }

    override var acceptButton: Button = root.findViewById(R.id.editWorkoutsPopupEditWindowButton)
        get() = field
        set(value) { field = value }

    override var cancelButton: Button = root.findViewById(R.id.cancelWorkoutsListPopupEditWindowButton)
        get() = field
        set(value) { field = value }

    fun updateWorkoutListItem(db: SQLiteDatabase, id: Int) {
        SQLUpdater(db).updateWorkout(id, this.workoutNameEditText.text.toString())
    }

    fun setInputFieldValue(value: String) {
        this.workoutNameEditText.setText(value)
    }

    init {
        this.cancelButton.setOnClickListener {
            this.hide()
            KeyboardHider(root).hideKeyboard()
        }
    }
}
