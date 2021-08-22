package com.example.fitnessapp.fragments

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
import com.example.fitnessapp.*
import com.example.fitnessapp.sql.SQLListGetter


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
        val dataSet = getDoneWorkoutsListRecyclerView(db, workoutDatesList)

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

fun getDoneWorkoutsListRecyclerView(db: SQLiteDatabase, dates: ArrayList<WorkoutDatesListItem>): ArrayList<DoneWorkoutsRecyclerViewItem> {
    val dataSet: ArrayList<DoneWorkoutsRecyclerViewItem> = arrayListOf()

    val listGetter = SQLListGetter(db)
    for(i in 0 until dates.size) {
        dataSet.add(DoneWorkoutsRecyclerViewItem(dates[i].day, dates[i].month, dates[i].year))
        val relatedDoneWorkouts = listGetter.getDoneWorkoutsFromDate(dates[i].dateId)

        for(j in 0 until relatedDoneWorkouts.size) {
            dataSet.add(DoneWorkoutsRecyclerViewItem(relatedDoneWorkouts[j].doneWorkoutId, relatedDoneWorkouts[j].doneWorkoutName))
        }
    }
    return dataSet
}

fun getDates(db: SQLiteDatabase): ArrayList<WorkoutDatesListItem> {
    val datesList: ArrayList<WorkoutDatesListItem> = arrayListOf()
    val cursor = db.rawQuery("SELECT * FROM calendarDates ORDER BY calendarDateId DESC", null)

    var dateId = 0
    var day = ""
    var month = ""
    var year = ""
    while(cursor.moveToNext()) {
        dateId = cursor.getInt(0)
        day = cursor.getString(1)
        month = cursor.getString(2)
        year = cursor.getString(3)

        datesList.add(WorkoutDatesListItem(dateId, day, month, year))
    }
    cursor.close()
    return datesList
}