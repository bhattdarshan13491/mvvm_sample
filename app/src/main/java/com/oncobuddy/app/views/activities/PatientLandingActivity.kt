package com.oncobuddy.app.views.activities

import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.ActivityHomeBinding
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseActivity
import com.oncobuddy.app.views.fragments.BlogDetailsFragment
import com.oncobuddy.app.views.fragments.HomeBottomNavFragment
import com.oncobuddy.app.views.fragments.NovHomeBottomNavFragment

/**
 * Patient landing activity
 * Contains main landing screen for the user type patient
 * @constructor Create empty Patient landing activity
 */

class PatientLandingActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("notification_click_lg", "on create called")
        Log.d("crash_log","Landing activity opened")
        FourBaseCareApp.activityFromApp = this
        init()
        openFragment(NovHomeBottomNavFragment())
    }

    override fun onPause() {
        super.onPause()
        Log.d("activity_log","Pause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("activity_log","Stop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("activity_log","Destroy")
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onNewIntent(intent: Intent?) {
            super.onNewIntent(intent)
            Log.d("notification_click_lg", "new intent called")
            checkIntent(intent)
    }

    private fun checkIntent(intent: Intent?) {
        if (intent != null && intent.hasExtra("notification_type")) {
            Log.d("notification_click_lg", "0")
            var notificationType = intent.getStringExtra("notification_type")
            Log.d("notification_click_lg", "1 " + notificationType)
            if (notificationType.equals("request_callback")) {
                Constants.IS_DOCTOR_LOGGED_IN = true
                Log.d("notification_click_lg", "2")
                val fm: FragmentManager = getSupportFragmentManager()
                var fragment = fm.findFragmentById(R.id.frameLayout) as HomeBottomNavFragment
                if(fragment == null){
                    Log.d("notification_click_lg", "fragment is null")
                    return
                }
                fragment.showAppointments()
                Log.d("notification_click_lg", "3")

            }else if(notificationType.equals("new_blog")){
                Log.d("notification_click_lg", "blog 0 "+intent.getStringExtra(Constants.BLOG_ID))
                var blogDetailsFragment = BlogDetailsFragment()
                var bundle  = Bundle()
                bundle.putString(Constants.BLOG_ID,""+intent.getStringExtra(Constants.BLOG_ID))
                blogDetailsFragment.arguments = bundle
                openFragment(blogDetailsFragment)
            }
        }
    }
    override fun init() {
        setBinding()
        Firebase.messaging.isAutoInitEnabled = true
        checkForUpdate()


    }
    private fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
    }

    fun openFragment(mFragment: Fragment?) {
        val transaction =
            supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, mFragment!!)
        transaction.commit()
    }
}