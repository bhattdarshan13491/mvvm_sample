package com.oncobuddy.app.views.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.broadcast_receivers.SmsBroadcastReceiver
import com.oncobuddy.app.databinding.ActivityNovSignInBinding
import com.oncobuddy.app.models.injectors.LoginInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.initial_login.LoginInput
import com.oncobuddy.app.models.pojo.initial_login.VerifyLoginOtpInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseActivity
import com.oncobuddy.app.view_models.LoginViewModel
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

/***
 *  It handles Signin with OTP for all users
 */
class NovSignInActivity : BaseActivity() {

    private lateinit var binding : ActivityNovSignInBinding
    private lateinit var loginViewModel: LoginViewModel
    private var isTimerRunning = false
    private var hasSentCode = false
    private lateinit var smsBroadcastReceiver : SmsBroadcastReceiver
    private lateinit var cdTimer: CountDownTimer

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }
    override fun onRestart() {
        super.onRestart()
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }





    override fun init() {
        binding = DataBindingUtil.setContentView(this@NovSignInActivity, R.layout.activity_nov_sign_in)
        setupVM()
        setupObservers()
        setClickListeners()
    }

    /***
     * It Fetches first 4 letters from the whole message
     */
    private fun getOtpFromMessage(message: String) {
        // This will match any 4 digit number in the message
        val pattern: Pattern = Pattern.compile("(|^)\\d{4}")
        val matcher: Matcher = pattern.matcher(message)
        if (matcher.find()) {
            binding.etOTP.setText(matcher.group(0))
        }
    }

    /***
     * It registers a listner for upcoming SMS to fetch the OTP and automatically set in the OTP EditBox
     */
    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver.smsBroadcastReceiverListener = object :
            SmsBroadcastReceiver.SmsBroadcastReceiverListener {
            override fun onSuccess(intent: Intent) {
                startActivityForResult(intent, Constants.REQ_USER_CONSENT)
            }

            override fun onFailure() {}
        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

        registerReceiver(smsBroadcastReceiver, intentFilter)
    }


    private fun startSmsUserConsent() {
        val client: SmsRetrieverClient = SmsRetriever.getClient(this)
        //We can add sender phone number or leave it blank
        // I'm adding null here
        client.startSmsUserConsent(null).addOnSuccessListener(OnSuccessListener<Void?> {
            /*      Toast.makeText(
                      applicationContext,
                      "On Success",
                      Toast.LENGTH_LONG
                  ).show()*/
        }).addOnFailureListener(
            OnFailureListener {
                /*Toast.makeText(
                    applicationContext,
                    "On OnFailure",
                    Toast.LENGTH_LONG
                ).show()*/
            })
    }

    private fun setClickListeners() {

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                if(charSequence.length == 0){
                    binding.tvSendOtp.setText("Send OTP")
                    if(::cdTimer.isInitialized && cdTimer != null){
                        cdTimer.cancel()
                        isTimerRunning = false
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })




        binding.etOTP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                if(charSequence.length>3){
                    CommonMethods.hideKeyboard(this@NovSignInActivity)
                    if (checkInterNetConnection(this@NovSignInActivity) && isValidInput()) {
                        callVerifyOtp()
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        binding.tvSignIn.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick() && checkInterNetConnection(this@NovSignInActivity) && isValidInput()) {
                callVerifyOtp()
            }
        })


        binding.tvSignInPwd.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.changeActivity(
                    this@NovSignInActivity,
                    NovSignInPwdActivity::class.java,
                    false
                )
            }
        })

        binding.tvSignUp.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.changeActivity(
                    this@NovSignInActivity,
                    NovSignupActivity::class.java,
                    false
                )
            }
        })



        binding.tvSendOtp.setOnClickListener {
            if (!isDoubleClick() && checkInterNetConnection(this@NovSignInActivity) && isValidMobileInput() && !isTimerRunning) {
                CommonMethods.hideKeyboard(this@NovSignInActivity)    
                var loginInput = LoginInput()
                loginInput.mobileNumber = getTrimmedText(binding.etEmail)
                loginViewModel.getOtp(loginInput, false)
                //binding.tvSendOtp.setText("Resend code")
                startCountdownTimer()
                hasSentCode = true
                startSmsUserConsent()
                    
            }
        }
    }

    private fun startCountdownTimer() {
        cdTimer = object : CountDownTimer(30000, 1000) {
            override fun onFinish() {
                isTimerRunning = false
                binding.tvSendOtp.setText("Resend OTP")
                //binding.tvSendOtp.setTextAppearance(R.style.MainTextBlue)
            }

            override fun onTick(millisUntilFinished: Long) {
                isTimerRunning = true
                var  time = millisUntilFinished / 1000
                binding.tvSendOtp.setText("00:" + CommonMethods.getStringWithOnePadding(time.toString()))
            }

        }.start()
    }

    private fun isValidInput() : Boolean {
        if(getTrimmedText(binding.etEmail).isNullOrEmpty()){
            showToast(getString(R.string.validation_enter_mobile_number))
            return  false
        } else if(getTrimmedText(binding.etEmail).trim().length < 10){
            showToast(getString(R.string.validation_invalid_mobile_number))
            return  false
        }else if(hasSentCode && getTrimmedText(binding.etOTP).isNullOrEmpty()){
            showToast(getString(R.string.validation_enter_otp))
            return false
        }
        return true
    }

    private fun isValidMobileInput() : Boolean {
        if(getTrimmedText(binding.etEmail).isNullOrEmpty()){
            showToast(getString(R.string.validation_enter_mobile_number))
            return  false
        } else if(getTrimmedText(binding.etEmail).trim().length < 10){
            showToast(getString(R.string.validation_invalid_mobile_number))
            return  false
        }
        return true
    }


    // API stuff
    private fun setupVM() {
        loginViewModel = ViewModelProvider(
            this,
            LoginInjection.provideViewModelFactory()
        ).get(LoginViewModel::class.java)
    }

    private fun setupObservers() {
        loginViewModel.loginResonseData.observe(this, loginDataObserver)
        loginViewModel.otpResonseData.observe(this, otpDataObserver)
        loginViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        loginViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private fun callVerifyOtp() {
        CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
        var verifyOtpInput = VerifyLoginOtpInput()
        verifyOtpInput.deviceType = Constants.ANDROID
        verifyOtpInput.mobileNumber = getTrimmedText(binding.etEmail)
        verifyOtpInput.otp = getTrimmedText(binding.etOTP)
        verifyOtpInput.token = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_FCM_TOKEN, "")
        loginViewModel.verifyOtp(verifyOtpInput)
    }

    // Setup observers
    
    private val loginDataObserver = Observer<Response<LoginResponse?>>{ loginResponseData ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()
        Log.d("otp_log","Response came")
        Log.d("otp_log","Response success")

        if (loginResponseData.isSuccessful()) {
            if(!loginResponseData.body()?.payLoad?.profile?.dateOfBirth.isNullOrEmpty()){
                Log.d("dob_log","date "+loginResponseData.body()?.payLoad?.profile?.dateOfBirth)
                var date = loginResponseData.body()?.payLoad?.profile?.dateOfBirth?.let {
                    CommonMethods.utcTOLocalDate(
                        it
                    )

                }
                loginResponseData.body()?.payLoad?.profile?.dateOfBirth = date
                Log.d("dob_log","changed date "+date)
            }else{
                Log.d("dob_log","dob date is null")
            }

            Log.d("bank_log","Details added? "+loginResponseData.body()?.payLoad?.profile?.bankDetailsAdded)

            val gson = Gson()
            val userObj = gson.toJson(loginResponseData.body()?.payLoad)

            if(loginResponseData.body()?.payLoad == null)
                Log.d("otp_log","user object null")
            else
                Log.d("otp_log","user object not null")


            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ,userObj)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_AUTH_TOKEN,"Bearer "+loginResponseData.body()?.payLoad?.authToken)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_ID,""+loginResponseData.body()?.payLoad?.userIdd)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_NAME,""+loginResponseData.body()?.payLoad?.firstName+" "+
                    loginResponseData.body()?.payLoad?.lastName)

            Log.d("user_id_log","otp id created "+getUserObject().userIdd)

            Log.d("otp_log", getUserObject().role)
            if (getUserObject().role.equals(Constants.ROLE_PATIENT, ignoreCase = true)) {
                Constants.IS_DOCTOR_LOGGED_IN = false
                CommonMethods.changeActivity(this@NovSignInActivity, PatientLandingActivity::class.java,true, true)
            }else if(getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER, ignoreCase = true)){
                Constants.IS_DOCTOR_LOGGED_IN = false
                if(getUserObject().linkedCareCode == null){
                    CommonMethods.changeActivity(this@NovSignInActivity, NovCGAccountSetupActivity::class.java,true, true)
                }else{
                    getUserObject().patientConnected = true
                    val gson = Gson()
                    val userObj = gson.toJson(getUserObject())
                    FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ,userObj)
                    CommonMethods.changeActivity(this@NovSignInActivity, PatientLandingActivity::class.java,true, true)
                }
            }
            else if (getUserObject().role.equals(Constants.ROLE_CARE_COMPANION, ignoreCase = true)) {
                Constants.IS_DOCTOR_LOGGED_IN = false
                CommonMethods.changeActivity(this@NovSignInActivity, PatientLandingActivity::class.java,true, true)
            } else if(getUserObject().role.equals(Constants.ROLE_DOCTOR, ignoreCase = true)){
                Constants.IS_DOCTOR_LOGGED_IN = true
                CommonMethods.changeActivity(this@NovSignInActivity, NovDoctorLandingActivity::class.java,true, true)
            }else if(getUserObject().role.equals(Constants.ROLE_NEW_USER, ignoreCase = true)){
                CommonMethods.showToast(applicationContext, "Please sign up first to use application")
                //CommonMethods.changeActivity(this@NovSignInActivity, SelectRoleActivity::class.java,true, true)
            }else{
                CommonMethods.showToast(applicationContext, "You are not yet authorized to use this app. Please contact customer support")
            }

        }else{
            CommonMethods.showToast(applicationContext, "Something went wrong")
        }

        /*CommonMethods.showLog("login_log","Data "+loginResponseData.toString())*/
    }

    private val otpDataObserver = Observer<Response<BaseResponse?>>{ loginResponseData ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if(loginResponseData.isSuccessful){
            Log.d("login_log","Otp success")
        }else{
            showToast("There was an issue sending OTP. Please try again!")
        }
    }

    private val isViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("registration_log", "is_loading is "+isLoading)
        showHideProgress(isLoading,binding.layoutProgress.frProgress)
    }

    private val errorMessageObserver = Observer<String>{message ->
        showToast(message)
    }


    //////


    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQ_USER_CONSENT) {
            if (resultCode == RESULT_OK && data != null) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                //Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                /*  textViewMessage.setText(
                      String.format(
                          "%s - %s",
                          getString(R.string.received_message),
                          message
                      )
                  )

                 */
                if (message != null) {
                    getOtpFromMessage(message)
                }
            }
        }
    }
}