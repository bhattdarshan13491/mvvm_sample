package com.oncobuddy.app.views.fragments


import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentDoctorProfileNewBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.emergency_contacts.EmergencyContact
import com.oncobuddy.app.models.pojo.login_response.LoginDetails
import com.oncobuddy.app.models.pojo.profile.WalletBalanceResponse
import com.oncobuddy.app.models.pojo.profile.WalletTransaction
import com.oncobuddy.app.models.pojo.profile.WalletTransactionsResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.utils.custom_views.FragmentModalBottomSheet
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.WalletTransactionsAdapter
import kotlinx.android.synthetic.main.fragment_patient_home_screen.*

class DoctorProfileFragment : BaseFragment(){

    private lateinit var binding: FragmentDoctorProfileNewBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var userObj : LoginDetails
    private lateinit var globalEmergencyContact: EmergencyContact
    private var walletBalance : Double = 0.0
    private lateinit var transactions : ArrayList<WalletTransaction>
    private lateinit var walletTransactionsDialog: Dialog
    private lateinit var walletTransactionsListingAdapter: WalletTransactionsAdapter
    private lateinit var contactUsBottomDialog: FragmentModalBottomSheet

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
            R.layout.fragment_doctor_profile_new, container, false
        )
        setupVM()
        //getContactsList()
        //checkEmergencyContactsInDB()
        getWalletBalanceFromServer()
        setupObservers()
        setClickListeners()
        setProfileData()

    }
    private fun showContactUsDialogue() {
       /* val li = LayoutInflater.from(FourBaseCareApp.activityFromApp)
        val myView: View = li.inflate(R.layout.bottom_contact_us_dialogue, null)

        contactUsBottomDialog = FragmentModalBottomSheet(myView)
        contactUsBottomDialog.isCancelable = true

        contactUsBottomDialog.show(
            FourBaseCareApp.activityFromApp.supportFragmentManager,
            "BottomSheet Fragment"
        )

        val linCall: LinearLayout = myView.findViewById(R.id.linCall)
        val linEmail: LinearLayout = myView.findViewById(R.id.linEmail)

        linCall.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+918105171596")
            startActivity(intent)
            contactUsBottomDialog.dismiss()
        })

        linEmail.setOnClickListener(View.OnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:info@4basecare.com")
            }
            startActivity(Intent.createChooser(emailIntent, "Application query"))
            contactUsBottomDialog.dismiss()
        })
*/



    }

    private fun setProfileData() {
        userObj = getUserObject()
        binding.tvName.setText(userObj.firstName)
        binding.tvPhoneNumber.setText(userObj.phoneNumber)
        binding.tvDesignation.setText(userObj.headline)
        binding.tvEmailVal.setText(userObj.email)
        if(userObj != null && userObj.profile != null){
            if(userObj.profile.designation.isNullOrEmpty())binding.tvDesignation.setText("NA")else binding.tvDesignation.setText(userObj.profile.designation)
            if(userObj.profile.specialization.isNullOrEmpty())binding.tvSpecialization.setText("NA")else binding.tvSpecialization.setText(userObj.profile.specialization)
        }else{
            binding.tvDesignation.setText("NA")
            binding.tvSpecialization.setText("NA")
        }

        if(!userObj.dpLink.isNullOrEmpty())
        Glide.with(FourBaseCareApp.activityFromApp).load(userObj.dpLink).circleCrop().into(binding.ivProfileImage)



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
        profileViewModel.walletBalanceResponse.observe(this, walletBalanceResponse)
        profileViewModel.walletTransactionsResponse.observe(this, walletTransactionsResponse)
    }

    private fun setClickListeners() {

        binding.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())fragmentManager?.popBackStack()
        })
        binding.cardMyPatients.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    PatientListingFragment(), this, false
                )
            }
        })

        binding.cardContactUS.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()) showContactUsDialogue()
        })

        binding.cardEdittimings.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DoctorEditTimingsFragment(), this, false
                )
            }
        })


        binding.cardAppHelp.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    AppHelpVIdeosFragment(), this, false
                )
            }

        })

        binding.cardABout.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()) CommonMethods.openWebBrowser("https://4basecare.com",FourBaseCareApp.activityFromApp)
        })


        binding.cardLogout.setOnClickListener {
            if(!isDoubleClick()) showLogoutDialog()
            //saveproduct()
        }

        binding.ivEdit.setOnClickListener(View.OnClickListener {
            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                DoctorEditProfileFragment(), this, false
            )
        })

        binding.cardChangePassword.setOnClickListener(View.OnClickListener {
            /*if(!isDoubleClick()){
                val intent = Intent(FourBaseCareApp.activityFromApp, ForgotPasswordActivity::class.java)
                intent.putExtra(Constants.SOURCE, "patient_profile")
                startActivity(intent)
                FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
            }*/
        })

        binding.tvViewTransactions.setOnClickListener(View.OnClickListener {
            Log.d("WALLET", "Fetching transactions")
            getWalletTransactionsFromServer()

        })

        binding.tvWithdraw.setOnClickListener(View.OnClickListener {
            Log.d("WALLET", "withdraw clicked")
            withdrawBalance()
        })
    }

    private fun showLogoutDialog() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(FourBaseCareApp.activityFromApp)
                .setMessage(R.string.logout_confirm)
                .setPositiveButton(R.string.yes,
                    DialogInterface.OnClickListener { dialogInterface, which ->
                        doLogoutProcess()
                    }).setNegativeButton(R.string.no,
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        builder.show()
    }

    private fun getWalletTransactionsFromServer(){
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.getWalletTransactions(
                getUserAuthToken()
            )
        }
    }

    private fun withdrawBalance(){
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.withdrawBalance(
                getUserAuthToken()
            )
        }
    }

    private fun showTransactionsDialog(){

        walletTransactionsDialog = Dialog(FourBaseCareApp.activityFromApp)
        walletTransactionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        walletTransactionsDialog.setContentView(R.layout.dialogue_view_wallet_transactions)

        val rvDoctors: RecyclerView = walletTransactionsDialog.findViewById(R.id.rvDoctors)
        val linAddNewDoctor: LinearLayout = walletTransactionsDialog.findViewById(R.id.linAddNewDoctor)
        val ivAddLogo: ImageView = walletTransactionsDialog.findViewById(R.id.ivAddLogo)
        val tvTitle: TextView = walletTransactionsDialog.findViewById(R.id.tvTitle)
        val ivClose: ImageView = walletTransactionsDialog.findViewById(R.id.ivClose)

        ivClose.setOnClickListener(View.OnClickListener {
            if(walletTransactionsDialog != null && walletTransactionsDialog.isShowing)
            walletTransactionsDialog.dismiss()
        })

        linAddNewDoctor.visibility = View.VISIBLE
        ivAddLogo.visibility = View.GONE
        tvTitle.setText(getString(R.string.transactions))

        rvDoctors.apply {
            layoutManager = LinearLayoutManager(activity)
            walletTransactionsListingAdapter = WalletTransactionsAdapter(transactions)
            adapter = walletTransactionsListingAdapter
        }
        walletTransactionsDialog.show()
    }

    private val walletTransactionsResponse = androidx.lifecycle.Observer<WalletTransactionsResponse> {
        if(it?.isSuccess == true){
            val gson = Gson()
            transactions = ArrayList()
            transactions.addAll(it?.transactions)
            showTransactionsDialog()
        }
    }

    private fun getWalletBalanceFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.getWalletBalance(
                getUserAuthToken()
            )
        }
    }

    private val walletBalanceResponse = androidx.lifecycle.Observer<WalletBalanceResponse?> {
        if(it?.isSuccess == true){
            if(!it?.userWallet?.balance!!.equals("null")){
                val gson = Gson()
                walletBalance = gson.toJson(it?.userWallet?.balance).toDouble()/100
                val solution:Double = String.format("%.1f", walletBalance).toDouble()
                binding.tvWalletBalance.setText(""+solution)
            }

        }
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            Log.d("resume_log","Called")
            setProfileData()
        }
    }


}