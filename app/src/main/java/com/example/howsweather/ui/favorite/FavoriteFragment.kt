package com.example.howsweather.ui.favorite

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.howsweather.R
import com.example.howsweather.databinding.FavoriteFragmentBinding
import com.example.howsweather.model.Forecast
import com.example.howsweather.utils.Helper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment(), OnDeleteClicked , OnHolderClicked{

    lateinit var binding: FavoriteFragmentBinding
    lateinit var recyclerView: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var favAdapter: FavoriteAdapter
    private val ID_PREFS = "idFavPRefs"
    lateinit var sharedPreferences:SharedPreferences
    lateinit var editor :SharedPreferences.Editor

    companion object {
        fun newInstance() = FavoriteFragment()
    }

    private lateinit var viewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = LayoutInflater.from(container?.context).inflate(R.layout.favorite_fragment,container,false)
        binding = FavoriteFragmentBinding.bind(view)
        recyclerView = binding.favRecycler
        linearLayoutManager = LinearLayoutManager(requireContext())

        val favoriteViewModelFactory = FavoriteViewModelFactory(requireContext())
        viewModel =
            ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavorite(this)
        recyclerView = view.findViewById(R.id.favRecycler)
        recyclerView.layoutManager = linearLayoutManager
        favAdapter = FavoriteAdapter(requireContext(), emptyList(), this, this)
        recyclerView.adapter = favAdapter
        recyclerView.setHasFixedSize(true)

        viewModel.favoriteList.observe(requireActivity(), Observer {
            if (it.isNullOrEmpty()) {
                binding.txtVemptyFavList.visibility = View.VISIBLE
                binding.favSplash.visibility = View.VISIBLE
                binding.favRecycler.visibility = View.GONE
            } else {
                binding.favRecycler.visibility = View.VISIBLE
                binding.favSplash.visibility = View.GONE
                binding.txtVemptyFavList.visibility = View.GONE
                favAdapter.setFavList(it)
                favAdapter.notifyDataSetChanged()
            }
        })

        binding.fab.setOnClickListener(View.OnClickListener {
//            onSendLocationData.onOpenMap(LAT, LONG)
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_favourite_to_mapsFragment)
        })
    }

    override fun onDelete(id: Int) {
        lifecycleScope.launch {
            viewModel.deleteFavorite(id)
            favAdapter.notifyDataSetChanged()
        }
    }

    override fun showFav(view: View, forecast: Forecast) {
        sharedPreferences = requireActivity().getSharedPreferences(ID_PREFS, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putInt("id", forecast.id)
        editor.putFloat("lat", forecast.lat.toFloat())
        editor.putFloat("lon", forecast.lon.toFloat())
        editor.commit()
        Navigation.findNavController(view).navigate(R.id.action_navigation_favourite_to_show)

    }
}