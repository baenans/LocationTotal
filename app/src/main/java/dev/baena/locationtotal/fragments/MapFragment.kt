package dev.baena.locationtotal.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.baena.locationtotal.BuildConfig
import dev.baena.locationtotal.R
import dev.baena.locationtotal.db.DBHelper
import dev.baena.locationtotal.models.Note
import dev.baena.locationtotal.services.LocationService
import kotlinx.android.synthetic.main.fragment_map.*
import org.jetbrains.anko.toast
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.OverlayItem

class MapFragment: Fragment() {

    companion object {
        val TAG = MapFragment::class.java.canonicalName
        fun newInstance(): MapFragment = MapFragment()
        const val DEFAULT_MAP_LAT = 50.9087
        const val DEFAULT_MAP_LNG = -1.4096
    }

    lateinit var mDatabase: DBHelper
    lateinit var mMapView: MapView
    lateinit var mMarkersOverlay: ItemizedIconOverlay<OverlayItem>
    lateinit var mBroadcastReceiver: BroadcastReceiver
    var mTrackingRoute: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?  =
        inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        mDatabase = DBHelper(view.context)

        mMapView = view.findViewById(R.id.map_view)
        mMapView.controller.setZoom(16.0)
        mMapView.setMultiTouchControls(true)
        mMapView.zoomController.setVisibility(
            CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT
        )
        setMapCenter(GeoPoint(DEFAULT_MAP_LAT, DEFAULT_MAP_LNG))

        // Markers Overlay
        mMarkersOverlay = ItemizedIconOverlay<OverlayItem>(
            view.context,
            getMarkerOverlayItems(),
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean = true  // unused
                override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                    context?.toast(item?.title ?: "")
                    return true
                }
            }
        )
        mMapView.overlays.add(mMarkersOverlay);

        // Events Overlay
        val mMapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint?): Boolean = true
            override fun longPressHelper(point: GeoPoint?): Boolean {
                TODO("Implement functionality to add notes on long press")
                return true
            }
        }

        mMapView.overlays.add(MapEventsOverlay(mMapEventsReceiver));

        // Broadcast Receiver
        mBroadcastReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                when (intent?.action) {
                    LocationService.ACTION_LOCATION_CHANGED -> {
                        Log.v(TAG, "location changed")
                        setMapCenter(
                            GeoPoint(
                                intent?.getDoubleExtra(LocationService.ACTION_LOCATION_CHANGED_LAT, DEFAULT_MAP_LAT),
                                intent?.getDoubleExtra(LocationService.ACTION_LOCATION_CHANGED_LNG, DEFAULT_MAP_LNG)
                            )
                        )
                    }
                    LocationService.ACTION_TRACKING_STATUS_CHANGED -> {
                        Log.v(TAG, "tracking status changed")
                        onTrackingStatusChange(
                            intent?.getBooleanExtra(LocationService.ACTION_TRACKING_STATUS_CHANGED_STATUS, false)
                        )
                    }
                }
            }
        }
        btn_toggle_tracking.setOnClickListener { toggleRouteTracking() }
    }

    override fun onResume() {
        activity?.registerReceiver(mBroadcastReceiver, IntentFilter().apply {
            addAction(LocationService.ACTION_LOCATION_CHANGED)
            addAction(LocationService.ACTION_TRACKING_STATUS_CHANGED)
        })
        broadcastAction(LocationService.ACTION_REQUEST_TRACKING_STATUS) // may not be working?
        super.onResume()
    }

    override fun onPause() {
        activity?.unregisterReceiver(mBroadcastReceiver)
        super.onPause()
    }

    private fun setMapCenter(center: GeoPoint) {
        mMapView?.controller.setCenter(center)
    }

    private fun broadcastAction(actionS: String) {
        activity?.sendBroadcast(Intent().apply{
            action = actionS
        })
    }

    private fun toggleRouteTracking() {
        if (!mTrackingRoute) {
            broadcastAction(LocationService.ACTION_START_TRACKING)
        } else {
            broadcastAction(LocationService.ACTION_STOP_TRACKING)
        }
    }

    private fun onTrackingStatusChange(trackingRoute: Boolean) {
        Log.v(TAG, "onTrackingStatusChange: $trackingRoute")
        mTrackingRoute = trackingRoute

        btn_toggle_tracking.setImageResource(
            if (trackingRoute)
                R.drawable.ic_stop
            else
                R.drawable.ic_record
        )
    }


    private fun getOverlayItemFor(note: Note) : OverlayItem {
        return OverlayItem(
            note.text,
            "Lat: ${note.lat} Lon: ${note.lng}",
            GeoPoint(note.lat, note.lng)
        )
    }

    private fun getMarkerOverlayItems(): ArrayList<OverlayItem> {
        return ArrayList(
            mDatabase.getNotes().map { getOverlayItemFor(it) }
        )
    }
}