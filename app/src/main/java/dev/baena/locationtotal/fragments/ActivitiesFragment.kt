package dev.baena.locationtotal.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.baena.locationtotal.R

class ActivitiesFragment: Fragment() {

    companion object {
        val TAG = ActivitiesFragment::class.java.canonicalName
        fun newInstance(): ActivitiesFragment = ActivitiesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_activities, container, false)

}