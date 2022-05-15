package com.oncobuddy.app.views.fragments


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentAddPostBinding
import com.oncobuddy.app.models.injectors.ForumsInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.AddPostUserOption
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.PdfImage
import com.oncobuddy.app.models.pojo.forums.AddQuestionInput
import com.oncobuddy.app.models.pojo.forums.PostQuestionDto
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.models.pojo.profile.CancerType
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.FileUtils
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.utils.custom_views.FragmentModalBottomSheet
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.AddPostUserSpinnerAdapter
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_add_post.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AddPostFragment : BaseFragment() {

    private lateinit var binding: FragmentAddPostBinding
    private lateinit var forumsViewModel: ForumsViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private var SOURCE =  Constants.COMMUNITY

    // Media
    private var bottomCameraOrGalleryDIalogue: FragmentModalBottomSheet? = null
    private lateinit var bottomAddFamilyMemberInputDIalogue : FragmentModalBottomSheet
    private var FILE_URL = ""
    private lateinit var  FILE_PATH: String
    private var FILE_NAME = ""
    var mCurrentPhotoPath: String? = null

    private lateinit var usersList: ArrayList<AddPostUserOption>
    private lateinit var addPostUserSpinnerAdapter: AddPostUserSpinnerAdapter

    private var mLastClickTime: Long = 0
    private val MAX_CLICK_INTERVAL = 1000

    private var pdfRenderer: PdfRenderer? = null

    private var IS_VIEWING_DOCUMENT = false
    private var IS_PDF_SELECTED = false

    private var attachmentType = ""
    private var HAS_ADDED_MEDIA = false
    private var IS_UPDATE_MODE = false
    private var addPostObj = TrendingVideoDetails()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)

        return binding.root
    }

    private fun setupVM() {
        forumsViewModel = ViewModelProvider(
            this,
            ForumsInjection.provideViewModelFactory()
        ).get(ForumsViewModel::class.java)

        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

    }

    private fun setupObservers() {
        forumsViewModel.addQuestionResponseData.observe(this, addQuestionResponseObserver)
        forumsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        forumsViewModel.onMessageError.observe(this, errorMessageObserver)

        profileViewModel.responseFileUploadData.observe(this, fileUploadResponseObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }


        override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_post, container, false
        )
            setBindingings()
            setupVM()
            setupObservers()
            initializeUserSpinner()
            setDataFromArguments()
            setClickListeners()


        }

    private fun setDataFromArguments() {
        Log.d("update_log","0.1")
        if (arguments != null && arguments!!.containsKey(Constants.SOURCE)) {
            Log.d("update_log","0")
            SOURCE = arguments!!.getString(Constants.SOURCE).toString()
            Log.d("update_log","SOURCE "+SOURCE)
            if (SOURCE.equals(Constants.COMMUNITY) || SOURCE.equals(Constants.ONCO_DISCUSSIONS_LIST)) {
                binding.linAddPost.visibility = View.VISIBLE
                binding.linAddQuestion.visibility = View.GONE
                Log.d("update_log","post visible")
            } else {
                binding.linAddPost.visibility = View.GONE
                binding.linAddQuestion.visibility = View.VISIBLE
                Log.d("update_log","question visibile")
            }

            if(arguments!!.containsKey("add_post_data")){
                IS_UPDATE_MODE = true
                addPostObj = arguments!!.getParcelable<TrendingVideoDetails>("add_post_data")!!
                if(addPostObj != null){

                    binding.etQuestion.setText(addPostObj.content)
                    binding.etSubject.setText(addPostObj.questionTitle)
                    binding.etShare.setText(addPostObj.content)
                    if(addPostObj.post.anonymous){
                        binding.spPostAs.setSelection(1)
                    }else{
                        binding.spPostAs.setSelection(0)
                    }
                    if(addPostObj.questionAttachmentType != null){
                        if(addPostObj.questionAttachmentType.equals(Constants.ATTACHEMENT_TYPE_IMAGE)){
                            binding.relImage.visibility = View.VISIBLE
                            FILE_URL = addPostObj.questionAttachmentUrl
                            attachmentType = Constants.ATTACHEMENT_TYPE_IMAGE
                            Glide.with(FourBaseCareApp.activityFromApp).load(addPostObj.questionAttachmentUrl).into(binding.ivImage)
                        }else{
                            FILE_URL = addPostObj.questionAttachmentUrl
                            IS_PDF_SELECTED = true
                            binding.relDocumentContainer.visibility = View.VISIBLE
                            var fileName = URLUtil.guessFileName(addPostObj.questionAttachmentUrl, null, null)
                            binding.tvFileName.setText(fileName)
                            attachmentType = Constants.ATTACHEMENT_TYPE_DOCUMENT
                        }

                    }

                    /*if(addPostObj.questionAttachmentUrl == null){

                    }*/
                }

            }else{
                Log.d("update_log","No data")
            }
        }
    }

    private fun setClickListeners() {

        binding.ivRemovePDF.setOnClickListener(View.OnClickListener {
                attachmentType = ""
                FILE_URL = ""
                binding.relDocumentContainer.visibility = View.GONE
                IS_PDF_SELECTED = false
            HAS_ADDED_MEDIA = false

        })

        binding.ivRemoveImage.setOnClickListener(View.OnClickListener {
            attachmentType = ""
            FILE_URL = ""
            HAS_ADDED_MEDIA = false
            binding.relImage.visibility = View.GONE
        })

        binding.ivCloseContainer.setOnClickListener {
            if(!isDoubleClick()){
                try {
                    binding.relFragmentContainer.visibility = View.GONE
                    IS_VIEWING_DOCUMENT = false
                } catch (e: Exception) {
                    Log.d("open_exec","err 2 "+e.toString())
                }
            }

        }

        binding.tvView.setOnClickListener(View.OnClickListener {

            if(!isDoubleClick()) {
                showToast(FourBaseCareApp.activityFromApp, "Opening document")
                showPDFFragment(FILE_URL)
            }
        })

        binding.linAsk.setOnClickListener(View.OnClickListener {
            if (CommonMethods.getTrimmedText(binding.etSubject).isNullOrEmpty()) {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    "Please enter question title!"
                )
            } else if (CommonMethods.getTrimmedText(binding.etQuestion).isNullOrEmpty()) {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    "Please enter your question!"
                )
            } else {
                CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
                Log.d("anonymous_log"," "+false)
                addQuestion(
                    getTrimmedText(binding.etQuestion),
                    getTrimmedText(binding.etSubject),
                    false
                )
            }
        })

        binding.linPost.setOnClickListener(View.OnClickListener {
            if (CommonMethods.getTrimmedText(binding.etShare).isNullOrEmpty()) {
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Please enter your post!")
            } else {
                CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
                Log.d("anonymous_log","got "+binding.spPostAs.selectedItemPosition.equals(1))
                addQuestion(
                    getTrimmedText(binding.etShare),
                    "",
                    (binding.spPostAs.selectedItemPosition.equals(1))
                )
            }
        })


        binding.linPhoto.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if (checkPermissionForCameraAndStorage()) {
                    if(!HAS_ADDED_MEDIA) showCameraOrGalleryDialogue()
                    else showToast(FourBaseCareApp.activityFromApp, "You have already uploaded an attachement!")
                } else {
                    requestPermissionForCameraAndStorage()
                }

            }
        })

        binding.llAttach.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if (checkPermissionForCameraAndStorage()) {
                    if(!HAS_ADDED_MEDIA)showBottomAddMemberInputDialogue()
                    else showToast(FourBaseCareApp.activityFromApp, "You have already uploaded an attachement!")
                } else {
                    requestPermissionForCameraAndStorage()
                }

            }
        })
    }

    private fun setBindingings() {
        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })
    }

    private fun initializeUserSpinner() {
        usersList = ArrayList()

        var appUser = AddPostUserOption()
        appUser.id = 0
        appUser.name = getUserObject().firstName
        usersList.add(appUser)

        var anonymousUser = AddPostUserOption()
        anonymousUser.id = 1
        anonymousUser.name = "Anonymous"
        usersList.add(anonymousUser)

        val array = arrayOfNulls<AddPostUserOption>(usersList.size)
        usersList.toArray(array)
        addPostUserSpinnerAdapter = AddPostUserSpinnerAdapter(FourBaseCareApp.activityFromApp,R.layout.raw_spinner, array)
        binding.spPostAs.adapter = addPostUserSpinnerAdapter

    }


    private fun addQuestion(que: String, title:String, isAnonymous: Boolean){

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            var addQuestionInput  =  AddQuestionInput()
            var postQuestionDto = PostQuestionDto()
            postQuestionDto.question = que

            postQuestionDto.title = title
            Log.d("attachement_log","0")
            if(!FILE_URL.isNullOrEmpty()){
                postQuestionDto.attachmentLink = FILE_URL
                postQuestionDto.attachmentType = attachmentType
                Log.d("attachement_log","1 "+attachmentType)
            }
            if(SOURCE.equals(Constants.COMMUNITY)){
                postQuestionDto.questionType = Constants.QUESTION_TYPE_OPEN
            }else if(SOURCE.equals(Constants.ONCO_DISCUSSIONS_LIST)){
                postQuestionDto.questionType = Constants.QUESTION_TYPE_ONCO_DISCUSSIONS
            }
            else{
                postQuestionDto.questionType = Constants.QUESTION_TYPE_EXPERT
            }
            addQuestionInput.postQuestionDto = postQuestionDto
            addQuestionInput.isAnonymous = isAnonymous
            addQuestionInput.postType = "QUESTION"


            Log.d("anonymous_log","sent "+isAnonymous)
            if(IS_UPDATE_MODE){
                forumsViewModel.callAddQuestion(""+addPostObj.post.id,
                    getUserAuthToken(), addQuestionInput)
            }else{
                forumsViewModel.callAddQuestion("",
                    getUserAuthToken(), addQuestionInput)
            }

        }

    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val fileUploadResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData

        FILE_URL = responseObserver.message
        binding.executePendingBindings()
        binding.invalidateAll()
        Log.d("insert_img_log","question pic set "+FILE_URL)
        Log.d("insert_img_log","Is pdf selected "+IS_PDF_SELECTED)
        HAS_ADDED_MEDIA = true
        if(IS_PDF_SELECTED) {
            binding.relDocumentContainer.visibility = View.VISIBLE
            IS_PDF_SELECTED = false
        }else{
            binding.relImage.visibility = View.VISIBLE
        }
    }

    private val addQuestionResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Question posted successfully!")
            Constants.IS_LIST_UPDATED =  true
            fragmentManager?.popBackStack()
        }

    }

    private fun showCameraOrGalleryDialogue() {
        val li = LayoutInflater.from(FourBaseCareApp.activityFromApp)
        val myView = li.inflate(R.layout.bottom_dialogue_camera_or_gallery, null)
        bottomCameraOrGalleryDIalogue = FragmentModalBottomSheet(myView)
        bottomCameraOrGalleryDIalogue!!.show(FourBaseCareApp.activityFromApp.supportFragmentManager, "BottomSheet Fragment")

        val tvDialogueTitle = myView.findViewById<TextView>(R.id.tvDialogueTitle)
        val linGallery = myView.findViewById<LinearLayout>(R.id.linGallery)
        val linCamera = myView.findViewById<LinearLayout>(R.id.linCamera)

        tvDialogueTitle.setText("Upload photo using")

        linGallery.setOnClickListener {
            bottomCameraOrGalleryDIalogue!!.dismiss()
            if(!isDoubleClick()){
                    openGalleryForImage()

            }
            bottomCameraOrGalleryDIalogue!!.dismiss()
        }
        linCamera.setOnClickListener {
            if(!isDoubleClick()){
                    openCameraIntent()
            }
            bottomCameraOrGalleryDIalogue!!.dismiss()

        }
    }

    // Camera related work

    private fun openCameraIntent() {
        val takePictureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(FourBaseCareApp.activityFromApp.getPackageManager()) != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ignored: IOException) {
                        Log.d("camera_img_log","exception 3 "+ignored.toString())
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                            FourBaseCareApp.activityFromApp,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile
                        )

                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            photoURI
                        )
                        startActivityForResult(takePictureIntent, Constants.START_CAMERA)
                    }
                } catch (e: Exception) {
                    Log.d("camera_img_log","exception 4 "+e.toString())
                    e.printStackTrace()
                }
            } else {
                startActivityForResult(takePictureIntent, Constants.START_CAMERA)
            }
        }
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

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.PICK_GALLERY_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.START_CAMERA) {
                try {
                    Log.d("camera_img_log", "0")
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
                        Log.d("camera_img_log","exception 1 "+e.toString())
                        e.printStackTrace()
                    }
                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    val mImageUri: Uri? = getImageUri(FourBaseCareApp.activityFromApp, imgBitmap)
                    // start cropping activity for pre-acquired image saved on the device
                    CropImage.activity(mImageUri)
                        .start(FourBaseCareApp.activityFromApp, this)
                    attachmentType = Constants.ATTACHEMENT_TYPE_IMAGE
                } catch (e: java.lang.Exception) {
                    Log.d("camera_img_log","exception 2 "+e.toString())
                    e.printStackTrace()
                }
            }else if(requestCode == Constants.PICK_GALLERY_IMAGE){
                Log.d("file_path", "1")
                val uri = data!!.data
                mCurrentPhotoPath = FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, uri)
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
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                //val mImageUri: Uri? = getImageUri(FourBaseCareApp.activityFromApp, imgBitmap)
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity(uri)
                    .start(FourBaseCareApp.activityFromApp, this)


            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                if(data != null) {
                    Log.d("file_path", "2")
                    val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                    Log.d("file_path", "Result "+result)
                    Log.d("file_path", "Result code "+resultCode)
                    if (resultCode == Activity.RESULT_OK) {
                        val resultUri: Uri = result.getUri()
                        Log.d("file_path", "uri "+resultUri)
                        FILE_PATH = FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, resultUri)
                        Log.d("file_path", "path "+FILE_PATH)
                        binding.ivImage.let {
                            Glide.with(FourBaseCareApp.activityFromApp).load(resultUri).into(it)
                        }
                        attachmentType = Constants.ATTACHEMENT_TYPE_IMAGE
                        uploadFileToS3()



                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        val error: java.lang.Exception = result.getError()
                        CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Error")
                    }
                }
            }else{
                Log.d("file_path", "3")
                val uri = data!!.data
                Log.d("file_path", "uri "+uri)

                if(uri != null){
                    try {
                        Log.d("file_path", "3.1")
                        val fullPath1: String = FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, uri)
                        Log.d("file_path", "3.2 "+fullPath1)
                        val f = File(fullPath1)
                        val size = f.length()/1024/1024
                        Log.d("file_path", "3.3")
                        val argUri = Uri.parse(fullPath1)

                        //var strSize = android.text.format.Formatter.formatFileSize(FourBaseCareApp.activityFromApp, size)
                        Log.d("file_path", "Full path $fullPath1")
                        Log.d("file_path", "Size  $size")
                        Log.d("file_path", "argUri  $argUri")
                        if(size <= 10.0){
                            FILE_PATH = fullPath1
                            FILE_NAME = ""+getFileName(uri)
                            binding.tvFileName.text = getFileName(uri)
                            uploadFileToS3()
                            attachmentType = Constants.ATTACHEMENT_TYPE_DOCUMENT
                        }else{
                            Log.d("file_path", "10 mb")
                            showToast(FourBaseCareApp.activityFromApp,"File size limit exceed. File with more than 10 MB cannot be uploaded!")
                        }

                    } catch (e: Exception) {
                        Log.d("file_path", "Err "+e.toString())
                        Log.d("multiple_share", "Error onact "+e.toString())
                        Toast.makeText(
                                FourBaseCareApp.activityFromApp,
                                "There was an error getting data",
                                Toast.LENGTH_SHORT
                            ).show()

                    }
                }else{
                    Log.d("file_path", "uri is null")
                    Toast.makeText(FourBaseCareApp.activityFromApp,"Uri is null",Toast.LENGTH_SHORT)
                }

            }
        }
    }

    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? =
                FourBaseCareApp.activityFromApp.getContentResolver().query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result =
                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                Objects.requireNonNull(cursor)?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: 0
            if (cut != -1) {
                if (result != null) {
                    result = result.substring(cut + 1)
                }
            }
        }
        return result
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
        val path = MediaStore.Images.Media.insertImage(FourBaseCareApp.activityFromApp.contentResolver,
            inImage,
            "IMG_" + System.currentTimeMillis(),
            "Image at "+System.currentTimeMillis()
        )
        return Uri.parse(path)
    }




    private fun uploadFileToS3(){
        if(checkInterNetConnection(FourBaseCareApp.activityFromApp)){
            Log.d("insert_img_log","File path1.2 "+FILE_PATH)

            val body: MultipartBody.Part
            val file  = File(FILE_PATH)

            val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

            body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            profileViewModel.callFileUpload(
                getUserAuthToken(),body)
            Log.d("insert_img_log","2 "+FILE_PATH)

        }
    }

    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun checkPermissionForCameraAndStorage(): Boolean {
        val resultCamera = ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.CAMERA
        )
        val resultMic = ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultMic == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForCameraAndStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                FourBaseCareApp.activityFromApp,
                Manifest.permission.CAMERA
            ) ||
            ActivityCompat.shouldShowRequestPermissionRationale(
                FourBaseCareApp.activityFromApp,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            Toast.makeText(
                FourBaseCareApp.activityFromApp,
                R.string.permissions_needed,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                FourBaseCareApp.activityFromApp,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d("permissions_log","came here "+requestCode)
        when (requestCode) {
            Constants.REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCameraOrGalleryDialogue()

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

    fun isDoubleClickBottom(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_INTERVAL) {
            Log.d("Returned", "Returned Fragment")
            return true
        }
        Log.d("Success", "Success")
        mLastClickTime = SystemClock.elapsedRealtime()
        return false
    }

    private fun showBottomAddMemberInputDialogue() {
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
                attachmentType = Constants.ATTACHEMENT_TYPE_DOCUMENT
                IS_PDF_SELECTED = true
                Log.d("bottom_click_log","came gallery")
            }
            bottomAddFamilyMemberInputDIalogue.dismiss()
        })

        linGallery.setOnClickListener(View.OnClickListener {
            attachmentType = Constants.ATTACHEMENT_TYPE_IMAGE
            Log.d("attachement_log","attachement set "+attachmentType)
            if(!isDoubleClickBottom()) openGalleryForImage()
            bottomAddFamilyMemberInputDIalogue.dismiss()
        })

        linCamera.setOnClickListener(View.OnClickListener {
            if(!isDoubleClickBottom()){
                attachmentType = Constants.ATTACHEMENT_TYPE_IMAGE
                Log.d("attachement_log","attachement cam "+attachmentType)
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

    private fun showPDFFragment(link: String) {
        try {
            Timer().schedule(object : TimerTask(){
                override fun run() {
                    activity?.runOnUiThread {
                        val bundle = Bundle()
                        bundle.putString(Constants.SOURCE, Constants.EDIT_RECORD_FRAGMENT)
                        bundle.putBoolean(Constants.SHOULD_SHOW_TITLE, false)
                        bundle.putString(Constants.SERVER_FILE_URL, link)
                        val fullScreenPDFViewFragment = FullScreenPDFViewFragmentKt()
                        fullScreenPDFViewFragment.arguments = bundle
                        val transaction = FourBaseCareApp.activityFromApp.supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.frAppointment, fullScreenPDFViewFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                        binding.relFragmentContainer.visibility = View.VISIBLE
                        IS_VIEWING_DOCUMENT = true
                    }

                }

            }, 1000)

            CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
        } catch (e: Exception) {
            Log.d("open_exec","err "+e.toString())
        }
    }

}