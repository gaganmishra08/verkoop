package com.verkoopapp.adapter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.verkoopapp.R
import com.verkoopapp.activity.ReportUserActivity
import com.verkoopapp.models.ReportResponse
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.reason_list.*


class ReportListAdapter(private val context: Context, private  val reportList: ArrayList<ReportResponse>):RecyclerView.Adapter<ReportListAdapter.ViewHolder>(){
    private  var mInflater:LayoutInflater= LayoutInflater.from(context)
     private lateinit var  onselectCallBack:OnSelectedCallBack
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
      val view=mInflater.inflate(R.layout.reason_list,parent,false)
        onselectCallBack=context as ReportUserActivity
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return reportList.size
}

    override fun onBindViewHolder(holder:ViewHolder, position: Int)  {
        val modal =reportList[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView!!),LayoutContainer{
        fun bind(modal: ReportResponse) {
            if(modal.isSelected){
                tvReportName.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
            }else{
                tvReportName.setTextColor(ContextCompat.getColor(context,R.color.brown))
            }
            tvReportName.text=modal.name
            tvReportName.setOnClickListener {
                onselectCallBack.onSelection(modal.id,modal.description,modal.type)
                offSelection(adapterPosition)
            }
        }
    }

    private fun offSelection(adapterPosition: Int) {
        for (i in reportList.indices){
            reportList[i].isSelected = adapterPosition == i
        }
        notifyDataSetChanged()
    }

    interface OnSelectedCallBack{
        fun onSelection(reportId:Int,description:String,type:Int)
    }

}