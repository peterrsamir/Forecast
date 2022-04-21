package com.example.howsweather.ui.favorite

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.howsweather.R
import com.example.howsweather.model.Forecast
import com.example.howsweather.utils.Helper

class FavoriteAdapter(val context: Context,  var list: List<Forecast>, var onDeleteClicked: OnDeleteClicked,
                      var onHolderClicked: OnHolderClicked) : RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder>() {

    val SETTINGS_SHARED_PREFS = "settings"
    val LANG = "language"
    lateinit var lang:String
    lateinit var myPreferences: SharedPreferences

    class FavoriteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCityName :TextView = itemView.findViewById(R.id.txtVCityName)
        val imgDelete :ImageView = itemView.findViewById(R.id.imgDelete)
        val favRow:CardView = itemView.findViewById(R.id.favRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.fav_custom_row, null)

        myPreferences =context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
        lang = myPreferences.getString(LANG, "en")!!
        return FavoriteHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteHolder, position: Int) {
        holder.txtCityName.text = Helper.getCityName(context, list[position].lat, list[position].lon, lang)
        holder.imgDelete.setOnClickListener(View.OnClickListener {
            onDeleteClicked.onDelete(list[position].id)
        })
        holder.favRow.setOnClickListener(View.OnClickListener {
            onHolderClicked.showFav(holder.favRow, list.get(position))
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setFavList(favList: List<Forecast>){
        this.list = favList
        notifyDataSetChanged()
    }



}