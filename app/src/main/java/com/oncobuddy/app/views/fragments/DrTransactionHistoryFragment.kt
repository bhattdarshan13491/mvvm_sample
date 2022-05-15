package com.oncobuddy.app.views.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R

import com.oncobuddy.app.databinding.LayoutTransactionHistoryBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.profile.WalletTransaction
import com.oncobuddy.app.models.pojo.profile.WalletTransactionsResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.WalletTransactionsAdapter

class DrTransactionHistoryFragment : BaseFragment() {

    private lateinit var binding: LayoutTransactionHistoryBinding
    private lateinit var transactions : ArrayList<WalletTransaction>
    private lateinit var creditedTransactions : ArrayList<WalletTransaction>
    private lateinit var debitedTransactions : ArrayList<WalletTransaction>
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var walletTransactionsListingAdapter: WalletTransactionsAdapter

    private val TIME_SLOT = 0
    private val BANK_DETAILS = 1
    private var IS_SHOWING_TIME_SLOT = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)

        return binding.root
    }

    private fun setSelectionColor(category : Int){
        if(category == TIME_SLOT){
            binding.ivUpcomingLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.reports_blue_title))
            binding.tvUpcomingAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                ))

        }else{
            binding.ivUpcomingLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.nov_line_gray))
            binding.tvUpcomingAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                ))
        }

        if(category == BANK_DETAILS){
            binding.ivPastLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.reports_blue_title))
            binding.tvPastAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                ))
        }else{
            binding.ivPastLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.gray))
            binding.tvPastAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                ))
        }

    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
        profileViewModel.walletTransactionsResponse.observe(this, walletTransactionsResponseObserver)
    }

    private fun getWalletTransactionsFromServer(){
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.getWalletTransactions(
                getUserAuthToken()
            )
        }
    }

    private val walletTransactionsResponseObserver = androidx.lifecycle.Observer<WalletTransactionsResponse> {
        if(it?.isSuccess == true){
            transactions = ArrayList()
            creditedTransactions = ArrayList()
            debitedTransactions = ArrayList()
            transactions.addAll(it?.transactions)

            if(!transactions.isNullOrEmpty()){

                for(transactionObj in transactions){
                    if(transactionObj.deposit){
                        creditedTransactions.add(transactionObj)
                    }else{
                        debitedTransactions.add(transactionObj)
                    }
                }
                Log.d("transactioon_log","Debited "+debitedTransactions.size)
                Log.d("transactioon_log","Credited "+creditedTransactions.size)

                binding.rvTransactions.apply {
                    layoutManager = LinearLayoutManager(activity)
                    walletTransactionsListingAdapter = WalletTransactionsAdapter(creditedTransactions)
                    adapter = walletTransactionsListingAdapter
                }
            }


        }
    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_transaction_history, container, false
        )
        binding.tvWalletBalance.setText(FourBaseCareApp.sharedPreferences.getString(Constants.PREF_WALLET_BALANCE,"0.0"))
        setupCLickListeners()
        setupVM()
        setupObservers()
        getWalletTransactionsFromServer()
    }

    private fun setupCLickListeners() {
        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.linTimeSlot.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                setSelectionColor(TIME_SLOT)
                IS_SHOWING_TIME_SLOT = true
                showSectionPerSelection()
            }
        })

        binding.linFeeBankDetails.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                setSelectionColor(BANK_DETAILS)
                IS_SHOWING_TIME_SLOT = false
                showSectionPerSelection()
            }

        })
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private fun showSectionPerSelection() {

        if (IS_SHOWING_TIME_SLOT) {
            binding.rvTransactions.apply {
                layoutManager = LinearLayoutManager(activity)
                walletTransactionsListingAdapter = WalletTransactionsAdapter(creditedTransactions)
                adapter = walletTransactionsListingAdapter
            }
        } else {
            binding.rvTransactions.apply {
                layoutManager = LinearLayoutManager(activity)
                walletTransactionsListingAdapter = WalletTransactionsAdapter(debitedTransactions)
                adapter = walletTransactionsListingAdapter
            }
        }
    }


}