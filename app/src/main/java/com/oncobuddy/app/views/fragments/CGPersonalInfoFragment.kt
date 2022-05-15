package com.oncobuddy.app.views.fragments


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.text.format.Time
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.DatePicker
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentCgPersonalInfoBinding
import com.oncobuddy.app.databinding.FragmentDoctorPersonalInfoBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.CareGiverInput
import com.oncobuddy.app.models.pojo.login_response.Profile

import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.FileUtils
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

/**
 * Care giver personal info fragment
 * It handles editing of the care giver basic details
 * @constructor Create empty C g personal info fragment
 */

class CGPersonalInfoFragment : BaseFragment() {

    private lateinit var binding: FragmentCgPersonalInfoBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var mCurrentPhotoPath: String? = null
    private var FILE_URL = ""
    private lateinit var FILE_PATH: String
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
            R.layout.fragment_cg_personal_info, container, false
        )
        setupTitle()
        setupClickListeners()
        setupVM()
        setupObservers()
        setUserData()
        showHideBottomButton()
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
                        binding.tvSave.setVisibility(View.GONE)


                    } else {
                        //ok now we know the keyboard is down...
                        CommonMethods.showLog("button_height", "button visible")
                        binding.tvSave.visibility = View.VISIBLE
                    }
                }
            })
    }

    private fun setUserData() {
        var userObject = getUserObject()
        binding.edName.setText(userObject.firstName)
        if(userObject.profile != null && userObject.profile.dateOfBirth != null && !userObject.profile.dateOfBirth.equals("null",true)) {
            binding.tvDob.setText(""+userObject.profile.dateOfBirth)
        }
        binding.edPhoneNumber.setText(userObject.phoneNumber)
        binding.edEmail.setText(userObject.email)
        if (!getUserObject().dpLink.isNullOrEmpty()) {
            FILE_URL = getUserObject().dpLink
            Glide.with(FourBaseCareApp.activityFromApp).load(userObject.dpLink).circleCrop()
                .into(binding.ivProfile)
        }
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

    }
    private fun setupObservers() {
        profileViewModel.responseFileUploadData.observe(this, fileUploadResponseObserver)
        profileViewModel.updateCGDetailsResonseData.observe(this, updateProfileObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }
    private fun setupClickListeners() {

        binding.ivEdit.setOnClickListener(View.OnClickListener {
            if (isPermissionsAllowed()) openGalleryForImage()
            else askForPermissions()
        })


        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())fragmentManager?.popBackStack()
        })


        binding.relDob.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                showToDatePickerDialogue()
            }
        })

        binding.tvSave.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                validateAndSendData()

                //decideRedirection()
            }
        })
    }

    private fun decideRedirection() {
            showToast(FourBaseCareApp.activityFromApp, "Data updated successfully!")
            fragmentManager?.popBackStack()

    }

    private fun setupTitle() {
            binding.layoutHeader.tvTitle.setText("Basic Info")
            binding.layoutHeader.relTitleCOntainer.visibility = View.VISIBLE
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val fileUploadResponseObserver =
        androidx.lifecycle.Observer<BaseResponse> { responseObserver ->
            //binding.loginModel = loginResponseData

            FILE_URL = responseObserver.message
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Profile picture uploaded successfully! Click on Save button to update profile picture."
            )
            binding.executePendingBindings()
            binding.invalidateAll()
            Log.d("edit_profile_log", "profile pic set " + FILE_URL)
        }

    private val updateProfileObserver = androidx.lifecycle.Observer<BaseResponse> { responseObserver ->
        if (responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Profile updated successfully!"
            )
            var userObj = getUserObject()
            userObj.firstName = getTrimmedText(binding.edName)
            userObj.dateOfBirth = binding.tvDob.text.toString()
            var profile = Profile()
            profile.dateOfBirth  = binding.tvDob.text.toString()
            userObj.dpLink = FILE_URL
            userObj.profile = profile
            val gson = Gson()
            val userStr = gson.toJson(userObj)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userStr)
            fragmentManager!!.popBackStack()



        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem updating profile!"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private fun showToDatePickerDialogue() {
        val calendar = Calendar.getInstance()
        val yy = calendar[Calendar.YEAR]
        val mm = calendar[Calendar.MONTH]
        val dd = calendar[Calendar.DAY_OF_MONTH]
        val datePicker =
            context?.let {
                DatePickerDialog(
                    it,
                    DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        val chosenDate = Time()
                        chosenDate[dayOfMonth, monthOfYear] = year
                        val dtDob = chosenDate.toMillis(true)
                        var strDate = DateFormat.format("yyyy-MM-dd", dtDob)
                        //               binding.tvToVal.setText(strDate.toString())
                        binding.tvDob.text = strDate

                    }, yy, mm, dd
                )
            }

        if (datePicker != null) {
            datePicker.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }

    }
    private fun validateAndSendData() {
        if (getTrimmedText(binding.edName).isNullOrEmpty()) {
            showToast(FourBaseCareApp.activityFromApp, "Please enter name")
        } else if (binding.tvDob.text.isNullOrEmpty()) {
            showToast(FourBaseCareApp.activityFromApp, "Please enter date of birth")
        } else {
            var careGiverInput = CareGiverInput()
            careGiverInput.dateOfBirth = binding.tvDob.text.toString()
            if(!FILE_URL.isNullOrEmpty())careGiverInput.dpLink = FILE_URL
            profileViewModel.callUpdateCGDetails(
                getUserAuthToken(),
                careGiverInput
            )
            //gotoNextFragment()
            Log.d("update_log", "1")

        }
    }

    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    FourBaseCareApp.activityFromApp,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                showPermissionDeniedDialog()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.REQUEST_EXTERNAL_STORAGE
                )
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalleryForImage()
                } else {
                    CommonMethods.showToast(
                        FourBaseCareApp.activityFromApp,
                        getString(R.string.msg_allow_permission)
                    )
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(FourBaseCareApp.activityFromApp)
            .setTitle(getString(R.string.msg_permission_denied))
            .setMessage(getString(R.string.msg_permission_from_settings))
            .setPositiveButton(getString(R.string.title_app_settings),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                        "package",
                        FourBaseCareApp.activityFromApp.getPackageName(),
                        null
                    )
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.PICK_GALLERY_IMAGE)
    }

    private fun uploadFileToS3() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            Log.d("upload_log", "File path1.2 " + FILE_PATH)

            val body: MultipartBody.Part
            val file = File(FILE_PATH)

            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            profileViewModel.callFileUpload(
                getUserAuthToken(), body
            )
            Log.d("upload_log", "2 " + FILE_PATH)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_GALLERY_IMAGE) {
            // binding.ivProduct.setImageURI(data?.data) // handle chosen image
            //strSelectedImagePath = data?.data.toString()

            val uri = data!!.data
            mCurrentPhotoPath =
                FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, uri)
            /*var imgBitmap: Bitmap?
            imgBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BitmapFactory.decodeFile(mCurrentPhotoPath)
            } else {
                val extras = data!!.extras
                extras!!["data"] as Bitmap?
            }*/
            /*try {
                if (imgBitmap != null) {
                    while (imgBitmap!!.height > 2048 || imgBitmap!!.width > 2048) {
                        imgBitmap = getResizedBitmap(imgBitmap)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }*/
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP

            //val mImageUri: Uri? = getImageUri(FourBaseCareApp.activityFromApp, imgBitmap)
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(uri)
                .start(FourBaseCareApp.activityFromApp, this)


        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.START_CAMERA) {
            try {
                /*var imgBitmap: Bitmap?
                imgBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    BitmapFactory.decodeFile(mCurrentPhotoPath)
                } else {
                    val extras = data!!.extras
                    extras!!["data"] as Bitmap?
                }
                try {
                    if (imgBitmap != null) {
                        while (imgBitmap!!.height > 2048 || imgBitmap!!.width > 2048) {
                            imgBitmap = getResizedBitmap(imgBitmap)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                val mImageUri: Uri? = getImageUri(FourBaseCareApp.activityFromApp, imgBitmap)
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity(mImageUri)
                    .start(FourBaseCareApp.activityFromApp)*/


            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri: Uri = result.getUri()
                    //val uri = data!!.data
                    Glide.with(FourBaseCareApp.activityFromApp).load(resultUri).transform(
                        CircleCrop()
                    ).into(binding.ivProfile)

                    FILE_PATH =
                        FileUtils.getRealPathFromURI_API19(
                            FourBaseCareApp.activityFromApp,
                            resultUri
                        )
                    uploadFileToS3()


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error: java.lang.Exception = result.getError()
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Error")
                }
            }

        }
    }

}