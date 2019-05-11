package dev.baena.locationtotal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import dev.baena.locationtotal.fragments.ActivitiesFragment
import dev.baena.locationtotal.fragments.MapFragment
import dev.baena.locationtotal.fragments.NotesFragment
import dev.baena.locationtotal.models.Note
import dev.baena.locationtotal.services.LocationService
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    val PERMISSIONS_FOREGROUND_SERVICE = 1
    val PERMISSIONS_WRITE_EXTERNAL_STORAGE = 2
    val PERMISSIONS_ACCESS_FINE_LOCATION = 3

    lateinit var mLocationServiceIntent: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
        checkPermissions()

        main_navigation_view.setNavigationItemSelectedListener {
            val fragment =
                if (it.itemId == R.id.nav_notes) NotesFragment.newInstance()
                else if (it.itemId == R.id.nav_activities) ActivitiesFragment.newInstance()
                else MapFragment.newInstance(MapFragment.FRAGMENT_ACTION_DEFAULT)
            drawerLayout.closeDrawers()
            replaceFragment(fragment)
        }

        replaceFragment(MapFragment.newInstance())
        mLocationServiceIntent = Intent(this, LocationService::class.java)
        startService(mLocationServiceIntent)
    }

    private fun replaceFragment(fragment: Fragment): Boolean {
        try {
            supportFragmentManager.beginTransaction().replace(R.id.main_fragment_layout, fragment).commit()
            return true
        } catch(e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun checkPermissions() {
        // TODO: enhance
        val dangerousPermissions = arrayOf(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

       if(
           dangerousPermissions.map {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED
            }.reduce { acc, b -> acc || b }
       ) {
           ActivityCompat.requestPermissions(this,
               dangerousPermissions,
               0
           )
       }
    }

    override fun onDestroy() {
        stopService(mLocationServiceIntent);
        super.onDestroy()
    }

    fun displayTrack(fileName: String): Unit {
        replaceFragment(MapFragment.newInstance(
            MapFragment.FRAGMENT_ACTION_DISPLAY_TRACK,
            fileName
        ))
    }

    fun displayMarker(latLng: String): Unit {
        replaceFragment(MapFragment.newInstance(
            MapFragment.FRAGMENT_ACTION_DISPLAY_MARKER,
            latLng
        ))
    }

}
