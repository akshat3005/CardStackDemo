package com.kotlinpractice.akshat.cardstack.cardstack

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import java.util.*

class CustomLayoutFinal : FrameLayout {

    constructor(context: Context) : super(context) {
        clipChildren = false
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        clipChildren = false
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        clipChildren = false
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        clipChildren = false
    }

    //this is so that on versions of android pre lollipop it will render the cardstack above
    //everything else within the layout
    override fun onFinishInflate() {
        super.onFinishInflate()
        val childCount = childCount

        val children = ArrayList<View>()
        var swipeDeck: View? = null
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is CardStackFinal) {
                swipeDeck = getChildAt(i)
            } else {
                children.add(child)
            }
        }
        removeAllViews()
        removeAllViewsInLayout()
        for (v in children) {
            addViewInLayout(v, -1, v.layoutParams, true)
        }
        if (swipeDeck != null) {
            addViewInLayout(swipeDeck, -1, swipeDeck.layoutParams, true)
        }
        invalidate()
        requestLayout()
    }
}
