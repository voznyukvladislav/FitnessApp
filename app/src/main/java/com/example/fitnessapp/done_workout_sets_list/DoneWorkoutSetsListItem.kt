package com.example.fitnessapp.done_workout_sets_list

class DoneWorkoutSetsListItem {
    var doneWorkoutSetsListItemId: Int = 0
    get() = field
    private set(value) { field = value }

    var doneWorkoutSetsListItemRepetitions: Int = 0
    get() = field
    private set(value) { field = value }

    var doneWorkoutSetsListItemRest: Int = 0
    get() = field
    private set(value) { field = value }

    var doneWorkoutSetsListItemWeight: Int = 0
    get() = field
    private set(value) { field = value }

    constructor()

    constructor(doneWorkoutSetsListItemId: Int,
                doneWorkoutSetsListItemRepetitions: Int,
                doneWorkoutSetsListItemRest: Int,
                doneWorkoutSetsListItemWeight: Int) {
        this.doneWorkoutSetsListItemId = doneWorkoutSetsListItemId
        this.doneWorkoutSetsListItemRepetitions = doneWorkoutSetsListItemRepetitions
        this.doneWorkoutSetsListItemRest = doneWorkoutSetsListItemRest
        this.doneWorkoutSetsListItemWeight = doneWorkoutSetsListItemWeight
    }
}