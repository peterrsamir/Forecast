package com.example.howsweather.ui.map

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.animation.content.Content
import com.example.howsweather.R
import com.example.howsweather.ui.home.HomeFragment
import com.example.howsweather.utils.Helper
import com.example.repository.Repository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.toolbar.*
import java.util.prefs.AbstractPreferences


class MapsFragment : Fragment() {

    lateinit var navController: NavController
    var lat: Double = 0.0
    var long: Double = 0.0
    private val MAP_PREFS = "mapPrefs"
    private lateinit var mapViewModel: MapViewModel
    lateinit var sharedPreferences:  SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    val SETTINGS_SHARED_PREFS = "settings"
    val LANG = "language"
    val WEATHER_UNIT = "weatherUnit"
    lateinit var myPreferences: SharedPreferences

    private val callback = OnMapReadyCallback { googleMap ->

        val sydney = LatLng(-34.0, 151.0)

        googleMap.setOnMapClickListener {
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(it))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(it))
            lat = it.latitude
            long = it.longitude
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        myPreferences = requireActivity().getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
        val mapViewModeFactory = MapViewModelFactory(requireContext())
        mapViewModel = ViewModelProvider(this, mapViewModeFactory).get(MapViewModel::class.java)

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        navController = Navigation.findNavController(view)
//        val args = arguments
        val id = requireArguments().getBoolean(getString(R.string.toMap))

        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        requireActivity().menuInflater.inflate(R.menu.menu_toolbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var lang = myPreferences.getString(LANG, "en")
        var weather = myPreferences.getString(WEATHER_UNIT, "metric")
        when (item.itemId) {
            R.id.btnSaveLocationFromMap -> {
                if (Helper.isNetworkAvailable(requireContext())) {

                    if(arguments?.getBoolean(getString(R.string.toMap)) == true){
                        if(lat!=0.0){
                            mapViewModel.getWeatherFromRetroAndInsertFavoriteDb(lat, long, weather!!, lang!!)
                            navController.navigate(R.id.navigation_favourite)
                            Toast.makeText(requireContext(), getString(R.string.location_added), Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.choose_loc), Toast.LENGTH_SHORT).show()
                        }

                    }else{
                        if(lat!=0.0){
                            writeToPrefsFromMap(lat, long)
                            mapViewModel.getWeatherFromRetroAndInsertDb(lat, long, weather!!, lang!!)
                            navController.navigate(R.id.navigation_home)
                            Toast.makeText(requireContext(), getString(R.string.location_changed), Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.choose_loc), Toast.LENGTH_SHORT).show()
                        }

                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.check_connection), Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                navController.navigate(R.id.navigation_home)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun writeToPrefsFromMap(lat:Double, long:Double){
        sharedPreferences = requireActivity().getSharedPreferences(MAP_PREFS, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putFloat("lat", lat.toFloat())
        editor.putFloat("long", long.toFloat())
        editor.putBoolean("isCurrent", false)
        editor.commit()
    }

}