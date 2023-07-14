package com.example.myapp.View

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.IOException
import java.util.Locale


class MapActivity : AppCompatActivity() {
    private lateinit var map: MapView
    private lateinit var confirmButton: FrameLayout
    private lateinit var marker : Marker
    private lateinit var markerIcon : ImageView
    private lateinit var currentLocationButtonContainer: FrameLayout
    private lateinit var currentLocationField: TextView
    private var countDownTimer: CountDownTimer? = null
    private lateinit var mLocationOverlay : MyLocationNewOverlay


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity)

        checkLocationAccess()


        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        Configuration.getInstance().userAgentValue = applicationContext.packageName


        map = findViewById(R.id.map)
        marker = Marker(map)
        confirmButton = findViewById(R.id.confirm_button_container)
        markerIcon = findViewById(R.id.current_location_pointer)
        currentLocationField = findViewById(R.id.current_location_field)
        currentLocationButtonContainer = findViewById(R.id.current_location_button_container)
        Configuration.getInstance().userAgentValue = "Ammar"

        setUpMarkerAnimations()
        setupMap()
        setupMyLocation()

        confirmButton.setOnClickListener {
            if (!currentLocationField.text.equals("Loading...")) {
                val intent = Intent()
                intent.putExtra("selectedAddress", currentLocationField.text)
                setResult(RESULT_OK, intent)
                finish()
            }
            else {
                Toast.makeText(this@MapActivity, "Please wait for location to load", Toast.LENGTH_SHORT).show()
            }

        }


    }

    private fun setupCurrentLocationButton() {

        currentLocationButtonContainer.setOnClickListener{

            if (mLocationOverlay.myLocation != null) {

                val geoPoint = GeoPoint(
                    mLocationOverlay.myLocation.latitude,
                    mLocationOverlay.myLocation.longitude
                )
                map.controller.setCenter(geoPoint)
                simulateLoading()
            }
        }
    }

    private fun simulateLoading() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(1500, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentLocationField.text = "Loading..."
            }

            override fun onFinish() {

                val centerPoint = map.mapCenter
                convertPointToLocation(centerPoint.latitude, centerPoint.longitude)
            }
        }.start()
    }

    private fun convertPointToLocation(latitude: Double, longitude: Double) {

        val geocoder = Geocoder(this@MapActivity, Locale.getDefault())
        try {

            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val res = StringBuilder()
            if (!addresses.isNullOrEmpty()) {
                for (i in 0 until addresses[0].maxAddressLineIndex + 1) {
                    res.append(addresses[0].getAddressLine(i))
                }
                currentLocationField.text = res.toString()
            }

        } catch (e: IOException) {
            currentLocationField.text = "Error Fetching Address."
            Toast.makeText(this@MapActivity, "Error while getting address", Toast.LENGTH_SHORT).show()
        }
    }




    @SuppressLint("ClickableViewAccessibility")
    private fun setUpMarkerAnimations() {

        map.setOnTouchListener { v, event ->

            if (event?.pointerCount == 1) {

                if (event!!.action == MotionEvent.ACTION_DOWN) {
                    val x = 1.1.toFloat()
                    val y = 1.1.toFloat()


                    markerIcon.scaleX = x
                    markerIcon.scaleY = y
                    val markerAnimation: Animation = TranslateAnimation(0f, 0f, 0f, -16f)
                    markerAnimation.duration = 100
                    markerAnimation.fillAfter = true

                    markerIcon.startAnimation(markerAnimation)

                }
                if (event!!.action == MotionEvent.ACTION_UP) {
                    val x = 1.toFloat()
                    val y = 1.toFloat()

                    val markerAnimation: Animation = TranslateAnimation(0f, 0f, -16f, 0f)
                    markerAnimation.duration = 100
                    markerAnimation.fillAfter = true

                    markerIcon.startAnimation(markerAnimation)
                    markerIcon.scaleX = x
                    markerIcon.scaleY = y

                    simulateLoading()

                }
            }
            false;
        }
    }

    private fun setupMap() {

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)
        val mapController: IMapController = map.controller
        mapController.setZoom(16)
        val startPoint = GeoPoint(33.6844, 73.0479) // Islamabad
        map.controller.setCenter(startPoint)

    }

    private fun checkLocationAccess() {

        Log.d("MSG", "Inside Location Check")
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (30 * 1000).toLong()
        locationRequest.fastestInterval = (5 * 1000).toLong()


        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        builder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                         // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                this@MapActivity,
                                69
                            )
                        } catch (e: SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        }
    }


    private fun setupMyLocation() {
        mLocationOverlay =
            object : MyLocationNewOverlay(GpsMyLocationProvider(this), map) {
                override fun onLocationChanged(location: Location?, source: IMyLocationProvider?) {
                    super.onLocationChanged(location, source)
                    location?.let {
                        Log.i("MyCurrentSpeed", it.speed.toString())
                    }
                }
            }
        mLocationOverlay.enableMyLocation()
        map.overlays.add(this.mLocationOverlay)
    }

    private fun initialCalibration() {
        Log.d("CALIBRATION", "Inside")

        if (mLocationOverlay.myLocation != null) {
            val geoPoint = GeoPoint(
                mLocationOverlay.myLocation.latitude,
                mLocationOverlay.myLocation.longitude
            )
            map.controller.setCenter(geoPoint)
        } else {
            val startPoint = GeoPoint(33.6844, 73.0479) // Islamabad
            map.controller.setCenter(startPoint)
        }

        val centerPoint = map.mapCenter
        convertPointToLocation(centerPoint.latitude, centerPoint.longitude)
    }

    override fun onResume() {
        super.onResume()
        Log.d("MSG", "inside Map's onResume")
        map.onResume()

        val handler = Handler(Looper.getMainLooper())

        val runnableCode = Runnable {
            initialCalibration()
            setupCurrentLocationButton()
        }

        handler.postDelayed(runnableCode, 3000)
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }



}
