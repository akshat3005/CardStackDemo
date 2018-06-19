package com.kotlinpractice.akshat.cardstack.eventlistener

import com.kotlinpractice.akshat.cardstack.cardstack.CardStackFinal

class EventListener {

    companion object {

        fun eventCallback(cardStack : CardStackFinal) {

            cardStack.setEventCallback(object : CardStackFinal.SwipeEventCallback {
                override fun cardActionDown() {
                    //TODO some changes on the action
                }

                override fun cardActionUp() {
                    //TODO some changes on the action
                }

                override fun cardSwipedLeft(position: Int) {
                    //TODO some changes on the action
                }

                override fun cardSwipedRight(position: Int) {
                    //TODO some changes on the action
                }

                override fun cardsDepleted() {
                    //TODO some changes on the action
                }
            })
        }

    }

}