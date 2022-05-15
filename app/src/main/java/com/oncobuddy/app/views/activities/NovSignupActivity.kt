package com.oncobuddy.app.views.activities

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.*
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
import com.oncobuddy.app.databinding.*
import com.oncobuddy.app.models.injectors.LoginInjection
import com.oncobuddy.app.models.injectors.Registrationnjection
import com.oncobuddy.app.models.injectors.VerifyOtpInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.initial_login.LoginInput
import com.oncobuddy.app.models.pojo.initial_login.VerifyLoginOtpInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseActivity
import com.oncobuddy.app.view_models.LoginViewModel
import com.oncobuddy.app.view_models.RegistrationViewModel
import com.oncobuddy.app.view_models.VerifyOTPViewModel
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

import android.webkit.WebViewClient
import androidx.annotation.Nullable
import com.google.gson.Gson
import com.oncobuddy.app.models.pojo.nov_signup.SignupResponse
import com.oncobuddy.app.models.pojo.registration_process.NovRegistration


class NovSignupActivity : BaseActivity() {

    private lateinit var binding: ActivityNovSignUpBinding
    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var verifyOTPViewModel: VerifyOTPViewModel
    private lateinit var loginViewModel: LoginViewModel
    private var shouldShowPassword = false
    private var selectedRole = "nothing"
    private var currentAuthToken = ""
    private var IS_NUMBER_VERIFIED = false
    private var IS_TERMS_READ = false
    private var IS_PRIVACY_POLICY_READ = false
    private var isTimerRunning = false
    private var hasSentCode = false
    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    private lateinit var openWebVIewDialogue: Dialog
    private lateinit var cdTimer: CountDownTimer

    //val tempToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4NTExMjMyMTMxIiwicm9sZSI6IlJPTEVfUEFUSUVOVCIsImlhdCI6MTYzNjUzNDA4NSwiZXhwIjoxNjM3ODMwMDg1fQ.hAXzlrQ55iCRGws8Oyl5eo65eyj4jDtgEdn9wjLESL0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun init() {
        binding = DataBindingUtil.setContentView(this@NovSignupActivity, R.layout.activity_nov_sign_up)
        setupVM()
        setupObservers()
        setupCLickListeners()
    }

    private fun setupCLickListeners() {

        binding.etMobileNumber.addTextChangedListener(object : TextWatcher {
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
                Log.d("text_0","text length "+charSequence.length)
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

        binding.ivShowHidePwd.setOnClickListener(View.OnClickListener {
            showHidePassword(binding.etPassword, binding.ivShowHidePwd, shouldShowPassword)
            shouldShowPassword = !shouldShowPassword
        })

        binding.ivDropDown.setOnClickListener(View.OnClickListener {
            binding.spSignupAs.performClick()
        })

        binding.spSignupAs.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    selectedRole = "nothing"
                } else if (position == 1) {
                    selectedRole = Constants.ROLE_PATIENT
                }else if (position == 2) {
                    selectedRole = Constants.ROLE_PATIENT_CARE_GIVER
                }else {
                    selectedRole = Constants.ROLE_DOCTOR
                }
            }

        }

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
                if (charSequence.length > 3) {
                    CommonMethods.hideKeyboard(this@NovSignupActivity)
                    if (checkInterNetConnection(this@NovSignupActivity)) {
                        callVerifyOtp()
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })


        binding.tvSendOtp.setOnClickListener {
            if (!isDoubleClick() && checkInterNetConnection(this@NovSignupActivity) && isValidMobileNoInput() && !isTimerRunning) {
                CommonMethods.hideKeyboard(this@NovSignupActivity)
                var loginInput = LoginInput()
                loginInput.mobileNumber = getTrimmedText(binding.etMobileNumber)
                Log.d("nov_otp_log", "0")
                loginViewModel.getOtp(loginInput, true)
                //binding.tvSendOtp.setText("Resend code")
                startCountdownTimer()
                hasSentCode = true
                startSmsUserConsent()
                binding.relOtp.visibility = View.VISIBLE
            }
        }

        binding.tvVerify.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick() && checkInterNetConnection(this@NovSignupActivity) && isValidMobileNoInput()) {
                CommonMethods.hideKeyboard(this@NovSignupActivity)
                callVerifyOtp()
            }
        })


        binding.tvSave.setOnClickListener(View.OnClickListener {
                  doSIgnupProcess()
                  //dummySIgnup()
                  //dummyDoctorSIgnup()
                   //dummyCGSignup()
        })

        binding.tvSignIn.setOnClickListener(View.OnClickListener {
            CommonMethods.changeActivity(
                this@NovSignupActivity,
                NovSignInActivity::class.java,
                true
            )
        })

        binding.tvPrivacyPolicy.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                IS_PRIVACY_POLICY_READ = true
                openDialogueWithWebView("PRIVACY POLICY", Constants.PRIVACY_POLICY_URL)
            }

            //CommonMethods.openWebBrowser("http://www.google.com", this@NovSignupActivity)
        })

        binding.tvTermsOfService.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                IS_TERMS_READ = true
                //CommonMethods.openWebBrowser("http://www.google.com", this@NovSignupActivity)
                openDialogueWithWebView("TERMS AND CONDITIONS", Constants.TERMS_URL)
            }

        })
    }

    private fun dummySIgnup() {
        var str = "{\n" +
                "  \"success\": true,\n" +
                "  \"message\": \"User registered Successfully\",\n" +
                "  \"payLoad\": {\n" +
                "    \"headers\": {\n" +
                "      \n" +
                "    },\n" +
                "    \"body\": {\n" +
                "      \"success\": true,\n" +
                "      \"message\": \"Login successful\",\n" +
                "      \"payLoad\": {\n" +
                "        \"userId\": 180,\n" +
                "        \"firstName\": \"Abcd\",\n" +
                "        \"lastName\": null,\n" +
                "        \"email\": \"abcdddd@gmail.com\",\n" +
                "        \"phoneNumber\": \"8123754264\",\n" +
                "        \"role\": \"ROLE_PATIENT\",\n" +
                "        \"authToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4NTExMjMyMTMxIiwicm9sZSI6IlJPTEVfUEFUSUVOVCIsImlhdCI6MTY0MzM2NTUyMywiZXhwIjoxNjQ0NjYxNTIzfQ.prvOufB1SYdu4KFKkF36baMH2kiToCkVI3Ds5uufRQQ\",\n"+
                "        \"dpLink\": null,\n" +
                "        \"headline\": null,\n" +
                "        \"experience\": null,\n" +
                "        \"profile\": {\n" +
                "          \"dateOfBirth\": null,\n" +
                "          \"gender\": null,\n" +
                "          \"cancerType\": null,\n" +
                "          \"cancerSubType\": null\n" +
                "        },\n" +
                "        \"careTakers\": [\n" +
                "          \n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    \"statusCodeValue\": 200,\n" +
                "    \"statusCode\": \"OK\"\n" +
                "  }\n" +
                "}"

        val gson = Gson()
        var signupResponse = gson.fromJson(str, SignupResponse::class.java)


        val userObj = gson.toJson(signupResponse.payLoad?.body?.payLoad)
        FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userObj)

        Log.d("act_setup_log", "token " + getUserObject().authToken)


        showToast("User will be registered successfully!")
        gotoPatientAcSetupActivity()
        //gotoDoctorIntroActivity()
    }



    private fun dummyCGSignup() {
        var str = "{\"success\":true,\"message\":\"User registered Successfully\",\"payLoad\":{\"headers\":{},\"body\":{\"success\":true,\"message\":\"Login successful\",\"payLoad\":{\"userId\":372,\"firstName\":\"New CG\",\"lastName\":null,\"email\":\"cg1235@gmail.com\",\"phoneNumber\":\"8123754264\",\"role\":\"ROLE_PATIENT_CARE_GIVER\",\"authToken\":\"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4MTIzNzU0MjY0Iiwicm9sZSI6IlJPTEVfUEFUSUVOVF9DQVJFX0dJVkVSIiwiaWF0IjoxNjQ1MDc0MzU5LCJleHAiOjE2NDYzNzAzNTl9.1J_BHMLWLsppsSRZOq2luu7xAMcvoDJyDIU8NhqPxjc\",\"dpLink\":null,\"headline\":null,\"experience\":null,\"profile\":null,\"careTakers\":null,\"numberOfPatientsForADoctor\":0,\"showProfileVerifyScreens\":false}},\"statusCode\":\"OK\",\"statusCodeValue\":200}}"
        val gson = Gson()
        var signupResponse = gson.fromJson(str, SignupResponse::class.java)


        val userObj = gson.toJson(signupResponse.payLoad?.body?.payLoad)
        FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userObj)

        Log.d("act_setup_log", "token " + getUserObject().authToken)


        showToast("Doctor will be registered successfully!")

        gotoCGAcSetupActivity()
    }


    private fun gotoDoctorIntroActivity() {
        Constants.IS_DOCTOR_LOGGED_IN = true
        val doctorIntent = Intent(this@NovSignupActivity, NovIntroductionScreensActivity::class.java)
        doctorIntent.putExtra(Constants.SOURCE,Constants.ROLE_DOCTOR)
        startActivity(doctorIntent)
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)

    }

    private fun gotoPatientAcSetupActivity() {
        Constants.IS_DOCTOR_LOGGED_IN = false
        CommonMethods.changeActivity(
            this@NovSignupActivity,
            NovAccountSetupActivity::class.java,
            true
        )

    }

    private fun gotoCGAcSetupActivity() {
        Constants.IS_DOCTOR_LOGGED_IN = false
        CommonMethods.changeActivity(
            this@NovSignupActivity,
            NovCGAccountSetupActivity::class.java,
            true
        )

    }

    private fun doSIgnupProcess() {
        if (!isDoubleClick() && checkInterNetConnection(this@NovSignupActivity) && isValidInput()) {

            var appUser = NovRegistration()
            appUser.fullName = getTrimmedText(binding.etFullName)
            appUser.email = getTrimmedText(binding.etEmail)
            appUser.mobileNumber = getTrimmedText(binding.etMobileNumber)
            appUser.password = getTrimmedText(binding.etPassword)
            appUser.role = selectedRole
            registrationViewModel.doPatientRegistration(appUser)
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
                var time = millisUntilFinished / 1000
                binding.tvSendOtp.setText("00:" + CommonMethods.getStringWithOnePadding(time.toString()))

            }

        }.start()
    }

    //

    private fun setupObservers() {
        registrationViewModel.loginResonseData.observe(this, signUpDataObserver)
        registrationViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        registrationViewModel.onMessageError.observe(this, errorMessageObserver)
        loginViewModel.otpResonseData.observe(this, otpDataObserver)
        loginViewModel.loginResonseData.observe(this, verifyOTPDataObserver)
        loginViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        loginViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val verifyOTPDataObserver = Observer<Response<LoginResponse?>> { loginResponseData ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()
        binding.etMobileNumber.isEnabled = false
        if (loginResponseData.isSuccessful()) {
            CommonMethods.showToast(applicationContext, "OTP verified successfully!")
            binding.tvSendOtp.visibility = View.GONE
            binding.relOtp.visibility = View.GONE
            binding.linVerified.visibility = View.VISIBLE
            IS_NUMBER_VERIFIED = true
            binding.etMobileNumber.isEnabled = false
            //gotoResetPwdActivity()
        } else {
            CommonMethods.showToast(applicationContext, "Something went wrong")
        }

        /*CommonMethods.showLog("login_log","Data "+loginResponseData.toString())*/
    }
    private val otpDataObserver = Observer<Response<BaseResponse?>> { loginResponseData ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (loginResponseData.isSuccessful) {
            Log.d("login_log", "Otp success")
        } else {
            showToast("There was an issue sending OTP. Please try again!")
        }
    }

    private fun callVerifyOtp() {
        CommonMethods.hideKeyboard(this@NovSignupActivity)
        var verifyOtpInput = VerifyLoginOtpInput()
        verifyOtpInput.deviceType = Constants.ANDROID
        verifyOtpInput.mobileNumber = getTrimmedText(binding.etMobileNumber)
        verifyOtpInput.otp = getTrimmedText(binding.etOTP)
        verifyOtpInput.token =
            FourBaseCareApp.sharedPreferences.getString(Constants.PREF_FCM_TOKEN, "")
        loginViewModel.verifyOtp(verifyOtpInput)
    }

    private fun setupVM() {

        loginViewModel = ViewModelProvider(
            this,
            LoginInjection.provideViewModelFactory()
        ).get(LoginViewModel::class.java)

        verifyOTPViewModel = ViewModelProvider(
            this,
            VerifyOtpInjection.provideViewModelFactory()
        ).get(VerifyOTPViewModel::class.java)

        registrationViewModel = ViewModelProvider(
            this,
            Registrationnjection.provideViewModelFactory()
        ).get(RegistrationViewModel::class.java)
    }

    private val loginDataObserver = Observer<Response<BaseResponse?>> { loginResponseData ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()
        CommonMethods.changeActivity(this@NovSignupActivity, NovAccountSetupActivity::class.java, true)
    }

    private val signUpDataObserver = Observer<Response<SignupResponse?>>{ loginResponseData ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (loginResponseData.isSuccessful()) {
            val gson = Gson()
            val userObj = gson.toJson(loginResponseData.body()?.payLoad?.body?.payLoad)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ,userObj)

            Log.d("dob_log","name "+loginResponseData.body()?.payLoad?.body?.payLoad?.firstName)
            Log.d("dob_log","authtoken "+loginResponseData.body()?.payLoad?.body?.payLoad?.authToken)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_AUTH_TOKEN,"Bearer "+getUserObject().authToken)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_ID,""+loginResponseData.body()?.payLoad?.userIdd)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_NAME,""+loginResponseData.body()?.payLoad?.firstName+" "+
                    loginResponseData.body()?.payLoad?.lastName)

            if(selectedRole.equals(Constants.ROLE_DOCTOR)){
                gotoDoctorIntroActivity()
            } else if(selectedRole.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
                gotoCGAcSetupActivity()
            } else{
                gotoPatientAcSetupActivity()
            }

        }else{
            CommonMethods.showToast(applicationContext, "Something went wrong")
        }

        /*CommonMethods.showLog("login_log","Data "+loginResponseData.toString())*/
    }

    private val isViewLoadingObserver = Observer<Boolean> { isLoading ->
        Log.d("registration_log", "is_loading is " + isLoading)
        if (isLoading) showHideProgress(true, binding.layoutProgress.frProgress)
        else showHideProgress(false, binding.layoutProgress.frProgress)
    }

    private val errorMessageObserver = Observer<String> { message ->
        CommonMethods.showToast(this@NovSignupActivity, message)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun isValidMobileNoInput(): Boolean {
        if (getTrimmedText(binding.etMobileNumber).isNullOrEmpty()) {
            showToast(getString(R.string.validation_enter_mobile_number))
            return false
        } else if (getTrimmedText(binding.etMobileNumber).trim().length < 10) {
            showToast(getString(R.string.validation_invalid_mobile_number))
            return false
        }
        return true
    }


    private fun isValidInput(): Boolean {
        if (selectedRole.equals("nothing")) {
            showToast(getString(R.string.validation_enter_role))
            return false
        } else if (getTrimmedText(binding.etFullName).isNullOrBlank()) {
            showToast(getString(R.string.validation_enter_name))
            return false
        } else if (getTrimmedText(binding.etMobileNumber).isNullOrBlank()) {
            showToast(getString(R.string.validation_enter_mobile_number))
            return false
        } else if (getTrimmedText(binding.etMobileNumber).trim().length < 10) {
            showToast(getString(R.string.validation_invalid_mobile_number))
            return false
        } else if (!IS_NUMBER_VERIFIED) {
            showToast(getString(R.string.validation_verify_mobile_number))
            return false
        } else if (getTrimmedText(binding.etEmail).isNullOrBlank()) {
            showToast(getString(R.string.validation_enter_email))
            return false
        }else if (!CommonMethods.isValidEmail(getTrimmedText(binding.etEmail))) {
            showToast(getString(R.string.validation_enter_valid_email))
            return false
        } else if (getTrimmedText(binding.etPassword).isNullOrBlank()) {
            showToast(getString(R.string.validation_enter_password))
            return false
        } else if (getTrimmedText(binding.etPassword).toString().length < 6) {
            showToast(getString(R.string.validation_password_min_length))
            return false
        } else if (getTrimmedText(binding.etRPassword).isNullOrBlank()) {
            showToast(getString(R.string.validation_enter_confirm_password))
            return false
        } else if (!getTrimmedText(binding.etPassword).equals(getTrimmedText(binding.etRPassword))) {
            showToast(getString(R.string.validation_passwords_not_same))
            return false
        } else if (!IS_TERMS_READ) {
            showToast(getString(R.string.validation_terms_not_read))
            return false
        } else if (!IS_PRIVACY_POLICY_READ) {
            showToast(getString(R.string.validation_privacy_policy_read))
            return false
        } else if (!binding.checkCOnditions.isChecked) {
            showToast(getString(R.string.validation_agree_terms_privacy_policy))
            return false
        }
        return true
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

    private fun openDialogueWithWebView(title: String, webUrl: String) {
        openWebVIewDialogue = Dialog(this@NovSignupActivity)
        openWebVIewDialogue.setContentView(R.layout.dialogue_with_webview)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(openWebVIewDialogue.window?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = openWebVIewDialogue?.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        openWebVIewDialogue.window?.setAttributes(lp)
        openWebVIewDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val webVIew: WebView = openWebVIewDialogue.findViewById(R.id.webView)
        val ivClose: ImageView = openWebVIewDialogue.findViewById(R.id.ivCross)
        val tvTitle: TextView = openWebVIewDialogue.findViewById(R.id.tvTitle)

        tvTitle.setText(title)

        ivClose.setOnClickListener(View.OnClickListener {
            openWebVIewDialogue.dismiss()
        })

        webVIew.getSettings().setJavaScriptEnabled(true)
        webVIew.setWebViewClient(WebViewClient())
        webVIew.loadUrl(webUrl)
        /*webVIew.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.d("web_view_log", "Should Override called")
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("web_view_log", "Page load finished")
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("web_view_log", "Page load started")
            }

        })*/


        openWebVIewDialogue.show()
        //showToast("SHpwing dialogue")
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