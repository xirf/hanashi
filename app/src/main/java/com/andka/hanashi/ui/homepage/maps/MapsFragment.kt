package com.andka.hanashi.ui.homepage.maps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.andka.hanashi.databinding.FragmentMapsBinding
import com.andka.hanashi.utils.Locator
import com.andka.hanashi.utils.ResultState
import com.andka.hanashi.utils.showToast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private val binding by lazy { FragmentMapsBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MapsViewModel>(factoryProducer = { Locator.mapsViewModelFactory })
    private val distances = mutableMapOf<Pair<LatLng, LatLng>, Double>()
    private val markerDistances = mutableListOf<Pair<LatLng, Double>>()
    private val marker = mutableListOf<LatLng>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.map
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this@MapsFragment)
    }

    override fun onMapReady(gmap: GoogleMap) {
        googleMap = gmap
        setMapStyle()
        getStories()
    }

    private fun setMapStyle() {
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this.requireContext(), com.andka.hanashi.R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Can't find style. Error: ${e.message}")
        }
    }

    private fun getStories() {
        viewModel.getStoriesWithLocation()
        lifecycleScope.launch {
            viewModel.mapsViewState.collect {
                when (it.mapStories) {
                    is ResultState.Success -> {
                        it.mapStories.data?.forEach { story ->
                            val latLng = LatLng(story.lat!!, story.long!!)
                            marker.add(latLng)
                            googleMap.addMarker(
                                MarkerOptions().position(latLng).title(story.name)
                                    .snippet(story.description)
                            )
                        }
                        calculateMarker()
                    }

                    is ResultState.Error -> {
                        showToast(this@MapsFragment.requireContext(), it.mapStories.message)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun calculateMarker() {
        try {
            marker.forEachIndexed { _, firstMarker ->
                marker.forEachIndexed { _, secondMarker ->
                    val calculatedDistance =
                        calculateDistance(firstMarker, secondMarker)
                    if (calculatedDistance.toInt() != 0) {
                        distances[Pair(firstMarker, secondMarker)] =
                            calculatedDistance
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
        } finally {
            calculateAllDistance()
        }
    }

    private fun calculateDistance(
        marker: LatLng,
        reference: LatLng
    ): Double {
        val dLat = Math.toRadians(marker.latitude - reference.latitude)
        val dLon = Math.toRadians(marker.longitude - reference.longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(reference.latitude)) * cos(Math.toRadians(marker.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val radiusEarth = 6371
        return radiusEarth * c
    }


    private fun calculateAllDistance() {
        if (distances.isEmpty()) {
            return
        }
        distances.forEach { pair ->
            val (first, second) = pair.key
            val distance = pair.value
            markerDistances.add(Pair(first, distance))
            markerDistances.add(Pair(second, distance))
        }

        markerDistances.sortBy { it.second }
        val closestMarker = markerDistances.take(5).map { it.first }
        val selectedBounds = LatLngBounds.builder()
        closestMarker.forEach { selectedBounds.include(it) }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(selectedBounds.build(), 100))
    }

    companion object {
        private const val TAG = "MapsFragment"
    }
}