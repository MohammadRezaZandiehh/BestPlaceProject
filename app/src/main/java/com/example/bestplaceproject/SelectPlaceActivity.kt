package com.example.bestplaceproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.bestplaceproject.databinding.ActivityMainBinding
import com.example.bestplaceproject.databinding.ActivitySelectPlaceBinding
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class SelectPlaceActivity : AppCompatActivity() {

    lateinit var binding: ActivitySelectPlaceBinding
    private var hoveringMarker: ImageView? = null
    private var droppedMarkerLayer: Layer? = null


    companion object {
        const val DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(
            this,
            "sk.eyJ1IjoiamFtc2hpZDEyMyIsImEiOiJja3RmbTVhaHkwOXFmMnZxbmdub2RwYXBlIn0.V_jogTHrDHMaLZraUFrhpg"
        )

        binding = ActivitySelectPlaceBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { mapBoxMap ->
            mapBoxMap.setStyle(Style.MAPBOX_STREETS) { style ->
                showRedPickerImage()
                creteGreenSelectedMark(style)
            }
        }
    }



    private fun showRedPickerImage() {
        hoveringMarker = ImageView(this@SelectPlaceActivity)
        hoveringMarker!!.setBackgroundResource(R.drawable.ic_marker_red)

        val imageLocation = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )

        hoveringMarker!!.layoutParams = imageLocation

        binding.mapView.addView(hoveringMarker)
    }



    private fun creteGreenSelectedMark(loadedStyle: Style) {
        loadedStyle.addImage(
            "dropped-icon-image",
            ContextCompat.getDrawable(this, R.drawable.ic_pin)!!
        )

        loadedStyle.addSource(GeoJsonSource("dropped-icon-image"))

        val symbol = SymbolLayer(
            DROPPED_MARKER_LAYER_ID, "dropped-icon-image"
        )

        symbol.withProperties(
            PropertyFactory.iconImage("dropped-icon-image"),
            PropertyFactory.visibility(Property.NONE),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true)
        )

        loadedStyle.addLayer(symbol)
    }
}