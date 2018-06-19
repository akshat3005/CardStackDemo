package com.kotlinpractice.akshat.cardstack.cardstack

import android.annotation.TargetApi
import android.content.Context
import android.database.DataSetObserver
import android.os.AsyncTask
import android.os.Build
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.FrameLayout
import com.kotlinpractice.akshat.cardstack.R
import java.util.*

/**
 * Created by akshat-3049 on 16/06/18.
 */
class CardStackFinal(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private var eventCallback: SwipeEventCallback? = null
    private val padLeft: Int
    private val padRight: Int
    private val padTop: Int
    private val padBottom: Int
    /**
     * The adapter with all the data
     */
    private var mAdapter: Adapter? = null
    internal lateinit var observer: DataSetObserver
    internal var nextAdapterCard = 0
    private var restoreInstanceState = false

    private var swipeListener: SwipeListenerFinal? = null
    private var cardInteraction: Boolean = false

    private val AnimationTime = 160

    init {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CardStack,
                0, 0)
        try {
            MAX_CARDS = a.getInt(R.styleable.CardStack_max_visible, 0)
            ROTATION = a.getFloat(R.styleable.CardStack_rotation, 15f)
            SPACING = a.getDimension(R.styleable.CardStack_spacing, 15f)
            ABOVE = a.getBoolean(R.styleable.CardStack_above, true)
            BELOW = a.getBoolean(R.styleable.CardStack_below, false)
            GRAVITY = a.getInt(R.styleable.CardStack_gravity, 0)
            OPACITY_END = a.getFloat(R.styleable.CardStack_opacity_end, 0.33f)

        } finally {
            a.recycle()
        }

        padBottom = getPaddingBottom()
        padLeft = getPaddingLeft()
        padRight = getPaddingRight()
        padTop = getPaddingTop()

        //set clipping of view parent to false so cards render outside their view boundary
        clipToPadding = false
        clipChildren = false

        this.setWillNotDraw(false)

        //render the cards and card deck above or below everything
        if (ABOVE) {
            ViewCompat.setTranslationZ(this, java.lang.Float.MAX_VALUE)
        }
        if (BELOW) {
            ViewCompat.setTranslationZ(this, java.lang.Float.MIN_VALUE)
        }
    }


    fun setAdapter(adapter: Adapter) {
        if (this.mAdapter != null) {
            this.mAdapter!!.unregisterDataSetObserver(observer)
        }
        mAdapter = adapter
        // if we're not restoring previous instance state
        if (!restoreInstanceState) nextAdapterCard = 0

        observer = object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()

                //handle data set changes
                val childCount = childCount

                //only perform action if there are less cards on screen than MAX_CARDS
                if (childCount < MAX_CARDS) {
                    for (i in childCount until MAX_CARDS) {
                        addCard()
                    }
                    //position the items correctly on screen
                    for (i in 0 until getChildCount()) {
                        positionItem(i)
                    }
                }
            }

            override fun onInvalidated() {
                //reset state, remove views and request layout
                nextAdapterCard = 0
                removeAllViews()
                requestLayout()
            }
        }

        adapter.registerDataSetObserver(observer)
        removeAllViewsInLayout()
        requestLayout()
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null || mAdapter!!.count == 0) {
            nextAdapterCard = 0
            removeAllViewsInLayout()
            return
        }

        //pull in views from the adapter at the position the top of the deck is set to
        //stop when you get to for cards or the end of the adapter
        val childCount = childCount
        for (i in childCount until MAX_CARDS) {
            addCard()
        }
        for (i in 0 until getChildCount()) {
            positionItem(i)
        }
        //position the new children we just added and set up the top card with a listener etc
    }

    private fun removeCard() {
        //top card is now the last in view children
        val childOffset = childCount - MAX_CARDS + 1
        val child = getChildAt(childCount - childOffset)
        if (child != null) {
            child.setOnTouchListener(null)
            swipeListener = null
            //this will also check to see if cards are depleted
            removeAfterAnimation(child)
        }
    }

    private fun removeAfterAnimation(child: View) {
        RemoveViewOnAnimCompleted().execute(child)
    }

    private fun addCard() {
        if (nextAdapterCard < mAdapter!!.count) {

            val newBottomChild = mAdapter!!.getView(nextAdapterCard, this, this)/*lastRemovedView*/

            //set the initial Y value so card appears from under the deck
            addAndMeasureChild(newBottomChild)
            nextAdapterCard++
        }
        topCard()
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setZTranslations() {

        //this is only needed to add shadows to cardviews on > lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val count = childCount
            for (i in 0 until count) {
                getChildAt(i).translationZ = (i * 10).toFloat()
            }
        }
    }

    /**
     * Adds a view as a child view and takes care of measuring it
     *
     * @param child The view to add
     */
    private fun addAndMeasureChild(child: View) {
        var params: ViewGroup.LayoutParams? = child.layoutParams
        if (params == null) {
            params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        }

        //ensure new card is under the deck at the beginning
        child.y = padTop.toFloat()

        //every time we add and measure a child refresh the children on screen and order them
        val children = ArrayList<View>()
        children.add(child)
        for (i in 0 until childCount) {
            children.add(getChildAt(i))
        }

        removeAllViews()

        for (c in children) {
            addViewInLayout(c, -1, params, true)
            val itemWidth = width - (padLeft + padRight)
            val itemHeight = height - (padTop + padBottom)
            c.measure(View.MeasureSpec.EXACTLY or itemWidth, View.MeasureSpec.EXACTLY or itemHeight) //MeasureSpec.UNSPECIFIED

        }
        setZTranslations()
    }

    /**
     * Positions the children at the "correct" positions
     */
    private fun positionItem(index: Int) {

        val child = getChildAt(index)

        val width = child.measuredWidth
        val height = child.measuredHeight
        val left = (getWidth() - width) / 2
        child.layout(left, padTop, left + width, padTop + height)
        //layout each child slightly above the previous child (we start with the bottom)
        val childCount = childCount
        val offset = ((childCount - 1) * SPACING - index * SPACING).toInt().toFloat()
        //child.setY(padTop + offset);

        child.animate()
                .setDuration((if (restoreInstanceState) 0 else 160).toLong())
                .y(padTop + offset)

        restoreInstanceState = false
    }

    private fun topCard() {

        val childOffset = childCount - MAX_CARDS + 1
        val child = getChildAt(childCount - childOffset)

        //the card position on setup top card is currently always the bottom card in the view
        val initialX = padLeft
        val initialY = padTop

        if (child != null) {
            //make sure we have a card
            swipeListener = SwipeListenerFinal(child, object : SwipeListenerFinal.SwipeCallback {
                override fun cardSwipedLeft() {
                    val positionInAdapter = nextAdapterCard - childCount
                    removeCard()
                    if (eventCallback != null) eventCallback!!.cardSwipedLeft(positionInAdapter)
                    addCard()
                }

                override fun cardSwipedRight() {
                    val positionInAdapter = nextAdapterCard - childCount
                    removeCard()
                    if (eventCallback != null) eventCallback!!.cardSwipedRight(positionInAdapter)
                    addCard()
                }

                override fun cardOffScreen() {}

                override fun cardActionDown() {
                    if (eventCallback != null) eventCallback!!.cardActionDown()
                    cardInteraction = true
                }

                override fun cardActionUp() {
                    if (eventCallback != null) eventCallback!!.cardActionUp()
                    cardInteraction = false
                }

            }, initialX.toFloat(), initialY.toFloat(), ROTATION, OPACITY_END)

            child.setOnTouchListener(swipeListener)
        }
    }

    fun setEventCallback(eventCallback: SwipeEventCallback) {
        this.eventCallback = eventCallback
    }


    interface SwipeEventCallback {
        //returning the object position in the adapter
        fun cardSwipedLeft(position: Int)

        fun cardSwipedRight(position: Int)

        fun cardsDepleted()

        fun cardActionDown()

        fun cardActionUp()
    }

    private inner class RemoveViewOnAnimCompleted : AsyncTask<View, Void, View>() {

        override fun doInBackground(vararg params: View): View {
            android.os.SystemClock.sleep(AnimationTime.toLong())
            return params[0]
        }

        override fun onPostExecute(view: View) {
            super.onPostExecute(view)
            removeView(view)

            //if there are no more children left after top card removal let the callback know
            if (childCount <= 0 && eventCallback != null) {
                eventCallback!!.cardsDepleted()
            }
        }
    }

    companion object {
        private var MAX_CARDS: Int = 0
        private var ROTATION: Float = 0.toFloat()
        private var SPACING: Float = 0.toFloat()
        private var ABOVE: Boolean = false
        private var BELOW: Boolean = false
        private var OPACITY_END: Float = 0.toFloat()
        private var GRAVITY: Int = 0
    }
}


