package com.oncobuddy.app.views.activities

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.broadcast_receivers.SmsBroadcastReceiver
import com.oncobuddy.app.databinding.ActivityResetPwdBinding
import com.oncobuddy.app.models.injectors.LoginInjection
import com.oncobuddy.app.models.injectors.VerifyOtpInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.initial_login.VerifyLoginOtpInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.models.pojo.otp_process.ForgotPwdInput
import com.oncobuddy.app.models.pojo.otp_process.GenerateOTPInput
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseActivity
import com.oncobuddy.app.view_models.LoginViewModel
import com.oncobuddy.app.view_models.VerifyOTPViewModel
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Nov forgot pwd activity
 * Handles forget password process of the application
 * @constructor Create empty Nov forgot pwd activity
 */

class NovForgotPwdActivity : BaseActivity() {

    private lateinit var binding: ActivityResetPwdBinding
    private lateinit var verifyOTPViewModel: VerifyOTPViewModel
    private lateinit var loginViewModel: LoginViewModel
    private var isTimerRunning = false
    private var hasSentCode = false
    private var IS_FROM_PROFILE = false
    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    private var shouldShowPassword = false
    private lateinit var cdTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun init() {

        binding = DataBindingUtil.setContentView(this@NovForgotPwdActivity, R.layout.activity_reset_pwd)

        if (intent != null && intent.hasExtra(Constants.SOURCE)) {
            IS_FROM_PROFILE = true
            binding.ivBack.visibility = View.VISIBLE
            binding.etEmail.setText("" + getUserObject().phoneNumber)

            binding.etEmail.isEnabled = false
            binding.etEmail.isFocusable = false
            binding.etEmail.isClickable = true

            binding.etEmail.setOnClickListener(View.OnClickListener {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.you_cant_change_your_number)
                )
            })
            binding.relNumber.setOnClickListener(View.OnClickListener {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.you_cant_change_your_number)
                )
            })

            binding.ivBack.setOnClickListener(View.OnClickListener {
                finish()
                //overridePendingTransition(R.anim.anim_right_out, R.anim.anim_left_in)
            })
        }
        setupVM()
        setupObservers()
        setClickListeners()

    }

    private fun isValidInput(): Boolean {
        if (getTrimmedText(binding.etEmail).isNullOrEmpty()) {
            showToast(getString(R.string.validation_enter_mobile_number))
            return false
        } else if (getTrimmedText(binding.etEmail).trim().length < 10) {
            showToast(getString(R.string.validation_invalid_mobile_number))
            return false
        } else if (hasSentCode && getTrimmedText(binding.etOTP).isNullOrEmpty()) {
            showToast(getString(R.string.validation_enter_otp))
            return false
        }
        return true
    }

    private fun isValidMobileNoInput(): Boolean {
        if (getTrimmedText(binding.etEmail).isNullOrEmpty()) {
            showToast(getString(R.string.validation_enter_mobile_number))
            return false
        } else if (getTrimmedText(binding.etEmail).trim().length != 10) {
            showToast(getString(R.string.validation_invalid_mobile_number))
            return false
        }
        return true
    }

    private fun setClickListeners() {

        binding.ivShowHidePwd.setOnClickListener(View.OnClickListener {
            showHidePassword(binding.etNewPassword, binding.ivShowHidePwd, shouldShowPassword)
            shouldShowPassword = !shouldShowPassword
        })

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
                if (charSequence.length == 0) {
                    binding.tvSendOtp.text = "Send OTP"
                    if(::cdTimer.isInitialized && cdTimer != null){
                        cdTimer.cancel()
                        isTimerRunning = false
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        binding.tvCOnfirmOtp.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick() && checkInterNetConnection(this@NovForgotPwdActivity) && isValidInput()) {
                if (getTrimmedText(binding.etOTP).isNullOrEmpty()) {
                    showToast("Please enter an OTP")
                } else if (getTrimmedText(binding.etNewPassword).isNullOrEmpty()) {
                    showToast("Please enter new password")
                } else if (getTrimmedText(binding.etNewPassword).length < 6) {
                    showToast("Password should contain minimum 6 characters")
                }else if (getTrimmedText(binding.etRPassword).isNullOrBlank()) {
                    showToast(getString(R.string.validation_enter_confirm_password))
                } else if (!getTrimmedText(binding.etNewPassword).equals(getTrimmedText(binding.etRPassword))) {
                    showToast(getString(R.string.validation_passwords_not_same))
                } else {
                    var forgotPasswordInput = ForgotPwdInput()
                    forgotPasswordInput.newPassword = getTrimmedText(binding.etNewPassword)
                    forgotPasswordInput.phoneNumber = getTrimmedText(binding.etEmail)
                    forgotPasswordInput.otp = getTrimmedText(binding.etOTP)
                    Log.d("change_pwd_log","0")
                    verifyOTPViewModel.callVerifyOTP(forgotPasswordInput)
                }
                CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
            }

            /*  var verifyOtpInput = VerifyLoginOtpInput()
              verifyOtpInput.deviceType = Constants.ANDROID
              verifyOtpInput.mobileNumber = intent.getStringExtra(Constants.PHONE_NUMBER)
              verifyOtpInput.otp = getTrimmedText(binding.etOTP)
              verifyOtpInput.token =
                  FourBaseCareApp.sharedPreferences.getString(Constants.PREF_FCM_TOKEN, "")
              loginViewModel.verifyOtp(verifyOtpInput)
*/

            /*if(!isDoubleClick() && checkInterNetConnection(this@NovForgotPwdActivity)){
                verifyOTP()
            }*/

        })

        binding.tvSendOtp.setOnClickListener {
            if (!isDoubleClick() && checkInterNetConnection(this@NovForgotPwdActivity) && isValidMobileNoInput() && !isTimerRunning) {
                CommonMethods.hideKeyboard(this@NovForgotPwdActivity)
                var generateOTPInput = GenerateOTPInput()
                generateOTPInput.phoneNumber = getTrimmedText(binding.etEmail)
                generateOTPInput.otpPurpose = Constants.PURPOSE_FORGOT_PASSWORD
                Log.d("change_pwd_log", "started")
                verifyOTPViewModel.generateOTP(generateOTPInput)
                startCountdownTimer()
                hasSentCode = true
                startSmsUserConsent()
                /*  var loginInput = LoginInput()
                  loginInput.mobileNumber = getTrimmedText(binding.etEmail)
                  loginViewModel.getOtp(loginInput, false)
                  //binding.tvSendOtp.setText("Resend code")
                  startCountdownTimer()
                  hasSentCode = true
                  startSmsUserConsent()*/


            }
        }
    }

    private fun verifyOTP() {
        var verifyOtpInput = VerifyLoginOtpInput()
        verifyOtpInput.deviceType = Constants.ANDROID
        verifyOtpInput.mobileNumber = getTrimmedText(binding.etEmail)
        verifyOtpInput.otp = getTrimmedText(binding.etOTP)
        verifyOtpInput.token =
            FourBaseCareApp.sharedPreferences.getString(Constants.PREF_FCM_TOKEN, "")
        loginViewModel.verifyOtp(verifyOtpInput)
    }

    private fun startCountdownTimer() {
        cdTimer = object : CountDownTimer(30000, 1000) {
            override fun onFinish() {
                isTimerRunning = false
                binding.tvSendOtp.text = "Resend OTP"
                //binding.tvSendOtp.setTextAppearance(R.style.MainTextBlue)
            }

            override fun onTick(millisUntilFinished: Long) {
                isTimerRunning = true
                var time = millisUntilFinished / 1000
                binding.tvSendOtp.text = "00:" + CommonMethods.getStringWithOnePadding(time.toString())

            }

        }.start()
    }

    // API Stuff
    private fun setupVM() {
        verifyOTPViewModel = ViewModelProvider(
            this,
            VerifyOtpInjection.provideViewModelFactory()
        ).get(VerifyOTPViewModel::class.java)

        loginViewModel = ViewModelProvider(
            this,
            LoginInjection.provideViewModelFactory()
        ).get(LoginViewModel::class.java)
    }

    private fun setupObservers() {
        verifyOTPViewModel.generateOtpResponseData.observe(this, generateOtpObserver)
        loginViewModel.loginResonseData.observe(this, loginDataObserver)

        verifyOTPViewModel.forGotPwdResponseData.observe(this, forgotPwdResponseObserver)

        verifyOTPViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        verifyOTPViewModel.onMessageError.observe(this, errorMessageObserver)

        loginViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        loginViewModel.onMessageError.observe(this, errorMessageObserver)
    }


    private val forgotPwdResponseObserver = Observer<BaseResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            if (IS_FROM_PROFILE) {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    "Password changed successfully!"
                )
            } else {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    "Password changed successfully! Please login with new password"
                )
            }
            finish()

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem creating password."
            )
        }
    }
    private val loginDataObserver = Observer<Response<LoginResponse?>> { loginResponseData ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (loginResponseData.isSuccessful) {
            CommonMethods.showToast(applicationContext, "OTP verified successfully!")
            gotoResetPwdActivity()
        } else {
            CommonMethods.showToast(applicationContext, "Something went wrong")
        }

        /*CommonMethods.showLog("login_log","Data "+loginResponseData.toString())*/
    }


    private val generateOtpObserver = Observer<BaseResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "OTP Generated successfully! Please check your sms!"
            )
            binding.linPassword.visibility = View.VISIBLE
            // }


        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem generating OTP."
            )
        }
    }

    private val isViewLoadingObserver = Observer<Boolean> { isLoading ->
        Log.d("verify_otp_log", "is_loading is " + isLoading)
        Log.d("change_pwd_log","loading "+isLoading)
        showHideProgress(isLoading, binding.layoutProgress.frProgress)
    }

    private val errorMessageObserver = Observer<String> { message ->
        Log.d("change_pwd_log","Err msg "+message)
        showToast(message)
    }

    /////
    private fun gotoResetPwdActivity() {
        val mLoginIntent =
            Intent(this@NovForgotPwdActivity, NovResetPwdActivity::class.java)
        mLoginIntent.putExtra("phone_number", getTrimmedText(binding.etEmail))
        mLoginIntent.putExtra("otp", getTrimmedText(binding.etOTP))
        startActivity(mLoginIntent)
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)

    }


    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }

    private fun getOtpFromMessage(message: String) {
        // This will match any 4 digit number in the message
        val pattern: Pattern = Pattern.compile("(|^)\\d{4}")
        val matcher: Matcher = pattern.matcher(message)
        if (matcher.find()) {
            binding.etOTP.setText(matcher.group(0))
        }
    }

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