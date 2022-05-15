package com.oncobuddy.app.utils.base_classes

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.models.pojo.login_response.LoginDetails
import com.oncobuddy.app.models.pojo.patient_details_by_cg.PatientDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.views.fragments.FullScreenImageFragment
import org.json.JSONObject

/**
 * Base fragment
 * An abstract class which is exttended by all other fragment classes
 * @constructor Create empty Base fragment
 */

abstract class BaseFragment : Fragment() {

    private val MAX_CLICK_INTERVAL = 1000
    private var mLastClickTime: Long = 0
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var mixPanel : MixpanelAPI

    abstract fun init(inflater: LayoutInflater, container: ViewGroup?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        if(FourBaseCareApp.activityFromApp != null){
            mixPanel = MixpanelAPI.getInstance(FourBaseCareApp.activityFromApp, getString(R.string.mix_panel_token))
        }

    }

    fun openFullScreenFragment(imgUrl: String) {
        var bundle = Bundle()
        bundle.putString(Constants.IMAGE_URL, imgUrl)
        var fullScreenFragment = FullScreenImageFragment()
        fullScreenFragment.arguments = bundle
        CommonMethods.addNextFragment(
            FourBaseCareApp.activityFromApp,
            fullScreenFragment, this, false
        )
    }

    fun logScreenViewEventMP(screenName: String){
        if(::mixPanel.isInitialized){
            val props = JSONObject()
            props.put(Constants.PROPERTY_SCREEN_NAME, screenName)
            mixPanel.track(Constants.EVENT_SCREEN_VIEW, props)
            Log.d("mp_log","screen view event sent "+screenName)
        }else{
            Log.d("mp_log","Mixpanel not initialized")
        }

    }


    fun showHideProgress(
        isShowProgress: Boolean,
        progressBar: FrameLayout
    ) {
        if (isShowProgress) {
            progressBar.visibility = View.VISIBLE
            progressBar.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            progressBar.isEnabled = true
        }
    }

    open fun isDoubleClick(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_INTERVAL) {
            Log.d("Returned", "Returned Fragment")
            return true
        }
        Log.d("Success", "Success")
        mLastClickTime = SystemClock.elapsedRealtime()
        return false
    }

    fun showToast(context: Context, message:String){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

    fun getResourceColor(color : Int) : Int{
        return ContextCompat.getColor(FourBaseCareApp.activityFromApp,color)
    }

    fun getResourceDrawable(drawableId : Int) : Drawable? {
        return ContextCompat.getDrawable(FourBaseCareApp.activityFromApp,drawableId)
    }

    fun getTrimmedText(editText: EditText): String {
        return editText.text.toString().trim { it <= ' ' }
    }

    fun getUserObject() : LoginDetails {
        val gson = Gson()
        val json: String? = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "")
        val userObj: LoginDetails = gson.fromJson(json, LoginDetails::class.java)
        return userObj
    }

    fun getPatientObjectByCG() : PatientDetails? {
        if(FourBaseCareApp.sharedPreferences.getString(Constants.PREF_PATIENT_DETAILS_FOR_CG, "").isNullOrEmpty()){
            return  null
        }
        val gson = Gson()
        val json: String? = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_PATIENT_DETAILS_FOR_CG, "")
        val userObj: PatientDetails = gson.fromJson(json, PatientDetails::class.java)
        return userObj
    }

    fun checkIFDoctor() : Boolean {
        return getUserObject().role.equals(Constants.ROLE_DOCTOR)
    }

    fun checkIFPatient() : Boolean {
        return getUserObject().role.equals(Constants.ROLE_PATIENT) || getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER)
    }

    fun checkIFCareCOmpanion() : Boolean {
        return getUserObject().role.equals(Constants.ROLE_CARE_COMPANION)
    }


    fun getUserIdd() : Int {
        if(!FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "").isNullOrEmpty()) {
            val gson = Gson()
            val json: String? =
                FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "")
            val userObj: LoginDetails = gson.fromJson(json, LoginDetails::class.java)
            Log.d("user_id","id "+userObj.userIdd)
            return userObj.userIdd
        }else{
            return  -1
        }
    }

    fun getUserAuthToken() : String {
        if(!FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "").isNullOrEmpty()) {
            val gson = Gson()
            val json: String? =
                FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "")
            val userObj: LoginDetails = gson.fromJson(json, LoginDetails::class.java)
            return userObj.authToken
        }else{
            return  ""
        }
    }

    fun checkInterNetConnection(context: Context) : Boolean{

        val connectivityManager =  context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        Toast.makeText(context,getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show()
        return false
    }

    fun doLogoutProcess(){
        var fcmToken = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_FCM_TOKEN, "")

        var shouldRememberCredentials = FourBaseCareApp.sharedPreferences.getBoolean(Constants.PREF_REMEMBER_ON, false)
        var email = ""
        var pwd = ""
        Log.d("remember_me_log","remember credentials "+shouldRememberCredentials)
        if(shouldRememberCredentials){
            email = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_REMEMBER_EMAIL, "").toString()
            pwd = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_REMEMBER_PWD, "").toString()
            Log.d("remember_me_log","existing email "+email)
        }

        FourBaseCareApp.clearPreferenceData()

        FourBaseCareApp.savePreferenceDataString(Constants.PREF_FCM_TOKEN,fcmToken) // re-saving the token

        if(shouldRememberCredentials){
            Log.d("remember_me_log","email saved b4 logout "+email)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_REMEMBER_EMAIL,email)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_REMEMBER_PWD,pwd)
        }

        CommonMethods.restartApp(FourBaseCareApp.activityFromApp)
    }

    fun checkIfDeviceNeedsOverlayPermission() : Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!Settings.canDrawOverlays(FourBaseCareApp.activityFromApp)) {
                return true
            }else{
                return false
            }
        }else{
            return false
        }
    }
    fun requestOverLayPermission() {
        // Check if Android P or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            )
            startActivityForResult(
                intent,
                140
            )
        }
    }

    fun logUserClick(eventName: String){
        /*Log.d("event_log","2")
        firebaseAnalytics.logEvent("user_click") {
            param("event_name", eventName)
        }
        Log.d("event_log","3")
        Log.d("event_log","Click Event sent "+eventName)*/
    }



    fun shareMultipleFilesTest(uris: ArrayList<Uri>) {
       try {
            val share = Intent(Intent.ACTION_SEND_MULTIPLE)
            share.type = "application/pdf"
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            share.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            share.putExtra(Intent.EXTRA_STREAM, uris)
            share.putExtra(Intent.EXTRA_SUBJECT, "Multiple files shared from Oncobuddy")
            share.putExtra(
               Intent.EXTRA_TEXT,
               """
                    Hello! I am sharing these files using Oncobuddy app. Please download the app using following link.
                    
                    https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                    """.trimIndent()
           )
           startActivity(share)
            Log.d("multiple_share","Done")
        } catch (e: Exception) {
            Log.d("multiple_share","Error while sharing "+e.toString())
        }
    }

    fun getRandomString() : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..8)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun changeProfileCOmpletionLevel(level:Int) {
        var userObj = getUserObject()
        userObj.profile.profileCompletionLevel = level
        val gson = Gson()
        val userStr = gson.toJson(userObj)
        FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userStr)
    }


}