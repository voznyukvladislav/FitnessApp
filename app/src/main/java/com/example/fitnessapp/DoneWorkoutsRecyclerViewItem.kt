package com.example.fitnessapp

private val TYPE_DONE_WORKOUT = 1
private val TYPE_DATE = 2

class DoneWorkoutsRecyclerViewItem {

    var doneWorkoutId: Int = 0
    get() = field
    private set(value) { field = value }

    var doneWorkoutName: String = ""
    get() = field
    private set(value) { field = value }

    var doneWorkoutDateDay: String = ""
    get() = field
    private set(value) { field = value }

    var doneWorkoutDateMonth: String = ""
    get() = field
    private set(value) { field = value }

    var doneWorkoutDateYear: String = ""
    get() = field
    private set(value) { field = value }

    var inListType: Int = 0
    get() = field
    private set(value) { field = value }

    constructor(doneWorkoutId: Int, doneWorkoutName: String) {
        this.doneWorkoutId = doneWorkoutId
        this.doneWorkoutName = doneWorkoutName

        this.inListType = TYPE_DONE_WORKOUT
    }

    constructor(doneWorkoutDateDay: String, doneWorkoutDateMonth: String, doneWorkoutDateYear: String) {
        this.doneWorkoutDateDay = doneWorkoutDateDay
        this.doneWorkoutDateMonth = doneWorkoutDateMonth
        this.doneWorkoutDateYear = doneWorkoutDateYear

        this.inListType = TYPE_DATE
    }
}