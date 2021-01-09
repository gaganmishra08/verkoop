package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.github.florent37.viewanimator.ViewAnimator
import com.verkoopapp.R
import com.verkoopapp.models.CommentModal
import com.verkoopapp.models.CommentResponse
import com.verkoopapp.models.PostCommentRequest
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.KeyboardUtil
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.comment_dialog_activity.*
import retrofit2.Response


class AddCommentActivity : AppCompatActivity() {
    var itemId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comment_dialog_activity)
        KeyboardUtil(this, llParentPost)
        itemId = intent.getIntExtra(AppConstants.ITEM_ID, 0)
        setData()
        setAnimation()
    }

    private fun setData() {
        pbProgressPost.indeterminateDrawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        ivFinishPost.setOnClickListener { onBackPressed() }
        tvPost.setOnClickListener {
            if (Utils.isOnline(this)) {
                if (!TextUtils.isEmpty(etComment.text.toString())) {
                    KeyboardUtil.hideKeyboard(this)
                    callPostCommentApi()
                } else {
                    Utils.showSimpleMessage(this, "Please enter Comment.").show()
                }
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
    }

    private fun setAnimation() {
        ViewAnimator
                .animate(flParentPost)
                .translationY(1000f, 0f)
                .duration(700)
                .start()
    }

    override fun onBackPressed() {
        ViewAnimator
                .animate(flParentPost)
                .translationY(0f, 2000f)
                .duration(700)
                .andAnimate(llParentPost)
                .alpha(1f, 0f)
                .duration(600)
                .onStop {
                    llParentPost.visibility = View.GONE
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_CANCELED, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                }
                .start()
    }

    private fun callPostCommentApi() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressPost.visibility = View.VISIBLE
        ServiceHelper().postCommentService(PostCommentRequest(Utils.getPreferencesString(this, AppConstants.USER_ID), itemId, etComment.text.toString()),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressPost.visibility = View.GONE
                        val commentResponse = response.body() as CommentResponse
                        if (commentResponse.data != null) {
                            setBackIntent(commentResponse.data)
                        } else {
                            Utils.showSimpleMessage(this@AddCommentActivity, commentResponse.message).show()
                        }

                    }

                    override fun onFailure(msg: String?) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressPost.visibility = View.GONE
                        Utils.showSimpleMessage(this@AddCommentActivity, msg!!).show()
                    }
                })

    }

    private fun setBackIntent(data: CommentModal?) {
        ViewAnimator
                .animate(flParentPost)
                .translationY(0f, 2000f)
                .duration(700)
                .andAnimate(llParentPost)
                .alpha(1f, 0f)
                .duration(600)
                .onStop {
                    llParentPost.visibility = View.GONE
                    val returnIntent = Intent()
                    returnIntent.putExtra(AppConstants.COMMENT_RESULR, data)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                }
                .start()
    }

}