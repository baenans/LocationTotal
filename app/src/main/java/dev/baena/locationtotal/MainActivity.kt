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
import dev.baena.locationtotal.services.LocationService
import kotlinx.android.synthetic.main.activity_main.*

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
            val fragmentClass =
                if (it.itemId == R.id.nav_notes) NotesFragment::class.java
                else if (it.itemId == R.id.nav_activities) ActivitiesFragment::class.java
                else MapFragment::class.java
            drawerLayout.closeDrawers()
            replaceFragment(fragmentClass.newInstance())
        }

        replaceFragment(MapFragment())
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

}
