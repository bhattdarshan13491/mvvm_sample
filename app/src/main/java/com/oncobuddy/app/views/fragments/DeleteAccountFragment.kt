package com.oncobuddy.app.views.fragments


import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R

import com.oncobuddy.app.databinding.FragmentDeleteAccountBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.DeleteAccountInput
import com.oncobuddy.app.models.pojo.education_degrees.AddEducationInput
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel

/**
 * Delete account fragment
 * User can delete account from here
 * @constructor Create empty Delete account fragment
 */

class DeleteAccountFragment : BaseFragment() {

    private lateinit var binding: FragmentDeleteAccountBinding
    private lateinit var profileViewModel: ProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)

        return binding.root
    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_delete_account, container, false
        )

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.layoutHeader.tvTitle.setText("Delete Account")

        binding.relContinue.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())deleteAccount()
        })
        showHideBottomButton()
        setupVM()
        setupObservers()

    }

    private fun deleteAccount() {
        if(getTrimmedText(binding.edName).isNullOrEmpty()){
            showToast(FourBaseCareApp.activityFromApp, "Please enter reason title")
        }else if(getTrimmedText(binding.edReason).isNullOrEmpty()){
            showToast(FourBaseCareApp.activityFromApp, "Please enter reason description")
        } else{
            showLogoutDialog()
        }

    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

    }
    private fun setupObservers() {
        profileViewModel.deleteAccountResponse.observe(this,deleteAccountResponseObserver )
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)
    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(context!!, message)
    }

    private val deleteAccountResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        if(responseObserver.success) {
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Account deleted successfully!")
            doLogoutProcess()

        }
        else
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Something went wrong while adding education")

        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private fun showLogoutDialog() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(FourBaseCareApp.activityFromApp)
                .setMessage("The action you are going to perform cannot be undone. Are you sure?")
                .setPositiveButton(R.string.yes,
                    DialogInterface.OnClickListener { dialogInterface, which ->
                        callDeleteAccountApi()
                    }).setNegativeButton(R.string.no,
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        builder.show()
    }

    private fun callDeleteAccountApi() {
        var deleteAccountInput = DeleteAccountInput()
        deleteAccountInput.title = getTrimmedText(binding.edName)
        deleteAccountInput.reason = getTrimmedText(binding.edReason)
        profileViewModel.callDeleteAccount(getUserObject().authToken, deleteAccountInput)
    }

    private fun showHideBottomButton() {
        binding.root.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    binding.root.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int =
                        binding.root.getRootView().getHeight() - (r.bottom - r.top)
                    CommonMethods.showLog("button_height", "" + heightDiff)
                    if (heightDiff > 300) { // if more than 100 pixels, its probably a keyboard...
                        //ok now we know the keyboard is up...
                        CommonMethods.showLog("button_height", "button hidden")
                        binding.relContinue.setVisibility(View.GONE)

                    } else {
                        //ok now we know the keyboard is down...
                        CommonMethods.showLog("button_height", "button visible")
                        binding.relContinue.setVisibility(View.VISIBLE)
                    }
                }
            })
    }



}