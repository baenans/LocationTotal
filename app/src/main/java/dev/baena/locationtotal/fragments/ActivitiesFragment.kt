package dev.baena.locationtotal.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.baena.locationtotal.MainActivity
import dev.baena.locationtotal.R
import dev.baena.locationtotal.models.Note
import dev.baena.locationtotal.utils.GPXTrackWriter
import kotlinx.android.synthetic.main.fragment_activities.*
import java.io.File

class ActivitiesFragment: Fragment() {

    companion object {
        val TAG = ActivitiesFragment::class.java.canonicalName
        fun newInstance(): ActivitiesFragment = ActivitiesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_activities, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // /data/user/0/dev.baena.locationtotal/files/tracks/2019-05-11T13:38:28Z.gpx
        // /data/data/dev.baena.locationtotal/files/tracks/2019-05-11T13:38:28Z.gpx
        btn_test_path.setOnClickListener {
            val exampleFilePath =
                File(view.context.filesDir, "/tracks/2019-05-11T13:38:28Z.gpx").canonicalPath
            openTrack(exampleFilePath)
        }

        super.onViewCreated(view, savedInstanceState)
    }
    fun openTrack(fileName: String) {
        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.displayTrack(fileName)
    }

    fun getTracks() {
        /**
         * Explore directory - get list of files
         *
         * open: tap over list item
         * erase: lateral slide
         * share:
         */
        // display in recycler view
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

}