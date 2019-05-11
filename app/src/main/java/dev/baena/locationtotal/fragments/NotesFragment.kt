package dev.baena.locationtotal.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.baena.locationtotal.MainActivity
import dev.baena.locationtotal.R
import dev.baena.locationtotal.models.Note

class NotesFragment: Fragment() {

    companion object {
        val TAG = NotesFragment::class.java.canonicalName
        fun newInstance(): NotesFragment = NotesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_notes, container, false)

    fun openNote(note: Note) {
        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.displayMarker("")
    }
}