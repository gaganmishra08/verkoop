package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.verkoopapp.R
import com.verkoopapp.adapter.PaymentHistoryAdapter
import kotlinx.android.synthetic.main.my_wallet_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import android.app.Activity
import android.content.Context
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Token
import com.stripe.android.view.CardMultilineWidget
import com.verkoopapp.models.AddMoneyRequest
import com.verkoopapp.models.UpdateWalletResponse
import com.verkoopapp.models.WalletHistoryResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.ResponseHelper
import com.verkoopapp.utils.Utils
import retrofit2.Response

import com.verkoopapp.utils.SignatureHelper
import com.wirecard.ecom.Client
import com.wirecard.ecom.model.out.PaymentResponse
import kotlinx.android.synthetic.main.dialog_payment.view.*
import java.lang.Exception
import java.math.BigDecimal

class MyWalletstripeActivity : AppCompatActivity() {
    private lateinit var paymentHistoryAdapter: PaymentHistoryAdapter
    val timestamp = SignatureHelper.generateTimestamp()!!
    private val currency = "USD"
    var amount: BigDecimal? = null
    var token: String? = null
     var amountMoney=0L
    private lateinit var cardMultilineWidget: CardMultilineWidget

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_wallet_activity)
        setAdapter()
        setData()
        if (Utils.isOnline(this)) {
            getWalletHistoryApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
//        cardMultilineWidget = findViewById(R.id.card_multiline_widget)
//        progressDialogController = ProgressDialogController(supportFragmentManager, resources)
//
//        errorDialogHandler = ErrorDialogHandler(this)

    }

    override fun onDestroy() {
//        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun saveCard() {
        val card = cardMultilineWidget.card ?: return

//        val stripe = Stripe(applicationContext,
//                "pk_test_IkEuiX8PBSrxqDOnx7W79ubE006HXByoRc")
        val stripe = Stripe(applicationContext,
                "pk_live_0QE5t1AQdS0YOx0xAzfzd8Dq00AYl5mZ6X")

        stripe.createToken(card, object : ApiResultCallback<Token> {
            override fun onSuccess(result: Token) {
                Log.d("Stripe", result.toString())
                token = result.id
                callUpdateWalletApi("20")
            }

            override fun onError(e: Exception) {
                Log.d("Stripe", e.printStackTrace().toString())
                if (e.message!!.contains("Please check your internet")) {
                    Utils.showSimpleMessage(this@MyWalletstripeActivity, "Please check your internet connection")
                }
            }

        })

//        val cardSourceParams = PaymentMethodCreateParams.create(card.toPaymentMethodParamsCard(), null)
//        // Note: using this style of Observable creation results in us having a method that
//        // will not be called until we subscribe to it.
//        val tokenObservable = Observable.fromCallable { stripe.createPaymentMethodSynchronous(cardSourceParams) }
//
//        compositeDisposable.add(tokenObservable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe { progressDialogController.show(R.string.progressMessage) }
//                .doOnComplete { progressDialogController.dismiss() }
//                .subscribe(
//                        { addToList(it) },
//                        { throwable -> errorDialogHandler.show(throwable.localizedMessage) }
//                )
//        )
    }

    private fun setStripePayment(amount: Int?) {
        val card = cardMultilineWidget.card ?: return

        val stripe = Stripe(applicationContext,
                "pk_test_IkEuiX8PBSrxqDOnx7W79ubE006HXByoRc")

        stripe.createToken(card, object : ApiResultCallback<Token> {
            override fun onSuccess(result: Token) {
                Log.d("Stripe", result.toString())
                token = result.id
                callUpdateWalletApi("20")
            }

            override fun onError(e: Exception) {
                Log.d("Stripe", e.printStackTrace().toString())
                if (e.message!!.contains("Please check your internet")) {
                    Utils.showSimpleMessage(this@MyWalletstripeActivity, "Please check your internet connection")
                }
            }

        })
    }

//    private fun addToList(paymentMethod: PaymentMethod?) {
//        if (paymentMethod?.card == null) {
//            return
//        }
//
//        val paymentMethodCard = paymentMethod.card
//        val endingIn = getString(R.string.endingIn)
//        val map = hashMapOf(
//                "last4" to endingIn + " " + paymentMethodCard!!.last4,
//                "tokenId" to paymentMethod.id!!
//        )
//        cardSources.add(map)
//        if (paymentMethod.id != null) {
//            token = paymentMethod.id
//            callUpdateWalletApi("20")
//        }
//
//
//        val stripe = Stripe(applicationContext,
//                "pk_test_IkEuiX8PBSrxqDOnx7W79ubE006HXByoRc")
//
//        val tokenParams: HashMap<String, Any> = HashMap<String, Any>()
//        val cardParams: HashMap<String, Any> = HashMap<String, Any>()
//        cardParams.put("number", "4242424242424242");
//        cardParams.put("exp_month", 10);
//        cardParams.put("exp_year", 2020);
//        cardParams.put("cvc", "314");
//
//        tokenParams.put("card", cardParams)
//    }

    private fun setAdapter() {
        val mManager = LinearLayoutManager(this)
        rvHistory.layoutManager = mManager
        paymentHistoryAdapter = PaymentHistoryAdapter(this, 0)
        rvHistory.adapter = paymentHistoryAdapter
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text = getString(R.string.my_wallet)
        currency_symbol.text = Utils.getPreferencesString(this@MyWalletstripeActivity,AppConstants.CURRENCY_SYMBOL)+" "
        tvAddMoney.setOnClickListener {
            tvAddMoney.isEnabled = false
            Handler().postDelayed(Runnable {
                tvAddMoney.isEnabled = true
            }, 700)
            val intent = Intent(this, AddMoneyDialogActivity::class.java)
            startActivityForResult(intent, 2)
//            saveCard()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra(AppConstants.INTENT_RESULT)
                amountMoney = data.getLongExtra(AppConstants.AMOUNT, 0)?:0
                if (Utils.isOnline(this)) {
                    Log.e("activityResult", "success")
//                    setPayment(amountMoney) .........this is for the worecard
//                    setStripePayment(amountMoney)
//                    popUp()
                    val intent = Intent(this, StripeCardPaymentActivity::class.java)
                    intent.putExtra(AppConstants.AMOUNT, amountMoney)
                    startActivityForResult(intent, 3)

                } else {
                    Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == Client.PAYMENT_SDK_REQUEST_CODE) {
            val paymentSdkResponse = data!!.getSerializableExtra(Client.EXTRA_PAYMENT_SDK_RESPONSE)
            if (paymentSdkResponse is PaymentResponse) {
                if (paymentSdkResponse.errorMessage.equals("User has canceled the action.")) {

                } else {
                    var formattedResponse: String = ResponseHelper.getFormattedResponse(paymentSdkResponse)
                    Toast.makeText(this, formattedResponse, Toast.LENGTH_SHORT).show()
                    if (!(paymentSdkResponse as PaymentResponse).payment!!.requestedAmount!!.amount!!.equals("")) {
                        val amount = (paymentSdkResponse as PaymentResponse).payment!!.requestedAmount!!.amount!!
                        callUpdateWalletApi(amount)
                    } else {
                        Toast.makeText(this@MyWalletstripeActivity, "Something went wrong", Toast.LENGTH_LONG)
                    }
                }
            }
        } else if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra(AppConstants.INTENT_RESULT)
                if (result.equals("success")) {
                    getWalletHistoryApi()
                }
            }
        }
    }//on

    private fun popUp() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val layoutInflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.dialog_payment, null)
        alertDialogBuilder.setView(view)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        view.rlCardPayment.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(this, StripeCardPaymentActivity::class.java)
            intent.putExtra(AppConstants.AMOUNT, amountMoney)
            startActivityForResult(intent, 3)
        }

        view.GooglePay.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun getWalletHistoryApi() {
        pbProgressWallet.visibility = View.VISIBLE
        ServiceHelper().getWalletHistoryService(Utils.getPreferencesString(this, AppConstants.USER_ID), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                pbProgressWallet.visibility = View.GONE
                val responseWallet = response.body() as WalletHistoryResponse
                tvTotalAmount.text = Utils.convertCurrencyWithoutSymbol(this@MyWalletstripeActivity, "USD", responseWallet.amount.toDouble()).toString()
                Utils.saveFloatPreferences(this@MyWalletstripeActivity, AppConstants.AMOUNT, responseWallet.amount)
                if (responseWallet.data!!.isNotEmpty()) {
                    paymentHistoryAdapter.setData(responseWallet.data!!)
                    paymentHistoryAdapter.notifyDataSetChanged()

                } else {
//                    Utils.showSimpleMessage(this@MyWalletstripeActivity, "No data found.").show()
                }

            }

            override fun onFailure(msg: String?) {
                pbProgressWallet.visibility = View.GONE
                Log.e("TAG", "onFailure: "+msg)
                Utils.showSimpleMessage(this@MyWalletstripeActivity, msg!!).show()
            }
        })
    }

    private fun callUpdateWalletApi(amount: String?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressWallet.visibility = View.VISIBLE
        ServiceHelper().addMoneyService(AddMoneyRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), amount!!.toDouble(), token!!, "USD"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressWallet.visibility = View.GONE
                        val loginResponse = response.body() as UpdateWalletResponse
                        Utils.showToast(this@MyWalletstripeActivity, loginResponse.message)
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.INTENT_RESULT, "success")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                        //  onBackPressed()
                    }

                    override fun onFailure(msg: String?) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressWallet.visibility = View.GONE
                        Utils.showSimpleMessage(this@MyWalletstripeActivity, msg!!).show()
                    }
                })

    }

}