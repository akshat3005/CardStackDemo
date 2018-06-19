package com.kotlinpractice.akshat.cardstack.eventlistener

import android.util.Log
import com.kotlinpractice.akshat.cardstack.cardstack.CardStackFinal

/**
 * Created by akshat-3049 on 18/06/18.
 */

class EventListener {

    companion object {

        fun eventCallback(cardStack : CardStackFinal) {

            cardStack.setEventCallback(object : CardStackFinal.SwipeEventCallback {
                override fun cardActionDown() {
                    Log.i("MainActivity", "card was swiped down, position in com.kotlinpractice.akshat.cardstack.adapter: " )
                }

                override fun cardActionUp() {
                    Log.i("MainActivity", "card was swiped up, position in com.kotlinpractice.akshat.cardstack.adapter: ")
                }

                override fun cardSwipedLeft(position: Int) {
                    Log.i("MainActivity", "card was swiped left, position in com.kotlinpractice.akshat.cardstack.adapter: " + position)
                }

                override fun cardSwipedRight(position: Int) {
                    Log.i("MainActivity", "card was swiped right, position in com.kotlinpractice.akshat.cardstack.adapter: " + position)
                }

                override fun cardsDepleted() {
                    Log.i("MainActivity", "no more cards")
                }
            })
        }

    }

}