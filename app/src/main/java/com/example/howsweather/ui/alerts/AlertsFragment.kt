package com.example.howsweather.ui.alerts

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Binder
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.howsweather.R
import com.example.howsweather.databinding.AlertsFragmentBinding
import com.example.howsweather.model.CustomAlert
import com.example.howsweather.ui.favorite.FavoriteAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlertsFragment : Fragment(), OnDeleteClicked {

    companion object {
        fun newInstance() = AlertsFragment()
    }

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var alertAdapter: AlertsAdapter
    lateinit var binding: AlertsFragmentBinding
    lateinit var myPreferences: SharedPreferences
    val SETTINGS_SHARED_PREFS = "settings"
    lateinit var editor: SharedPreferences.Editor
    private val alertViewModel: AlertsViewModel by viewModels {
        AlertsViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = LayoutInflater.from(container?.context)
            .inflate(R.layout.alerts_fragment, container, false)
        binding = AlertsFragmentBinding.bind(view)

        linearLayoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(false)


        binding.alertRecycler.layoutManager = linearLayoutManager
        alertAdapter = AlertsAdapter(requireContext(), emptyList(), this)
        binding.alertRecycler.adapter = alertAdapter

        alertViewModel.getAllAlerts()
        alertViewModel.customList.observe(requireActivity(), Observer {
            if (it.size != 0) {
                binding.alarm.visibility = View.GONE
                binding.alertRecycler.visibility = View.VISIBLE
                binding.txtVEmptyAlarm.visibility = View.GONE

            } else {
                binding.alarm.visibility = View.VISIBLE
                binding.alertRecycler.visibility = View.GONE
                binding.txtVEmptyAlarm.visibility = View.VISIBLE
            }
            alertAdapter.setAlertList(it)
            alertAdapter.notifyDataSetChanged()
        })

        binding.alertBtn.setOnClickListener(View.OnClickListener {
            if (checkFirstAdd()) {
                checkDrawOverlayPermission()
                setFirstAdd()
            } else {
                CustomDialogFragment().show(requireActivity().supportFragmentManager, "tag")
            }
        })
    }

    private fun checkFirstAdd(): Boolean {
        myPreferences =
            requireActivity().getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
        return myPreferences.getBoolean("firstTime", true)
    }

    private fun setFirstAdd() {
        myPreferences.edit().putBoolean("firstTime", false).apply()
    }

    private fun checkDrawOverlayPermission() {
        // Check if we already  have permission to draw over other apps
        if (
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                !Settings.canDrawOverlays(context)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            // if not construct intent to request permission
            val alertDialogBuilder =
                MaterialAlertDialogBuilder(requireContext())
            alertDialogBuilder.setTitle("Please set our Permission")
                .setBackground(ColorDrawable(android.graphics.Color.GRAY))
                .setMessage("We are taking these Permissions to Help you")
                .setPositiveButton("OKAY") { dialog: DialogInterface, i: Int ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + requireContext().applicationContext.packageName)
                    )
                    // request permission via start activity for result
                    startActivityForResult(
                        intent,
                        1
                    ) //It will call onActivityResult Function After you press Yes/No and go Back after giving permission
                    dialog.dismiss()
                    CustomDialogFragment().show(requireActivity().supportFragmentManager, "tag")
                }.setNegativeButton(
                    "Cancel"
                ) { dialog: DialogInterface, i: Int ->
                    dialog.dismiss()
                    CustomDialogFragment().show(requireActivity().supportFragmentManager, "tag")
                }.show()
        }
    }

    override fun onDeleteClicked(customAlert: CustomAlert) {
        alertViewModel.deleteCustomAlert(customAlert)
    }

}