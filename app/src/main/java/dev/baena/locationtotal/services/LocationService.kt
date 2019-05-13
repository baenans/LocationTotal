package dev.baena.locationtotal.services

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import dev.baena.locationtotal.MainActivity
import dev.baena.locationtotal.R
import dev.baena.locationtotal.utils.GPXTrackWriter
import java.util.*

class LocationService: Service(), LocationListener {

    companion object {
        val TAG = LocationService::class.java.canonicalName
        val ACTION_START_TRACKING = "$TAG.start_tracking"
        val ACTION_STOP_TRACKING = "$TAG.stop_tracking"
        val ACTION_LOCATION_CHANGED = "$TAG.location_changed"
        val ACTION_LOCATION_CHANGED_LAT = "$ACTION_LOCATION_CHANGED.lat"
        val ACTION_LOCATION_CHANGED_LNG = "$ACTION_LOCATION_CHANGED.lng"
        val ACTION_REQUEST_TRACKING_STATUS = "$TAG.request_tracking_status"
        val ACTION_TRACKING_STATUS_CHANGED = "$TAG.tracking_status_changed"
        val ACTION_TRACKING_STATUS_CHANGED_STATUS = "$ACTION_TRACKING_STATUS_CHANGED.status"
        private const val NOTIFICATION_ID = 12345678
        private const val LOCATION_MIN_TIME_IN_MILLISECONDS: Long = 1000
        private const val LOCATION_MIN_DISTANCE_IN_METERS: Float = 5.5f
    }

    lateinit var mBroadcastReceiver: BroadcastReceiver
    lateinit var mLocationManager: LocationManager
    lateinit var mNotificationManager: NotificationManager
    var mTrackingLocation: Boolean = false
    var mTrackFile: GPXTrackWriter? = null

    inner class LocationServiceBinder() : Binder() {
        fun getService(): LocationService {
            return this@LocationService
        }
    }

    override fun onBind(intent: Intent?): IBinder? = LocationServiceBinder()

    override fun onCreate() {
        super.onCreate()
        // Implement BroadcastReceiver interface
        mBroadcastReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                when (intent?.action) {
                    ACTION_START_TRACKING -> startTracking()
                    ACTION_STOP_TRACKING -> stopTracking()
                    ACTION_REQUEST_TRACKING_STATUS -> notifyTrackingStatus()
                }
            }
        }
        // Register Broadcast Receiver
        registerReceiver(mBroadcastReceiver, IntentFilter().apply {
            addAction(ACTION_START_TRACKING)
            addAction(ACTION_STOP_TRACKING)
            addAction(ACTION_REQUEST_TRACKING_STATUS)
        })
        // Initialize notification manager
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return

        mLocationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            LOCATION_MIN_TIME_IN_MILLISECONDS,
            LOCATION_MIN_DISTANCE_IN_METERS,
            this
        )
    }

    override fun onDestroy() {
        unregisterReceiver(mBroadcastReceiver)  // stop receiver leak!
        if (mTrackingLocation) return
        mLocationManager?.removeUpdates(this)
        super.onDestroy()
    }

    /**
     * Location Logic
     */

    override fun onLocationChanged(location: Location) {
        sendBroadcast(Intent().apply{
            action = ACTION_LOCATION_CHANGED
            putExtra(ACTION_LOCATION_CHANGED_LAT, location?.latitude)
            putExtra(ACTION_LOCATION_CHANGED_LNG, location?.longitude)
        })
        if (mTrackingLocation) persistLatestLocation(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}

    /**
     * Tracking route logic
     */

    private fun startTracking() {
        startForeground(NOTIFICATION_ID, getNotification())
        mTrackFile = GPXTrackWriter(this)
        mTrackingLocation = true
        notifyTrackingStatus()
    }

    private fun stopTracking() {
        stopForeground(true)
        mTrackingLocation = false
        mTrackFile?.closeGPXFile()
        mTrackFile = null
        notifyTrackingStatus()
    }

    private fun notifyTrackingStatus() {
        sendBroadcast(Intent().apply{
            action = ACTION_TRACKING_STATUS_CHANGED
            putExtra(ACTION_TRACKING_STATUS_CHANGED_STATUS, mTrackingLocation)
        })
    }

    private fun persistLatestLocation(location: Location) {
        mTrackFile?.addTrackPoint(
            location.latitude,
            location.longitude,
            location.altitude,
            Date()
        )
    }

    /**
     * Notification Logic
     * TODO: refactor + implement actions (stop from notification?)
     */
    private fun getNotification(): Notification {
        return getUnbuiltNotification().build()
    }

    private fun getUnbuiltNotification(): NotificationCompat.Builder {
        val action = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val builder: NotificationCompat.Builder
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val CHANNEL_ID = "channel_01"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Track GPS Location",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Allows to track GPS location in the background"
            mNotificationManager.createNotificationChannel(channel)
            builder = NotificationCompat.Builder(this, CHANNEL_ID)
        }
        else {
            builder = NotificationCompat.Builder(applicationContext)
        }

        return builder.setContentIntent(action)
            .setContentTitle("GPS Tracking")
            .setTicker("GPS Tracking")
            .setContentText("The application is tracking your route")
            .setSmallIcon(R.drawable.ic_navigation)
            .setContentIntent(action)
            .setOngoing(true)
    }

}