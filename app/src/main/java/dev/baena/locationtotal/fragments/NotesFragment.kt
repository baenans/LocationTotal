package dev.baena.locationtotal.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.baena.locationtotal.R

class NotesFragment: Fragment() {

    companion object {
        val TAG = NotesFragment::class.java.canonicalName
        fun newInstance(): NotesFragment = NotesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_notes, container, false)

}