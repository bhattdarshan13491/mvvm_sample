package com.oncobuddy.app.views.activities

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.FirebaseInstallations
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.ActivityNovSignInPwdBinding
import com.oncobuddy.app.models.injectors.LoginInjection
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseActivity
import com.oncobuddy.app.view_models.LoginViewModel
import retrofit2.Response


class NovSignInPwdActivity : BaseActivity() {
    
    private lateinit var binding : ActivityNovSignInPwdBinding
    private lateinit var fcmToken : String
    private lateinit var loginViewModel: LoginViewModel
    private var shouldShowPassword = false
    


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun init() {
        binding = DataBindingUtil.setContentView(this@NovSignInPwdActivity, R.layout.activity_nov_sign_in_pwd)

        autoFill()

        setupVM()

        setupObservers()

        setClickListeners()

        getFCMToken()
        
    }

    private fun autoFill() {
        Log.d(
            "remember_me_log",
            "email " + FourBaseCareApp.sharedPreferences.getString(
                Constants.PREF_REMEMBER_EMAIL,
                ""
            ).toString()
        )
        binding.etEmail.setText(
            FourBaseCareApp.sharedPreferences.getString(
                Constants.PREF_REMEMBER_EMAIL,
                ""
            ).toString()
        )
        binding.etPassword.setText(
            FourBaseCareApp.sharedPreferences.getString(
                Constants.PREF_REMEMBER_PWD,
                ""
            ).toString()
        )
    }

    private fun setClickListeners() {

        binding.tvSignUp.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.changeActivity(
                    this@NovSignInPwdActivity,
                    NovSignupActivity::class.java,
                    false
                )
            }
        })

        /*if (!isDoubleClick()) {
            CommonMethods.changeActivity(
                this@NovSignInPwdActivity,
                NovSignupActivity::class.java,
                false
            )
        }*/

        binding.ivShowHidePwd.setOnClickListener(View.OnClickListener {
            showHidePassword(binding.etPassword, binding.ivShowHidePwd, shouldShowPassword)
            shouldShowPassword = !shouldShowPassword
        })

        binding.tvSignInOtp.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
              /*  CommonMethods.changeActivity(
                    this@NovSignInPwdActivity,
                    NovSignInActivity::class.java,
                    false
                )*/
                finish()
            }
        })

        binding.tvForgotPassword.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.changeActivity(
                    this@NovSignInPwdActivity,
                    NovForgotPwdActivity::class.java,
                    false
                )
            }
        })

        binding.tvSignIn.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick() && checkInterNetConnection(this) && isValidInput()){
                logEvent("Login with pwd", "Login button clicked")
                var loginInput = com.oncobuddy.app.models.pojo.login_response.LoginInput()
                loginInput.mobileNumber = getTrimmedText(binding.etEmail)
                loginInput.password = getTrimmedText(binding.etPassword)
                loginInput.token = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_FCM_TOKEN, "")
                loginInput.deviceType = Constants.ANDROID
                loginViewModel.retiveLoginData(loginInput)
            }
        })

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
        loginViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        loginViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val loginDataObserver = Observer<Response<LoginResponse?>>{ loginResponseData ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

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

            val gson = Gson()
            val userObj = gson.toJson(loginResponseData.body()?.payLoad)
            Log.d("dob_log","Came here only")
            Log.d("dob_log","Date "+loginResponseData.body()?.payLoad?.profile?.dateOfBirth)
            Log.d("dob_log","Gender "+loginResponseData.body()?.payLoad?.profile?.gender)
            Log.d("dob_log","aadhar number "+loginResponseData.body()?.payLoad?.profile?.aadharNumber)
            Log.d("dob_log","height "+loginResponseData.body()?.payLoad?.profile?.height)
            Log.d("dob_log","weight "+loginResponseData.body()?.payLoad?.profile?.weight)
            Log.d("dob_log","carecode "+loginResponseData.body()?.payLoad?.profile?.careCode)
            Log.d("dob_log","patient type "+loginResponseData.body()?.payLoad?.profile?.patientType)
            Log.d("dob_log","dob "+loginResponseData.body()?.payLoad?.profile?.dateOfBirth)
            Log.d("dob_log","desc "+loginResponseData.body()?.payLoad?.profile?.description)

            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ,userObj)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_AUTH_TOKEN,"Bearer "+loginResponseData.body()?.payLoad?.authToken)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_ID,""+loginResponseData.body()?.payLoad?.userIdd)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_NAME,""+loginResponseData.body()?.payLoad?.firstName+" "+
                    loginResponseData.body()?.payLoad?.lastName)

            if(binding.switchRememberMe.isChecked){
                Log.d("remember_me_log","email saved")
                FourBaseCareApp.savePreferenceDataBoolean(Constants.PREF_REMEMBER_ON,true)
                FourBaseCareApp.savePreferenceDataString(Constants.PREF_REMEMBER_EMAIL,getTrimmedText(binding.etEmail))
                FourBaseCareApp.savePreferenceDataString(Constants.PREF_REMEMBER_PWD,getTrimmedText(binding.etPassword))
            }else{
                Log.d("remember_me_log","email not saved ")
                FourBaseCareApp.savePreferenceDataBoolean(Constants.PREF_REMEMBER_ON,false)
                FourBaseCareApp.savePreferenceDataString(Constants.PREF_REMEMBER_EMAIL,"")
                FourBaseCareApp.savePreferenceDataString(Constants.PREF_REMEMBER_PWD,"")
            }

            Log.d("otp_log", getUserObject().role)

            if (getUserObject().role.equals(Constants.ROLE_PATIENT, ignoreCase = true)) {
                Constants.IS_DOCTOR_LOGGED_IN = false
                CommonMethods.changeActivity(this@NovSignInPwdActivity, PatientLandingActivity::class.java,true, true)
            }else if(getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER, ignoreCase = true)){
                Constants.IS_DOCTOR_LOGGED_IN = false
                if(getUserObject().linkedCareCode == null){
                    CommonMethods.changeActivity(this@NovSignInPwdActivity, NovCGAccountSetupActivity::class.java,true, true)
                }else{
                    CommonMethods.changeActivity(this@NovSignInPwdActivity, PatientLandingActivity::class.java,true, true)
                }
            }
            else if (getUserObject().role.equals(Constants.ROLE_CARE_COMPANION, ignoreCase = true)) {
                Constants.IS_DOCTOR_LOGGED_IN = false
                CommonMethods.changeActivity(this@NovSignInPwdActivity, PatientLandingActivity::class.java,true, true)
            } else if(getUserObject().role.equals(Constants.ROLE_DOCTOR, ignoreCase = true)){
                Constants.IS_DOCTOR_LOGGED_IN = true
                CommonMethods.changeActivity(this@NovSignInPwdActivity, NovDoctorLandingActivity::class.java,true, true)
            }else if(getUserObject().role.equals(Constants.ROLE_NEW_USER, ignoreCase = true)){
                //CommonMethods.changeActivity(this@NovSignInPwdActivity, SelectRoleActivity::class.java,true, true)
            }else{
                CommonMethods.showToast(applicationContext, "You are not yet authorized to use this app. Please contact customer support")
            }

        }else{
            CommonMethods.showToast(applicationContext, "Something went wrong")
        }

        /*CommonMethods.showLog("login_log","Data "+loginResponseData.toString())*/
    }
    private val isViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("login_log", "is_loading is "+isLoading)
        showHideProgress(isLoading,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = Observer<String>{message ->
        Log.d("login_log", "coming here...")
        CommonMethods.showToast(applicationContext, message)
    }

    ////////

    private fun isValidInput() : Boolean {
        if(getTrimmedText(binding.etEmail).isNullOrBlank()){
            showToast(getString(R.string.please_add_mobile_number_or_email))
            return  false
        }else if(getTrimmedText(binding.etPassword).isNullOrBlank()){
            showToast(getString(R.string.validation_enter_password))
            return  false
        }
        return true
    }

    private fun getFCMToken() {
        FirebaseInstallations.getInstance().getToken(true)
            .addOnCompleteListener(OnCompleteListener { task ->
                Log.d("token_log", "2")
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                Log.d("token_log", "2")
                val token = task.result!!.token
                fcmToken = token
                Log.d("token_log", "3 " + token)
            })
    }



}