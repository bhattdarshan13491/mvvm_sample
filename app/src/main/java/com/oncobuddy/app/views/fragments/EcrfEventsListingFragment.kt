package com.oncobuddy.app.views.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentEcrfEventsListBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.ecrf_events.EcrfventsResponse
import com.oncobuddy.app.models.pojo.ecrf_events.EventsItem
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.EcrdEventsAdapter
import java.util.*
import kotlin.collections.ArrayList


class EcrfEventsListingFragment : BaseFragment(), EcrdEventsAdapter.Interaction{

    private lateinit var binding : FragmentEcrfEventsListBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var ecrfEventListingAdapter: EcrdEventsAdapter
    private lateinit var ecrfEventsList: ArrayList<EventsItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        init(inflater, container)
        return binding.root
    }

    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_ecrf_events_list, container, false
        )
        setClickListeners()
        setupVM()
        getDataFromServer()
        setupObservers()

        /*ecrfEventsList = arguments?.getParcelableArrayList<Doctor>(Constants.DOCTOR_LIST) as ArrayList<Doctor>
        setRecyclerView(ecrfEventsList)*/
    }

    private fun setupObservers() {
        profileViewModel.ecrfEventsResonseData.observe(this, responseObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)

    }

    private val responseObserver =
        androidx.lifecycle.Observer<EcrfventsResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()
            if(responseObserver !=null && responseObserver.payLoad != null){
                ecrfEventsList = ArrayList()
                ecrfEventsList.addAll(responseObserver?.payLoad!!.events)
                setRecyclerView(ecrfEventsList)
                binding.tvNoData.visibility = View.GONE
                showHideNoData(false)
            }else{
                showToast(FourBaseCareApp.activityFromApp,"Patient not found on ECRF")
                showHideNoData(true)
            }

        }

        private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun getDataFromServer() {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
           /* Timer().schedule(Constants.FUNCTION_DELAY) {
                profileViewModel.getEcrfEvents(
                    getUserIdd().toString(),
                    getUserAuthToken()

                )
            }*/

        }

    }


    private val isViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("appointment_log", "is_loading is "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = Observer<String>{message ->
        Log.d("appointment_log", "Error "+message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
        showHideNoData(true)
    }

    private fun setClickListeners() {

        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })


    }




    private fun showHideNoData(shouldShowNoData: Boolean){
        if(shouldShowNoData){
            binding.tvNoData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            //binding.linAppointmentsContainer.visibility = View.GONE
            //binding.ivStartSearch.visibility = View.GONE
        }else{
            binding.tvNoData.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            //binding.linAppointmentsContainer.visibility = View.VISIBLE
            //binding.ivStartSearch.visibility = View.VISIBLE
        }
    }


    private fun setRecyclerView(list: List<EventsItem>?) {

        val arrayList = list?.let { ArrayList<EventsItem>(it) }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            ecrfEventListingAdapter = EcrdEventsAdapter(arrayList!!, this@EcrfEventsListingFragment)
            adapter = ecrfEventListingAdapter
            ecrfEventListingAdapter.submitList(arrayList)


        }
    }

    /*override fun onItemSelected(position: Int, item: Doctor, view: View) {
        CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
        val intent = Intent(context, AddOrEditAppointmentFragment::class.java)
        intent.putExtra(Constants.DOCTOR_DATA, item)
        targetFragment!!.onActivityResult(Constants.FRAGMENT_SELECT_DOCTOR_RESULT, Activity.RESULT_OK, intent)
        fragmentManager!!.popBackStack()
    }*/

    override fun onSelected(position: Int, item: EventsItem, view: View) {
        //TODO("Not yet implemented")
    }


}