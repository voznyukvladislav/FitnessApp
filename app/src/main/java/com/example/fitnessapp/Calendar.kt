package com.example.fitnessapp

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import android.database.sqlite.SQLiteDatabase
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class Calendar : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_calendar, container, false)
        val activity = getActivity()

        val db = root.context.openOrCreateDatabase("workout.db", Context.MODE_PRIVATE, null)

        var widget: MaterialCalendarView = root.findViewById(R.id.progressionCalendar)

        val workoutDatesList = getDates(db)
        for (i in workoutDatesList) {
            var mydate = CalendarDay.from(i.year.toInt(),  i.month.toInt(), i.day.toInt()) // year, month, date
            widget.addDecorators(CurrentDayDecorator(activity, mydate))
        }


        val doneWorkoutsListRecyclerView: RecyclerView = root.findViewById(R.id.doneWorkoutsList)
        val dataSet = getDoneWorkoutsList(db)

        val manager = LinearLayoutManager(this.requireContext())
        doneWorkoutsListRecyclerView.layoutManager = manager

        val adapter = DoneWorkoutsListAdapter(dataSet)
        doneWorkoutsListRecyclerView.adapter = adapter



        return root
    }
}

class CurrentDayDecorator(context: Activity?, currentDay: CalendarDay) : DayViewDecorator {
    private val drawable: Drawable?
    var myDay = currentDay
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == myDay
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable!!)
    }

    init {
        // You can set background for Decorator via drawable here
        drawable = ContextCompat.getDrawable(context!!, R.drawable.calendar_mark)
    }
}

fun getDates(db: SQLiteDatabase): ArrayList<WorkoutDatesListItem> {
    val datesList: ArrayList<WorkoutDatesListItem> = arrayListOf()
    val cursor = db.rawQuery("SELECT * FROM workoutDates", null)

    var day = ""
    var month = ""
    var year = ""
    while(cursor.moveToNext()) {
        day = cursor.getString(2)
        month = cursor.getString(3)
        year = cursor.getString(4)

        datesList.add(WorkoutDatesListItem(day, month, year))
    }
    return datesList
}

class WorkoutDatesListItem {
    var day: String = ""
    var month: String = ""
    var year: String = ""

    constructor(day: String, month: String, year: String) {
        this.day = day
        this.month = month
        this.year = year
    }
}

fun getDoneWorkoutsList(db: SQLiteDatabase): ArrayList<DoneWorkoutsListItem> {
    val dataSet = arrayListOf<DoneWorkoutsListItem>()

    val cursor = db.rawQuery("SELECT * FROM workoutDates", null)

    return dataSet
}