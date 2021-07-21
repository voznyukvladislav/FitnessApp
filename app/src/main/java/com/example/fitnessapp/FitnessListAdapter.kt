package com.example.fitnessapp

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

val TYPE_HEADER = 1
val TYPE_ITEM = 2
val TYPE_SAVE_BUTTON = 3

class FitnessListAdapter(val root: View, var values: ArrayList<FitnessListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return values.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var itemView: View
        if(viewType == TYPE_HEADER) {
            itemView = LayoutInflater.from(parent?.context).inflate(R.layout.fitness_list_header_item, parent, false)
            return FitnessListHeaderViewHolder(itemView)
        } else if(viewType == TYPE_ITEM) {
            itemView = LayoutInflater.from(parent?.context).inflate(R.layout.fitness_list_item, parent, false)
            return FitnessListItemViewHolder(itemView)
        } else {
            itemView = LayoutInflater.from(parent?.context).inflate(R.layout.fitness_list_workout_done_button, parent, false)
            return FitnessListDoneButtonViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == TYPE_HEADER) {
            (holder as FitnessListHeaderViewHolder).setHeaderInformation(values[position])
        } else if(getItemViewType(position) == TYPE_ITEM) {
            (holder as FitnessListItemViewHolder).setItemInformation(values[position], position + 1)

            // Done set button listener
            (holder as FitnessListItemViewHolder).doneTaskButton!!.setOnClickListener {

                (holder as FitnessListItemViewHolder).doneTaskButton!!.setEnabled(false)
                //(holder as FitnessListItemViewHolder).doneTaskButton!!.setBackgroundColor(R.color.black)

                val animationShow: Animation = AnimationUtils.loadAnimation(root.context, R.anim.popup_show)
                val animationHide: Animation = AnimationUtils.loadAnimation(root.context, R.anim.popup_hide)

                val timerWindow: ConstraintLayout = root.findViewById(R.id.timerWindow)

                timerWindow.visibility = View.VISIBLE
                timerWindow.startAnimation(animationShow)

                val timerRemainingTextView: TextView = root.findViewById(R.id.timerText)
                val timerSkipButton: Button = root.findViewById(R.id.timerSkip)

                val timer = object: CountDownTimer(values[position].workoutRest.toLong() * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val timerText = getMinutesAndSeconds(millisUntilFinished)
                        timerRemainingTextView.setText("${timerText}")
                    }

                    override fun onFinish() {
                        timerWindow.visibility = View.GONE
                        timerWindow.startAnimation(animationHide)
                    }
                }.start()

                timerSkipButton.setOnClickListener {
                    timer.cancel()
                    timer.onFinish()
                }
            }
        } else {
            (holder as FitnessListDoneButtonViewHolder).saveDoneWorkoutButton!!.setOnClickListener {
                val animationHide: Animation = AnimationUtils.loadAnimation(root.context, R.anim.popup_hide)
                val recyclerView: RecyclerView = root.findViewById(R.id.fitnessRecyclerView)

                recyclerView.visibility = View.GONE
                recyclerView.startAnimation(animationHide)

                //TODO("Save done workouts inside database")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(values[position].inListType == TYPE_HEADER) {
            return TYPE_HEADER
        } else if(values[position].inListType == TYPE_ITEM) {
            return TYPE_ITEM
        } else {
            return TYPE_SAVE_BUTTON
        }
    }

    fun getMinutesAndSeconds(milliseconds: Long): String {
        val secondsFromMilliseconds = milliseconds / 1000

        var strMinutes = ""
        var strSeconds = ""

        val minutes: Long = secondsFromMilliseconds / 60
        val seconds: Long = secondsFromMilliseconds - (minutes * 60)

        if(minutes < 10) {
            strMinutes = "0${minutes}"
        } else {
            strMinutes = "${minutes}"
        }

        if(seconds < 10) {
            strSeconds = "0${seconds}"
        } else {
            strSeconds = "${seconds}"
        }

        val timerText = "${strMinutes} : ${strSeconds}"
        return timerText
    }

}

class FitnessListHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var headerTextView: TextView? = null
    init {
        headerTextView = itemView.findViewById(R.id.fitnessListHeaderTextView)
    }

    fun setHeaderInformation(item: FitnessListItem) {
        this.headerTextView!!.text = "Task name: ${item.workoutTaskName}"
    }
}

class FitnessListItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var setNum: TextView? = null
    var repetitionsNum: TextView? = null
    var restNum: TextView? = null
    var doneTaskButton: Button? = null
    init {
        setNum = itemView.findViewById(R.id.setNum)
        repetitionsNum = itemView.findViewById(R.id.repetitionsNum)
        restNum = itemView.findViewById(R.id.restNum)
        doneTaskButton = itemView.findViewById(R.id.doneTaskButton)
    }

    fun setItemInformation(item: FitnessListItem, setNum: Int) {
        this.setNum!!.text = "Set num: ${setNum.toString()}"
        this.repetitionsNum!!.text = "Repetitions: ${item.workoutRepetitions.toString()}"
        this.restNum!!.text = "Rest: ${item.workoutRest.toString()}"
    }
}

class FitnessListDoneButtonViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var saveDoneWorkoutButton: Button? = null
    init {
        saveDoneWorkoutButton = itemView.findViewById(R.id.saveDoneWorkout)
    }
}

class FitnessListItem {
    var workoutTaskName: String = ""
        get() = field
        private set(value) { field = value }

    var workoutSetNum: Int = 0
        get() = field
        private set(value) { field = value }

    var workoutRepetitions: Int = 0
        get() = field
        private set(value) { field = value }

    var workoutRest: Int = 0
        get() = field
        private set(value) { field = value }

    var inListType: Int = 0
        get() = field
        private set(value) { field = value }

    // If inputting ony name in list type will be TYPE_HEADER
    constructor(workoutTaskName: String) {
        this.workoutTaskName = workoutTaskName
        this.inListType = TYPE_HEADER
    }

    // If inputting few parameters in list type will be TYPE_ITEM
    constructor(workoutSetNum: Int, workoutRepetitions: Int, workoutRest: Int) {
        this.workoutSetNum = workoutSetNum
        this.workoutRepetitions = workoutRepetitions
        this.workoutRest = workoutRest
        this.inListType = TYPE_ITEM
    }

    // Constructor without arguments for button
    constructor() {
        this.inListType = TYPE_SAVE_BUTTON
    }
}