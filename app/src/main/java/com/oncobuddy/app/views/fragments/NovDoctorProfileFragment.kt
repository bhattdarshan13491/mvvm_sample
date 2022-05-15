package com.oncobuddy.app.views.fragments


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentNovDoctorProfileBinding
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment

class NovDoctorProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentNovDoctorProfileBinding

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
            R.layout.fragment_nov_doctor_profile, container, false
        )

        binding.tvName.setText(getUserObject().firstName)
        if(!getUserObject().dpLink.isNullOrEmpty())
            Glide.with(FourBaseCareApp.activityFromApp).load(getUserObject().dpLink).circleCrop().into(binding.ivProfileImage)

        setupClickListeners()

        binding.cardProfileVerify.visibility = View.GONE

        binding.tvWalletBalance.setText(FourBaseCareApp.sharedPreferences.getString(Constants.PREF_WALLET_BALANCE,"0.0"))

    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })




        binding.cardConsultation.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                Constants.IS_ACC_SETUP_MODE = false
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    VirtualConsultNovFragment(), this, false
                )
            }
        })

        binding.tvTransactionHistory.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DrTransactionHistoryFragment(), this, false
                )
            }
        })

        binding.cardQRCode.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DoctorQrCOdeFragment(), this, false
                )
            }
        })



        binding.cardBankAcc.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DoctorBankDetailsFragment(), this, false
                )
            }
        })

        binding.linHeader.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DoctorEditProfileOptionsFragment(), this, false
                )
            }
        })

        binding.cardEstablishment.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                Constants.IS_ACC_SETUP_MODE = false
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DoctorEstablishmentListingFragment(), this, false
                )
            }
        })

        binding.linInvite.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.shareApp(FourBaseCareApp.activityFromApp,"Download Onco buddy app : https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID)
            }
        })

        binding.linAbout.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.openWebBrowser("https://4basecare.com/about-us/overview/", FourBaseCareApp.activityFromApp)
            }
        })

        binding.linTerms.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                var bundle = Bundle()
                bundle.putString("url", Constants.TERMS_URL)
                bundle.putString("title",getString(R.string.terms_of_service_simple))

                var fragment = NovWebViewFragment()
                fragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    fragment, this, false
                )
            }
        })

        binding.linPrivacyPolicy.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                var bundle = Bundle()
                bundle.putString("url", Constants.PRIVACY_POLICY_URL)
                bundle.putString("title",getString(R.string.privacy_policy_simple))

                var fragment = NovWebViewFragment()
                fragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    fragment, this, false
                )
            }
        })

        binding.linAccountSettings.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    PatientAccountSettingsFragment(), this, false
                )
            }
        })

        binding.linLogout.setOnClickListener {
            if(!isDoubleClick())showLogoutDialog()
            //saveproduct()
        }

        binding.linFaq.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                var bundle = Bundle()
                bundle.putString("url", "https://www.google.com")
                bundle.putString("title","FAQ")

                var fragment = NovWebViewFragment()
                fragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    fragment, this, false
                )
            }
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


}