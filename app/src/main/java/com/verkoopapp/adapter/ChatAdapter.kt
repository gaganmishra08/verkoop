package com.verkoopapp.adapter



import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso

import com.verkoopapp.R
import com.verkoopapp.models.ChatData
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.chat_image_left_row.tvPhotoLeft
import kotlinx.android.synthetic.main.chat_image_left_row.tvImageDataLeft
import kotlinx.android.synthetic.main.chat_image_right_row.tvImageRight
import kotlinx.android.synthetic.main.chat_image_right_row.tvDateImageRight
import kotlinx.android.synthetic.main.chat_offer_left_row.tvLeftOfferMssg
import kotlinx.android.synthetic.main.chat_offer_left_row.tvLeftOfferPrice
import kotlinx.android.synthetic.main.chat_offer_left_row.tvLeftOfferTime
import kotlinx.android.synthetic.main.chat_offer_right_row.tvRightOfferMssg
import kotlinx.android.synthetic.main.chat_offer_right_row.tvRightOfferTime
import kotlinx.android.synthetic.main.chat_offer_right_row.tvRightPrice
import kotlinx.android.synthetic.main.chat_text_left_row.tvLeftMssg
import kotlinx.android.synthetic.main.chat_text_left_row.tvLeftTime
import kotlinx.android.synthetic.main.chat_text_right_row.tvRightMssg
import kotlinx.android.synthetic.main.chat_text_right_row.tvRightTime
import kotlinx.android.synthetic.main.rate_user_left_row.tvLeftRateMssg
import kotlinx.android.synthetic.main.rate_user_left_row.tvLeftRateTime
import kotlinx.android.synthetic.main.rate_user_left_row.rbRatingLeft
import kotlinx.android.synthetic.main.rate_user_right_row.tvRightRateTime
import kotlinx.android.synthetic.main.rate_user_right_row.tvRightRaterMssg
import kotlinx.android.synthetic.main.rate_user_right_row.rbRatingRight
import org.apache.commons.lang3.StringEscapeUtils


class ChatAdapter(private val context:Context ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var chatHistoryList = ArrayList<ChatData>()
    val ROW_TEXT_RIGHT = 0
    val ROW_TEXT_LEFT = 1
    val ROW_IMAGE_RIGHT = 2
    val ROW_IMAGE_LEFT = 3
    val ROW_OFFER_RIGHT = 4
    val ROW_OFFER_LERFT = 5
    val ROW_RATE_RIGHT = 6
    val ROW_RATE_LEFT = 7


    override fun getItemViewType(position: Int): Int {
        return if (chatHistoryList[position].type == 0 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_TEXT_RIGHT
        } else if (chatHistoryList[position].type == 0 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_TEXT_LEFT
        }else if (chatHistoryList[position].type == 1 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_IMAGE_RIGHT
        }else if (chatHistoryList[position].type == 1 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_IMAGE_LEFT
        } else if (chatHistoryList[position].type == 2 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_RIGHT
        } else if (chatHistoryList[position].type == 2 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_LERFT
        } else if (chatHistoryList[position].type == 3 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_RIGHT
        } else if (chatHistoryList[position].type == 3 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_LERFT
        } else if (chatHistoryList[position].type == 4 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_RIGHT
        } else if (chatHistoryList[position].type == 4 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_LERFT
        }else if (chatHistoryList[position].type == 5 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_RIGHT
        } else if (chatHistoryList[position].type == 5 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_LERFT
        }else if (chatHistoryList[position].type == 6 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_RATE_RIGHT
        } else if (chatHistoryList[position].type == 6 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_RATE_LEFT
        } else {
            ROW_TEXT_LEFT
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            ROW_TEXT_RIGHT -> {
                view = layoutInflater.inflate(R.layout.chat_text_right_row, parent, false)
                return RightTextHolder(view)
            }
            ROW_TEXT_LEFT -> {
                view = layoutInflater.inflate(R.layout.chat_text_left_row, parent, false)
                return LeftTextHolder(view)
            }
            ROW_IMAGE_RIGHT -> {
                view = layoutInflater.inflate(R.layout.chat_image_right_row, parent, false)
                return RightImageHolder(view)
            }
            ROW_IMAGE_LEFT -> {
                view = layoutInflater.inflate(R.layout.chat_image_left_row, parent, false)
                return LeftImageHolder(view)
            }
            ROW_OFFER_RIGHT -> {
                view = layoutInflater.inflate(R.layout.chat_offer_right_row, parent, false)
                return RightOfferHolder(view)
            }
            ROW_OFFER_LERFT -> {
                view = layoutInflater.inflate(R.layout.chat_offer_left_row, parent, false)
                return LeftOfferHolder(view)
            }
            ROW_RATE_RIGHT -> {
                view = layoutInflater.inflate(R.layout.rate_user_right_row, parent, false)
                return RightRateHolder(view)
            }
            ROW_RATE_LEFT -> {
                view = layoutInflater.inflate(R.layout.rate_user_left_row, parent, false)
                return LeftRateHolder(view)
            }
            else -> {
                view = layoutInflater.inflate(R.layout.chat_text_left_row, parent, false)
                return LeftTextHolder(view)
            }
        }
    }


    override fun getItemCount(): Int {
        return chatHistoryList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (chatHistoryList[position].type == 0 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as RightTextHolder).bind(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 0 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as LeftTextHolder).bindLeft(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 1 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as RightImageHolder).bindImageRight(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 1 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as LeftImageHolder).bindImageLeft(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 2 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as RightOfferHolder).bind(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 2 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as LeftOfferHolder).bindLeft(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 3 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as RightOfferHolder).bind(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 3 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as LeftOfferHolder).bindLeft(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 4 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as RightOfferHolder).bind(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 4 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as LeftOfferHolder).bindLeft(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 5 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as RightOfferHolder).bind(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 5 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as LeftOfferHolder).bindLeft(chatHistoryList[position])
        }else if (chatHistoryList[position].type == 6 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as RightRateHolder).bind(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 6 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as LeftRateHolder).bindLeft(chatHistoryList[position])
        } else {
            (holder as LeftTextHolder).bindLeft(chatHistoryList[position])
        }
    }

    inner class LeftTextHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bindLeft(chatData: ChatData) {
            tvLeftMssg.text = StringEscapeUtils.unescapeJava(chatData.message)
            tvLeftTime.text = Utils.setDate(chatData.timeStamp)
        }

    }

  inner  class RightTextHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(chatData: ChatData) {
            tvRightMssg.text = StringEscapeUtils.unescapeJava(chatData.message)
            tvRightTime.text = Utils.setDate(chatData.timeStamp)
        }

    }

    inner class LeftRateHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bindLeft(chatData: ChatData) {
            tvLeftRateMssg.text ="RATED YOU"
            rbRatingLeft.rating=chatData.message.toFloat()
            tvLeftRateTime.text = Utils.setDate(chatData.timeStamp)
        }
    }

    inner  class RightRateHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(chatData: ChatData) {
            tvRightRaterMssg.text = "YOU RATED"
            rbRatingRight.rating = chatData.message.toFloat()
            tvRightRateTime.text = Utils.setDate(chatData.timeStamp)
        }
    }

   inner class RightImageHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bindImageRight(chatData: ChatData) {
            if (!TextUtils.isEmpty(chatData.message)) {
                Picasso.with(context).load(AppConstants.IMAGE_URL +chatData.message)
                        .resize(250, 250)
                        .centerCrop()
                        .placeholder(R.mipmap.gallery_place)
                        .error(R.mipmap.gallery_place)
                        .into( tvImageRight)
            }
            tvDateImageRight.text = Utils.setDate(chatData.timeStamp)
        }
    }

    inner class LeftImageHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bindImageLeft(chatData: ChatData) {
            if (!TextUtils.isEmpty(chatData.message)) {
                Picasso.with(context).load(AppConstants.IMAGE_URL +chatData.message)
                        .resize(250, 250)
                        .centerCrop()
                        .placeholder(R.mipmap.gallery_place)
                        .error(R.mipmap.gallery_place)
                        .into( tvPhotoLeft)
            }
            tvImageDataLeft.text = Utils.setDate(chatData.timeStamp)
        }
    }
    inner class RightOfferHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(chatData: ChatData) {
            if (chatData.type == 2) {
                tvRightOfferMssg.text = "MADE AN OFFER"
            } else if (chatData.type == 3) {
                tvRightOfferMssg.text = "ACCEPTED OFFER"
            } else if (chatData.type == 4) {
                tvRightOfferMssg.text = "DECLINED OFFER"
            }else if (chatData.type == 5) {
                tvRightOfferMssg.text = "CANCELLED OFFER"
            }
            tvRightPrice.text = StringBuilder().append(Utils.getPreferencesString(context,AppConstants.CURRENCY_SYMBOL)).append(chatData.message)
            tvRightOfferTime.text = Utils.setDate(chatData.timeStamp)
        }
    }

    inner class LeftOfferHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bindLeft(chatData: ChatData) {
            if (chatData.type == 2) {
                tvLeftOfferMssg.text = "MADE AN OFFER"
            } else if (chatData.type == 3) {
                tvLeftOfferMssg.text = "ACCEPTED OFFER"
            } else if (chatData.type == 4) {
                tvLeftOfferMssg.text = "DECLINED OFFER"
            } else if (chatData.type == 5) {
                tvLeftOfferMssg.text = "CANCELLED OFFER"
            }
            tvLeftOfferPrice.text = StringBuilder().append(Utils.getPreferencesString(context,AppConstants.CURRENCY_SYMBOL)).append(chatData.message)
            tvLeftOfferTime.text = Utils.setDate(chatData.timeStamp)
        }

    }

    fun setData(chatList: ArrayList<ChatData>) {
        chatHistoryList = chatList
    }

}