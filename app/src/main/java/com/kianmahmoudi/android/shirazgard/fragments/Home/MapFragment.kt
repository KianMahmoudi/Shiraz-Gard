package com.kianmahmoudi.android.shirazgard.fragments.Home

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.FragmentHomeBinding
import com.kianmahmoudi.android.shirazgard.databinding.FragmentMapBinding
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var googleMap: GoogleMap? = null

    companion object {
        private const val TAG = "MapFragment"
        private val SHIRAZ_CENTER = LatLng(29.5918, 52.5836)
        private val SHIRAZ_BOUNDS = LatLngBounds(
            LatLng(29.50, 52.50),
            LatLng(29.70, 52.70)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment) as? SupportMapFragment

        mapFragment?.getMapAsync(this)

        extractLocationFromArguments()

        binding.btnNavigation.setOnClickListener {
            shareLocationWithApps()
        }

    }

    private fun extractLocationFromArguments() {
        arguments?.let { bundle ->
            latitude = bundle.getDouble("latitude", 0.0)
            longitude = bundle.getDouble("longitude", 0.0)

            Log.d(TAG, "Received Location - Lat: $latitude, Lng: $longitude")

            if (isValidLocation(latitude, longitude)) {
                googleMap?.let { showLocationOnMap() }
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
            val latLng = LatLng(latitude, longitude)
            googleMap?.apply {
                moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("موقعیت مورد نظر")
                )
            }
            binding.btnNavigation.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.e(TAG, "Error showing location: ${e.message}")
            showDefaultLocation()
        }
    }

    private fun showDefaultLocation() {
        googleMap?.apply {
            moveCamera(CameraUpdateFactory.newLatLngZoom(SHIRAZ_CENTER, 12f))
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.apply {
            moveCamera(CameraUpdateFactory.newLatLngZoom(SHIRAZ_CENTER, 12f))
            setLatLngBoundsForCameraTarget(SHIRAZ_BOUNDS)
            setMinZoomPreference(11f)
            setMaxZoomPreference(20f)
        }
        if (isValidLocation(latitude, longitude)) {
            showLocationOnMap()
        }
    }

    override fun onPause() {
        super.onPause()
        arguments?.clear()
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

}