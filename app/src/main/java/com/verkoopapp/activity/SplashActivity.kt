package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clickzin.tracking.ClickzinTracker
import com.clickzin.tracking.utils.ClickzinUtils
import com.google.gson.Gson
import com.verkoopapp.R
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import io.branch.referral.Branch
import org.json.JSONException


class SplashActivity : AppCompatActivity() {
    private var id = 0
    private var type = 0
    private var typeNoti = 0
    private var idNoti = 0
    //private lateinit var bundle: Bundle

    private fun setAppIntent() {
        val bundle = intent.extras
        Handler().postDelayed({
            if (bundle != null) {
                if (bundle.get("type") != null) {
                    val i = bundle.get("type").toString()
                    typeNoti=i.toInt()
                }
                if (bundle.get("user_id") != null) {
                    val i = bundle.get("user_id").toString()
                    idNoti=i.toInt()
                }
                if (bundle.get("item_id") != null) {
                    val i = bundle.get("item_id").toString()
                    idNoti=i.toInt()
                }
                if (typeNoti != null) {
                    if (typeNoti == 1 || typeNoti == 3 || typeNoti == 6) {
                        type=1
                        id=idNoti
                    } else if (typeNoti == 2 || typeNoti == 4){
                        type=2
                        id=idNoti
                    } else if( typeNoti == 5){
                        type=3
                    } else if(typeNoti == 7){
                        type =4
                    } else if(typeNoti == 8){
                        type = 5
                    }
                }
            }
            if (!TextUtils.isEmpty(Utils.getPreferencesString(this, AppConstants.USER_ID))) {
                if (!TextUtils.isEmpty(Utils.getPreferencesString(this, AppConstants.COMING_FROM)) && Utils.getPreferencesString(this, AppConstants.COMING_FROM).equals("category_screen", ignoreCase = true)) {
                    val intent = Intent(this, PickOptionActivity::class.java)
                    intent.putExtra(AppConstants.ID, id)
                    intent.putExtra(AppConstants.TYPE, type)
                    startActivity(intent)
                    finish()
                } else if (!TextUtils.isEmpty(Utils.getPreferencesString(this, AppConstants.COMING_FROM)) && Utils.getPreferencesString(this, AppConstants.COMING_FROM).equals("PickOptionActivity", ignoreCase = true)) {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra(AppConstants.ID, id)
                    intent.putExtra(AppConstants.TYPE, type)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, CategoriesActivity::class.java)
                    intent.putExtra(AppConstants.ID, id)
                    intent.putExtra(AppConstants.TYPE, type)
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(this, WalkThroughActivity::class.java)
                intent.putExtra(AppConstants.ID, id)
                intent.putExtra(AppConstants.TYPE, type)
                startActivity(intent)
                finish()
            }

        }, 2000)
    }

    override fun onStart() {
        super.onStart()
        branchInit()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        Log.d("testing", "onCreate@: ")
        Log.i("testing", "onCreate@: ")

//        Please explain the scenario to our manager and get the exact event name  with exactt spelling.
//        ClickzinTracker.getInstance().trackEvents(applicationContext, "Signup",null);
//
//        ClickzinTracker.getInstance().sourceOfInstall
//        ClickzinTracker.getInstance().referrer


    }

    private fun branchInit() {

//      clickzintracker
        ClickzinTracker.getInstance().init(applicationContext,resources.getString(R.string.app_name), null);
        ClickzinTracker.getInstance().setCustomerId(ClickzinUtils.getUniquePsuedoID());
        ClickzinTracker.getInstance().startTracking();
        // Branch init
        Branch.getInstance().initSession({ referringParams, error ->
            if (error == null) {
                Log.e("BRANCH SDK", referringParams.toString())
                Log.e("<<<LinkRequest>>>", Gson().toJson(referringParams))
                Log.e("<<<LinkRequestString>>>", referringParams.toString())
                Log.i("BRANCH SDK", referringParams.toString())
                try {
                    type = referringParams.getString("type").toInt()
                    if (type == 1) {
                        id = referringParams.getString("product_id").toInt()
                    } else if (type == 2) {
                        id = referringParams.getString("user_id").toInt()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else {
                Log.e("BRANCH SDK", error.message)
            }
//            bundle=intent.extras

            setAppIntent()
        }, this.intent.data, this)
    }

    public override fun onNewIntent(intent: Intent) {
        this.intent = intent
    }


}