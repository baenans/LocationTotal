package dev.baena.locationtotal.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.baena.locationtotal.MainActivity
import dev.baena.locationtotal.R
import dev.baena.locationtotal.adapters.NotesRecyclerAdapter
import dev.baena.locationtotal.db.DBHelper
import dev.baena.locationtotal.models.Note
import kotlinx.android.synthetic.main.fragment_notes.*
import org.osmdroid.util.GeoPoint

class NotesFragment: Fragment(), NotesRecyclerAdapter.OnNoteClickListener {


    companion object {
        val TAG = NotesFragment::class.java.canonicalName
        fun newInstance(): NotesFragment = NotesFragment()
    }

    lateinit var mDatabase: DBHelper
    lateinit var mNotes: ArrayList<Note>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_notes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDatabase = (activity as MainActivity).mDatabase
        mNotes = ArrayList(mDatabase.getNotes())

        notes_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = NotesRecyclerAdapter(mNotes, this@NotesFragment)
        }
    }

    fun openNote(pointInfo: String) {
        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.displayMarker(pointInfo)
    }

    override fun onNoteClick(notePosition: Int) {
        val note = mNotes.get(notePosition)
        openNote("lat=${note.lat}&lng=${note.lng}")
    }
}
