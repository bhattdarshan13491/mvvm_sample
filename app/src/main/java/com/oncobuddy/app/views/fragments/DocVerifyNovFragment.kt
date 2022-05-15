package com.oncobuddy.app.views.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.LinearLayout
import com.oncobuddy.app.R
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.databinding.LayoutDoctorExpSpecializationBinding
import com.oncobuddy.app.databinding.LayoutDoctorVerifyDocsBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.doctor_certification.AddCertificateInput
import com.oncobuddy.app.models.pojo.doctor_certification.Certification
import com.oncobuddy.app.models.pojo.education_degrees.AddEducationInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.FileUtils
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.utils.custom_views.FragmentModalBottomSheet
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.fragments.VirtualConsultNovFragment
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DocVerifyNovFragment : BaseFragment() {
    private lateinit var binding: LayoutDoctorVerifyDocsBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var FILE_URL = ""
    private lateinit var  FILE_PATH: String
    private var mCurrentPhotoPath: String? = null
    private lateinit var bottomAddFamilyMemberInputDIalogue : FragmentModalBottomSheet
    private var mLastClickTime: Long = 0
    private val MAX_CLICK_INTERVAL = 1000
    private lateinit var FILE_NAME: String
    private var selectedCouncil = "nothing"
    private var IS_EDIT_MODE =  false
    private var selectedPos = 0


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_doctor_verify_docs, container, false
        )
        setupTitle()
        setUserData()
        setupClickListeners()
        setupVM()
        setupObservers()
        showHideBottomButton()
    }

    private fun setUserData() {
        var userObject = getUserObject()
        if(userObject.certification != null){
            var certification = userObject.certification
            FILE_URL = certification.certificateLink
            val separated = FILE_URL.split("/".toRegex()).toTypedArray()
            val separatedDash =
                separated[separated.size - 1].split("-".toRegex()).toTypedArray()
            FILE_NAME = separatedDash[separatedDash.size - 1]
            binding.tvUploadCerti.setText(FILE_NAME)
            binding.etRegNo.setText(certification.regNo)
            selectedCouncil =  certification.council
            binding.spCouncil.setSelection(resources.getStringArray(R.array.council).indexOf(selectedCouncil))

        }

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
                        binding.relContinue.visibility = View.GONE
                        binding.tvSave.visibility = View.GONE
                        CommonMethods.showLog("button_height", "button hidden")
                        //binding.tvEditProfile.setVisibility(View.GONE)

                    } else {
                        //ok now we know the keyboard is down...
                        if(!Constants.IS_ACC_SETUP_MODE){
                            binding.relContinue.visibility = View.GONE
                            binding.tvSave.visibility = View.VISIBLE
                            CommonMethods.showLog("button_height", "tvsave VIsible")
                        }else{
                            CommonMethods.showLog("button_height", "tvsave gone")
                            binding.relContinue.visibility = View.VISIBLE
                        }
                        CommonMethods.showLog("button_height", "button visible")
                        // binding.tvEditProfile.setVisibility(View.VISIBLE)
                    }
                }
            })
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

    }
    private fun setupObservers() {
        profileViewModel.addCertificationResonseData.observe(this, addCertificationObserver)
        profileViewModel.responseFileUploadData.observe(this, fileUploadResponseObserver)
        profileViewModel.loginResonseData.observe(this, updateProfileObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val addCertificationObserver = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        if(responseObserver.success) {
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Certification added successfully!")
            val certification = Certification()
            certification.certificateLink = FILE_URL
            certification.council = selectedCouncil
            certification.regNo = getTrimmedText(binding.etRegNo)
            var userObj = getUserObject()
            userObj.certification = certification
            val gson = Gson()
            val userStr = gson.toJson(userObj)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userStr)

            decideRedirection()

        }
        else
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Something went wrong while adding education")


        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val updateProfileObserver = androidx.lifecycle.Observer<LoginResponse> { responseObserver ->
        if (responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Profile updated successfully!"
            )
            var userObj = getUserObject()
           /* userObj.firstName = getTrimmedText(binding.edName)
            userObj.dateOfBirth = binding.tvDob.text.toString()
            userObj.description = getTrimmedText(binding.edAbout)
*/
            val gson = Gson()
            val userStr = gson.toJson(userObj)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userStr)

            //fragmentManager!!.popBackStack()
            decideRedirection()

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem updating profile!"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }


    private fun setupTitle() {
        if (Constants.IS_ACC_SETUP_MODE) {
            binding.layoutHeader.relTitleCOntainer.visibility = View.GONE
            binding.layoutAcSetup.linAcSetupHeader.visibility = View.VISIBLE
            binding.layoutAcSetup.tvCurrentStep.setText("Document Verification")
            binding.layoutAcSetup.tvNextStep.setText("Next : Virtual Consultation Setup")
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_5_of_6).into(binding.layoutAcSetup.ivStep)
        } else {
            binding.layoutHeader.tvTitle.setText("Document Verification")
            binding.layoutHeader.relTitleCOntainer.visibility = View.VISIBLE
            binding.layoutAcSetup.linAcSetupHeader.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {

        binding.layoutAcSetup.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())fragmentManager?.popBackStack()
        })

        binding.spCouncil.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if(position == 0){
                    selectedCouncil = "nothing"
                }else{
                    selectedCouncil = resources.getStringArray(R.array.council).get(position)
                }
            }

        }



        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.ivDropDownOne.setOnClickListener(View.OnClickListener {
            binding.spCouncil.performClick()
        })

        binding.relContinue.setOnClickListener {
            if(!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)){
                if (FILE_URL.isNullOrEmpty()) {
                    showToast(FourBaseCareApp.activityFromApp, "Please upload certificate")
                }else if (selectedCouncil.equals("nothing")) {
                    showToast(FourBaseCareApp.activityFromApp, "Please select council")
                } else if (getTrimmedText(binding.etRegNo).isNullOrEmpty()) {
                    showToast(FourBaseCareApp.activityFromApp, "Please enter registration number")
                } else {
                    addCertification()
                }
            }
        }

        binding.tvSave.setOnClickListener {
            if(!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)){
                if (FILE_URL.isNullOrEmpty()) {
                    showToast(FourBaseCareApp.activityFromApp, "Please upload certificate")
                }else if (selectedCouncil.equals("nothing")) {
                    showToast(FourBaseCareApp.activityFromApp, "Please select council")
                } else if (getTrimmedText(binding.etRegNo).isNullOrEmpty()) {
                    showToast(FourBaseCareApp.activityFromApp, "Please enter registration number")
                } else {
                    addCertification()
                }
            }
        }

        binding.ivUpload.setOnClickListener {
            if(checkPermission(FourBaseCareApp.activityFromApp))uploadPDF()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)
        return binding.root
    }

    private fun decideRedirection() {
        if (Constants.IS_ACC_SETUP_MODE) {
            changeProfileCOmpletionLevel(6)
            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                VirtualConsultNovFragment(), this, false
            )
        } else {
            //showToast(FourBaseCareApp.activityFromApp, "Data updated successfully!")
            fragmentManager?.popBackStack()
        }
    }

    // Images coding

    fun isDoubleClickBottom(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_INTERVAL) {
            Log.d("Returned", "Returned Fragment")
            return true
        }
        Log.d("Success", "Success")
        mLastClickTime = SystemClock.elapsedRealtime()
        return false
    }

    private fun showBottomChooseOptionDialogue() {
        val li = LayoutInflater.from(FourBaseCareApp.activityFromApp)
        val myView: View = li.inflate(R.layout.bottom_nov_dialogue_camera_or_gallery, null)

        bottomAddFamilyMemberInputDIalogue = FragmentModalBottomSheet(myView)
        bottomAddFamilyMemberInputDIalogue.show(
            FourBaseCareApp.activityFromApp.supportFragmentManager,
            "BottomSheet Fragment"
        )

        val linUploadDoc: LinearLayout = myView.findViewById(R.id.linUploadDoc)
        val linGallery: LinearLayout = myView.findViewById(R.id.linGallery)
        val linCamera: LinearLayout = myView.findViewById(R.id.linCamera)

        linUploadDoc.setOnClickListener(View.OnClickListener {
            Log.d("bottom_click_log","Gallery clicked")
            if(!isDoubleClickBottom()){
                uploadPDF()
                Log.d("bottom_click_log","came gallery")
            }
            bottomAddFamilyMemberInputDIalogue.dismiss()
        })

        linGallery.setOnClickListener(View.OnClickListener {
            if(!isDoubleClickBottom()) openGalleryForImage()
            bottomAddFamilyMemberInputDIalogue.dismiss()
        })

        linCamera.setOnClickListener(View.OnClickListener {
            if(!isDoubleClickBottom()){
                openCameraIntent()
                Log.d("bottom_click_log","came camera")
            }
            bottomAddFamilyMemberInputDIalogue.dismiss()
        })


    }

    private fun uploadPDF() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        startActivityForResult(intent, 112)
    }

    private fun openCameraIntent() {
        val takePictureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        Log.d("bottom_click_log","0")
        if (takePictureIntent.resolveActivity(FourBaseCareApp.activityFromApp.getPackageManager()) != null) {
            Log.d("bottom_click_log","1")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    Log.d("bottom_click_log","2")
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ignored: IOException) {
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                            FourBaseCareApp.activityFromApp,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile
                        )

                        /*FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                            BuildConfig.APPLICATION_ID + ".provider", file);
                        */
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            photoURI
                        )
                        startActivityForResult(takePictureIntent, Constants.START_CAMERA)
                        Log.d("open_cam_log","Cam opened ")
                    }
                } catch (e: Exception) {
                    Log.d("bottom_click_log","Err "+e.toString())
                    e.printStackTrace()
                    Log.d("open_cam_log","Err "+e.toString())
                }
            } else {
                Log.d("bottom_click_log","else called")
                startActivityForResult(takePictureIntent, Constants.START_CAMERA)
            }
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.PICK_GALLERY_IMAGE)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = FourBaseCareApp.activityFromApp.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.START_CAMERA) {
                try {
                    Log.d("camera_img_log","captured image")
                    var imgBitmap: Bitmap?
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
                                Log.d("camera_img_log","Got resized img")
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    val mImageUri: Uri? = getImageUri(FourBaseCareApp.activityFromApp, imgBitmap)
                    // start cropping activity for pre-acquired image saved on the device
                    CropImage.activity(mImageUri)
                        .start(FourBaseCareApp.activityFromApp, this)



                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }else if(requestCode == Constants.PICK_GALLERY_IMAGE){
                val uri = data!!.data
                mCurrentPhotoPath = FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, uri)
                var imgBitmap: Bitmap?
                imgBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { BitmapFactory.decodeFile(mCurrentPhotoPath)
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
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                val mImageUri: Uri? = getImageUri(FourBaseCareApp.activityFromApp, imgBitmap)
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity(mImageUri)
                    .start(FourBaseCareApp.activityFromApp, this)


            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                if(data != null) {
                    val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                    if (resultCode == Activity.RESULT_OK) {
                        Log.d("camera_img_log","cropped img")
                        val resultUri: Uri = result.getUri()
                        Log.d("camera_img_log","on result path "+resultUri.path)
                        var bundle  = Bundle()
                        bundle.putString(Constants.IMAGE_PATH,resultUri.toString())
                        bundle.putInt(Constants.DOC_MODE,Constants.PHOTO_MODE)
                        var fullScreenPDFViewFragment  = FullScreenPDFViewFragmentKt()
                        fullScreenPDFViewFragment.arguments = bundle
                        CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp, fullScreenPDFViewFragment,this,false)
                        Log.d("camera_img_log","Got crop uri")


                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        val error: java.lang.Exception = result.getError()
                        CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Error")
                    }
                }
            }
            else{
                val uri = data!!.data

                if(data != null && data!!.data != null){

                    try {
                        val fullPath1: String = FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, uri)
                        val f = File(fullPath1)
                        val size = f.length()/1024/1024
                        //var strSize = android.text.format.Formatter.formatFileSize(FourBaseCareApp.activityFromApp, size)
                        Log.d("file_path", "Full path $fullPath1")
                        Log.d("file_path", "Size  $size")
                        if(size <= 10.0){
                            FILE_PATH = fullPath1
                            uploadFileToS3()

                        }else{
                            showToast(FourBaseCareApp.activityFromApp,"File size limit exceed. File with more than 10 MB cannot be uploaded!")
                        }

                    } catch (e: Exception) {
                        Log.d("multiple_share", "Error onact "+e.toString())
                        /*  Toast.makeText(
                                FourBaseCareApp.activityFromApp,
                                "There was an error getting data",
                                Toast.LENGTH_SHORT
                            ).show()*/

                    }
                }else{
                    Toast.makeText(FourBaseCareApp.activityFromApp,"Uri is null", Toast.LENGTH_SHORT)
                }
            }



        }else{
            /*   Toast.makeText(
                   FourBaseCareApp.activityFromApp,
                   "File not selected",
                   Toast.LENGTH_SHORT
               ).show()*/
        }

    }

    private fun getResizedBitmap(image: Bitmap): Bitmap? {
        return try {
            Log.d("camera_img_log","Resizing started")
            val width = image.width / 2
            val height = image.height / 2
            Bitmap.createScaledBitmap(image, width, height, true)
        } catch (e: NullPointerException) {
            Log.d("camera_img_log","Resize error")
            image
        }
    }

    fun getImageUri(
        inContext: Context?,
        inImage: Bitmap?
    ): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext?.contentResolver,
            inImage,
            "IMG_" + System.currentTimeMillis(),
            null
        )
        return Uri.parse(path)
    }

    private fun uploadFileToS3(){
        if(checkInterNetConnection(FourBaseCareApp.activityFromApp)){

            Log.d("upload_log","File path1.2 "+FILE_PATH)

            val body: MultipartBody.Part
            val file  = File(FILE_PATH)

            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            profileViewModel.callFileUpload(
                getUserAuthToken(),body)
            Log.d("upload_log","2 "+FILE_PATH)

        }
    }

    private val fileUploadResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        FILE_URL = responseObserver.message
        val separated = FILE_URL.split("/".toRegex()).toTypedArray()
        val separatedDash =
            separated[separated.size - 1].split("-".toRegex()).toTypedArray()
        FILE_NAME = separatedDash[separatedDash.size - 1]
        binding.tvUploadCerti.text = FILE_NAME
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Document uploaded successfully!")
        binding.executePendingBindings()
        binding.invalidateAll()
        Log.d("edit_profile_log","profile pic set "+FILE_URL)
    }

    private fun addCertification() {
        var addEducationInput = AddCertificateInput()
        addEducationInput.certificateLink = FILE_URL
        addEducationInput.council = selectedCouncil
        addEducationInput.regNo = getTrimmedText(binding.etRegNo)
        profileViewModel.callAddCertification(getUserObject().authToken, addEducationInput)
    }

    fun checkPermission(context: Context?): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (context as Activity?)!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    val alertBuilder = AlertDialog.Builder(
                        context
                    )
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle(R.string.permission_necessary)
                    alertBuilder.setMessage(R.string.external_storage_permission_needed)
                    alertBuilder.setPositiveButton(
                        android.R.string.yes
                    ) { dialog, which ->
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                            ),
                            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                        )
                    }
                    val alert = alertBuilder.create()
                    alert.setCancelable(false)
                    alert.show()
                } else {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        ),
                        Constants. MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE ->{
                showBottomChooseOptionDialogue()

            }
        }

    }



}