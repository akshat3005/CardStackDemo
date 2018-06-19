package com.kotlinpractice.akshat.cardstack.adapter

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.kotlinpractice.akshat.cardstack.R

class Adapter(private val data: List<String>, internal var activity: Activity) : BaseAdapter() {

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {

        var v: View? = convertView

            val inflater = activity.layoutInflater
            // normally use a viewholder
            v = inflater.inflate(R.layout.card_layout, parent, false)

        (v!!.findViewById<View>(R.id.textView) as TextView).text = data[position]

        v.setOnClickListener {
            Log.i("MainActivity", position.toString())
        }

        return v
    }
}