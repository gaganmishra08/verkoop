package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.verkoopapp.R
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.pick_option_ctivity.*


class PickOptionActivity : AppCompatActivity() {
    private var id = 0
    private var type = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pick_option_ctivity)
        type = intent.getIntExtra(AppConstants.TYPE, 0)
        id = intent.getIntExtra(AppConstants.ID, 0)
        setData()
    }

    private fun setData() {
        tvSellNow.setOnClickListener {
            Utils.savePreferencesString(this,AppConstants.COMING_FROM,"PickOptionActivity")
            val intent = Intent(this@PickOptionActivity, HomeActivity::class.java)
            intent.putExtra(AppConstants.PICK_OPTION,1)
            startActivity(intent)
            finish()
        }
        tvShopNow.setOnClickListener {
            Utils.savePreferencesString(this,AppConstants.COMING_FROM,"PickOptionActivity")
            val intent = Intent(this@PickOptionActivity, HomeActivity::class.java)
            intent.putExtra(AppConstants.ID, id)
            intent.putExtra(AppConstants.TYPE, type)
            startActivity(intent)
            finish()
        }
        tvMayBeLater.setOnClickListener {
            Utils.savePreferencesString(this, AppConstants.COMING_FROM,"PickOptionActivity")
            val intent = Intent(this@PickOptionActivity, HomeActivity::class.java)
            intent.putExtra(AppConstants.ID, id)
            intent.putExtra(AppConstants.TYPE, type)
            startActivity(intent)
            finish()
        }

    }
}