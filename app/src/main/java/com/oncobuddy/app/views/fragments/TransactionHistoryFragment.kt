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
import com.oncobuddy.app.databinding.FragmentTransactionHistoryBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.patient_transactions.PatientTransactionsResponse
import com.oncobuddy.app.models.pojo.patient_transactions.Transaction
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.TransactionHistoryAdapter

/**
 * Transaction history fragment
 * It shows list of all transactions
 * @constructor Create empty Transaction history fragment
 */

class TransactionHistoryFragment : BaseFragment(), TransactionHistoryAdapter.Interaction {

    private lateinit var binding: FragmentTransactionHistoryBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var transactionsList: ArrayList<Transaction>
    private lateinit var transactionsAdapter: TransactionHistoryAdapter


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
            R.layout.fragment_transaction_history, container, false
        )

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })



        binding.layoutHeader.tvTitle.text = "Transaction History"
        setupVM()
        setupObservers()
        getTransactionsListingFromServer()

    }

    private fun getTransactionsListingFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.callGetPatientTransactions(
                getUserAuthToken()
            )
        }
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        profileViewModel.patientTransactionsData.observe(this, patientTransactionsListResponseObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val isViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("appointment_log", "is_loading is "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = Observer<String>{message ->
        Log.d("appointment_log", "Error "+message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }




    private val patientTransactionsListResponseObserver = Observer<PatientTransactionsResponse> { responseObserver ->
         transactionsList = ArrayList()

        if (responseObserver.isSuccess) {
            transactionsList.addAll(responseObserver.payLoad)
            setRecyclerView(transactionsList)
        }

        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private fun setRecyclerView(list: List<Transaction>) {

        transactionsList = ArrayList()
        transactionsList.addAll(list)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(FourBaseCareApp.activityFromApp)
            transactionsAdapter = TransactionHistoryAdapter(transactionsList, this@TransactionHistoryFragment)
            adapter = transactionsAdapter
            transactionsAdapter.submitList(list)
        }
    }

    override fun onSpecialistSelected(position: Int, item: Transaction, view: View) {
        //TODO("Not yet implemented")
    }


}