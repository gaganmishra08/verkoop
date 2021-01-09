package com.verkoopapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.verkoopapp.R
import com.verkoopapp.activity.CarModalActivity
import com.verkoopapp.models.CarModelList
import com.verkoopapp.models.City
import com.verkoopapp.utils.AppConstants
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.region_row.*


class CarModalAdapter(private var context: Context, private val carBrand: String, private val carBrandId: Int) : RecyclerView.Adapter<CarModalAdapter.ViewHolder>(), Filterable {
    private var carBrandList = ArrayList<CarModelList>()
    private var mFilteredList = ArrayList<CarModelList>()
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

                    val filteredList = ArrayList<CarModelList>()

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
                mFilteredList = filterResults.values as ArrayList<CarModelList>
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
        fun bind(data: CarModelList) {
            cbRegion.typeface = font
            cbRegion.isChecked = data.isSelected
            cbRegion.text = data.name
            cbRegion.scaleX=1f
            cbRegion.scaleY=1f
            cbRegion.gravity = Gravity.LEFT or Gravity.CENTER
            cbRegion.setOnClickListener {
                refreshList(adapterPosition)
                val returnIntent = Intent()
                returnIntent.putExtra(AppConstants.CAR_MODEL, data.name)
                returnIntent.putExtra(AppConstants.CAR_MODEL_ID, data.id)
                returnIntent.putExtra(AppConstants.CAR_BRAND_NAME, carBrand)
                returnIntent.putExtra(AppConstants.CAR_BRAND_ID, data.brand_id)
                (context as CarModalActivity).setResult(Activity.RESULT_OK, returnIntent)
                (context as CarModalActivity).finish()
                (context as CarModalActivity).overridePendingTransition(0, 0)
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

    fun setData(carBrandData: ArrayList<CarModelList>) {
        mFilteredList = carBrandData
        carBrandList = carBrandData
    }

    interface SelectBrandCallBack {
        fun onSelectRegion(regionName: String, regionId: Int, coming: Int, cityList: ArrayList<City>)
    }
}