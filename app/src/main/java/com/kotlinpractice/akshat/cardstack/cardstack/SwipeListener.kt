package com.kotlinpractice.akshat.cardstack.cardstack

import android.animation.Animator
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.OvershootInterpolator

/**
 * Created by aaron on 4/12/2015.
 */
class SwipeListener : View.OnTouchListener {

    private var ROTATION_DEGREES = 30f
    internal var OPACITY_END = 0.33f
    private var initialX: Float = 0.toFloat()
    private var initialY: Float = 0.toFloat()

    private var mActivePointerId: Int = 0
    private var initialXPress: Float = 0.toFloat()
    private var initialYPress: Float = 0.toFloat()
    private var parent: ViewGroup? = null
    private var parentWidth: Float = 0.toFloat()
    private var parentHeight: Float = 0.toFloat()
    private var paddingLeft: Int = 0

    private var card: View? = null
    internal var callback: SwipeCallback
    private var deactivated: Boolean = false

    private var click = true


    constructor(card: View, callback: SwipeCallback, initialX: Float, initialY: Float, rotation: Float, opacityEnd: Float) {
        this.card = card
        this.initialX = initialX
        this.initialY = initialY
        this.callback = callback
        this.parent = card.parent as ViewGroup
        this.parentWidth = parent!!.width.toFloat()
        this.parentHeight = parent!!.height.toFloat()
        this.ROTATION_DEGREES = rotation
        this.OPACITY_END = opacityEnd
        this.paddingLeft = (card.parent as ViewGroup).paddingLeft
    }

    constructor(card: View, callback: SwipeCallback, initialX: Float, initialY: Float, rotation: Float, opacityEnd: Float, screenWidth: Int, screenHeight: Int) {
        this.card = card
        this.initialX = initialX
        this.initialY = initialY
        this.callback = callback
        this.parent = card.parent as ViewGroup
        this.parentWidth = screenWidth.toFloat()
        this.parentHeight = screenHeight.toFloat()
        this.ROTATION_DEGREES = rotation
        this.OPACITY_END = opacityEnd
        this.paddingLeft = (card.parent as ViewGroup).paddingLeft
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (deactivated) return false
        when (event.action and MotionEvent.ACTION_MASK) {

            MotionEvent.ACTION_DOWN -> {
                click = true
                //gesture has begun
                val x: Float
                val y: Float
                //cancel any current animations
                v.clearAnimation()

                mActivePointerId = event.getPointerId(0)

                x = event.x
                y = event.y

                if (event.findPointerIndex(mActivePointerId) == 0) {
                    callback.cardActionDown()
                }

                initialXPress = x
                initialYPress = y
            }

            MotionEvent.ACTION_MOVE -> {
                //gesture is in progress

                val pointerIndex = event.findPointerIndex(mActivePointerId)
                //Log.i("pointer index: " , Integer.toString(pointerIndex));
                if (pointerIndex < 0 || pointerIndex > 0) {
                    //Do Nothing
                }

                val xMove = event.getX(pointerIndex)
                val yMove = event.getY(pointerIndex)

                //calculate distance moved
                val dx = xMove - initialXPress
                val dy = yMove - initialYPress

                //calc rotation here
                val posX = card!!.x + dx
                val posY = card!!.y + dy

                //in this circumstance consider the motion a click
                if (Math.abs(dx + dy) > 5) click = false

                card!!.x = posX
                card!!.y = posY

                //card.setRotation
                val distobjectX = posX - initialX
                val rotation = ROTATION_DEGREES * 2f * distobjectX / parentWidth
                card!!.rotation = rotation

            }

            MotionEvent.ACTION_UP -> {
                //gesture has finished
                //check to see if card has moved beyond the left or right bounds or reset
                //card position
                checkCardForEvent()

                if (event.findPointerIndex(mActivePointerId) == 0) {
                    callback.cardActionUp()
                }
                //check if this is a click event and then perform a click

                if (click) v.performClick()
            }

            else -> return false
        }//if(click) return false;
        return true
    }

    fun checkCardForEvent() {

        if (cardBeyondLeftBorder()) {
            animateOffScreenLeft(160)
                    .setListener(object : Animator.AnimatorListener {

                        override fun onAnimationStart(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {

                            callback.cardOffScreen()
                        }

                        override fun onAnimationCancel(animation: Animator) {

                        }

                        override fun onAnimationRepeat(animation: Animator) {}
                    })
            callback.cardSwipedLeft()
            this.deactivated = true
        } else if (cardBeyondRightBorder()) {
            animateOffScreenRight(160)
                    .setListener(object : Animator.AnimatorListener {

                        override fun onAnimationStart(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {
                            callback.cardOffScreen()
                        }

                        override fun onAnimationCancel(animation: Animator) {

                        }

                        override fun onAnimationRepeat(animation: Animator) {

                        }
                    })
            callback.cardSwipedRight()
            this.deactivated = true
        } else {
            resetCardPosition()
        }
    }

    private fun cardBeyondLeftBorder(): Boolean {
        //check if cards middle is beyond the left quarter of the screen
        return card!!.x + card!!.width / 2 < parentWidth / 4f
    }

    private fun cardBeyondRightBorder(): Boolean {
        //check if card middle is beyond the right quarter of the screen
        return card!!.x + card!!.width / 2 > parentWidth / 4f * 3
    }

    private fun cardBeyondTopBorder(): Boolean {
        //check if cards middle is beyond the left quarter of the screen
        return card!!.y + card!!.height / 2 < parentHeight / 4f
    }

    private fun cardBeyondBottomBorder(): Boolean {
        //check if card middle is beyond the right quarter of the screen
        return card!!.y + card!!.height / 2 > parentHeight / 4f * 3
    }

    private fun resetCardPosition(): ViewPropertyAnimator {

        return card!!.animate()
                .setDuration(200)
                .setInterpolator(OvershootInterpolator(1.5f))
                .x(initialX)
                .y(initialY)
                .rotation(0f)
    }

    fun animateOffScreenLeft(duration: Int): ViewPropertyAnimator {
        return card!!.animate()
                .setDuration(duration.toLong())
                .x(-parentWidth)
                .y(0f)
                .rotation(-30f)
    }


    fun animateOffScreenRight(duration: Int): ViewPropertyAnimator {
        return card!!.animate()
                .setDuration(duration.toLong())
                .x(parentWidth * 2)
                .y(0f)
                .rotation(30f)
    }

    interface SwipeCallback {
        fun cardSwipedLeft()
        fun cardSwipedRight()
        fun cardOffScreen()
        fun cardActionDown()
        fun cardActionUp()
    }
}
