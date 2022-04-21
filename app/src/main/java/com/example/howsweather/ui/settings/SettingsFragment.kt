package com.example.howsweather.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.howsweather.MainActivity
import com.example.howsweather.R
import com.example.howsweather.databinding.SettingsFragmentBinding
import com.example.howsweather.utils.Helper
import kotlinx.android.synthetic.main.settings_fragment.*
import java.util.*


class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }
    private lateinit var viewModel: SettingsViewModel
    lateinit var binding: SettingsFragmentBinding
    lateinit var langRadioGroup: RadioGroup
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    val SETTINGS_SHARED_PREFS = "settings"
    val LANG = "language"
    val WEATHER_UNIT = "weatherUnit"
    var getLang = ""
    var weather = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = LayoutInflater.from(container?.context)
            .inflate(R.layout.settings_fragment, container, false)
        binding = SettingsFragmentBinding.bind(view)

        sharedPreferences =
            requireActivity().getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        langRadioGroup = binding.radioGroupLanguage

        val viewModelFactory = SettingsViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)
        var myPreferences =
            requireActivity().getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
        getLang = myPreferences.getString(LANG, "en")!!
        weather = myPreferences.getString(WEATHER_UNIT, "metric")!!

        when (getLang) {
            "en" -> binding.radioBtnEnglish.isChecked = true
            "ar" -> binding.radioBtnArabic.isChecked = true
        }
        when (weather) {
            "metric" -> binding.settingsRBCelsius.isChecked = true
            "imperial" -> binding.settingsRBFahrenheit.isChecked = true
            "standard" -> binding.settingsRBKelvin.isChecked = true

        }

        langRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.radioBtnArabic -> setLocale("ar")
                R.id.radioBtnEnglish -> setLocale("en")
            }
        }

        binding.radioGroupUnits.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.settingsRBCelsius ->{
                    changeUnit("metric")
                }
                R.id.settingsRBKelvin -> {
                    changeUnit("standard")
                }
                R.id.settingsRBFahrenheit -> {
                    changeUnit("imperial")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        requireActivity().menuInflater.inflate(R.menu.settings_toolbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnSaveSettings -> {
                if(Helper.isNetworkAvailable(requireContext())){
//                    viewModel.updateDatabase(weather, getLang)
                    Toast.makeText(requireContext(), getString(R.string.settings_changed), Toast.LENGTH_SHORT
                    ).show()
                    val refresh = Intent(requireContext(), MainActivity::class.java)
                    activity?.finish()
                    startActivity(refresh)
                }else{
                    Toast.makeText(requireContext(), getString(R.string.check_connection), Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
        return super.onOptionsItemSelected(item)

    }



    private fun setLocale(lang: String) {
        editor.putString(LANG, lang)
        editor.commit()
        val myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale
        conf.setLayoutDirection(myLocale)
        res.updateConfiguration(conf, dm)
        /*val refresh = Intent(requireContext(), MainActivity::class.java)
        activity?.finish()
        startActivity(refresh)*/
    }

    private fun changeUnit(unit: String) {
        editor.putString(WEATHER_UNIT, unit)
        editor.commit()
    }
}