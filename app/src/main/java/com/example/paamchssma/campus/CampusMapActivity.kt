package com.example.paamchssma.campus

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.paamchssma.MainActivityFull
import com.example.paamchssma.R
import com.example.paamchssma.databinding.ActivityCampusMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CampusMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var binding: ActivityCampusMapBinding
    private lateinit var googleMap: GoogleMap

    private val campusMarkers: List<CampusMarker> by lazy {
        listOf(
            CampusMarker(
                title = getString(R.string.map_marker_automatic),
                position = LatLng(45.748654, 21.227113),
                opensModel = true
            ),
            CampusMarker(
                title = getString(R.string.map_marker_electronics),
                position = LatLng(45.748159, 21.225791)
            ),
            CampusMarker(
                title = getString(R.string.map_marker_constructions),
                position = LatLng(45.747262, 21.228487)
            ),
            CampusMarker(
                title = getString(R.string.map_marker_rectorate),
                position = LatLng(45.748873, 21.231708)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityCampusMapBinding.inflate(layoutInflater)
            setContentView(binding.root)

            android.util.Log.d("CampusMap", "onCreate: Layout setat")

            setupMapFragment()
            android.util.Log.d("CampusMap", "onCreate: Map fragment setup")

            // Buton pentru a deschide pagina cu orare
            binding.openScheduleButton.setOnClickListener {
                startActivity(Intent(this, com.example.paamchssma.schedule.ScheduleActivity::class.java))
            }
            
            android.util.Log.d("CampusMap", "onCreate: Totul OK!")
        } catch (e: Exception) {
            android.util.Log.e("CampusMap", "CRASH în onCreate: ${e.message}", e)
            Toast.makeText(this, "Eroare: ${e.message}\n\nVerifică Logcat!", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            finish() // Închide activity-ul pentru a preveni crash loop
        }
    }

    private fun setupMapFragment() {
        try {
            val mapFragment = supportFragmentManager
                .findFragmentById(binding.mapFragment.id) as? SupportMapFragment
            
            if (mapFragment == null) {
                android.util.Log.e("CampusMap", "ERROR: MapFragment e NULL!")
                Toast.makeText(this, "Eroare: Nu pot găsi Maps Fragment!", Toast.LENGTH_LONG).show()
                return
            }
            
            android.util.Log.d("CampusMap", "MapFragment găsit, se încarcă async...")
            mapFragment.getMapAsync(this)
        } catch (e: Exception) {
            android.util.Log.e("CampusMap", "CRASH setupMapFragment: ${e.message}", e)
            Toast.makeText(this, "Eroare Maps: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        android.util.Log.d("CampusMap", "✅ Hartă încărcată!")
        
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = false
        }
        googleMap.setOnMarkerClickListener(this)

        // Adaugă markere cu culori diferite
        campusMarkers.forEachIndexed { index, marker ->
            val markerColor = if (marker.opensModel) 
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
            else 
                com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
            
            val markerAdded = googleMap.addMarker(
                MarkerOptions()
                    .position(marker.position)
                    .title(marker.title)
                    .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(markerColor))
            )
            if (marker.opensModel) {
                markerAdded?.tag = TAG_MODEL_MARKER
            }
            
            android.util.Log.d("CampusMap", "📍 Marker ${index + 1}: ${marker.title} la ${marker.position}")
        }

        val campusCenter = LatLng(45.748413, 21.227977)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campusCenter, 15f))
        
        android.util.Log.d("CampusMap", "📷 Cameră mutată la Timișoara (zoom 15)")
        
        Toast.makeText(this, "Hartă încărcată! ${campusMarkers.size} markere adăugate.", Toast.LENGTH_LONG).show()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return if (marker.tag == TAG_MODEL_MARKER) {
            startActivity(Intent(this, MainActivityFull::class.java))
            true
        } else {
            marker.showInfoWindow()
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        
        // Logout automat când aplicația se închide complet
        // (nu când doar navighezi către alte ecrane)
        if (isFinishing && isTaskRoot) {
            try {
                Firebase.auth.signOut()
                android.util.Log.d("CampusMap", "✅ Logout automat la închiderea aplicației")
            } catch (e: Exception) {
                android.util.Log.e("CampusMap", "Eroare logout automat: ${e.message}", e)
            }
        }
    }

    private data class CampusMarker(
        val title: String,
        val position: LatLng,
        val opensModel: Boolean = false
    )

    companion object {
        private const val TAG_MODEL_MARKER = "MODEL_MARKER"
    }
}

