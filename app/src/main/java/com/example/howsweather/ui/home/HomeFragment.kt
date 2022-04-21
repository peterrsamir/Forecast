package com.example.howsweather.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.howsweather.MainActivity
import com.example.howsweather.R
import com.example.howsweather.databinding.FragmentHomeBinding
import com.example.howsweather.ui.map.OnSendLocationData
import com.example.howsweather.utils.Helper
import com.example.weatherforecast.model.Hourly
import com.google.android.gms.location.*
import com.google.android.gms.maps.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment() {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var hourlyRecyclerView: RecyclerView
    private lateinit var hoursAdapter: HoursAdapter
    private lateinit var hourlyLinearLayoutManager: LinearLayoutManager
    private lateinit var dailyRecyclerView: RecyclerView
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var dailyLinearLayoutManager: LinearLayoutManager
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    val cal = Calendar.getInstance()
    val PERMISSION_ID: Int = 1
    var LAT = 0.0
    var MY_LOCATION = "0.0"
    var LONG = 0.0
    var address = emptyList<Address>()
    val SETTINGS_SHARED_PREFS = "settings"
    val LANG = "language"
    val WEATHER_UNIT = "weatherUnit"
    var tempUnit = "°c"
    var windSpeedUnit = "m/sec"
    lateinit var myPreferences: SharedPreferences
    lateinit var mapPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var lang: String
    lateinit var weather: String
    private val MAP_PREFS = "mapPrefs"
    private var isCurrent = true


//    var isCurrent = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val homeViewModelFactory = HomeViewModelFactory(requireContext())
        homeViewModel =
            ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        myPreferences =
            requireActivity().getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
        mapPreferences = requireActivity().getSharedPreferences(MAP_PREFS, Context.MODE_PRIVATE)
        editor = myPreferences.edit()
        lang = myPreferences.getString(LANG, "en")!!
        weather = myPreferences.getString(WEATHER_UNIT, "metric")!!
        LAT = mapPreferences.getFloat("lat", 0f).toDouble()
        LONG = mapPreferences.getFloat("long", 0f).toDouble()

        setLocale(lang!!)
        setUnits(weather!!, lang)


        val root: View = binding.root

        hoursAdapter = HoursAdapter(requireContext(), emptyList())
        hourlyLinearLayoutManager = LinearLayoutManager(activity)
        hourlyLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        dailyAdapter = DailyAdapter(requireContext(), emptyList())
        dailyLinearLayoutManager = LinearLayoutManager(activity)

//        permissions()

//        homeViewModel.getWeatherData(LAT, LONG)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        requireActivity().menuInflater.inflate(R.menu.get_current, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.getCurrent -> {
                permissions()
                if (Helper.isNetworkAvailable(requireContext()) && LAT != 0.0) {
                    mapPreferences.edit().putBoolean("isCurrent" , true).apply()
                    mapPreferences.edit().putFloat("lat" ,LAT.toFloat()).apply()
                    mapPreferences.edit().putFloat("long" , LONG.toFloat()).apply()

                    homeViewModel.getWeatherDataAndInsertInDatabase(LAT, LONG, weather, lang)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.check_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)

    }

    private fun permissions() {
        if (checkPermission()) {
            if (checkIsEnabled()) {
                getLocation()
            } else {
                enableGps()
            }
        } else {
            requestPer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var latitude = 0f
        var longitude = 0f

        permissions()
        var sharedPrefs =
            requireActivity().getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
        latitude = sharedPrefs.getFloat("lat", 0f)
        longitude = sharedPrefs.getFloat("lat", 0f)

        if (latitude != 0f && LAT != 0.0) {
            try {
                if(Helper.isNetworkAvailable(requireContext())){
                    homeViewModel.getWeatherDataAndInsertInDatabase(LAT, LONG, weather!!, lang!!)
                }else{
                    Toast.makeText(requireContext(), getString(R.string.check_connection), Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "" + e.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        homeViewModel.getWeatherFromLocal(this)
        hourlyRecyclerView = view.findViewById(R.id.hoursRecycler)
        dailyRecyclerView = view.findViewById(R.id.dailyRecycler)
        dailyRecyclerView.setHasFixedSize(true)
        hourlyRecyclerView.layoutManager = hourlyLinearLayoutManager
        dailyRecyclerView.layoutManager = dailyLinearLayoutManager
        hourlyRecyclerView.adapter = hoursAdapter
        val progressBar: ProgressBar = view.findViewById(R.id.progress_circular)
        dailyRecyclerView.adapter = dailyAdapter


        if (!MY_LOCATION.isBlank()) {
            homeViewModel.hourlyList.observe(requireActivity(), Observer<List<Hourly>> {
                if (it.isNotEmpty()) {
                    progressBar.visibility = View.GONE
                    hourlyRecyclerView.visibility = View.VISIBLE
                    hoursAdapter.setHourlyList(it)
                }
                hoursAdapter.notifyDataSetChanged()
            })

            homeViewModel.dailyList.observe(requireActivity(), Observer {

                selectBackGround(it[0].weather[0].main)

                if (it.isNotEmpty()) {
                    dailyAdapter.setDailyList(it)
                    dailyAdapter.notifyDataSetChanged()
                }
            })
            homeViewModel.forecast.observe(requireActivity(), Observer {

                if (it.lat != 0.0 && it.lon != 0.0) {
                    binding.txtVTimeZone.text = getCityName(it.lat, it.lon, lang)
                }
                binding.txtVDate.text = Helper.convertToDate(it.current.dt, lang)
                binding.txtVTempDegree.text =
                    Helper.ArabicToEnglish(it.current.temp.toInt().toString() + " $tempUnit", lang)
                binding.txtVWeatherState.text = it.current.weather[0].description
                binding.txtVPressureSpeed.text =
                    Helper.ArabicToEnglish(it.current.pressure.toString(), lang)
                binding.txtVHumidityDegree.text =
                    Helper.ArabicToEnglish(it.current.humidity.toString(), lang)
                binding.txtVWindSpeed.text = Helper.ArabicToEnglish(
                    it.current.wind_speed.toString() + " $windSpeedUnit",
                    lang
                )
                binding.txtVCloudDensity.text =
                    Helper.ArabicToEnglish(it.current.clouds.toString(), lang)
                binding.txtVRadiationDegree.text =
                    Helper.ArabicToEnglish(it.current.feels_like.toString(), lang)
                binding.txtVVisibilityClearance.text =
                    Helper.ArabicToEnglish(it.current.visibility.toString(), lang)
            })
        }

        binding.fab.setOnClickListener(View.OnClickListener {
//            onSendLocationData.onOpenMap(LAT, LONG)
            if (Helper.isNetworkAvailable(requireContext())) {
                if (checkIsEnabled()) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_home_to_mapsFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.enable_gps),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.check_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    fun setUnits(unit: String, lang: String) {
        if (lang.equals("en")) {
            when (unit) {
                "metric" -> {
                    tempUnit = "°c"
                    windSpeedUnit = "m/s"
                }
                "imperial" -> {
                    tempUnit = "°f"
                    windSpeedUnit = "m/h"
                }
                "standard" -> {
                    tempUnit = "°k"
                    windSpeedUnit = "m/s"
                }
            }
        } else {
            when (unit) {
                "metric" -> {
                    tempUnit = "°س"
                    windSpeedUnit = "متر/ث"
                }
                "imperial" -> {
                    tempUnit = "°ف"
                    windSpeedUnit = "ميل/ س"
                }
                "standard" -> {
                    tempUnit = "°ك"
                    windSpeedUnit = "متر/ س"
                }
            }
        }

    }

    private fun getCityName(lat: Double, lon: Double, lang: String): String {
        var city = ""
        val geocoder = Geocoder(context, Locale(lang))
        val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 1)
        Log.i("location", "getCityText: $lat + $lon + $addresses")
        if (addresses.isNotEmpty()) {
            val state = addresses[0].adminArea // damietta
            val country = addresses[0].countryName
            city = "$state, $country"
        }
        return city
    }

    fun checkPermission(): Boolean // check if location permission is granted
    {
        return ActivityCompat.checkSelfPermission(
            requireActivity(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireActivity(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun enableGps() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    fun getDate(): String {
        val today = LocalDate.now()
        val tst = DateTimeFormatter.ofPattern("EE, MMM d, uuuu", Locale.forLanguageTag("ar-OM"))
        val formatter = today.format(DateTimeFormatter.ofPattern("EE, d MMM"))
        return formatter
    }

    fun requestPer() //ask user for location permession
    {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    fun checkIsEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.VISIBLE
    }

    var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            isCurrent = mapPreferences.getBoolean("isCurrent", true)

            var lang = myPreferences.getString(LANG, "en")
            var weather = myPreferences.getString(WEATHER_UNIT, "metric")
            val geocoder = Geocoder(requireContext())
            try {
                address = geocoder.getFromLocation(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude, 1
                )

                if (isCurrent) {
                    if (Helper.isNetworkAvailable(requireContext())) {
                        LAT = locationResult.lastLocation.latitude
                        LONG = locationResult.lastLocation.longitude
                        mapPreferences.edit().putBoolean("isCurrent" , true).apply()
                    }
                }

                MY_LOCATION =
                    getCityName(LAT, LONG, lang!!)




                var sharedPrefs =
                    requireActivity().getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
                var latitude = sharedPrefs.getFloat("lat", 0f)
                var longitude = sharedPrefs.getFloat("lat", 0f)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun setLocale(lang: String) {
        val myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale
        conf.setLayoutDirection(myLocale)
        res.updateConfiguration(conf, dm)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.numUpdates = 1
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient?.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()
        )
    }

    fun selectBackGround(weather: String) {
        when (weather) {
            "Snow" -> {
                binding.snow.visibility = View.VISIBLE
                binding.rainJson.visibility = View.GONE
                binding.cloudJson.visibility = View.GONE
            }

            "Light Snow" -> {
                binding.snow.visibility = View.GONE
                binding.rainJson.visibility = View.GONE
                binding.cloudJson.visibility = View.GONE
            }
            "Rain" -> {
                binding.snow.visibility = View.GONE
                binding.cloudJson.visibility = View.GONE
                binding.rainJson.visibility = View.VISIBLE
            }
            else -> {
                binding.cloudJson.visibility = View.VISIBLE
                binding.rainJson.visibility = View.GONE
                binding.snow.visibility = View.GONE
            }
        }

    }
}