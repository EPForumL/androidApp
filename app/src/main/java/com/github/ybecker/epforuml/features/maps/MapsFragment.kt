package com.github.ybecker.epforuml.features.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.concurrent.TimeUnit

/**
 * Google map fragment that shows the user and other users on the map.
 */
class MapsFragment : Fragment(),
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    OnMapReadyCallback,
    MenuProvider {

    // Used to get the current user's location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Called on user's location updates
    private lateinit var locationCallback: LocationCallback

    // Sets the behaviour of the user's location updates
    private lateinit var locationRequest: LocationRequest

    // Asks the user to give localization permissions : Fine Location, Coarse Location or None
    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { onRequestPermissionsResult(it) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Callback called when the location changes
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                if (location != null) {
                    updateUserLocation(location)
                }
            }
        }

        // Set location update properties
        val interval = TimeUnit.SECONDS.toMillis(60)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval).apply {
            setMinUpdateDistanceMeters(10.0F)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        // Add upper right menu components
        requireActivity().addMenuProvider(
            this,
            viewLifecycleOwner,
            androidx.lifecycle.Lifecycle.State.RESUMED
        )
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    /**
     * Gets all users in the database (except the current user) that share their localization
     * and displays them on the map.
     */
    private fun displayOtherUsers(map: GoogleMap) {
        DatabaseManager.db.getOtherUsers(DatabaseManager.user?.userId ?: "")
            .thenAccept { users ->
                val usersSharingLocation = users.filter { it.sharesLocation }
                usersSharingLocation.forEach {
                    val position = LatLng(it.latitude, it.longitude)
                    map.addMarker(
                        MarkerOptions().position(position).title(it.username)
                    )
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        val user = DatabaseManager.user
        if (user != null) {
            // Enables the user's position marker
            map.isMyLocationEnabled = true
            // Button that centers the view on the user's position
            map.setOnMyLocationButtonClickListener(this)
            map.setOnMyLocationClickListener(this)

            // Move and zoom view on user
            val position = LatLng(user.latitude, user.longitude)
            map.animateCamera(CameraUpdateFactory.newLatLng(position))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))

            displayOtherUsers(map)
        }
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(requireContext(), "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(requireContext(), "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (isLocalizationNotGranted()) {
            // Asks user location permissions
            requestPermissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        } else if (DatabaseManager.user?.sharesLocation == false) {
            // If user disables the location sharing, put fake coordinates
            val userId = DatabaseManager.user?.userId
            val position = LatLng(-200.0, -200.0)
            DatabaseManager.user?.latitude = position.latitude
            DatabaseManager.user?.longitude = position.longitude
            DatabaseManager.db.updateLocalization(userId!!, position, false)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                updateUserLocation(location)

                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

                mapFragment.getMapAsync(this)
            }
        }
    }

    private fun updateUserLocation(location: Location) {
        DatabaseManager.user.let {
            if (it != null) {
                it.latitude = location.latitude
                it.longitude = location.longitude
                val sharesLocation = DatabaseManager.user?.sharesLocation ?: false
                DatabaseManager.db.updateLocalization(
                    it.userId,
                    LatLng(location.latitude, location.longitude),
                    sharesLocation
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (!isLocalizationNotGranted()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun isLocalizationNotGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks which permissions where granted after asking the user.
     */
    private fun onRequestPermissionsResult(permissions: Map<String, Boolean>) {
        DatabaseManager.user.let { user ->
            if (user != null) {
                val position = LatLng(user.latitude, user.longitude)
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted.
                        // requireActivity().invalidateOptionsMenu()
                        DatabaseManager.user?.sharesLocation = true
                        DatabaseManager.db.updateLocalization(user.userId, position, true)
                        getCurrentLocation()
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        // Only approximate location access granted.
                        DatabaseManager.user?.sharesLocation = true
                        DatabaseManager.db.updateLocalization(user.userId, position, true)
                        getCurrentLocation()
                    }
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.map_menu, menu)
        // If the user granted all permissions then hide the "Change permissions" button
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            menu.findItem(R.id.change_map_perm).isVisible = false
        }
        // Synchronize state of the "Share position" button with the user's attribute
        menu.findItem(R.id.share_position).isChecked = DatabaseManager.user?.sharesLocation ?: false
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.change_map_perm -> {
                requestPermissions.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            R.id.share_position -> {
                // Change state of the share position button and save it to the user
                if (DatabaseManager.user != null) {
                    val sharesLocation = !menuItem.isChecked
                    DatabaseManager.user!!.sharesLocation = sharesLocation
                    val position = LatLng(DatabaseManager.user!!.latitude, DatabaseManager.user!!.longitude)
                    DatabaseManager.db
                        .updateLocalization(DatabaseManager.user!!.userId, position, sharesLocation)
                    getCurrentLocation()
                    menuItem.isChecked = !menuItem.isChecked
                }
            }
        }
        return true
    }
}