package com.example.fitnessapp.workout_list

import android.view.View
import android.widget.EditText
import android.database.sqlite.SQLiteDatabase
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.*
import com.example.fitnessapp.popup_window.PopupWindow
import com.example.fitnessapp.sql.SQLInserter

class WorkoutsListPopupAddWindow(root: View): PopupWindow(root) {

    var workoutNameEditText: EditText = root.findViewById(R.id.newWorkoutName)

    override var popupWindow: ConstraintLayout = root.findViewById(R.id.workoutListPopupWindow)
        get() = field
        set(value) { field = value }

    override var acceptButton: Button = root.findViewById(R.id.addWorkoutsPopupWindowButton)
        get() = field
        set(value) { field = value }

    override var cancelButton: Button = root.findViewById(R.id.cancelWorkoutsPopupWindowButton)
        get() = field
        set(value) { field = value }

    fun clearInputFields() {
        this.workoutNameEditText.setText("")
    }

    fun add(db: SQLiteDatabase) {
        SQLInserter(db).insertWorkout(this.workoutNameEditText.text.toString())
    }

    init {
        this.cancelButton.setOnClickListener {
            this.hide()
            this.clearInputFields()
            KeyboardHider(root).hideKeyboard()
        }
    }
}