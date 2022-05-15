package com.oncobuddy.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.HomeScreenBottomNavigationNovBinding
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.views.adapters.DoctorFragmentPagerAdapter
import com.oncobuddy.app.views.adapters.SampleFragmentPagerAdapter

class NovDoctorBottomNavFragment : BaseFragment() {
    
    private lateinit var binding: HomeScreenBottomNavigationNovBinding


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        Log.d("crash_log","Home bottom opened")
        setBinding(inflater, container)
        val pagerAdapter =
            DoctorFragmentPagerAdapter(
                FourBaseCareApp.activityFromApp.supportFragmentManager,
                FourBaseCareApp.activityFromApp
            )
        binding.viewpager.setAdapter(pagerAdapter)



        binding.tabLayout.setupWithViewPager(binding.viewpager)

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
            tab!!.customView = pagerAdapter.getTabView(i)
        }

        setBottomSectionVisibility()
        binding.viewpager.offscreenPageLimit = 1

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fm: FragmentManager = FourBaseCareApp.activityFromApp.getSupportFragmentManager()

                var fragment = fm.findFragmentById(R.id.frameLayout)
                Log.d("fragment_back_log","0")

                if(fragment is VideoQueueByDoctorFragment){
                    fragment.handleBackPress()
                    Log.d("fragment_back_log","1")
                }else if(fragment is PatientHomeScreenNewFragment){
                    fragment.handleScrollTOp()
                    Log.d("fragment_back_log","2")
                }/*else if(fragment is AddOrEditRecordFragment){
                    fragment.handleBackPress()
                    Log.d("fragment_back_log","add or eddit fragment")
                }*/
                else{
                    Log.d("fragment_back_log","3 "+fragment.toString())
                    val count = fm.backStackEntryCount
                    if(count > 0){
                        fm.popBackStackImmediate()
                    }else{
                        if(binding.viewpager.currentItem == 0){
                            FourBaseCareApp.activityFromApp.finish()
                        }else{
                            binding.viewpager.setCurrentItem(0,true)
                        }
                    }
                }
            }
        })

    }



    private fun setBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_screen_bottom_navigation_nov, container, false
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)

        return binding.root
    }

    fun showHideBottomBar(shouldHide : Boolean){
        if(shouldHide){
            binding.tabLayout.visibility = View.GONE
        }else{
            binding.tabLayout.visibility = View.VISIBLE
        }
    }

    fun changeTab(pos: Int){
        showToast(FourBaseCareApp.activityFromApp,"Moved to "+pos)

    }

    private fun setBottomSectionVisibility() {
        binding.root.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    binding.root.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int =
                        binding.root.getRootView().getHeight() - (r.bottom - r.top)
                    CommonMethods.showLog("button_height", "" + heightDiff)
                    if (heightDiff > 300) {
                        showHideBottomBar(true)
                    } else {
                        showHideBottomBar(false)
                    }
                }
            })
    }

    interface Interaction {
        fun onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        val lbm = LocalBroadcastManager.getInstance(FourBaseCareApp.activityFromApp)
        lbm.registerReceiver(onPatientScreen, IntentFilter("action_open_patients"))
        lbm.registerReceiver(onApptScreen, IntentFilter("action_open_appointments"))

    }

    override fun onPause() {
        super.onPause()
        if(onPatientScreen != null){
            LocalBroadcastManager.getInstance(FourBaseCareApp.activityFromApp).unregisterReceiver(onPatientScreen)
            LocalBroadcastManager.getInstance(FourBaseCareApp.activityFromApp).unregisterReceiver(onApptScreen)
            Log.d("notification_data_log", "Receiver destroyed")
        }
    }

    private val onPatientScreen: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // intent can contain anydata
            binding.viewpager.setCurrentItem(1,true)
        }
    }

    private val onApptScreen: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // intent can contain anydata
            binding.viewpager.setCurrentItem(2,true)
        }
    }


}