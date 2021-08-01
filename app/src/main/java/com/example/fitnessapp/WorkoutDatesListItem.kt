package com.example.fitnessapp

open class WorkoutDatesListItem {
    var dateId: Int = 0
    var day: String = ""
    var month: String = ""
    var year: String = ""

    constructor(dateId: Int, day: String, month: String, year: String) {
        this.dateId = dateId
        this.day = day
        this.month = month
        this.year = year
    }
}