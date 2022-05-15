package com.oncobuddy.app.views.activities

import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.oncobuddy.app.FourBaseCareApp.Companion.savePreferenceDataBoolean
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.ActivityIntroductionBinding
import com.oncobuddy.app.models.injectors.LoginInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.initial_login.LoginInput
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseActivity
import com.oncobuddy.app.view_models.LoginViewModel
import retrofit2.Response

/**
 * Nov introduction screens activity
 * Landing screens after signup and basic account details screen.
 * @constructor Create empty Nov introduction screens activity
 */
class NovIntroductionScreensActivity : BaseActivity() {
    private lateinit var binding : ActivityIntroductionBinding
    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    private lateinit var layouts: IntArray
    private var source = Constants.ROLE_PATIENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("event_log","0")
        logEvent("Tutorial screen", "Screen viewed")
        Log.d("event_log","1")
        init()
        //checkForUpdate()
        logScreenViewEventMP("Introduction screen")
    }

    override fun init() {

        binding = DataBindingUtil.setContentView(this@NovIntroductionScreensActivity, R.layout.activity_introduction)
        //binding.titleContainer.tvRightNav.setOnClickListener { binding.viewPager.currentItem = 2 }

        if(intent.hasExtra(Constants.SOURCE)){
            source = intent.getStringExtra(Constants.SOURCE).toString()
        }else{
            source = Constants.ROLE_PATIENT
        }

        if(source.equals(Constants.ROLE_DOCTOR)){
            layouts = intArrayOf(
                R.layout.nov_dr_intro_slide_1,
                R.layout.nov_dr_intro_slide_2,
                R.layout.nov_dr_intro_slide_3
            )
        }else{
            layouts = intArrayOf(
                R.layout.nov_intro_slide_1,
                R.layout.nov_intro_slide_2,
                R.layout.nov_intro_slide_3,
                R.layout.nov_intro_slide_4
            )
        }
        myViewPagerAdapter = MyViewPagerAdapter()
        binding.viewPager.adapter = myViewPagerAdapter
        binding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        binding.pagerIndicator.setViewPager(binding!!.viewPager)

        binding.relContinue.setOnClickListener(View.OnClickListener {
            if(source.equals(Constants.ROLE_DOCTOR)){
                CommonMethods.changeActivity(this@NovIntroductionScreensActivity, NovDoctorLandingActivity::class.java,true, true)
            }else{
                CommonMethods.changeActivity(this@NovIntroductionScreensActivity, PatientLandingActivity::class.java,true, true)
            }

        })

    }



    //  viewpager change listener
    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.size - 1) {
                //gotoLoginActivity();
                //binding!!.pagerIndicator.visibility = View.GONE
                binding.relContinue.visibility = View.VISIBLE
            } else {
                binding!!.pagerIndicator.visibility = View.VISIBLE
                binding.relContinue.visibility = View.INVISIBLE
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }


    /*private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }
*/
    inner class MyViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null
        override fun instantiateItem(
            container: ViewGroup,
            position: Int
        ): Any {
            layoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view =
                layoutInflater!!.inflate(layouts[position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(
            view: View,
            obj: Any
        ): Boolean {
            return view === obj
        }

        override fun destroyItem(
            container: ViewGroup,
            position: Int,
            `object`: Any
        ) {
            val view = `object` as View
            container.removeView(view)
        }
    }


}