package com.kotlinpractice.akshat.cardstack

import com.kotlinpractice.akshat.cardstack.adapter.Adapter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.kotlinpractice.akshat.cardstack.cardstack.CardStack
import com.kotlinpractice.akshat.cardstack.eventlistener.EventListener


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardStack = findViewById<View>(R.id.swipe_deck) as CardStack

        val items = ArrayList<String>()
        items.add("0")
        items.add("1")
        items.add("2")
        items.add("3")
        items.add("4")
        items.add("5")
        items.add("6")
        items.add("7")
        items.add("8")
        items.add("9")
        items.add("10")
        items.add("11")
        items.add("12")
        items.add("13")
        items.add("14")
        items.add("15")
        items.add("16")

        val adapter = Adapter(items, this)
        cardStack.setAdapter(adapter)

        EventListener.eventCallback(cardStack)
    }
}
