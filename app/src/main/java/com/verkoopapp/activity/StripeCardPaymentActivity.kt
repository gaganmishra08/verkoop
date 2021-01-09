package com.verkoopapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.manojbhadane.PaymentCardView
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Token
import com.stripe.android.view.CardMultilineWidget
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.models.AddMoneyRequest
import com.verkoopapp.models.MakePaymentRequest
import com.verkoopapp.models.PaymentResponse
import com.verkoopapp.models.UpdateWalletResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.activity_stripe_card_payment.*
import retrofit2.Response


class StripeCardPaymentActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private lateinit var cardInputWidget: CardMultilineWidget
    lateinit var tvHeaderLoc:TextView
    lateinit var tv_continue:TextView
    lateinit var ivLeftLocation:ImageView
    var token: String? = null
    var amountMoney: Long? = null
    lateinit var amount:String
    lateinit var amount_usd:String
    lateinit var paymentCard : PaymentCardView
    var previousLength = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_card_payment)
        paymentCard=findViewById(R.id.creditCard)
        cardInputWidget = findViewById(R.id.card_input_widget)
        tvHeaderLoc = findViewById(R.id.tvHeaderLoc)
        tv_continue = findViewById(R.id.tv_continue)
        ivLeftLocation = findViewById(R.id.ivLeftLocation)
        tvHeaderLoc.setText("Payment")
        cardInputWidget.visibility==View.GONE
        amountMoney = intent.getLongExtra(AppConstants.AMOUNT, 0L)
        amount= amountMoney.toString()
        tv_continue.setOnClickListener(this)
        ivLeftLocation.setOnClickListener(this)
        edt_expiry.addTextChangedListener(this)
        Log.e("TAG", "onCreate: "+Utils.getPreferencesString(this, AppConstants.CURRENCY))
        amount_usd= Utils.convertCurrencyToUsd(this@StripeCardPaymentActivity,Utils.getPreferencesString(this, AppConstants.CURRENCY),amount.toDouble()).toString()
        Log.e("TAG", "onCreate: "+amount_usd)
//        Log.e("TAG", "validations: "+Utils.convertCurrencyToUsd(this@StripeCardPaymentActivity,Utils.getPreferencesString(this, AppConstants.CURRENCY),amount.toDouble()))
        paymentCard.setOnPaymentCardEventListener(object : PaymentCardView.OnPaymentCardEventListener {
            override fun onCardDetailsSubmit(month: String, year: String, cardNumber: String, cvv: String) {

                Log.e("TAG", "onCardDetailsSubmit: "+month)
                Log.e("TAG", "onCardDetailsSubmit: "+year)
                Log.e("TAG", "onCardDetailsSubmit: "+cardNumber)
                Log.e("TAG", "onCardDetailsSubmit: "+cvv)

                if (month.toString().trim().isEmpty()){
                    Utils.showSimpleMessage(this@StripeCardPaymentActivity,"Please Enter Month").show()
                }else if (year.toString().trim().isEmpty()){
                    Utils.showSimpleMessage(this@StripeCardPaymentActivity,"Please Enter Year").show()
                }else if (cardNumber.toString().trim().isEmpty()){
                    Utils.showSimpleMessage(this@StripeCardPaymentActivity,"Please Enter Card Number").show()
                }else if (cvv.toString().trim().isEmpty()){
                    Utils.showSimpleMessage(this@StripeCardPaymentActivity,"Please Enter CVV").show()
                }else{

                }
            }
            override fun onError(error: String) {
                Utils.showSimpleMessage(this@StripeCardPaymentActivity,error)
            }

            override fun onCancelClick() {
                Utils.showSimpleMessage(this@StripeCardPaymentActivity,"Payment Cancelled")
            }
        })
        tvPurchase.visibility=View.GONE
        tvPurchase.setOnClickListener {
            tvPurchase.isEnabled = false
            setStripePayment()
        }
    }



    private fun setStripePayment() {
        if (cardInputWidget.card == null) {
            tvPurchase.isEnabled = true
            Toast.makeText(this, "Please fill the card details correctly", Toast.LENGTH_SHORT).show()
        }
        val card = cardInputWidget.card ?: return

//        val stripe = Stripe(applicationContext,
//                "pk_test_IkEuiX8PBSrxqDOnx7W79ubE006HXByoRc")

        val stripe = Stripe(applicationContext,
                "pk_live_0QE5t1AQdS0YOx0xAzfzd8Dq00AYl5mZ6X")

        stripe.createToken(card, object : ApiResultCallback<Token> {
            override fun onSuccess(result: Token) {
                Log.d("Stripe", result.toString())
                token = result.id
                callUpdateWalletApi(amountMoney.toString())
            }

            override fun onError(e: Exception) {
                Log.d("Stripe", e.printStackTrace().toString())
                if (e.message!!.contains("Please check your internet")) {
                    Utils.showSimpleMessage(this@StripeCardPaymentActivity, "Please check your internet connection")
                }
                tvPurchase.isEnabled = true
            }

        })
    }

    private fun callUpdateWalletApi(amount: String?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressStripeWallet.visibility = View.VISIBLE
        ServiceHelper().addMoneyService(AddMoneyRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), amount!!.toDouble(), token!!,  Utils.getPreferencesString(this@StripeCardPaymentActivity,AppConstants.CURRENCY)),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        tvPurchase.isEnabled = true
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressStripeWallet.visibility = View.GONE
                        val loginResponse = response.body() as UpdateWalletResponse
                        Utils.showToast(this@StripeCardPaymentActivity, loginResponse.message)
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.INTENT_RESULT, "success")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                        //  onBackPressed()
                    }

                    override fun onFailure(msg: String?) {
                        tvPurchase.isEnabled = true
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressStripeWallet.visibility = View.GONE
                        Utils.showSimpleMessage(this@StripeCardPaymentActivity, msg!!).show()
                    }
                })

    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.tv_continue -> validations()
                R.id.ivLeftLocation -> onBackPressed()
            }
        }
    }

    private fun validations() {
        if (edt_card.text.toString().length < 16) {
            Utils.showSimpleMessage(this@StripeCardPaymentActivity,"Please Enter Valid Card Number").show()
        } else if (edt_card.text.toString().trim { it <= ' ' }.isEmpty()) {
            Utils.showSimpleMessage(this@StripeCardPaymentActivity,"Please Enter Card Number").show()

        } else if (edt_expiry.text.toString().trim { it <= ' ' }.isEmpty()) {
            Utils.showSimpleMessage(this@StripeCardPaymentActivity,"Please Enter Expiry Date").show()

        } else if (edt_expiry.text.toString().length < 5) {

            Utils.showSimpleMessage(this@StripeCardPaymentActivity,"Please Enter Valid Expiry Date").show()

        } else if (edt_cvv.text.toString().trim { it <= ' ' }.isEmpty()) {
            Utils.showSimpleMessage(this@StripeCardPaymentActivity,"Please Enter CVV").show()

        } else if (edt_expiry.text.toString().length < 3) {

            Utils.showSimpleMessage(this@StripeCardPaymentActivity,"Please Enter Valid CVV").show()

        } else if (edt_name.text.toString().length < 3) {

            Utils.showSimpleMessage(this@StripeCardPaymentActivity,"Please Enter Name").show()

        } else {
            apiPaymentAddedWallet()
        }
    }

    private fun apiPaymentAddedWallet() {
        VerkoopApplication.instance.loader.show(this)
        val strs =edt_expiry.text.toString().split("/").toTypedArray()
        ServiceHelper().getPaymentCreditCard(MakePaymentRequest(Utils.getPreferencesString(this@StripeCardPaymentActivity,AppConstants.USER_ID),
                "USD",amount_usd,strs[0],strs[1],edt_cvv.text.toString(),
        edt_card.text.toString(),edt_name.text.toString()),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@StripeCardPaymentActivity)
                        val paymetntResponse = response.body() as PaymentResponse
                        Utils.showSimpleMessageShort(this@StripeCardPaymentActivity, paymetntResponse.message).show()
                        edt_name.setText("")
                        edt_card.setText("")
                        edt_cvv.setText("")
                        edt_expiry.setText("")
                        Handler().postDelayed({
                            val intent = Intent(this@StripeCardPaymentActivity, MyWalletstripeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 2000)

                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@StripeCardPaymentActivity)
                        Utils.showSimpleMessageShort(this@StripeCardPaymentActivity, msg!!).show()
                    }
                })
    }

    //    private void savecardDetails() {
    //        commonMethods.showProgressDialog(PaymentCreditCard.this, customDialog);
    //        apiService.setpayment(item,edt_cvv.getText().toString(),edt_expiry.getText().toString(),edt_card.getText().toString(),token,user_id).enqueue(new RequestCallback(1, this));
    //    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        previousLength = edt_expiry.text.toString().length
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val length = edt_expiry.text.toString().trim { it <= ' ' }.length
        if (previousLength <= length && length < 3) {
            val month = edt_expiry.text.toString().toInt()
            if (length == 1 && month >= 2) {
                val autoFixStr = "0$month/"
                edt_expiry.setText(autoFixStr)
                edt_expiry.setSelection(3)
            } else if (length == 2 && month <= 12) {
                val autoFixStr = edt_expiry.text.toString() + "/"
                edt_expiry.setText(autoFixStr)
                edt_expiry.setSelection(3)
            } else if (length == 2 && month > 12) {
                edt_expiry.setText("1")
                edt_expiry.setSelection(1)
            }
        } else if (length == 5) {
            edt_expiry.requestFocus() // auto move to next edittext
        }
    }

    override fun afterTextChanged(editable: Editable?) {}

}
