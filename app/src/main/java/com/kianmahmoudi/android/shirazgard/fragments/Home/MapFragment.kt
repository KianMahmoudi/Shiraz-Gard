package com.kianmahmoudi.android.shirazgard.fragments.Home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.FragmentMapBinding
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    companion object {
        private const val TAG = "MapFragment"

        // محدوده شیراز
        private val SHIRAZ_BOUNDS = BoundingBox(
            29.70, // north
            52.70, // east
            29.50, // south
            52.50  // west
        )

        // مرکز شیراز
        private val SHIRAZ_CENTER = GeoPoint(29.5918, 52.5836)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Configuration.getInstance().load(requireContext(), androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()))
        binding = FragmentMapBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapView
        setupMap()

        extractLocationFromArguments()

        binding.btnNavigation.setOnClickListener {
            shareLocationWithApps()
        }

        mapView.addMapListener(object : org.osmdroid.events.MapListener {
            override fun onScroll(event: org.osmdroid.events.ScrollEvent?): Boolean {
                event?.let {
                    val currentCenter = mapView.mapCenter as GeoPoint
                    if (!isInsideShirazBounds(currentCenter)) {
                        forceCenterInsideBounds(currentCenter)
                    }
                }
                return false
            }
            override fun onZoom(event: org.osmdroid.events.ZoomEvent?): Boolean {
                return false
            }
        })

    }

    private fun setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.minZoomLevel = 11.0
        mapView.maxZoomLevel = 20.0
        mapView.controller.setZoom(12.0)
        mapView.controller.setCenter(SHIRAZ_CENTER)
    }

    private fun extractLocationFromArguments() {
        arguments?.let { bundle ->
            latitude = bundle.getDouble("latitude", 0.0)
            longitude = bundle.getDouble("longitude", 0.0)

            Log.d(TAG, "Received Location - Lat: $latitude, Lng: $longitude")

            if (isValidLocation(latitude, longitude)) {
                showLocationOnMap()
            } else {
                showDefaultLocation()
            }
        }
    }

    private fun isValidLocation(lat: Double, lng: Double): Boolean {
        return lat != 0.0 && lng != 0.0 &&
                lat in -90.0..90.0 &&
                lng in -180.0..180.0
    }

    private fun showLocationOnMap() {
        try {
            val geoPoint = GeoPoint(latitude, longitude)
            mapView.controller.setZoom(15.0)
            mapView.controller.setCenter(geoPoint)

            val marker = Marker(mapView).apply {
                position = geoPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "موقعیت مورد نظر"
            }
            mapView.overlays.clear()
            mapView.overlays.add(marker)

            binding.btnNavigation.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.e(TAG, "Error showing location: ${e.message}")
            showDefaultLocation()
        }
    }

    private fun showDefaultLocation() {
        mapView.controller.setZoom(12.0)
        mapView.controller.setCenter(SHIRAZ_CENTER)
    }

    private fun shareLocationWithApps() {
        val locationUri = Uri.parse("geo:$latitude,$longitude")
        val shareIntent = Intent(Intent.ACTION_VIEW, locationUri)
        val chooserIntent = Intent.createChooser(
            shareIntent,
            "${getString(R.string.open_in_map)}:"
        )
        startActivity(chooserIntent)
    }

    private fun isInsideShirazBounds(point: GeoPoint): Boolean {
        return point.latitude in SHIRAZ_BOUNDS.latSouth..SHIRAZ_BOUNDS.latNorth &&
                point.longitude in SHIRAZ_BOUNDS.lonWest..SHIRAZ_BOUNDS.lonEast
    }

    private fun forceCenterInsideBounds(currentCenter: GeoPoint) {
        var newLat = currentCenter.latitude
        var newLon = currentCenter.longitude

        if (newLat > SHIRAZ_BOUNDS.latNorth) newLat = SHIRAZ_BOUNDS.latNorth
        if (newLat < SHIRAZ_BOUNDS.latSouth) newLat = SHIRAZ_BOUNDS.latSouth
        if (newLon > SHIRAZ_BOUNDS.lonEast) newLon = SHIRAZ_BOUNDS.lonEast
        if (newLon < SHIRAZ_BOUNDS.lonWest) newLon = SHIRAZ_BOUNDS.lonWest

        mapView.controller.setCenter(GeoPoint(newLat, newLon))
    }

    override fun onPause() {
        super.onPause()
        arguments?.clear()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
}
