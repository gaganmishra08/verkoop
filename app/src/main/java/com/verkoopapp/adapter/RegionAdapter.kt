package com.verkoopapp.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.activity.RegionActivity
import com.verkoopapp.models.CityDataValue
import com.verkoopapp.models.CityResponse
import com.verkoopapp.models.StateDataValue
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.region_row.*
import retrofit2.Response


class RegionAdapter(private val context: Context, private val coming: Int) : RecyclerView.Adapter<RegionAdapter.ViewHolder>(), Filterable {
    private var statesList = ArrayList<StateDataValue>()
    private var mFilteredList = ArrayList<StateDataValue>()
    private lateinit var clickEventCallBack: ClickEventCallBack
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var citylist = ArrayList<CityDataValue>()
    val font = Typeface.createFromAsset(context.assets, "fonts/gothic.ttf")

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                val charString = charSequence.toString()

                if (charString.isEmpty()) {
                    mFilteredList = statesList
                } else {

                    val filteredList = ArrayList<StateDataValue>()

                    for (androidVersion in statesList) {

                        if (androidVersion.name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(androidVersion)
                        }
                    }

                    mFilteredList = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                mFilteredList = filterResults.values as ArrayList<StateDataValue>
                notifyDataSetChanged()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.region_row, parent, false)
        clickEventCallBack = context as RegionActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mFilteredList[position]
        holder.bind(data,position)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: StateDataValue, position: Int) {
            cbRegion.typeface = font
//            cbRegion.isChecked = data.isSelected
            cbRegion.text = data.name

//            for (i in statesList) {
//
//                if (!Utils.getPreferencesString(context, AppConstants.STATE_NAME).isEmpty()){
//                    if (Utils.getPreferencesString(context, AppConstants.STATE_NAME).equals(statesList[position].name)){
//                        cbRegion.isChecked=true
//                    }
//                }
//            }
            cbRegion.setOnClickListener {
                refreshList(adapterPosition)
                if (coming == 0) {

                    for (i in 0 until statesList.size) {

                        Log.e("TAG", "bind: "+statesList[i].name)
                        Log.e("TAG", "bind: "+cbRegion.text)
                        if (statesList[i].name.equals(cbRegion.text.toString())) {
                            getMyCity(data.name, data.id, 0, statesList[i].id)
                        }
                    }

//                    if (statesList[position].name.equals(cbRegion.text.toString())){
//
//                        getMyCity(data.name,data.id,0,statesList[position].id)
//                    }
                } else {
                   val cityList=ArrayList<CityDataValue>()
                    clickEventCallBack.onSelectRegion(data.name, data.id, 1,cityList)
                }
            }
        }

    }


    private fun getMyCity(code: String, id: Int, i1: Int, id1: Int) {
        Log.e("TAG", "getMyCity: "+code)
        Log.e("TAG", "getMyCity: "+id)
        Log.e("TAG", "getMyCity: "+i1)
        Log.e("TAG", "getMyCity: "+id1)
        VerkoopApplication.instance.loader.show(context as Activity)
        ServiceHelper().myCityList(id1, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                VerkoopApplication.instance.loader.hide(context as Activity)
                val responseCity = response.body() as CityResponse
                Log.e("TAG", "onSuccess: "+responseCity.data)
                if (responseCity.data != null) {

                    citylist = responseCity.data.city
                    if (citylist.size==0){

                        Utils.showSimpleMessage(context, responseCity.message).show()
                    }
                    else{

                        clickEventCallBack.onSelectRegion(code, id, 0,citylist)
                    }


                } else {
                    VerkoopApplication.instance.loader.hide(context as Activity)
                    Utils.showSimpleMessage(context, "No data found.").show()
                }

            }

            override fun onFailure(msg: String?) {
                VerkoopApplication.instance.loader.hide(context as Activity)
                Utils.showSimpleMessage(context, msg!!).show()
            }
        })

    }

    private fun refreshList(adapterPosition: Int) {

//        for (i in mFilteredList.indices) {
//            mFilteredList[i].isSelected = false
//        }
//        for (j in statesList.indices) {
//            statesList[j].isSelected = false
//        }
//        mFilteredList[adapterPosition].isSelected = true
        notifyDataSetChanged()
    }

    fun setData(data: ArrayList<StateDataValue>) {
        this.statesList = data
        this.mFilteredList = data
    }

    interface ClickEventCallBack {
        fun onSelectRegion(regionName: String, regionId: Int, coming: Int, cityList: ArrayList<CityDataValue>)
    }
}