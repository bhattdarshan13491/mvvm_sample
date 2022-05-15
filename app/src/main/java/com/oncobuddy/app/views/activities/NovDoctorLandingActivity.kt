package com.oncobuddy.app.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.ActivityHomeBinding
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseActivity
import com.oncobuddy.app.views.fragments.BlogDetailsFragment
import com.oncobuddy.app.views.fragments.HomeBottomNavFragment
import com.oncobuddy.app.views.fragments.NovDoctorBottomNavFragment

/**
 * Nov doctor landing activity
 *
 * @constructor Create empty Nov doctor landing activity
 */
class NovDoctorLandingActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("notification_click_lg", "on create called")
        Log.d("crash_log", "Landing activity opened")
        FourBaseCareApp.activityFromApp = this
        init()
        openFragment(NovDoctorBottomNavFragment())


    }

    override fun onResume() {
        Log.d("notification_click_lg", "onresume called")
        Log.d("android_11_log", "Came here " + Constants.LOGOUT_NEEDED)
        // LocalBroadcastManager.getInstance(this@PatientLandingActivity).registerReceiver(mMessageReceiver, IntentFilter(Constants.ACTION_VIDEO_NOTIFICATION))
        if (Constants.LOGOUT_NEEDED) {
            doLogoutProcessForUpdate(this)
            Constants.LOGOUT_NEEDED = false
        }
        checkIntent(intent)
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
                val fm: FragmentManager = supportFragmentManager
                var fragment = fm.findFragmentById(R.id.frameLayout) as HomeBottomNavFragment
                if (fragment == null) {
                    Log.d("notification_click_lg", "fragment is null")
                    return
                }
                fragment.showAppointments()
                Log.d("notification_click_lg", "3")

            } else if (notificationType.equals("new_blog")) {
                Log.d("notification_click_lg", "blog 0 " + intent.getStringExtra(Constants.BLOG_ID))
                var blogDetailsFragment = BlogDetailsFragment()
                var bundle = Bundle()
                bundle.putString(Constants.BLOG_ID, "" + intent.getStringExtra(Constants.BLOG_ID))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}