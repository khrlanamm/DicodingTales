package com.khrlanamm.dicodingtales.ui.maps

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.khrlanamm.dicodingtales.R
import com.khrlanamm.dicodingtales.data.Result
import com.khrlanamm.dicodingtales.data.local.pref.SessionManager
import com.khrlanamm.dicodingtales.data.remote.response.ListStoryItem
import com.khrlanamm.dicodingtales.databinding.ActivityMapsBinding
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMaps: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var sessionManager: SessionManager
    private val mapsViewModel: MapsViewModel by viewModels {
        MapsFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val token = sessionManager.getAuthToken()

        if (token != null) {
            mapsViewModel.getAllStoriesWithMap(token)
        }

        observeViewModel()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    private fun observeViewModel() {
        mapsViewModel.stories.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    val stories = result.data
                    addManyMarker(stories)
                }

                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        mapsViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }


    private fun truncateSnippet(snippet: String, maxLength: Int): String {
        return if (snippet.length > maxLength) {
            snippet.take(maxLength) + "..."
        } else {
            snippet
        }
    }

    private fun addManyMarker(stories: List<ListStoryItem>) {
        if (::gMaps.isInitialized) {
            val boundsBuilder = LatLngBounds.builder()
            val markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.bangkit_mini)

            stories.forEach { story ->
                val lat = story.lat
                val lon = story.lon
                if (lat != null && lon != null) {
                    val latLng = LatLng(lat, lon)

                    val truncatedSnippet = story.description?.let {
                        truncateSnippet(it, maxLength = 40)
                    } ?: ""

                    gMaps.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(story.name)
                            .snippet(truncatedSnippet)
                            .icon(markerIcon)
                    )
                    boundsBuilder.include(latLng)
                }
            }

            val bounds = boundsBuilder.build()
            val padding = (resources.displayMetrics.widthPixels * 0.10).toInt()
            gMaps.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    padding
                )
            )
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        gMaps = googleMap

        showLoading(true)

        gMaps.uiSettings.isZoomControlsEnabled = true
        gMaps.uiSettings.isIndoorLevelPickerEnabled = true
        gMaps.uiSettings.isCompassEnabled = true
        gMaps.uiSettings.isMapToolbarEnabled = true

        setMapStyle()

        lifecycleScope.launch {
            kotlinx.coroutines.delay(5000)
            showLoading(false)
        }
    }


    private fun setMapStyle() {
        try {
            val success =
                gMaps.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_style, menu)

        menu?.findItem(R.id.normal_type)?.isChecked = true

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        binding.toolbar.menu.forEach { menuItem ->
            menuItem.isChecked = false
        }
        item.isChecked = true

        return when (item.itemId) {
            R.id.normal_type -> {
                gMaps.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }

            R.id.satellite_type -> {
                gMaps.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }

            R.id.terrain_type -> {
                gMaps.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }

            R.id.hybrid_type -> {
                gMaps.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}