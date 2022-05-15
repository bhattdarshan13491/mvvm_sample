package com.oncobuddy.app.views.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentAlliedCareBinding
import com.oncobuddy.app.databinding.FragmentAppHelpVideosListBinding
import com.oncobuddy.app.databinding.FragmentChatListBinding
import com.oncobuddy.app.databinding.FragmentContactSupportBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.*
import com.oncobuddy.app.models.pojo.add_care_taker.AddCareTakerResponse
import com.oncobuddy.app.models.pojo.registration_process.AddCareCompanionInput
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.activities.AppHelpVideoViewerActivity
import com.oncobuddy.app.views.adapters.AlliedCategoriesAdapter
import com.oncobuddy.app.views.adapters.AppHelpVideosAdapter
import com.oncobuddy.app.views.adapters.ChatListAdapter
import com.oncobuddy.app.views.adapters.NovChatAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Contact support fragment
 * It sends queries entered by the user to the care team
 * @constructor Create empty Contact support fragment
 */

class ContactSupportFragment : BaseFragment(){

    private lateinit var binding : FragmentContactSupportBinding
    private lateinit var profileViewModel: ProfileViewModel

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
            R.layout.fragment_contact_support, container, false
        )
        setClickListeners()
        binding.layoutHeader.tvTitle.text = "Customer Support"
        setupVM()
        setupObservers()
    }
    private fun setupVM() {
        profileViewModel = ViewModelProvider(
                this,
                ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        profileViewModel.complainyResonseData.observe(this, complaintResponseObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }


    private fun setClickListeners() {
        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.tvSUbmit.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp) && isValidInput()){
                sendCOmplaintRequest()
            }
        })

        binding.tvBackToHome.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })
    }

    private fun sendCOmplaintRequest() {
        var complaintInput = ComplaintInput()
        complaintInput.title = getTrimmedText(binding.etReason)
        complaintInput.notes = getTrimmedText(binding.etNotes)
        profileViewModel.callSendComplaint(getUserObject().authToken, complaintInput)
    }

    private val complaintResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        if(responseObserver.success){
            showToast(FourBaseCareApp.activityFromApp, "Complaint registered successfully!")
            showCOmplaintRegisteredView()
        }else{
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Something went wrong while sending complaint")
        }

        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private fun showCOmplaintRegisteredView() {
        binding.linFOrum.visibility = View.GONE
        binding.tvReasonVal.text = getTrimmedText(binding.etReason)
        binding.tvNotesVal.text = getTrimmedText(binding.etNotes)
        binding.linResponse.visibility = View.VISIBLE
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }


    private fun isValidInput() : Boolean {
        Log.d("cg_log", "0")
        if (getTrimmedText(binding.etReason).isNullOrBlank()) {
            showToast(FourBaseCareApp.activityFromApp, "Please enter reason")
            return false
        }else if (getTrimmedText(binding.etNotes).isNullOrBlank()) {
            showToast(FourBaseCareApp.activityFromApp, "Please enter notes")
            return false
        }
        return true
    }

}