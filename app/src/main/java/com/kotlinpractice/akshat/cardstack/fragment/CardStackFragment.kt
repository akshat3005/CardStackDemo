package com.kotlinpractice.akshat.cardstack.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlinpractice.akshat.cardstack.R
import com.kotlinpractice.akshat.cardstack.adapter.Adapter
import com.kotlinpractice.akshat.cardstack.cardstack.CardStackFinal
import com.kotlinpractice.akshat.cardstack.eventlistener.EventListener

class CardStackFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater?.inflate(R.layout.main_fragment, container, false)

        val cardStack = view?.findViewById(R.id.swipe_deck) as CardStackFinal

        val items = ArrayList<String>()
        for (i in 0..16) {
            items.add(i.toString())
        }

        val adapter = Adapter(items, activity)
        cardStack.setAdapter(adapter)

        EventListener.eventCallback(cardStack)

        return  view
    }

}
