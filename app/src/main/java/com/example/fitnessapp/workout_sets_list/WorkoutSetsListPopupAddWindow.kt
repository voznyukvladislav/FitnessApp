package com.example.fitnessapp.workout_sets_list

import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.R
import com.example.fitnessapp.popup_window.PopupWindow
import android.database.sqlite.SQLiteDatabase
import com.example.fitnessapp.sql.SQLInserter

class WorkoutSetsListPopupAddWindow(root: View) : PopupWindow(root) {

    var setRepetitionsEditText: EditText = root.findViewById(R.id.newSetRepetitionsNum)
    var setRestNumEditText: EditText = root.findViewById(R.id.newSetRestNum)
    var setWeightEditText: EditText = root.findViewById(R.id.newSetWeight)

    override var popupWindow: ConstraintLayout = root.findViewById(R.id.workoutSetsPopupAddWindow)
        get() = field
        set(value) { field = value }

    override var acceptButton: Button = root.findViewById(R.id.workoutSetPopupAddWindowAcceptButton)
        get() = field
        set(value) { field = value }

    override var cancelButton: Button = root.findViewById(R.id.workoutSetPopupAddWindowCancelButton)
        get() = field
        set(value) { field = value }

    fun clearInputFields() {
        this.setRepetitionsEditText.setText("")
        this.setRestNumEditText.setText("")
        this.setWeightEditText.setText("")
    }

    fun addWorkoutSet(db: SQLiteDatabase, workoutTaskId: Int) {
        if(!this.setWeightEditText.text.toString().isNullOrBlank()) {
            SQLInserter(db).insertWorkoutSet(workoutTaskId,
                this.setRepetitionsEditText.text.toString().toInt(),
                this.setRestNumEditText.text.toString().toInt(),
                this.setWeightEditText.text.toString().toInt())
        } else {
            SQLInserter(db).insertWorkoutSet(workoutTaskId,
                this.setRepetitionsEditText.text.toString().toInt(),
                this.setRestNumEditText.text.toString().toInt(),
                0)
        }
    }
}