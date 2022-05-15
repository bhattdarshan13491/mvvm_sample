package com.oncobuddy.app.utils.base_classes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.models.pojo.login_response.LoginDetails
import com.oncobuddy.app.models.pojo.patient_details_by_cg.PatientDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.views.activities.NovSplashActivity
import org.json.JSONObject
import java.util.*

/**
 * Abstract class which is inherited by all child activities.
 * contains all the common, repeatedly used functions like checking internet, checking user role etc
 */
abstract class BaseActivity : AppCompatActivity() {

    private val MAX_CLICK_INTERVAL = 3000
    private var mLastClickTime: Long = 0
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var mixPanel : MixpanelAPI



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Obtain the FirebaseAnalytics instance.
        if(checkInterNetConnection(this)){
            firebaseAnalytics = Firebase.analytics
            mixPanel = MixpanelAPI.getInstance(this, getString(R.string.mix_panel_token))
        }
         //FourBaseCareApp.activityFromApp = this
    }

    fun logScreenViewEventMP(screenName: String){
        if(::mixPanel.isInitialized){
            Log.d("mp_log","activity screen "+screenName)
            val props = JSONObject()
            props.put(Constants.PROPERTY_SCREEN_NAME, screenName)
            mixPanel.track(Constants.EVENT_SCREEN_VIEW, props)
        }
    }

    fun logEvent(screenName: String,eventName: String){
        Log.d("event_log","2")
        if(::firebaseAnalytics.isInitialized){
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                param(FirebaseAnalytics.Param.SCREEN_CLASS, eventName)
            }
            Log.d("event_log","3")
            Log.d("event_log","Event sent "+eventName)
        }
    }

    fun logEvent2(eventName: String){
        Log.d("event_log","2")
        firebaseAnalytics.logEvent("user_click") {
            param("event_name", eventName)
        }
        Log.d("event_log","3")
        Log.d("event_log","Event sent "+eventName)
    }




    abstract fun init()

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

    fun showHideProgressTransparent(
        isShowProgress: Boolean,
        frameLayout: FrameLayout,
        imageView: ImageView,
        progressBar: ProgressBar
    ) {
        if (isShowProgress) {
            frameLayout.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            frameLayout.isEnabled = false
        } else {
            frameLayout.visibility = View.GONE
            progressBar.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            frameLayout.isEnabled = true
        }
    }



    open fun showHidePassword(editText: EditText, passwordIcon: ImageView, shouldShowPassword: Boolean){
        if (shouldShowPassword) {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance())
            passwordIcon.setImageDrawable(getDrawable(R.drawable.ic_hide_pwd))

        } else {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            passwordIcon.setImageDrawable(getDrawable(R.drawable.ic_show_password))
        }
        editText.setSelection(editText.text.length)
    }

    open fun isDoubleClick(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_INTERVAL) {
            Log.d("Returned", "Returned")
            return true
        }
        Log.d("Success", "Success")
        mLastClickTime = SystemClock.elapsedRealtime()
        return false
    }

    fun showToast(message:String){
   /*     val inflater = layoutInflater
        val layout: View = inflater.inflate(
            R.layout.toast_layout,
            findViewById<View>(R.id.toast_layout_root) as ViewGroup
        )

     *//*   val image = layout.findViewById<View>(R.id.image) as ImageView
        image.setImageResource(R.drawable.android)*//*
        val text = layout.findViewById<View>(R.id.text) as TextView
        text.text = "Hello! This is a custom toast!"

        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()*/

        Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
    }

    fun getTrimmedText(editText: EditText): String {
        return editText.text.toString().trim { it <= ' ' }
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
        Toast.makeText(context,"Please check your internet connection", Toast.LENGTH_SHORT).show()
        return false
    }

    fun getUserObject() : LoginDetails {
        val gson = Gson()
        val json: String? = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "")
        val userObj: LoginDetails = gson.fromJson(json, LoginDetails::class.java)
        return userObj
    }

    fun getPatientObjectByCG() : PatientDetails {
        val gson = Gson()
        val json: String? = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_PATIENT_DETAILS_FOR_CG, "")
        val userObj: PatientDetails = gson.fromJson(json, PatientDetails::class.java)
        return userObj
    }

   /* fun getUserId() : Int {
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
    }*/

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

    fun callFunAtFixTime(){

    }

    // Checking for app update
    fun checkForUpdate() {

        val appVersion: String = getAppVersion(this)
        val remoteConfig = FirebaseRemoteConfig.getInstance()

        val minVersion =
            remoteConfig.getString("latest_version_of_app")

        Log.d("update_log","0 current version "+appVersion)
        Log.d("update_log","0 latest version "+minVersion)
        if (!TextUtils.isEmpty(appVersion) && !TextUtils.isEmpty(minVersion) && !TextUtils.equals(
                appVersion,
                minVersion
            )
        ) {
            Log.d("update_log","3")
            onUpdateNeeded(true)


        }
        else {
            Log.d("update_log","4")
            moveForward()
        }
    }

    fun getAppVersion(context: Context): String {
        var result: String? = ""
        try {
            result = context.packageManager
                .getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("TAG", ""+e.message.toString())
        }
        return result?:""
    }

    private fun getAppVersionWithoutAlphaNumeric(result: String): String {
        var version_str = ""
        version_str = result.replace(".", "")
        return version_str
    }

    fun doLogoutProcess(){
        var fcmToken = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_FCM_TOKEN, "")
        FourBaseCareApp.clearPreferenceData()
        FourBaseCareApp.savePreferenceDataString(Constants.PREF_FCM_TOKEN,fcmToken) // re-saving the token
        CommonMethods.restartApp(this@BaseActivity)
    }

    fun doLogoutProcessForUpdate(activity: Activity){

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
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_REMEMBER_EMAIL,pwd)
        }

        val intent = Intent(activity, NovSplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()
        System.exit(0)
    }

    private fun onUpdateNeeded(isMandatoryUpdate: Boolean) {
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle(getString(R.string.update_app))
            .setCancelable(false)
            .setMessage(if (isMandatoryUpdate) getString(R.string.dialog_update_available_message) else "A new version is found on Play store, please update for better usage.")
            .setPositiveButton(getString(R.string.update_now))
            { dialog, which ->
                openAppOnPlayStore(this, null)
                Constants.LOGOUT_NEEDED = true
                /*Timer().schedule(Constants.FUNCTION_DELAY) {
                    //doLogoutProcess()
                }*/
                //doLogoutProcess()
            }

        if (!isMandatoryUpdate) {
            dialogBuilder.setNegativeButton(getString(R.string.later)) { dialog, which ->
                moveForward()
                dialog?.dismiss()
            }.create()
        }
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()
    }

    private fun moveForward() {
        //Toast.makeText(this, "Next Page Intent", Toast.LENGTH_SHORT).show()
    }

    fun openAppOnPlayStore(ctx: Context, package_name: String?) {
        var package_name = package_name
        if (package_name == null) {
            package_name = ctx.packageName
        }
        val uri = Uri.parse("market://details?id=$package_name")
        openURI(ctx, uri, "Play Store not found in your device")
    }

    fun openURI(
        ctx: Context,
        uri: Uri?,
        error_msg: String?
    ) {
        val i = Intent(Intent.ACTION_VIEW, uri)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        if (ctx.packageManager.queryIntentActivities(i, 0).size > 0) {
            ctx.startActivity(i)
        } else if (error_msg != null) {
            Toast.makeText(this, error_msg, Toast.LENGTH_SHORT).show()
        }
    }



}