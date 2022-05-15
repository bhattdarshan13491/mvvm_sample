package com.oncobuddy.app.views.fragments

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.HomeScreenBottomNavigationBinding
import com.oncobuddy.app.utils.BottomNavigationViewHelper
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.views.adapters.ViewPagerAdapter

class HomeBottomNavFragment : BaseFragment() {
    
    var prevMenuItem: MenuItem? = null
    private lateinit var binding: HomeScreenBottomNavigationBinding


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        Log.d("crash_log","Home bottom opened")
        setBinding(inflater, container)
        setBottomSectionVisibility()
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

        binding.bottomNavigation.getMenu().clear(); //clear old inflated items.

        if(checkIFPatient()){
            binding.bottomNavigation.inflateMenu(R.menu.menu_bottom_navigation)
        }else if(checkIFDoctor()){
            binding.bottomNavigation.inflateMenu(R.menu.menu_bottom_navigation_doctor)
        }else{
            binding.bottomNavigation.inflateMenu(R.menu.menu_bottom_navigation_cc)
        }


        BottomNavigationViewHelper.disableShiftMode(binding.bottomNavigation)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> binding.viewpager.currentItem = 0
                R.id.action_records -> binding.viewpager.currentItem = 1
                R.id.action_appointments -> binding.viewpager.currentItem = 2
                R.id.action_oncohub -> binding.viewpager.currentItem = 3
                R.id.action_profile -> binding.viewpager.currentItem = 4
            }
            false
        }
        binding.viewpager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem!!.isChecked = false
                } else {
                    binding.bottomNavigation.menu.getItem(0).isChecked = false
                }
                Log.d("page", "onPageSelected: $position")
                binding.bottomNavigation.menu.getItem(position).isChecked = true
                prevMenuItem = binding.bottomNavigation.menu.getItem(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        //Disable ViewPager Swipe
       // binding.viewpager.setOnTouchListener { v, event -> true }

        setupViewPager()
    }

    fun showAppointments() {
        binding.viewpager.currentItem = 2
        Log.d("notification_click_lg", "3.1")
    }

    private fun setBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_screen_bottom_navigation, container, false
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
            binding.bottomNavigation.visibility = View.GONE
        }else{
            binding.bottomNavigation.visibility = View.VISIBLE
        }
    }



    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(fragmentManager)
        adapter.addFragment(PatientHomeScreenNewFragment())

        if(checkIFPatient()){
            adapter.addFragment(MedicalRecordsListingFragmentNew())
        }else{
            var bundle = Bundle()
            bundle.putBoolean(Constants.SHOULD_HIDE_BACK, true)
            var fragment = PatientListingFragment()
            fragment.arguments = bundle
            adapter.addFragment(fragment)
            //adapter.addFragment(PatientListingFragment())
        }

        if(checkIFCareCOmpanion()){
            var bundle = Bundle()
            bundle.putBoolean(Constants.SHOULD_HIDE_BACK, true)
            var fragment = CareCompanionDoctorListingFragment()
            fragment.arguments = bundle
            adapter.addFragment(fragment)
        }else{
            var bundle = Bundle()
            bundle.putBoolean(Constants.SHOULD_HIDE_BACK, true)
            var fragment = AppointmentsListingFragmentNew()
            fragment.arguments = bundle
            adapter.addFragment(fragment)
        }


        var bundle = Bundle()
        bundle.putString(Constants.SOURCE, Constants.VIDEO_LIST)
        bundle.putBoolean(Constants.SHOULD_HIDE_BACK, true)
        var fragment = AllVideosListingFragment()
        fragment.arguments = bundle

        adapter.addFragment(fragment)   // When clicked from here, It will show videos list by default

        if(checkIFDoctor()){
            adapter.addFragment(DoctorProfileFragment())
        }else{
            adapter.addFragment(PatientProfileFragment())
        }
        binding.viewpager.adapter = adapter


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
                    val heightDiff: Int =
                        binding.root.getRootView().getHeight() - (r.bottom - r.top)
                    CommonMethods.showLog("button_height", "home " + heightDiff)
                    if (heightDiff > 300) { // if more than 100 pixels, its probably a keyboard...
                        //ok now we know the keyboard is up...
                        CommonMethods.showLog("button_height", "home button hidden")
                        showHideBottomBar(true)

                    } else {
                        //ok now we know the keyboard is down...
                        CommonMethods.showLog("button_height", "home button visible")
                        showHideBottomBar(false)
                    }
                }
            })
    }

    interface Interaction {
        fun onBackPressed()
    }


}