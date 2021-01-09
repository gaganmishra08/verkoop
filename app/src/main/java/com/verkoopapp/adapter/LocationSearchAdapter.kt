package com.verkoopapp.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.activity.SearchLocationActivity
import com.verkoopapp.models.Location
import com.verkoopapp.models.ResultLocation
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_location_row.*


class LocationSearchAdapter(private val context: Context) : RecyclerView.Adapter<LocationSearchAdapter.ViewHolder>() {
    lateinit var selectedPlaceListener: SelectedPlaceListener
    var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var searchResultList = ArrayList<ResultLocation>()
    private var type: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.search_location_row, parent, false)
        selectedPlaceListener = context as SearchLocationActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = searchResultList[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(modal: ResultLocation) {
            tvNameLocation.text = modal.name
            if (!type.equals(context.getString(R.string.search), ignoreCase = true)) {
                tvFullAddress.text = modal.vicinity
            } else {
                tvFullAddress.text = modal.formatted_address
            }
            itemView.setOnClickListener {
                selectedPlaceListener.selectedAddress(modal.name, modal.geometry.location)
            }
        }

    }

    fun setData(results: ArrayList<ResultLocation>, comingState: String) {
        searchResultList = results
        type = comingState
    }

    interface SelectedPlaceListener {
        fun selectedAddress(address: String, location: Location)
    }
}