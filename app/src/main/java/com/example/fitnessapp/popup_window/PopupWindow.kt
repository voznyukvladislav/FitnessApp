package com.example.fitnessapp.popup_window

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.R

abstract class PopupWindow(root: View) {

    var root: View = root

    var isShown: Boolean = false

    var animationShow: Animation = AnimationUtils.loadAnimation(root.context, R.anim.popup_show)
    var animationHide: Animation = AnimationUtils.loadAnimation(root.context, R.anim.popup_hide)

    abstract var popupWindow: ConstraintLayout
    abstract var acceptButton: Button
    abstract var cancelButton: Button

    fun show() {
        if(!this.isShown) {
            popupWindow.visibility = View.VISIBLE
            this.popupWindow.startAnimation(this.animationShow)
            //this.isShown = true
        }
    }

    fun hide() {
        if(this.isShown) {
            this.popupWindow.startAnimation(this.animationHide)
            //this.isShown = false
        }
    }

    init {
        this.animationShow.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                isShown = true
            }
            override fun onAnimationEnd(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        this.animationHide.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                popupWindow.visibility = View.GONE
                isShown = false
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }
}