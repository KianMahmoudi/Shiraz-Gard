package com.kianmahmoudi.android.shirazgard.fragments.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.FragmentMapBinding
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import timber.log.Timber

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val args: MapFragmentArgs by navArgs()
    private lateinit var mapView: MapView

    private var targetLatitude: Double = 0.0
    private var targetLongitude: Double = 0.0
    private var placeName: String? = null

    companion object {
        private const val TAG = "MapFragment"

        // محدوده شیراز
        private val SHIRAZ_BOUNDS = BoundingBox(
            29.95,
            52.95,
            29.35,
            52.35
        )

        // مرکز شیراز
        private val SHIRAZ_CENTER = GeoPoint(29.5918, 52.5836)

        // زوم levels
        private const val DEFAULT_ZOOM = 12.0
        private const val LOCATION_ZOOM = 15.0
        private const val MIN_ZOOM = 11.0
        private const val MAX_ZOOM = 20.0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // تنظیم OSMDroid configuration
        Configuration.getInstance().load(
            requireContext(),
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapView
        setupMap()
        extractLocationFromArguments()
        setupClickListeners()
        setupMapListeners()
    }

    private fun setupMap() {
        mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            minZoomLevel = MIN_ZOOM
            maxZoomLevel = MAX_ZOOM
            controller.setZoom(DEFAULT_ZOOM)
            controller.setCenter(SHIRAZ_CENTER)
        }
    }

    private fun extractLocationFromArguments() {
        try {
            // دریافت arguments از Navigation
            targetLatitude = args.latitude.toDouble()
            targetLongitude = args.longitude.toDouble()
            placeName = args.placeName

            Timber.tag(TAG).d("Received Location - Lat: $targetLatitude, Lng: $targetLongitude, Place: $placeName")

            if (isValidLocation(targetLatitude, targetLongitude)) {
                showLocationOnMap()
            } else {
                showDefaultLocation()
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error extracting location from arguments")
            showDefaultLocation()
        }
    }

    private fun isValidLocation(lat: Double, lng: Double): Boolean {
        return lat != 0.0 && lng != 0.0 &&
                lat in -90.0..90.0 &&
                lng in -180.0..180.0
    }

    private fun showLocationOnMap() {
        try {
            val geoPoint = GeoPoint(targetLatitude, targetLongitude)

            // انیمیشن به مکان هدف
            mapView.controller.animateTo(geoPoint, LOCATION_ZOOM, 1000L)

            // اضافه کردن marker
            val marker = Marker(mapView).apply {
                position = geoPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = placeName ?: "موقعیت مورد نظر"

                // تنظیم icon مخصوص اگر نیاز است
                // icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_pin)
            }

            // پاک کردن marker های قبلی و اضافه کردن marker جدید
            mapView.overlays.clear()
            mapView.overlays.add(marker)
            mapView.invalidate()

            // نمایش دکمه navigation
            binding.btnNavigation.visibility = View.VISIBLE

            Timber.tag(TAG).d("Location marker added successfully")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error showing location on map")
            showDefaultLocation()
        }
    }

    private fun showDefaultLocation() {
        try {
            mapView.controller.setZoom(DEFAULT_ZOOM)
            mapView.controller.setCenter(SHIRAZ_CENTER)

            // مخفی کردن دکمه navigation
            binding.btnNavigation.visibility = View.GONE

            // پاک کردن marker های موجود
            mapView.overlays.clear()
            mapView.invalidate()

            Timber.tag(TAG).d("Showing default location (Shiraz center)")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error showing default location")
        }
    }

    private fun setupClickListeners() {
        binding.btnNavigation.setOnClickListener {
            if (isValidLocation(targetLatitude, targetLongitude)) {
                openLocationInExternalApps()
            }
        }
    }

    private fun setupMapListeners() {
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

    private fun openLocationInExternalApps() {
        try {
            val locationUri = Uri.parse("geo:$targetLatitude,$targetLongitude?q=$targetLatitude,$targetLongitude(${placeName ?: "مکان"})")
            val mapIntent = Intent(Intent.ACTION_VIEW, locationUri)
            if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
                val chooserTitle = if (placeName != null) {
                    "${getString(R.string.open_in_map)}: $placeName"
                } else {
                    getString(R.string.open_in_map)
                }
                val chooserIntent = Intent.createChooser(mapIntent, chooserTitle)
                startActivity(chooserIntent)
            } else {
                val googleMapsUri = Uri.parse("https://www.google.com/maps?q=$targetLatitude,$targetLongitude")
                val browserIntent = Intent(Intent.ACTION_VIEW, googleMapsUri)
                startActivity(browserIntent)
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error opening location in external apps")
        }
    }
    private fun isInsideShirazBounds(point: GeoPoint): Boolean {
        return point.latitude in SHIRAZ_BOUNDS.latSouth..SHIRAZ_BOUNDS.latNorth &&
                point.longitude in SHIRAZ_BOUNDS.lonWest..SHIRAZ_BOUNDS.lonEast
    }

    private fun forceCenterInsideBounds(currentCenter: GeoPoint) {
        try {
            var newLat = currentCenter.latitude
            var newLon = currentCenter.longitude

            newLat = newLat.coerceIn(SHIRAZ_BOUNDS.latSouth, SHIRAZ_BOUNDS.latNorth)

            newLon = newLon.coerceIn(SHIRAZ_BOUNDS.lonWest, SHIRAZ_BOUNDS.lonEast)

            val correctedPoint = GeoPoint(newLat, newLon)
            mapView.controller.setCenter(correctedPoint)

            Timber.tag(TAG).d("Map center corrected to stay within Shiraz bounds")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error forcing center inside bounds")
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.overlays.clear()
        _binding = null
    }
}