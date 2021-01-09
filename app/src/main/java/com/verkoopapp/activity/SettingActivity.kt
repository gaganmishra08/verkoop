package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.socket.client.Ack
import io.socket.client.Socket
import com.google.gson.Gson
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.models.AddItemResponse
import com.verkoopapp.models.DeactivateAccountRequest
import com.verkoopapp.models.LogOutRequest
import com.verkoopapp.models.WalletHistoryResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.DeleteCommentDialog
import com.verkoopapp.utils.SelectionListener
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.my_profile_details_row.*
import kotlinx.android.synthetic.main.my_wallet_activity.*
import kotlinx.android.synthetic.main.setting_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class SettingActivity : AppCompatActivity() {
    lateinit var tvHeaderLoc: TextView
    lateinit var ivLeftLocation: ImageView
    private var socket: Socket? = VerkoopApplication.getAppSocket()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_activity)
        tvHeaderLoc=findViewById(R.id.tvHeaderLoc)
        ivLeftLocation=findViewById(R.id.ivLeftLocation)
        setData()
    }

    private fun setData() {
        tvHeaderLoc.text = getString(R.string.settings)
        ivLeftLocation.setOnClickListener {
            onBackPressed()
        }
        rlLogout.setOnClickListener {
            tvLogout.isEnabled = false
            logOutDialogBox()
            Handler().postDelayed(Runnable {
                tvLogout.isEnabled = true
            }, 1500)
        }
        rlEditProfile.setOnClickListener {
            tvEditProfile.isEnabled = false
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvEditProfile.isEnabled = true
            }, 1000)
        }
        rlChangePassword.setOnClickListener {
            tvChangePassword.isEnabled = false
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            Handler().postDelayed(Runnable {
                tvChangePassword.isEnabled = true
            }, 1000)
        }
        rlNotification.setOnClickListener {
            tvNotification.isEnabled = false
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvNotification.isEnabled = true
            }, 1000)
        }
        rlBanner.setOnClickListener {
            tvBanner.isEnabled = false
            val intent = Intent(this, ViewAllBannerActivity::class.java)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvBanner.isEnabled = true
            }, 1000)
        }
        rlAboutVerkoop.setOnClickListener {
            tvAboutVerkoop.isEnabled = false
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 1)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvAboutVerkoop.isEnabled = true
            }, 1000)
        }
        rlTermsService.setOnClickListener {
            tvTermsService.isEnabled = false
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 2)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvTermsService.isEnabled = true
            }, 1000)
        }
        rlPrivacyPolicy.setOnClickListener {
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 3)
            startActivity(intent)
        }
        rlContactUs.setOnClickListener {
            tvContactUs.isEnabled = false
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 4)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvContactUs.isEnabled = true
            }, 1000)
        }
        rlHelpCenter.setOnClickListener {
            tvHelpCenter.isEnabled = false
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 5)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvHelpCenter.isEnabled = true
            }, 1000)
        }
        rlDataPrivacy.setOnClickListener {
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 6)
            startActivity(intent)
        }
        rlDeActivateAcct.setOnClickListener {
            tvLogout.isEnabled = false
            deactivateDialogBox()
            Handler().postDelayed(Runnable {
                tvLogout.isEnabled = true
            }, 1500)
        }

    }

    private fun logOutDialogBox() {
        val shareDialog = DeleteCommentDialog(this, getString(R.string.logout_heading), getString(R.string.logout_des), object : SelectionListener {
            override fun leaveClick() {
                if (Utils.isOnline(this@SettingActivity)) {
                    logout()
                } else {
                    Utils.showSimpleMessage(this@SettingActivity, getString(R.string.check_internet)).show()
                }

            }
        })
        shareDialog.show()
    }

    private fun deactivateDialogBox() {
        val shareDialog = DeleteCommentDialog(this, getString(R.string.deactivate_heading), getString(R.string.deactivate_des), object : SelectionListener {
            override fun leaveClick() {
                if (Utils.isOnline(this@SettingActivity)) {
                    deactivateAccountApi()
                } else {
                    Utils.showSimpleMessage(this@SettingActivity, getString(R.string.check_internet)).show()
                }

            }
        })
        shareDialog.show()
    }

    private fun deactivateAccountApi() {
        ServiceHelper().getDeactivateAccountService(DeactivateAccountRequest(VerkoopApplication.getToken()), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                val responseWallet = response.body() as AddItemResponse
                if (responseWallet.message.equals("Account deactivated")) {
                    Utils.showSimpleMessage(this@SettingActivity, "Account deactivated successfully")
                    logout()
                } else {
                    Utils.showSimpleMessage(this@SettingActivity, responseWallet.message!!).show()
                }
            }

            override fun onFailure(msg: String?) {
                pbProgressWallet.visibility = View.GONE
                Utils.showSimpleMessage(this@SettingActivity, msg!!).show()
            }
        })
    }

    private fun logout() {
        if (Utils.isOnline(this)) {
            callInit()
            callLogOutApi()
            com.verkoopapp.utils.Utils.clearPreferences(this@SettingActivity)
//            val intent = Intent(this@SettingActivity, WalkThroughActivity::class.java)
//            startActivity(intent)
//            finishAffinity()
        } else {
            Utils.showSimpleMessage(this, "Please check your internet connection")
        }
    }

    private fun callLogOutApi() {
        ServiceHelper().getLogOutService(LogOutRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                val responseWallet = response.body() as AddItemResponse
                if (responseWallet.message.equals("Logged out successfully")) {
                    val intent = Intent(this@SettingActivity, WalkThroughActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    Utils.showSimpleMessage(this@SettingActivity, responseWallet.message!!).show()
                }
            }

            override fun onFailure(msg: String?) {
                pbProgressWallet.visibility = View.GONE
                Utils.showSimpleMessage(this@SettingActivity, msg!!).show()
            }
        })
    }

    private fun callInit() {
        socket?.emit(AppConstants.SOCKET_DISCONNECT, getObj(), Ack {
            Log.e("<<<DisConnectLogOut>>>", Gson().toJson(it[0]))
        })

    }

    private fun getObj(): Any {
        val jsonObject: JSONObject?
        jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", Utils.getPreferencesString(applicationContext, AppConstants.USER_ID).toInt())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }
}