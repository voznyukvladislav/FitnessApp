package com.example.fitnessapp

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class KeyboardHider(root: View) {
    var root = root

    fun hideKeyboard() {
        if(root != null) {
            val imm: InputMethodManager = root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(root.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}