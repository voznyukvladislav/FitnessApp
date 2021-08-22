package com.example.fitnessapp

import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.database.sqlite.SQLiteDatabase
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.fitness_list.FitnessListItem
import com.example.fitnessapp.sql.SQLInserter
import com.example.fitnessapp.sql.SQLListGetter
import java.text.SimpleDateFormat
import java.util.Calendar

private val TYPE_HEADER = 1
private val TYPE_ITEM = 2
private val TYPE_SAVE_BUTTON = 3

class FitnessListAdapter(val root: View, var values: ArrayList<FitnessListItem>, val workoutId: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

                val db = root.context.openOrCreateDatabase("workout.db", Context.MODE_PRIVATE, null)

                val dateFormatYear = SimpleDateFormat("yyyy")
                val year = dateFormatYear.format(Calendar.getInstance().time)

                val dateFormatMonth = SimpleDateFormat("M")
                val month = dateFormatMonth.format(Calendar.getInstance().time)

                val dateFormatDay = SimpleDateFormat("d")
                val day = dateFormatDay.format(Calendar.getInstance().time)

                val currentDateId = insertDate(db, day, month, year)

                // Saving done workout in db
                val workoutCursor = db.rawQuery("SELECT workoutName FROM workouts WHERE workoutId = ${workoutId}", null)
                var doneWorkoutName = ""
                while(workoutCursor.moveToNext()) {
                    doneWorkoutName = workoutCursor.getString(0)
                }
                workoutCursor.close()

                val sqlInserter = SQLInserter(db)
                sqlInserter.insertDoneWorkout(db, doneWorkoutName)
                val doneWorkoutItem = sqlInserter.getInsertedDoneWorkout()

                val listGetter = SQLListGetter(db)
                val workoutTasksList = listGetter.getWorkoutTasksList(workoutId)
                val doneWorkoutTasksList: ArrayList<DoneWorkoutTasksListItem> = arrayListOf()
                for(i in 0 until workoutTasksList.size) {
                    sqlInserter.insertDoneWorkoutTask(db,
                        doneWorkoutItem.doneWorkoutId,
                        workoutTasksList[i].workoutTasksListItemName)
                    doneWorkoutTasksList.add(sqlInserter.getInsertedDoneWorkoutTask())
                }


                val doneWorkoutSetsList: ArrayList<ArrayList<DoneWorkoutSetsListItem>> = arrayListOf()
                for(i in 0 until doneWorkoutTasksList.size) {
                    val workoutSetsList = listGetter.getWorkoutSetsList(workoutTasksList[i].workoutTasksListItemId)
                    doneWorkoutSetsList.add(arrayListOf())
                    for(j in 0 until workoutSetsList.size) {
                        sqlInserter.insertDoneWorkoutSet(db,
                            doneWorkoutTasksList[i].doneWorkoutTaskListItemId,
                            workoutSetsList[j].workoutSetRepetitions,
                            workoutSetsList[j].workoutSetRest,
                            workoutSetsList[j].workoutSetWeight)
                        doneWorkoutSetsList[i].add(sqlInserter.getInsertedDoneWorkoutSet())
                    }
                }

                db.execSQL("INSERT INTO doneWorkoutsCalendar VALUES (NULL, ${doneWorkoutItem.doneWorkoutId}, ${currentDateId})")
            }
        }
    }

    // Inserts current date in database and returns its id
    private fun insertDate(db: SQLiteDatabase, day: String, month: String, year: String): Int {
        var dateId = 0
        if(isDateExists(db, day, month, year)) {
            val dateCursor = db.rawQuery("SELECT calendarDateId FROM calendarDates" +
                    " WHERE calendarDateDay = ${day}" +
                    " AND calendarDateMonth = ${month}" +
                    " AND calendarDateYear = ${year}", null)
            while(dateCursor.moveToNext()) {
                dateId = dateCursor.getInt(0)
            }
            dateCursor.close()
        } else {
            db.execSQL("INSERT INTO calendarDates VALUES (NULL, ${day}, ${month}, ${year})")

            val dateCursor = db.rawQuery("SELECT MAX(calendarDateId) FROM calendarDates", null)
            while(dateCursor.moveToNext()) {
                dateId = dateCursor.getInt(0)
            }
            dateCursor.close()
        }
        return dateId
    }

    private fun isDateExists(db: SQLiteDatabase, day: String, month: String, year: String): Boolean {
        // Checking current date existence in db
        val checkIfDateExistsCursor = db.rawQuery("SELECT EXISTS (SELECT * FROM calendarDates " +
                "WHERE calendarDateDay = ${day}" +
                " AND calendarDateMonth = ${month}" +
                " AND calendarDateYear = ${year})", null)
        while(checkIfDateExistsCursor.moveToNext()) {
            return checkIfDateExistsCursor.getInt(0) == 1
        }
        return false
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

    private fun getMinutesAndSeconds(milliseconds: Long): String {
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
    var repetitionsNum: TextView? = null
    var restNum: TextView? = null
    var weightNum: TextView? = null
    var doneTaskButton: Button? = null
    init {
        repetitionsNum = itemView.findViewById(R.id.repetitionsNum)
        restNum = itemView.findViewById(R.id.restNum)
        weightNum = itemView.findViewById(R.id.weightNum)
        doneTaskButton = itemView.findViewById(R.id.doneTaskButton)
    }

    fun setItemInformation(item: FitnessListItem, setNum: Int) {
        this.repetitionsNum!!.text = "Repetitions: ${item.workoutRepetitions.toString()}"
        this.restNum!!.text = "Rest: ${item.workoutRest.toString()}"
        this.weightNum!!.text = "Weight: ${item.workoutSetWeight}"
    }
}

class FitnessListDoneButtonViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var saveDoneWorkoutButton: Button? = null
    init {
        saveDoneWorkoutButton = itemView.findViewById(R.id.saveDoneWorkout)
    }
}