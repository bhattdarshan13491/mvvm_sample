package com.oncobuddy.app.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.FourBaseCareApp

import com.oncobuddy.app.databinding.*
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import android.graphics.Bitmap

import androidmads.library.qrgenearator.QRGContents

import androidmads.library.qrgenearator.QRGEncoder

import android.text.TextUtils
import com.bumptech.glide.Glide
import com.google.zxing.WriterException
import com.oncobuddy.app.BuildConfig
import android.content.Intent

import android.graphics.Bitmap.CompressFormat


import android.net.Uri
import android.os.Environment
import com.oncobuddy.app.R
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


class DoctorQrCOdeFragment : BaseFragment(){

    private lateinit var binding : FragmentDoctorQrCodeBinding
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
            R.layout.fragment_doctor_qr_code, container, false
        )
        setClickListeners()
        generateQRCode()

        binding.tvDrName.setText(getUserObject().firstName)
        binding.tvSpecialization.setText(getUserObject().profile.specialization)

     /*   setupVM()
        setupObservers()*/
    }
    private fun setupVM() {
        profileViewModel = ViewModelProvider(
                this,
                ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }


    private fun setClickListeners() {
        binding.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())fragmentManager?.popBackStack()
        })

        binding.ivShare.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                //shareImage()
               CommonMethods.shareApp(FourBaseCareApp.activityFromApp,binding.tvCOde.text.toString() + " is my code.\n\nDownload Onco buddy app : https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID)
            }
        })
    }

    private fun shareImage() {
        binding.ivQRCode.isDrawingCacheEnabled = true
        val bitmap = binding.ivQRCode.drawingCache
        val storageDir: File? =
            FourBaseCareApp.activityFromApp.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            "doctor_code",  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        try {
            image.createNewFile()
            val ostream = FileOutputStream(image)
            bitmap.compress(CompressFormat.JPEG, 100, ostream)
            ostream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/*"
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image))
        startActivity(Intent.createChooser(share, "Share via"))
    }


    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private fun generateQRCode() {
        try {
            binding.tvCOde.text = ""+getUserObject().userIdd
            val qrgEncoder = QRGEncoder(""+getUserObject().userIdd, null, QRGContents.Type.TEXT, 150)
            val bitmap = qrgEncoder.encodeAsBitmap()
            Glide.with(FourBaseCareApp.activityFromApp).load(bitmap).into(binding.ivQRCode)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }


}