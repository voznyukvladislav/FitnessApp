package com.example.fitnessapp.workout_sets_list

import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.R
import com.example.fitnessapp.popup_window.PopupWindow
import android.database.sqlite.SQLiteDatabase
import com.example.fitnessapp.sql.SQLUpdater

class WorkoutSetsListPopupEditWindow(root: View): PopupWindow(root) {

    var setRepetitionsEditText: EditText = root.findViewById(R.id.editSetRepetitionsNum)
    var setRestNumEditText: EditText = root.findViewById(R.id.editSetRestNum)
    var setWeightEditText: EditText = root.findViewById(R.id.editSetWeight)

    override var popupWindow: ConstraintLayout = root.findViewById(R.id.workoutSetsPopupEditWindow)
        get() = field
        set(value) { field = value }

    override var acceptButton: Button = root.findViewById(R.id.workoutSetPopupEditWindowAcceptButton)
        get() = field
        set(value) { field = value }

    override var cancelButton: Button = root.findViewById(R.id.workoutSetPopupEditWindowCancelButton)
        get() = field
        set(value) { field = value }

    fun updateWorkoutSet(db: SQLiteDatabase, workoutSetId: Int) {
        if(!this.setWeightEditText.text.toString().isNullOrBlank()) {
            SQLUpdater(db).updateWorkoutSet(workoutSetId,
                this.setRepetitionsEditText.text.toString().toInt(),
                this.setRestNumEditText.text.toString().toInt(),
                this.setWeightEditText.text.toString().toInt())
        } else {
            SQLUpdater(db).updateWorkoutSet(workoutSetId,
                this.setRepetitionsEditText.text.toString().toInt(),
                this.setRestNumEditText.text.toString().toInt(),
                0)
        }
    }

    fun setInputFieldValue(repetitions: Int,
                           rest: Int,
                           weight: Int) {
        this.setRepetitionsEditText.setText(repetitions.toString())
        this.setRestNumEditText.setText(rest.toString())
        this.setWeightEditText.setText(weight.toString())
    }
}