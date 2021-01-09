package com.verkoopapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.verkoopapp.R
import com.verkoopapp.activity.CarBrandActivity
import com.verkoopapp.activity.CarModalActivity
import com.verkoopapp.models.City
import com.verkoopapp.models.DataCarBrand
import com.verkoopapp.utils.AppConstants
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.region_row.*

class CarBrandAdapter(private var context: Context, private val coming: Int,private val carBrand:String,private val carBrandId:Int,private  val carTypeId:Int) : RecyclerView.Adapter<CarBrandAdapter.ViewHolder>(), Filterable {
    private var carBrandList = ArrayList<DataCarBrand>()
    private var mFilteredList = ArrayList<DataCarBrand>()
    private lateinit var selectBrandCallBack: SelectBrandCallBack
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    val font = Typeface.createFromAsset(context.assets, "fonts/gothic.ttf")

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                val charString = charSequence.toString()

                if (charString.isEmpty()) {
                    mFilteredList = carBrandList
                } else {

                    val filteredList = ArrayList<DataCarBrand>()

                    for (androidVersion in carBrandList) {

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
                mFilteredList = filterResults.values as ArrayList<DataCarBrand>
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.region_row, parent, false)
        //  clickEventCallBack = context as RegionActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = mFilteredList[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: DataCarBrand) {
            cbRegion.typeface = font
            cbRegion.isChecked = data.isSelected
            cbRegion.text = data.name
            cbRegion.setOnClickListener {
                refreshList(adapterPosition)
                when (coming) {
                    3 -> {
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.ZONE,data.name)
                        returnIntent.putExtra(AppConstants.ZONE_ID,data.id)
                        (context as CarBrandActivity).setResult(Activity.RESULT_OK, returnIntent)
                        (context as CarBrandActivity).finish()
                        (context as CarBrandActivity).overridePendingTransition(0, 0)
                    }
                    0 -> {
                        if(data.car_models!!.size>0) {
                            val intent = Intent(context, CarModalActivity::class.java)
                            intent.putExtra(AppConstants.CAR_BRAND_NAME, data.name)
                            intent.putExtra(AppConstants.CAR_BRAND_ID, data.id)
                            intent.putParcelableArrayListExtra(AppConstants.CAR_MODEL_LIST, data.car_models)
                            (context as CarBrandActivity).startActivityForResult(intent, 3)
                        }else{
                            val returnIntent = Intent()
                            returnIntent.putExtra(AppConstants.CAR_MODEL,"")
                            returnIntent.putExtra(AppConstants.CAR_MODEL_ID,0)
                            returnIntent.putExtra(AppConstants.CAR_BRAND_NAME,data.name)
                            returnIntent.putExtra(AppConstants.CAR_BRAND_ID,data.id)
                            (context as CarBrandActivity).setResult(Activity.RESULT_OK, returnIntent)
                            (context as CarBrandActivity).finish()
                            (context as CarBrandActivity).overridePendingTransition(0, 0)
                        }
                    }
                    else -> {
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.CAR_TYPE,data.name)
                        returnIntent.putExtra(AppConstants.CAR_TYPE_ID,data.id)
                        returnIntent.putExtra(AppConstants.CAR_BRAND_NAME,carBrand)
                        returnIntent.putExtra(AppConstants.CAR_BRAND_ID,carBrandId)
                        (context as CarBrandActivity).setResult(Activity.RESULT_OK, returnIntent)
                        (context as CarBrandActivity).finish()
                        (context as CarBrandActivity).overridePendingTransition(0, 0)
                    }
                }
            }
        }


        private fun refreshList(adapterPosition: Int) {
            for (i in mFilteredList.indices) {
                mFilteredList[i].isSelected = false
            }
            for (j in carBrandList.indices) {
                carBrandList[j].isSelected = false
            }
            mFilteredList[adapterPosition].isSelected = true
            notifyDataSetChanged()
        }

    }

    fun setData(carBrandData: ArrayList<DataCarBrand>) {
        mFilteredList = carBrandData
        carBrandList = carBrandData
    }

    interface SelectBrandCallBack {
        fun onSelectRegion(regionName: String, regionId: Int, coming: Int, cityList: ArrayList<City>)
    }
}