package com.verkoopapp.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.verkoopapp.R
import kotlinx.android.synthetic.main.notifizcatin_fragment.*
import kotlinx.android.synthetic.main.toolbar_location.*

class NotificationActivity: AppCompatActivity(){
    lateinit var tvHeaderLoc:TextView
    lateinit var ivLeftLocation:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifizcatin_fragment)
        tvHeaderLoc=findViewById(R.id.tvHeaderLoc)
        ivLeftLocation=findViewById(R.id.ivLeftLocation)
        setData()
    }

    private fun setData() {
        tvHeaderLoc.text = getString(R.string.notifications)
        ivLeftLocation.setOnClickListener {
            onBackPressed()
        }
        /*  if(AppPreferences.is_Push==1){
            switchNotification.setChecked(true,false )
        }else{
            switchNotification.setChecked(false,false )
        }
*/
        switchNotification.setOnStateChangeListener { process, state, jtb ->
            if (state == com.nightonke.jellytogglebutton.State.LEFT) {
                //allCountRequest(0)
            }
            if (state == com.nightonke.jellytogglebutton.State.RIGHT) {
                // allCountRequest(1)
            }
        }
    }

}