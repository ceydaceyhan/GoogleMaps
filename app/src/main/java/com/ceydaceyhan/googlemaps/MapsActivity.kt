package com.ceydaceyhan.googlemaps

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ceydaceyhan.googlemaps.databinding.ActivityMapsBinding
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(dinleyici)
        //Latitude = Enlem
        //Longitude = Boylam
        //LatLng = enlem ve boylam bildiren fonk
        //39.737969, 37.034721

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = LocationListener { p0 -> //konum değişince yapılacak işlemler
            val detaySoft = LatLng(39.737969, 37.034721)
            mMap.addMarker(MarkerOptions().position(detaySoft).title("Detay Soft"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(detaySoft, 17f))

            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
            try {
                val adresListesi = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)
                if (adresListesi?.size!! > 0) {
                    println(adresListesi[0].toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //izin verilmemiş
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            //izin verilmiş
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1,
                1f,
                locationListener
            )
            val sonKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (sonKonum != null) {
                val sonBilinenLatLng = LatLng(sonKonum.latitude, sonKonum.longitude)
                mMap.addMarker(MarkerOptions().position(sonBilinenLatLng).title("Detay Soft"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLatLng, 13f))

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty()) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                )
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1,
                        1f,
                        locationListener
                    )

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val dinleyici = GoogleMap.OnMapLongClickListener { p0 ->
        mMap.clear()
        val gecoder = Geocoder(this@MapsActivity, Locale.getDefault())
        if (p0 != null) {
            var adres = " "
            try {
                val adresListesi = gecoder.getFromLocation(p0.latitude, p0.longitude, 1)
                if (adresListesi?.size!! > 0) {
                    if (adresListesi[0].thoroughfare != null) {
                        adres += adresListesi[0].thoroughfare
                        if (adresListesi[0].subThoroughfare != null) {
                            adres += adresListesi[0].subThoroughfare

                        }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mMap.addMarker(MarkerOptions().position(p0).title(adres))
        }
    }
}