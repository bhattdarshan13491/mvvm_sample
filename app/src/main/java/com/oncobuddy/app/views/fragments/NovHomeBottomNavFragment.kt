package com.oncobuddy.app.views.fragments

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.HomeScreenBottomNavigationNovBinding
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.views.adapters.SampleFragmentPagerAdapter

class NovHomeBottomNavFragment : BaseFragment() {
    
    private lateinit var binding: HomeScreenBottomNavigationNovBinding

    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        Log.d("crash_log","Home bottom opened")
        setBinding(inflater, container)
        val pagerAdapter =
            SampleFragmentPagerAdapter(
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
                }else if(fragment is AddOrEditRecordFragment){
                    fragment.handleBackPress()
                    Log.d("fragment_back_log","add or eddit fragment")
                }
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

    fun showAppointments() {
        binding.viewpager.currentItem = 2
        Log.d("notification_click_lg", "3.1")
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





    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.d("visible_hint","visiblehint Called "+isVisibleToUser)

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        Log.d("visible_hint","hidden Called "+isHidden)
    }

    private fun setBottomSectionVisibility() {
        binding.root.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    binding.root.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int = binding.root.getRootView().getHeight() - (r.bottom - r.top)
                    CommonMethods.showLog("nov home button_height", "" + heightDiff)
                    if (heightDiff > 300) { // if more than 100 pixels, its probably a keyboard...
                        //ok now we know the keyboard is up...
                        CommonMethods.showLog("button_height", " nov home button hidden")
                        showHideBottomBar(true)

                    } else {
                        //ok now we know the keyboard is down...
                        CommonMethods.showLog("button_height", "nov home button visible")
                        showHideBottomBar(false)
                    }
                }
            })
    }

    interface Interaction {
        fun onBackPressed()
    }


}