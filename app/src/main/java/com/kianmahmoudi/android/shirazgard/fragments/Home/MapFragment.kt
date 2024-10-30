package com.kianmahmoudi.android.shirazgard.fragments.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PlaceFeature
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.FragmentHomeBinding
import com.kianmahmoudi.android.shirazgard.databinding.FragmentMapBinding

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment

        mapFragment?.getMapAsync(this)

        

    }

    override fun onMapReady(map: GoogleMap) {
        val shirazLatLng = LatLng(29.5918, 52.5836)
        val shirazBounds = LatLngBounds(
            LatLng(29.50, 52.50),
            LatLng(29.70, 52.70)
        )

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(shirazLatLng, 12f))

        map.setLatLngBoundsForCameraTarget(shirazBounds)
        map.setMinZoomPreference(11f)
        map.setMaxZoomPreference(15f)

    }

}