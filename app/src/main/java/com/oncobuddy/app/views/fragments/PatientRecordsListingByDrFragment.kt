package com.oncobuddy.app.views.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentPatientRecordsForDoctorsBinding
import com.oncobuddy.app.models.injectors.RecordsInjection
import com.oncobuddy.app.models.pojo.FilterTag
import com.oncobuddy.app.models.pojo.patient_list.PatientDetails
import com.oncobuddy.app.models.pojo.records_list.Record
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.FileUtils
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.utils.custom_views.FragmentModalBottomSheet
import com.oncobuddy.app.view_models.RecordsViewModel
import com.oncobuddy.app.views.adapters.FilterTagListingAdapter
import com.oncobuddy.app.views.adapters.MedicalRecordsAdapterNew
import com.theartofdev.edmodo.cropper.CropImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PatientRecordsListingByDrFragment : BaseFragment(), MedicalRecordsAdapterNew.Interaction, FilterTagListingAdapter.Interaction{

    private lateinit var binding: FragmentPatientRecordsForDoctorsBinding
    private lateinit var recordsViewModel: RecordsViewModel
    private lateinit var recordsList: ArrayList<Record>
    private lateinit var reportsList: ArrayList<Record>
    private lateinit var prescriptionsList: ArrayList<Record>
    private lateinit var billsList: ArrayList<Record>
    private lateinit var medicalRecordsAdapter: MedicalRecordsAdapterNew
    private var totalSize = 0
    private var patientId = "0"
    private lateinit var patientDetails: PatientDetails
    private lateinit var bottomAddPrescritionDialogue : FragmentModalBottomSheet
    private val taskHandler = Handler()
    private val MAX_CLICK_INTERVAL = 1000
    private var mLastClickTime: Long = 0
    private var IS_VIEWING_REPORTS = true
    var mCurrentPhotoPath: String? = null
    private lateinit var bottomAddFamilyMemberInputDIalogue : FragmentModalBottomSheet

    private lateinit var filterTagsList: ArrayList<FilterTag>
    var tagListWithSize = ArrayList<FilterTag>()
    private lateinit var filterTagListingAdapter: FilterTagListingAdapter

    private val repeatativeTaskRunnable = Runnable {
        //getRecordsFromDb()
        getRecordsFromServer()
    }

    fun startHandler() {
        taskHandler.postDelayed(repeatativeTaskRunnable, Constants.FUNCTION_DELAY)
    }

    fun stopHandler() {
        if (taskHandler != null) taskHandler.removeCallbacks(repeatativeTaskRunnable)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)

        return binding.root
    }

    private fun setFilterTagsListView() {
        Log.d("delete_tag_log","Came in filter tags")
        Log.d("delete_tag_log","Filter Reports list size "+reportsList.size)
        filterTagsList = ArrayList()
        tagListWithSize = ArrayList()
        filterTagsList.add(FilterTag("All", "" + reportsList.size, true))
        Log.d("filtering_log", "All size added")

        var strObjects = ArrayList<String>()
        for (record in reportsList) {
            Log.d("filtering_log", "record name " + record.title)
            //if(!strObjects.contains(record.title)) strObjects.add(record.title)
            if (!record.categories.isNullOrEmpty()) {
                strObjects.add(record.categories.get(0))
                Log.d("filtering_log", "Added " + record.categories.get(0))
                /*for (tag in record.tags.distinct()) {
                    strObjects.add(tag)
                }*/
            }
        }

        if (strObjects.size > 0) {

            for (strObject in strObjects) {
                var count = Collections.frequency(strObjects, strObject)
                Log.d("size_log","tag "+strObject+" Items frequency "+count)
                var filterTagWithSIze = FilterTag(strObject, "" + count, false)
                filterTagsList.add(filterTagWithSIze)
            }
        }

        for (filterTag in filterTagsList) {
            var isFound = false
            // check if the event name exists in noRepeat
            for (filterObj in tagListWithSize) {
                if (filterObj != null && filterObj.tagName!= null && filterObj.tagName.equals(filterTag.tagName)) {
                    isFound = true
                    break
                }
            }
            if (!isFound) tagListWithSize.add(filterTag)
        }
        Log.d("filtering_log", "Rows found " + tagListWithSize.size)
        binding.rvFilterTags.apply {
            layoutManager = LinearLayoutManager(context)
            (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
            // layoutManager = GridLayoutManager(FourBaseCareApp.activityFromApp, 3)
            filterTagListingAdapter = FilterTagListingAdapter(this@PatientRecordsListingByDrFragment)
            adapter = filterTagListingAdapter
            tagListWithSize.sortByDescending { it -> it.tagName }
            filterTagListingAdapter.submitList(tagListWithSize.asReversed())
            Log.d("delete_tag_log","final size set "+tagListWithSize.size)
            Log.d("delete_tag_log","final all tags size set "+tagListWithSize.get(0).numbers)
        }

    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_patient_records_for_doctors, container, false
        )

        if (getUserObject().role.equals(Constants.ROLE_DOCTOR)) {
            Log.d("patient_id_log","role dr patient id "+arguments?.get(Constants.PATIENT_ID))
            patientId = "" + (arguments?.get(Constants.PATIENT_ID))
            Constants.PATIENT_ID_FOR_RECORDS = patientId
        } else {
            Log.d("patient_id_log","role patient patient id "+getUserObject().userIdd)
            patientId = "" + getUserObject().userIdd
        }

        Log.d("record_back","1")
        if(arguments != null && arguments!!.containsKey(Constants.SHOULD_HIDE_BACK)){
            Log.d("record_back","2")
            if(arguments!!.getBoolean(Constants.SHOULD_HIDE_BACK)){
                Log.d("record_back","3")
                binding.ivBack.visibility = View.GONE
            }else{
                Log.d("record_back","4")
                binding.ivBack.visibility = View.VISIBLE
            }
        }

        setupVM()
        setupClickListeners()

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            // getRecordsFromServer()
            startHandler()

        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
        setupObservers()


    }


    private fun setupObservers() {
        //recordsViewModel.responseDeleteRecord.observe(this, deleteResponseObserver)
        recordsViewModel.recordsList.observe(this, responseObserver)
        recordsViewModel.allDBRecordsList.observe(this, localDbDataObserver)
        recordsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        recordsViewModel.onMessageError.observe(this, errorMessageObserver)
    }




    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean> { isLoading ->
        if (isLoading) showHideProgress(true, binding.layoutProgress.frProgress)
        else showHideProgress(false, binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String> { message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }


    private fun getRecordsFromServer() {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("patient_id_log","Final patient id "+patientId)
            recordsViewModel!!.callGetRecords(
                Constants.ROLE_DOCTOR,
                getUserObject().authToken,
                "" + getUserObject().userIdd,
                "" + patientId
            )
        }

    }

    private fun setupClickListeners() {

        binding.ivAdd.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                if(askForPermissions()){
                    Constants.IS_FROM_PATIENT_LIST_SCREEN = true
                    Log.d("msg_log","IS_PATIENT_LIST true")
                    showBottomAddMemberInputDialogue()

                }

            }
        })

        binding.linReports.setOnClickListener(View.OnClickListener {
            IS_VIEWING_REPORTS = true
            setSelectionColor()
            setRecyclerView(reportsList)
            setFilterTagsListView()
        })


        binding.linPrescriptions.setOnClickListener(View.OnClickListener {
            IS_VIEWING_REPORTS = false
            setSelectionColor()
            setRecyclerView(prescriptionsList)
        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) fragmentManager?.popBackStack()
        })

    }

    private fun setupVM() {
        recordsViewModel = ViewModelProvider(
            this,
            RecordsInjection.provideViewModelFactory()
        ).get(RecordsViewModel::class.java)
    }

    private fun setRecyclerView(list: ArrayList<Record>) {
        if(!list.isNullOrEmpty()) {
            binding.tvNoData.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                medicalRecordsAdapter =
                    MedicalRecordsAdapterNew(
                        list,
                        this@PatientRecordsListingByDrFragment,
                        false,
                        getUserObject().userIdd,
                        getUserObject().role
                    )
                adapter = medicalRecordsAdapter
                list.sortByDescending { it.recordDate }
                medicalRecordsAdapter.submitList(list)
            }
        }else{
            binding.tvNoData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }

    }

    override fun onItemSelected(position: Int, item: Record, view: View, medicalRecordsAdapterNew: MedicalRecordsAdapterNew) {
        if (!isDoubleClick()) {
            if (view.id == R.id.ivDelete) {
              //  showDeleteConfirmDialogue(item)
            }
            else if (view.id == R.id.ivEdit) {

                var bundle = Bundle()

                bundle.putParcelable(Constants.RECORD_OBJ, item)
                var addOrEditRecordFragment = AddOrEditRecordFragment()
                addOrEditRecordFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    addOrEditRecordFragment, this, false
                )

            }
            else if (view.id == R.id.relMainCOntainer) {
                if(askForPermissions()){
                    var bundle = Bundle()
                    bundle.putString(Constants.SOURCE, Constants.EDIT_RECORD_FRAGMENT)
                    bundle.putString(Constants.SERVER_FILE_URL, item.link)
                    var fullScreenPDFViewFragment = FullScreenPDFViewFragmentKt()
                    fullScreenPDFViewFragment.arguments = bundle
                    Log.d("update_doc_log", "List FILE_URL ${item.link}")
                    CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        fullScreenPDFViewFragment, this, false
                    )
                }

            }
        }

    }

    override fun onItemLongPress(
        position: Int,
        item: Record,
        view: View,
        medicalRecordsAdapterNew: MedicalRecordsAdapterNew
    ) {
        TODO("Not yet implemented")
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
                ActivityCompat
                    .requestPermissions(
                        FourBaseCareApp.activityFromApp,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
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
                   showBottomAddMemberInputDialogue()
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

    private fun applyTagFilter(strTag: String) {

        var filteredList: ArrayList<Record> = ArrayList()

        if(strTag.equals("All")){
            filteredList = reportsList
            Log.d("filter_result", "All filtered reports size "+filteredList.size)
        }else{
            for (record in reportsList) {

                for (tag in record.tags.distinct()) {
                    if (record.tags != null) {
                        if (tag.equals(strTag, true)) {
                            Log.d("filter_result", "tag found " + record.title)
                            filteredList.add(record)
                        }

                    }
                }
            }

        }
        Log.d("filter_result", "Filtered size " + filteredList.size)
        setRecyclerView(filteredList)


    }

    private fun getDate(strDate: String?): Date {
        val df = SimpleDateFormat("yyyy-MM-dd")
        var date: Date = df.parse(strDate)
        return date
    }


    private val responseObserver = androidx.lifecycle.Observer<List<Record>> { responseObserver ->

        Log.d("list_log", "response came")

        setupDataInList(responseObserver)
    }

    private fun setupDataInList(responseObserver: List<Record>) {
        recordsList = ArrayList()
        billsList = ArrayList()
        reportsList = ArrayList()
        prescriptionsList = ArrayList()

        if (!responseObserver.isNullOrEmpty()) {
            if (responseObserver.size > 0) {
                Log.d("delete_tag_log","list "+responseObserver.size)
                recordsList.addAll(responseObserver)
                Log.d("filter_result", "All records size "+recordsList.size)

                for (record in recordsList.asReversed()) {

                    totalSize += record.size.toInt()

                    // Doctors need reports and prescriptions. Not bills
                    if(!record.recordType.equals("BILL",true)){
                        reportsList.add(record)
                        setFilterTagsListView()
                    }else if (record.recordType.equals("PRESCRIPTION", true)) {
                        prescriptionsList.add(record)
                    }
                }

                Log.d("filter_result", "reports size "+reportsList.size)
                Log.d("filter_result", "reports Presciption "+prescriptionsList.size)



                setRecyclerView(reportsList)
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE

            } else {
                binding.recyclerView.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            }


        }else{
            binding.recyclerView.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val localDbDataObserver =
        androidx.lifecycle.Observer<List<Record>> { responseObserver ->
            if (responseObserver.isNullOrEmpty()) {
                getRecordsFromServer()
            } else {
                setupDataInList(responseObserver)
            }

        }

    private fun uploadPDF() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        startActivityForResult(intent, 112)
    }
    open fun isDoubleClickBottom(): Boolean {
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



    private fun setSelectionColor() {
        if (IS_VIEWING_REPORTS) {
            binding.ivReportsLine.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )
            binding.tvReports.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )

            binding.ivPrescriptionsLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.nov_line_gray))
            binding.tvPrescriptions.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                ))


        } else {
            binding.ivPrescriptionsLine.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )
            binding.tvPrescriptions.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )

            binding.ivReportsLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.nov_line_gray))
            binding.tvReports.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                ))
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
                            var bundle  = Bundle()
                            bundle.putString(Constants.PDF_PATH,fullPath1)
                            bundle.putInt(Constants.DOC_MODE,Constants.PDF_MODE)
                            var fullScreenPDFViewFragment  = FullScreenPDFViewFragmentKt()
                            fullScreenPDFViewFragment.arguments = bundle

                            CommonMethods.addNextFragment(
                                FourBaseCareApp.activityFromApp,
                                fullScreenPDFViewFragment,this,false)

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
                    Toast.makeText(FourBaseCareApp.activityFromApp,"Uri is null",Toast.LENGTH_SHORT)
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
        Log.d("img_crash_log","0")
        val bytes = ByteArrayOutputStream()
        inImage?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        Log.d("img_crash_log","1")
        val path = MediaStore.Images.Media.insertImage(
            inContext?.contentResolver,
            inImage,
            "IMG_" + System.currentTimeMillis(),
            null
        )
        Log.d("img_crash_log","Path "+path)
        return Uri.parse(path)
    }

    override fun onFilterTagSelected(position: Int, item: FilterTag, view: View) {
        //showToast(FourBaseCareApp.activityFromApp,"Clicked "+position)
        item.isSelected = true
        for (filterTag in tagListWithSize) {
            if (filterTag.tagName.equals(item.tagName)) {
                filterTag.isSelected = true
            } else {
                filterTag.isSelected = false
            }
        }
        tagListWithSize.sortByDescending { it -> it.tagName }
        filterTagListingAdapter.submitList(tagListWithSize.asReversed())
        filterTagListingAdapter.notifyDataSetChanged()
        applyTagFilter(item.tagName)

    }

}


