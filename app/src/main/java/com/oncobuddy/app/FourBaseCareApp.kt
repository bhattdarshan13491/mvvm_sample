package com.oncobuddy.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.util.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast

/**
 * Four base care app
 * Its an application class which is having all the glogbal initializations like shared prefs or firebase crashlytics
 * Also, It initialized Firebase remote config
 * @constructor Create empty Four base care app
 */

open class FourBaseCareApp : Application(){

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)

        if(checkInterNetConnection(this)){
            initFirebaseRemoteConfig()
        }

        if(instance == null){
            instance = this
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
        Toast.makeText(context,"Please check your internet connection", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

    }

    private fun initFirebaseRemoteConfig() {
        FirebaseApp.initializeApp(this)
        FirebaseRemoteConfig.getInstance().apply {
            //set this during development
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            setConfigSettingsAsync(configSettings)
            //set this during development

            setDefaultsAsync(R.xml.remote_config_defaults)
            fetchAndActivate().addOnCompleteListener { task ->
                val updated = task.result
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("remote_update_log", "Config params updated: $updated")
                } else {
                    Log.d("remote_update_log", "Config params updated: $updated")
                }
            }
        }


    }

    open fun isNetworkConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }





    companion object{
        lateinit var activityFromApp: AppCompatActivity
        lateinit var sharedPreferences: SharedPreferences
        var instance: FourBaseCareApp? = null
       /* lateinit var firebaseAnalytics: FirebaseAnalytics

        open fun getFirebaseAnalytics() : FirebaseAnalytics{

            if(firebaseAnalytics == null){
                firebaseAnalytics = Firebase.analytics
            }
            return firebaseAnalytics
        }*/

        @JvmStatic
        open fun hasNetwork(): Boolean {
            return instance!!.isNetworkConnected()
        }

        @JvmStatic
        open fun savePreferenceDataString(key: String?, value: String?) {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.commit()
        }

        open fun savePreferenceDataInteger(key: String?, value: Int?) {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putInt(key, value!!)
            editor.commit()
        }

        @JvmStatic
        open fun savePreferenceDataBoolean(key: String?, value: Boolean?) {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean(key, value!!)
            editor.commit()
        }

        open fun savePreferenceDataArray(
            key: String?,
            value: ArrayList<*>
        ) {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString(key, value.toString())
            editor.commit()
        }

        open fun clearPreferenceData() {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.commit()
        }


    }
}