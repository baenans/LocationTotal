package dev.baena.locationtotal.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.baena.locationtotal.MainActivity
import dev.baena.locationtotal.R
import dev.baena.locationtotal.adapters.TracksRecyclerAdapter
import dev.baena.locationtotal.models.Track
import kotlinx.android.synthetic.main.fragment_activities.*

import java.io.File

class ActivitiesFragment: Fragment(), TracksRecyclerAdapter.OnTrackClickListener {


    companion object {
        val TAG = ActivitiesFragment::class.java.canonicalName
        fun newInstance(): ActivitiesFragment = ActivitiesFragment()
    }

    lateinit var mTracks: ArrayList<Track>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_activities, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mTracks = ArrayList(getTracks())

        tracks_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = TracksRecyclerAdapter(mTracks, this@ActivitiesFragment)
        }

    }

    override fun onTrackClick(notePosition: Int) {
        openTrack(mTracks[notePosition].path)
    }

    fun openTrack(fileName: String) {
        (activity as MainActivity).displayMapAndTrack(fileName)
    }

    fun getTracks(): List<Track> {
        var tracks = mutableListOf<Track>()
        val folder = File(context?.filesDir, "/tracks/")
        if (!folder.exists()) return tracks
        val files = folder.listFiles()
        files.forEach {
            tracks.add(
                Track(it.name, it.canonicalPath)
            )
        }
        return tracks
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

}