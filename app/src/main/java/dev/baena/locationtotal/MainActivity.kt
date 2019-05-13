package dev.baena.locationtotal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.SparseArray
import dev.baena.locationtotal.db.DBHelper
import dev.baena.locationtotal.fragments.ActivitiesFragment
import dev.baena.locationtotal.fragments.MapFragment
import dev.baena.locationtotal.fragments.NotesFragment
import dev.baena.locationtotal.services.LocationService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val NOTES_FRAGMENT = 101
        const val ACTIVITIES_FRAGMENT = 202
        const val MAP_FRAGMENT = 303
    }

    lateinit var mLocationServiceIntent: Intent
    lateinit var mDatabase: DBHelper
    private val mMapFragments = SparseArray<Fragment>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
        checkPermissions()

        mDatabase = DBHelper(this)

        main_navigation_view.setNavigationItemSelectedListener {
            val fragment =
                if (it.itemId == R.id.nav_notes) getFragment(NOTES_FRAGMENT)
                else if (it.itemId == R.id.nav_activities) getFragment(ACTIVITIES_FRAGMENT)
                else getFragment(MAP_FRAGMENT)
            drawerLayout.closeDrawers()
            replaceFragment(fragment)
        }

        replaceFragment(getFragment(MAP_FRAGMENT))
        mLocationServiceIntent = Intent(this, LocationService::class.java)
        startService(mLocationServiceIntent)
    }

    private fun getFragment(key: Int): Fragment {
        var fragment = mMapFragments.get(key, null)
        if (fragment != null) return fragment
        when(key) {
            NOTES_FRAGMENT -> {fragment = NotesFragment.newInstance()}
            ACTIVITIES_FRAGMENT -> {fragment = ActivitiesFragment.newInstance()}
            MAP_FRAGMENT -> {fragment = MapFragment.newInstance()}
        }
        mMapFragments.put(key, fragment)
        return fragment
    }

    private fun replaceFragment(fragmentInstance: Fragment): Boolean {
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_layout, fragmentInstance).commit()
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
        mDatabase.close()
        stopService(mLocationServiceIntent);
        super.onDestroy()
    }

    fun displayMapAndTrack(fileName: String): Unit {
        replaceFragment(MapFragment.newInstance(
            MapFragment.FRAGMENT_ACTION_DISPLAY_TRACK,
            fileName
        ))
    }

    fun displayMapCenteredInNote(latLng: String): Unit {
        replaceFragment(MapFragment.newInstance(
            MapFragment.FRAGMENT_ACTION_FOCUS_NOTE,
            latLng
        ))
    }

}
