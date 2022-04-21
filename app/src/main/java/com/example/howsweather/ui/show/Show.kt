package com.example.howsweather.ui.show

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.howsweather.MainActivity
import com.example.howsweather.R
import com.example.howsweather.databinding.FragmentHomeBinding
import com.example.howsweather.model.Forecast
import com.example.howsweather.ui.favorite.FavoriteFragment
import com.example.howsweather.ui.favorite.FavoriteViewModel
import com.example.howsweather.ui.favorite.FavoriteViewModelFactory
import com.example.howsweather.ui.favorite.OnHolderClicked
import com.example.howsweather.ui.home.DailyAdapter
import com.example.howsweather.ui.home.HoursAdapter
import com.example.howsweather.utils.Helper
import com.example.weatherforecast.model.Hourly
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class Show : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var hourlyRecyclerView: RecyclerView
    private lateinit var hoursAdapter: HoursAdapter
    private lateinit var hourlyLinearLayoutManager: LinearLayoutManager
    private lateinit var dailyRecyclerView: RecyclerView
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var dailyLinearLayoutManager: LinearLayoutManager
    private lateinit var showViewModel: ShowViewModel
    val cal = Calendar.getInstance()
    lateinit var prefs: SharedPreferences
    var myID = 0
    lateinit var settingPrefs: SharedPreferences
    val SETTINGS_SHARED_PREFS = "settings"
    val LANG = "language"
    val WEATHER_UNIT = "weatherUnit"
    var lang: String = ""
    var weather: String = ""
    var lat = 0.0
    var long = 0.0
    lateinit var navController: NavController
    var tempUnit ="°c"
    var windSpeedUnit = "m/sec"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        prefs = requireActivity().getSharedPreferences("idFavPRefs", Context.MODE_PRIVATE)
        settingPrefs =
            requireActivity().getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
        lat = prefs.getFloat("lat", 0f).toDouble()
        long = prefs.getFloat("lon", 0f).toDouble()
        myID = prefs.getInt("id", 1)
        lang = settingPrefs.getString(LANG, "en")!!
        weather = settingPrefs.getString(WEATHER_UNIT, "metric")!!

        setHasOptionsMenu(true)
        val showViewModelFactory = ShowViewModelFactory(requireContext())
        showViewModel =
            ViewModelProvider(this, showViewModelFactory).get(ShowViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        hoursAdapter = HoursAdapter(requireContext(), emptyList())
        hourlyLinearLayoutManager = LinearLayoutManager(activity)
        hourlyLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        dailyAdapter = DailyAdapter(requireContext(), emptyList())
        dailyLinearLayoutManager = LinearLayoutManager(activity)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.GONE

        navController = Navigation.findNavController(view)
        if (Helper.isNetworkAvailable(requireContext())) {
            setLocale(lang)
            setUnits(weather, lang)
            val response = showViewModel.getWeatherDataAndInsertInDatabase(lat, long, weather, lang)
        }

        showViewModel.getFavById(myID, this)
        hourlyRecyclerView = view.findViewById(R.id.hoursRecycler)
        dailyRecyclerView = view.findViewById(R.id.dailyRecycler)
        dailyRecyclerView.setHasFixedSize(true)
        hourlyRecyclerView.layoutManager = hourlyLinearLayoutManager
        dailyRecyclerView.layoutManager = dailyLinearLayoutManager
        hourlyRecyclerView.adapter = hoursAdapter
        val progressBar: ProgressBar = view.findViewById(R.id.progress_circular)
        dailyRecyclerView.adapter = dailyAdapter

        showViewModel.hourlyList.observe(requireActivity(), Observer {
            if (it.isNotEmpty()) {
                progressBar.visibility = View.GONE
                hourlyRecyclerView.visibility = View.VISIBLE
                hoursAdapter.setHourlyList(it)
            }
            hoursAdapter.notifyDataSetChanged()
        })

        showViewModel.dailyList.observe(requireActivity(), Observer {

            selectBackGround(it[0].weather[0].main)

            if (it.isNotEmpty()) {
                dailyAdapter.setDailyList(it)
                dailyAdapter.notifyDataSetChanged()
            }
        })

        showViewModel.forecast.observe(requireActivity(), Observer {

            binding.txtVTimeZone.text = Helper.getCityName(requireContext(), lat, long, lang)
            binding.txtVDate.text = Helper.convertToDate(it.current.dt, lang)
            binding.txtVTempDegree.text = Helper.ArabicToEnglish(it.current.temp.toInt().toString()+ "${tempUnit}",lang)
            binding.txtVWeatherState.text = Helper.ArabicToEnglish(it.current.weather[0].description, lang)
            binding.txtVPressureSpeed.text = Helper.ArabicToEnglish(it.current.pressure.toString(), lang)
            binding.txtVHumidityDegree.text = Helper.ArabicToEnglish(it.current.humidity.toString(), lang)
            binding.txtVWindSpeed.text = Helper.ArabicToEnglish(it.current.wind_speed.toString(), lang)
            binding.txtVCloudDensity.text =Helper.ArabicToEnglish(it.current.clouds.toString(), lang)
            binding.txtVRadiationDegree.text = Helper.ArabicToEnglish(it.current.feels_like.toString(), lang)
            binding.txtVVisibilityClearance.text = Helper.ArabicToEnglish(it.current.visibility.toString(), lang)

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        navController.navigate(R.id.navigation_favourite)
        return super.onOptionsItemSelected(item)

    }
/*    private fun getCityName(lat: Double, lon: Double, lang:String): String {
        var city = ""
        val geocoder = Geocoder(context, Locale(lang))
        val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 1)
        if (addresses.isNotEmpty()) {
            val state = addresses[0].adminArea // damietta
            val country = addresses[0].countryName
            city = "$state, $country"
        }
        return city
    }*/

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

    private fun setLocale(lang: String) {
        val myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale
        conf.setLayoutDirection(myLocale)
        res.updateConfiguration(conf, dm)
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