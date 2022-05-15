package com.oncobuddy.app.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseActivity
import com.oncobuddy.app.views.fragments.AddOrEditAppointmentFragment
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

/**
 * Payment activity
 * Handles the all payments using the Razorpay library.
 * @constructor Create empty Payment activity
 */

class PaymentActivity: BaseActivity(), PaymentResultListener {

    val TAG:String = PaymentActivity::class.toString()
    var amount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_payment)
        Log.d("payment_log","1 "+intent.getDoubleExtra("FEES",1.0))
        /*
        * To ensure faster loading of the Checkout form,
        * call this method as early as possible in your checkout flow
        * */
        Checkout.preload(applicationContext)
        amount = intent.getDoubleExtra("FEES",1.0)
        if(amount < 1.0){
            amount = 1.0
            Log.d("payment_log","Amount was 0 so became 1 "+amount)
            Log.d("payment_log","Amount was 0 so became toint "+amount.toInt())
        }else{
            Log.d("payment_log","Amount was not 0.0")
        }

        var tvAMount = findViewById<TextView>(R.id.tvAmount)
        Log.d("payment_log","Amount final "+amount.toInt())
        tvAMount.setText("INR "+amount.toInt())

        var button: Button = findViewById(R.id.btn_pay)

       /* button.setOnClickListener {
            startPayment()
        }*/
        startPayment()
    }
    override fun init() {
        //TODO("Not yet implemented")
    }

    private fun startPayment() {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        var finalAmount = amount * 100
        Log.d("payment_log","2")
        val activity:Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","Oncobuddy")
            options.put("description","Virtual Appointment charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency","INR")
            options.put("amount",""+finalAmount)  // Here amount will be in Paise not Rupee

            val prefill = JSONObject()
            prefill.put("contact",getUserObject().phoneNumber)

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        try{
            Log.d("payment_log","3.1 "+errorCode)
            Log.d("payment_log","3.1 "+response)
            //Toast.makeText(applicationContext,"Payment Error!",Toast.LENGTH_SHORT).show()
            val intent = Intent(FourBaseCareApp.activityFromApp, AddOrEditAppointmentFragment::class.java)
            intent.putExtra(Constants.TRANSACTION_ID, "")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }catch (e: Exception){
            Log.e(TAG,"Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try{
            Log.d("payment_log","3 "+razorpayPaymentId)
            Toast.makeText(applicationContext,"Payment Successful!",Toast.LENGTH_SHORT).show()
            val intent = Intent(FourBaseCareApp.activityFromApp, AddOrEditAppointmentFragment::class.java)
            intent.putExtra(Constants.TRANSACTION_ID, razorpayPaymentId)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }catch (e: Exception){
            Log.e("payment_log","Exception in onPaymentSuccess "+e)
        }
    }


}