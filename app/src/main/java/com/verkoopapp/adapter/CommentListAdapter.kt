package com.verkoopapp.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.models.CommentModal
import com.verkoopapp.models.DisLikeResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.comment_row.ivOption
import kotlinx.android.synthetic.main.comment_row.ivProfilePic
import kotlinx.android.synthetic.main.comment_row.tvComment
import kotlinx.android.synthetic.main.comment_row.tvNameCom
import kotlinx.android.synthetic.main.comment_row.tvTimeCom
import retrofit2.Response


class CommentListAdapter(private val context: Context,private val progressBar: ProgressBar,private  val comingType:Int):RecyclerView.Adapter<CommentListAdapter.ViewHolder>(){
    private var mInflater:LayoutInflater= LayoutInflater.from(context)
    private var commentsList= ArrayList<CommentModal>()
    private var isClickedDialog:Boolean=false
    private var selectedId: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view=mInflater.inflate(R.layout.comment_row,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
            val modal=commentsList[position]
            holder.bind(modal)
    }
 inner  class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView!!),LayoutContainer{
     fun bind(modal: CommentModal) {
         if(comingType!=0||modal.user_id==Utils.getPreferencesString(context,AppConstants.USER_ID).toInt()){
             ivOption.visibility=View.VISIBLE
         }else{
             ivOption.visibility=View.INVISIBLE
         }
         if(!TextUtils.isEmpty(modal.profile_pic)){
             Picasso.with(context).load(AppConstants.IMAGE_URL+modal.profile_pic)
                     .resize(720, 720)
                     .centerInside()
                     .error(R.mipmap.pic_placeholder)
                     .placeholder(R.mipmap.pic_placeholder)
                     .into(ivProfilePic)
         }else{
             Picasso.with(context).load(R.mipmap.pic_placeholder)
                     .resize(720, 720)
                     .centerInside()
                     .error(R.mipmap.gallery_place)
                     .placeholder(R.mipmap.gallery_place)
                     .into(ivProfilePic)
         }
         tvNameCom.text=modal.username
         tvComment.text=modal.comment
         tvTimeCom.text = StringBuilder().append(Utils.getDateDifferenceDiff(modal.created_at)).append(" ").append("ago")
         ivOption.setOnClickListener {
             if(!isClickedDialog) {
                 resumeActivityDialog(adapterPosition, modal.id)
             }
         }
     }

 }
    private fun resumeActivityDialog(adapterPosition: Int, id: Int) {
        val shareDialog = DeleteCommentDialog(context,"Delete Comment","Are you sure you want to delete this comment?",object : SelectionListener {
            override fun leaveClick() {
                deleteCommentApi(adapterPosition,id)
            }
        })
        shareDialog.show()
    }
    private fun deleteCommentApi(adapterPosition: Int, commentId: Int) {
        isClickedDialog=true
        progressBar.visibility=View.VISIBLE
        ServiceHelper().deleteCommentService(commentId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isClickedDialog=false
                        progressBar.visibility=View.GONE
                        val likeResponse = response.body() as DisLikeResponse
                        commentsList.removeAt(adapterPosition)
                       notifyDataSetChanged()
                        Utils.showSimpleMessage(context, context.getString(R.string.deleted_successfully)).show()
                    }
                    override fun onFailure(msg: String?) {
                        isClickedDialog=false
                        progressBar.visibility=View.GONE
                        Utils.showSimpleMessage(context, msg!!).show()
                    }
                })
    }

    fun setData(comments: ArrayList<CommentModal>) {
        commentsList=comments
    }
}