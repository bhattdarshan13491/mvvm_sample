package com.oncobuddy.app.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentQrCodeBinding
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseActivity
import com.oncobuddy.app.views.fragments.AddOrEditAppointmentFragment

/**
 * Q r scan activity
 * It scans the QR code and fetches code which is sent to the server for getting data
 * @constructor Create empty Q r scan activity
 */

class QRScanActivity : BaseActivity() {
    private lateinit var binding : FragmentQrCodeBinding
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@QRScanActivity, R.layout.fragment_qr_code)

        codeScanner = CodeScanner(this,binding.scannerView)
        
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
                                                      // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        
        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                showToast("Result "+it.text)
                val intent = Intent(this@QRScanActivity, NovAccountSetupActivity::class.java)
                intent.putExtra("qr_code", it.text)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Log.d("qr_log","Sending result")

            }
        }
        
        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun init() {

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}