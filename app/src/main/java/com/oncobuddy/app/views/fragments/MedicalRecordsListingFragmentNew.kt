package com.oncobuddy.app.views.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentMedicalRecordsListNewBinding
import com.oncobuddy.app.models.injectors.RecordsInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.FilterTag
import com.oncobuddy.app.models.pojo.records_list.MonthyRecords
import com.oncobuddy.app.models.pojo.records_list.Record
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.FileUtils
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.utils.custom_views.FragmentModalBottomSheet
import com.oncobuddy.app.view_models.RecordsViewModel
import com.oncobuddy.app.views.adapters.FilterTagListingAdapter
import com.oncobuddy.app.views.adapters.MedicalRecordsAdapterNew
import com.oncobuddy.app.views.adapters.MonthlyRecordsAdapter
import com.theartofdev.edmodo.cropper.CropImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule


class MedicalRecordsListingFragmentNew : BaseFragment(), MedicalRecordsAdapterNew.Interaction,
    FilterTagListingAdapter.Interaction, MonthlyRecordsAdapter.Interaction {

    private lateinit var binding: FragmentMedicalRecordsListNewBinding
    private lateinit var recordsViewModel: RecordsViewModel
    private lateinit var recordsList: ArrayList<Record>
    private lateinit var reportsList: ArrayList<Record>
    private lateinit var filterTagsList: ArrayList<FilterTag>
    private lateinit var prescriptionsList: ArrayList<Record>
    private lateinit var billsList: ArrayList<Record>
    private lateinit var medicalRecordsAdapter: MedicalRecordsAdapterNew
    private lateinit var filterTagListingAdapter: FilterTagListingAdapter
    private lateinit var downloadManager: DownloadManager
    private var SELECTED_CATEGORY = 0
    private val CATEGORY_REPORTS = 0
    private val CATEGORY_PRESCRIPTIONS = 1
    private val CATEGORY_BILLS = 2
    private var totalSize = 0
    private var patientId = "0"
    var tagListWithSize = ArrayList<FilterTag>()
    private lateinit var confirmRemoveMessageDilogue: Dialog
    private lateinit var bottomAddFamilyMemberInputDIalogue : FragmentModalBottomSheet
    private val taskHandler = Handler()
    private val MAX_CLICK_INTERVAL = 1000
    private var mLastClickTime: Long = 0
    private var mLastClickItemTime: Long = 0
    private var downloadUrls = ArrayList<String>()
    private var shareURIs = ArrayList<Uri>()
    private val DOWNLOAD_PERMISSION = 101
    private val ADD_DOCUMENT_PERMISSION = 103
    private var IS_VIEWING_DOCUMENT = false
    private val prePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath+"/OncoDocs/"
    private var IS_SHARE_MODE = false
    private var count  = 0
    private var totalCounts = 0
    var mCurrentPhotoPath: String? = null
    private lateinit var monthlyRecordsAdapter : MonthlyRecordsAdapter
    private lateinit var deleteConfirmationDialogue: Dialog
    private lateinit var fullScreenPDFViewFragment: FullScreenPDFViewFragmentKt
    private var pdfFile: File? = null
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
    private var IS_SHOWING_MENU = false
    private var RECORD_TYPE = Constants.RECORD_TYPE_REPORT

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
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m: Method = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d("life_cycle","start called")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.d("life_cycle","start User visible hint "+isVisibleToUser)
    }

    override fun onResume() {
        super.onResume()
        Log.d("life_cycle","on resume")
        binding.edSearch.setText("")
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

    }

    override fun onPause() {
        super.onPause()
        Log.d("life_cycle","on pause")
    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_medical_records_list_new, container, false
        )
        downloadManager = FourBaseCareApp.activityFromApp.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        FourBaseCareApp.activityFromApp.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        //registerReceiver(downloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (getUserObject().role.equals(Constants.ROLE_CARE_COMPANION)) {
            patientId = "" + (arguments?.get(Constants.PATIENT_ID))
            Constants.PATIENT_ID_FOR_RECORDS = patientId
            Log.d("upload_tag_log","records patient id "+Constants.PATIENT_ID_FOR_RECORDS)
        } else {
            patientId = "" + getUserObject().userIdd
        }

        Log.d("record_back","1")
        if(arguments != null && arguments!!.containsKey(Constants.SHOULD_HIDE_BACK)){
            Log.d("record_back","2")
            if(arguments!!.getBoolean(Constants.SHOULD_HIDE_BACK)){
                Log.d("record_back","3")
                //binding.ivBack.visibility = View.GONE
            }else{
                Log.d("record_back","4")
                //binding.ivBack.visibility = View.VISIBLE
            }
        }
        setupVM()
        setupClickListeners()
        setupObservers()

        logScreenViewEventMP("Records screen")
        setBottomSectionVisibility()

    }

    private fun setupObservers() {
        recordsViewModel.responseDeleteRecord.observe(this, deleteResponseObserver)
        recordsViewModel.recordsList.observe(this, responseObserver)
        recordsViewModel.allDBRecordsList.observe(this, localDbDataObserver)
        recordsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        recordsViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val deleteResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?> { responseObserver ->

            Log.d("delete_log", "0")
            if (responseObserver != null) {
                Log.d("delete_log", "1")
                if (responseObserver.success) {
                    Log.d("delete_log", "2")
                    //Constants.IS_LIST_UPDATED = true
                    getRecordsFromServer()
                    binding.executePendingBindings()
                    binding.invalidateAll()

                }
            }

        }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean> { isLoading ->
        if (isLoading) showHideProgress(true, binding.layoutProgress.frProgress)
        else showHideProgress(false, binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String> { message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }


    private fun getRecordsFromServer() {
        if (getUserObject().role.equals(Constants.ROLE_CARE_COMPANION)) {
            recordsViewModel.callGetRecords(
                Constants.ROLE_CARE_COMPANION,
                getUserAuthToken(),
                getUserIdd().toString(),
                patientId
            )
        }else if (getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER)) {
            recordsViewModel.callGetRecords(
                Constants.ROLE_PATIENT_CARE_GIVER,
                getUserAuthToken(),
                getUserIdd().toString(),
                patientId
            )
        } else
            recordsViewModel.callGetRecords(
                Constants.ROLE_PATIENT,
                getUserAuthToken(),
                getUserIdd().toString(),
                patientId
            )
    }

    private fun showHideMenu(shouldSHow : Boolean) {
        if (shouldSHow) {
            Log.d("new_style_log","menu show")
            binding.frFloating.visibility = View.VISIBLE
            binding.fabmenu.open(true)
        } else {
            Log.d("new_style_log","menu hide")
            binding.frFloating.visibility = View.GONE
            binding.fabmenu.close(true)
        }
    }

    private fun setupClickListeners() {

        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                //medicalRecordsAdapter.getFilter().filter(s)
                if(::recordsList.isInitialized){
                    Log.d("filter_log","Initial size "+reportsList.size)
                    var filterList = reportsList.filter { record: Record ->
                        if(!record.title.isNullOrEmpty())
                            record.title.contains(s.toString(),true)
                        else
                            record.recordType.contains(s.toString(),true)
                    }
                    Log.d("filter_log","Filter size "+filterList.size)
                    setRecyclerView(filterList as ArrayList<Record>)
                }

            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.ivSearch.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                showHideSearchBar(true)
            }
        })

        binding.ivClose.setOnClickListener(View.OnClickListener {
            binding.edSearch.setText("")
        })

        binding.ivBackResults.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                showHideSearchBar(false)
            }
        })


        /*binding.fabmenu.setOnMenuButtonClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                if(checkPermission(FourBaseCareApp.activityFromApp)){
                    Log.d("new_log","Showing menu")
                    showHideMenu(true)
                }
            }
        })*/

        binding.fabmenu.setOnMenuButtonClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                if(checkPermission(FourBaseCareApp.activityFromApp)){
                    Log.d("new_log","Showing menu")
                    if(binding.fabmenu.isOpened)
                        showHideMenu(false)
                    else
                        showHideMenu(true)
                }
            }
        })

        binding.floatAddReport.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                RECORD_TYPE = Constants.RECORD_TYPE_REPORT
                showBottomAddMemberInputDialogue()
                showHideMenu(false)
            }
        })

        binding.floatAddPrescription.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                RECORD_TYPE = Constants.RECORD_TYPE_PRESCRIPTION
                showBottomAddMemberInputDialogue()
                showHideMenu(false)
            }
        })

        binding.frFloating.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                showHideMenu(false)
            }
        })

        binding.floatAddBill.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                RECORD_TYPE = Constants.RECORD_TYPE_BILL
                showBottomAddMemberInputDialogue()
                showHideMenu(false)
            }

        })




        binding.ivDownload.setOnClickListener(View.OnClickListener {

         if (!isDoubleClick()) {
                if(checkInterNetConnection(FourBaseCareApp.activityFromApp)){
                    Log.d("multiple_share","Clicked download")
                    showToast(FourBaseCareApp.activityFromApp, "Downloading started! Please check status bar for progress.")
                    //showHideProgress(true, binding.layoutProgress.frProgress)
                    IS_SHARE_MODE = false
                    startDownload()
                }else{
                    Toast.makeText(
                        context,
                        getString(R.string.please_check_internet_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                }else{
                    showToast(FourBaseCareApp.activityFromApp, "Downloading started! Please check status bar.")
                   }


         })

        binding.ivShare.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if(checkInterNetConnection(FourBaseCareApp.activityFromApp)){
                    Log.d("multiple_share","Clicked share")
                    showToast(FourBaseCareApp.activityFromApp, "Downloading started! Please check status bar.")
                    showHideProgress(true, binding.layoutProgress.frProgress)
                    IS_SHARE_MODE = true
                    shareURIs = ArrayList()
                    startDownload()
                }else{
                    Toast.makeText(
                        context,
                        getString(R.string.please_check_internet_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }

             }
        })



        binding.linReports.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                SELECTED_CATEGORY = CATEGORY_REPORTS
                showHideSearchBar(false)
                setSelectionColor(CATEGORY_REPORTS)
                if(!reportsList.isNullOrEmpty()){
                    showHideNoData(false)
                    setRecyclerView(reportsList)
                    setFilterTagsListView()
                    binding.linTagsContainer.visibility = View.VISIBLE

                }else{
                    showHideNoData(true)
                    binding.tvNoData.text = "No Records Found"
                }
           }

        })

        binding.linBills.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                SELECTED_CATEGORY = CATEGORY_BILLS
                showHideSearchBar(false)
                setSelectionColor(CATEGORY_BILLS)
                if(!billsList.isNullOrEmpty()){
                    showHideNoData(false)
                    setRecyclerView(billsList)
                }else{
                    showHideNoData(true)
                    binding.tvNoData.text = "No Bills Found"
                }
                binding.linTagsContainer.visibility = View.GONE
            }
        })

        binding.linPrescriptions.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                SELECTED_CATEGORY = CATEGORY_PRESCRIPTIONS
                setSelectionColor(CATEGORY_PRESCRIPTIONS)
                showHideSearchBar(false)
                if(!prescriptionsList.isNullOrEmpty()){
                    showHideNoData(false)
                    setRecyclerView(prescriptionsList)
                }else{
                    showHideNoData(true)
                    binding.tvNoData.text = "No Prescriptions Found"
                }
                binding.linTagsContainer.visibility = View.GONE
            }
        })


    }

    private fun setBottomSectionVisibility() {
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
                        binding.fabmenu.setVisibility(View.GONE)

                    } else {
                        //ok now we know the keyboard is down...
                        CommonMethods.showLog("button_height", "button visible")
                        binding.fabmenu.setVisibility(View.VISIBLE)
                    }
                }
            })
    }

    private fun startDownload() {
        try {
            if (downloadUrls != null && downloadUrls.size > 0) {
                Log.d("multiple_share","download urls size "+downloadUrls.size)
                if (askForPermissions(DOWNLOAD_PERMISSION)) {
                    for (url in downloadUrls) {
                        downloadFile(url)
                        Log.d("multiple_download_url", "Multiple downloads started " + url)
                    }
                    for (record: Record in recordsList) {
                        if (record.selected) {
                            record.selected = false
                        }
                    }
                    setupDataInList(recordsList)
                    //downloadUrls.clear()
                }
            } else {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    "Please select atleast one file to be downloaded"
                )
            }

        } catch (e: Exception) {
            Log.d("multiple_share", "Err 0 " + e.toString())
            showToast(FourBaseCareApp.activityFromApp,"Share err "+e.toString())
        }
    }

    private fun downloadFile(strUrl: String) {
        try {
            val uri = Uri.parse(strUrl)
            val seperated: Array<String> = strUrl.split("/".toRegex()).toTypedArray()
            var FILENAME = seperated[seperated.size - 1]
            val request = DownloadManager.Request(uri)
            request.setTitle(FILENAME)
            request.setDescription("Downloading...")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/OncoDocs/File $FILENAME")
            downloadManager.enqueue(request)
            Log.d("multiple_share","came in download file")
            if(IS_SHARE_MODE){
                //showToast(FourBaseCareApp.activityFromApp, "Sharing will be done once selected files are downloaded")
                Log.d("multiple_share","share mode on")
            }else{
                Log.d("multiple_share","Came here also.")
                showToast(FourBaseCareApp.activityFromApp, "Downloading started! Please check status bar for progress.")
            }


        } catch (e: java.lang.Exception) {
            Log.d("multiple_share","download Err "+e.toString())
            Toast.makeText(
                FourBaseCareApp.activityFromApp,
                "Downloading failed! with err ."+e.toString(),
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }


    private fun setSelectionColor(category: Int) {
        if (category == CATEGORY_REPORTS) {
            binding.tvReports.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )
            binding.ivReports.visibility = View.VISIBLE
            binding.tvReportCOunt.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.badge_background_red)

        } else {
            binding.tvReports.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                )
            )
            binding.ivReports.visibility = View.INVISIBLE
            binding.tvReportCOunt.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.rounded_gray_bg)
        }

        if (category == CATEGORY_PRESCRIPTIONS) {
            binding.tvPrescriptions.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )
            binding.tvPrescriptionsCount.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.badge_background_red)
            binding.ivPrescription.visibility = View.VISIBLE
        } else {
            binding.tvPrescriptions.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                )
            )
            binding.ivPrescription.visibility = View.INVISIBLE
            binding.tvPrescriptionsCount.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.rounded_gray_bg)

        }

        if (category == CATEGORY_BILLS) {
            binding.tvBills.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )
            binding.ivBills.visibility = View.VISIBLE
            binding.tvBillsCount.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.badge_background_red)
        } else {
            binding.tvBills.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                )

            )

            binding.ivBills.visibility = View.INVISIBLE
            binding.tvBillsCount.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.rounded_gray_bg)
        }
    }

    private fun setupVM() {
        recordsViewModel = ViewModelProvider(this, RecordsInjection.provideViewModelFactory()).get(RecordsViewModel::class.java)
    }

    private fun getmmYYDate(strDate: String): String{
        val format = SimpleDateFormat("yyyy-MM-dd")
        try {
            var utcDate = CommonMethods.utcTOLocalDate(strDate)
            val date = format.parse(utcDate)
            val opFormat = SimpleDateFormat(Constants.Y_M_FORMAT)
            return opFormat.format(date)
            System.out.println(date)
        } catch (e: Exception) {
            Log.d("month_log_e","error is here "+strDate)
            e.printStackTrace()
            return strDate
        }
    }

    private fun getmmYYDateForSOrting(strDate: String): Date{
        try {
            Log.d("date_sort","date "+strDate)
            val opFormat = SimpleDateFormat(Constants.Y_M_FORMAT)
            Log.d("date_sort","Converted date "+opFormat.parse(strDate).time)
            return opFormat.parse(strDate)
        } catch (e: Exception) {
            Log.d("date_sort","error is here "+e.toString())
            e.printStackTrace()
            return Date()
        }
    }

    private fun setRecyclerView(list: ArrayList<Record>) {

        var monthlyList = ArrayList<MonthyRecords>()
        var datesString = ArrayList<String>()

        Log.d("month_log","0")
        Log.d("month_log_d","list size "+list.size)
         for(record in list){
             if(!datesString.contains(getmmYYDate(record.recordDate)))
              datesString.add(getmmYYDate(record.recordDate))
         }
        Log.d("month_log","dates string size "+datesString.size)


        for(strDate in datesString){
            Log.d("month_log","loop date "+strDate)
            var recordsList = ArrayList<Record>()
            for(recordObj in list){
                if(getmmYYDate(recordObj.recordDate).equals(strDate)){
                    Log.d("month_log","record title "+recordObj.title)
                    Log.d("month_log","original date "+recordObj.recordDate)
                    Log.d("month_log","record date "+getmmYYDate(recordObj.recordDate))
                    Log.d("month_log","str date "+getmmYYDate(recordObj.recordDate))
                    recordsList.add(recordObj)
                }
            }
            var monthlyRecord = MonthyRecords(strDate)
            monthlyRecord.recordArrayList = recordsList
            monthlyList.add(monthlyRecord)
            Log.d("month_log","month "+monthlyRecord.monthName)
            Log.d("month_log","records added "+monthlyRecord.recordArrayList.size)
        }

        Log.d("month_log_d","visivility "+binding.recyclerView.visibility)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            monthlyRecordsAdapter =
                MonthlyRecordsAdapter(
                    this@MedicalRecordsListingFragmentNew,
                    this@MedicalRecordsListingFragmentNew,
                    true,
                    getUserObject().userIdd,
                    getUserObject().role
                )
            adapter = monthlyRecordsAdapter
            //list.sortByDescending { it.recordDate}
            monthlyList.sortByDescending { getmmYYDateForSOrting(it.monthName)}
            monthlyRecordsAdapter.submitList(monthlyList)

        }
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
            filterTagListingAdapter = FilterTagListingAdapter(this@MedicalRecordsListingFragmentNew)
            adapter = filterTagListingAdapter
            tagListWithSize.sortByDescending { it -> it.tagName }
            filterTagListingAdapter.submitList(tagListWithSize.asReversed())
            Log.d("delete_tag_log","final size set "+tagListWithSize.size)
            Log.d("delete_tag_log","final all tags size set "+tagListWithSize.get(0).numbers)
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
                bundle.putString(Constants.RECORD_TYPE, item.recordType)

                var addOrEditRecordFragment = AddOrEditRecordFragment()
                addOrEditRecordFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    addOrEditRecordFragment, this, false
                )

            }
            else if (view.id == R.id.relMainCOntainer) {
                //showToast(FourBaseCareApp.activityFromApp,"1")
                Log.d("new_style_log", "Update record called")
                var bundle = Bundle()
                bundle.putString(Constants.SOURCE, Constants.EDIT_RECORD_FRAGMENT)
                bundle.putString(Constants.SERVER_FILE_URL, item.link)
                bundle.putString(Constants.RECORD_TYPE, item.recordType)
                fullScreenPDFViewFragment = FullScreenPDFViewFragmentKt()
                fullScreenPDFViewFragment.arguments = bundle
                IS_VIEWING_DOCUMENT = true
                if(!isDoubleClickItem() && checkPermission(FourBaseCareApp.activityFromApp)){
                        Log.d("item_click_1", "next fragment")
                        Log.d("update_doc_log", "List FILE_URL ${item.link}")
                        CommonMethods.addNextFragment(
                                FourBaseCareApp.activityFromApp,
                                fullScreenPDFViewFragment, this, false

                        )
                    //deleteDirectory()

                    }else{
                        Log.d("item_click_1", "double click captured")
                    }

            }else if(view.id == R.id.ivDoctorImage){
                Log.d("multiple_download_lg","selection mode  "+Constants.IS_SELECTION_MODE)
                if(Constants.IS_SELECTION_MODE){
                    if(item.selected){
                        item.selected = false
                        downloadUrls.remove(item.link)
                        Log.d("multiple_download_lg","removed download urls  "+downloadUrls.size)
                        totalCounts -= 1
                    }else{
                        item.selected = true
                        downloadUrls.add(item.link)
                        totalCounts += 1
                        Log.d("multiple_download_lg","added download urls  "+downloadUrls.size)
                    }

                    medicalRecordsAdapterNew.notifyItemChanged(position)

                    Log.d("multiple_download_lg","download urls size "+downloadUrls.size)
                    if(downloadUrls.size > 0){
                        binding.linTitleActions.visibility = View.VISIBLE
                        Constants.IS_SELECTION_MODE = true
                        Log.d("multiple_download_lg","Actions VISIBLE")
                    }else{
                        binding.linTitleActions.visibility = View.INVISIBLE
                        Constants.IS_SELECTION_MODE = false
                        Log.d("multiple_download_lg","Actions GONE")
                    }

                    Log.d("multiple_download_lg","item changed")
                }
            } else if(view.id == R.id.ivMenu){

                val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, view)
                CommonMethods.prepareMenuForIcons(popupMenu)
                popupMenu.menuInflater.inflate(R.menu.menu_record_options, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                when(menuItem.itemId){
                    R.id.menu_edit ->{
                        var bundle = Bundle()
                        bundle.putParcelable(Constants.RECORD_OBJ, item)

                        var addOrEditRecordFragment = AddOrEditRecordFragment()
                        addOrEditRecordFragment.arguments = bundle

                        CommonMethods.addNextFragment(
                            FourBaseCareApp.activityFromApp,
                            addOrEditRecordFragment, this, false
                        )
                    }
                    R.id.menu_delete -> {
                        showDeleteConfirmDialogue(item)
                    }
                    R.id.menu_share -> {
                        IS_SHARE_MODE = true
                        totalCounts = 1 //SHaring single file
                        downloadFile(item.link)
                    }
                }
                popupMenu.dismiss()
                true
            }

            // Showing the popup menu
            popupMenu.show()
           }
        }

    }

    override fun onItemLongPress(
        position: Int,
        item: Record,
        view: View,
        medicalRecordsAdapterNew: MedicalRecordsAdapterNew
    ) {

        if (view.id == R.id.relMainCOntainer || view.id == R.id.ivDoctorImage) {
            if(item.selected){
                item.selected = false
                downloadUrls.remove(item.link)
                totalCounts -= 1
                Log.d("multiple_download_lg","long remove download urls  "+downloadUrls.size)
            }else{
                showHideSearchBar(false)
                item.selected = true
                downloadUrls.add(item.link)
                totalCounts += 1
                Log.d("multiple_download_lg","long added download urls  "+downloadUrls.size)
            }

            medicalRecordsAdapterNew.notifyItemChanged(position)

            Log.d("multiple_download_lg","long download urls size "+downloadUrls.size)
            if(downloadUrls.size > 0){
                binding.linTitleActions.visibility = View.VISIBLE
                Log.d("multiple_download_lg","long Actions VISIBLE")
                Constants.IS_SELECTION_MODE = true
            }else{
                binding.linTitleActions.visibility = View.INVISIBLE
                Constants.IS_SELECTION_MODE = false
                Log.d("multiple_download_lg","long Actions GONE")
            }



            Log.d("multiple_download_lg","item changed")
        }

    }

    private fun showHideSearchBar(shouldSHowSearch: Boolean) {
        if(shouldSHowSearch){
            binding.relTitleCOntainer.visibility = View.GONE
            binding.linSearchConteiner.visibility = View.VISIBLE
        }else{
            if(!binding.edSearch.text.isNullOrEmpty())binding.edSearch.setText("")
            binding.relTitleCOntainer.visibility = View.VISIBLE
            binding.linSearchConteiner.visibility = View.GONE
        }
    }

    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermissions(permissionOption: Int): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    FourBaseCareApp.activityFromApp,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                showPermissionDeniedDialog()
            } else {
                Log.d("permissions_log","Asked for permission")
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data = Uri.parse(
                            java.lang.String.format(
                                "package:%s",
                                FourBaseCareApp.activityFromApp.getPackageName()
                            )
                        )
                        startActivityForResult(intent, Constants.OS_R_STORAGE_PERMISSION)
                    } catch (e: java.lang.Exception) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        startActivityForResult(intent, Constants.OS_R_STORAGE_PERMISSION)
                    }
                }else{
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE),
                        permissionOption
                    )
                }

            }
            return false
        }
        return true
    }

    /*override fun onResume() {
        super.onResume()
        Log.d("multiple_download","Resume "+downloadUrls.size)
        downloadUrls.clear()
        binding.linTitleActions.visibility = View.GONE
        Log.d("multiple_download","Resume post "+downloadUrls.size)
    }*/


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d("permissions_log","permission result "+requestCode)
        when (requestCode) {
            ADD_DOCUMENT_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permissions_log","permission granted")
                    Log.d("permissions_log","permission granted "+IS_VIEWING_DOCUMENT)
                    Log.d("permissions_log","permission granted "+::fullScreenPDFViewFragment.isInitialized)
                    if(IS_VIEWING_DOCUMENT && ::fullScreenPDFViewFragment.isInitialized){
                        CommonMethods.addNextFragment(
                            FourBaseCareApp.activityFromApp,
                            fullScreenPDFViewFragment, this, false

                        )
                    }else{
                        showBottomAddMemberInputDialogue()
                    }

                } else {
                    CommonMethods.showToast(
                        FourBaseCareApp.activityFromApp,
                        getString(R.string.msg_allow_permission)
                    )
                }
                return
            }
            DOWNLOAD_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permissions_log","permission granted")
                    for(url in downloadUrls){
                        downloadFile(url)
                    }
                    for(record : Record in recordsList){
                        if(record.selected){
                            record.selected = false
                        }
                    }
                    setupDataInList(recordsList)

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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.d("hidden_log","hide "+hidden)
        Log.d("hidden_log","selected "+CATEGORY_REPORTS)
        if (!hidden) {
            showHideSearchBar(false)
            FourBaseCareApp.activityFromApp.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            downloadUrls = ArrayList()
            shareURIs = ArrayList()
            totalSize = 0
            setSelectionColor(SELECTED_CATEGORY)
             Timer().schedule(Constants.FUNCTION_DELAY) {
                 Log.d("delete_tag_log","is updated "+Constants.IS_LIST_UPDATED)
                if (Constants.IS_LIST_UPDATED) getRecordsFromServer()
                Constants.IS_LIST_UPDATED = false
            }
        }else{
            Constants.IS_SELECTION_MODE = false
            FourBaseCareApp.activityFromApp.unregisterReceiver(onComplete)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("permissions_log","on activity result")
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == Constants.OS_R_STORAGE_PERMISSION){
                showToast(FourBaseCareApp.activityFromApp, "Permission result came here")

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    if (Environment.isExternalStorageManager()) {
                        uploadPDF()
                    } else {
                        showToast(FourBaseCareApp.activityFromApp, "Please allow all files permission from app settings")
                    }
                }
            }
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
                        bundle.putString(Constants.RECORD_TYPE, RECORD_TYPE)
                        var fullScreenPDFViewFragment  = FullScreenPDFViewFragmentKt()
                        fullScreenPDFViewFragment.arguments = bundle
                        CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp, fullScreenPDFViewFragment,this,false)
                        Log.d("camera_img_log","Got crop uri")

                        deleteDirectory()
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
                        var fullPath1: String = FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, uri)  //uri?.path.toString()//FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, uri)
                        val f = File(fullPath1)
                        val size = f.length()/1024/1024
                        //fullPath1 = f.absolutePath
                        Log.d("file_path", "Full path $fullPath1")
                        Log.d("file_path", "Size  $size")
                        Log.d("file_path", "main length  ${f.length()}")
                            if(size <= 10.0){
                                var bundle  = Bundle()
                                bundle.putString(Constants.PDF_PATH,fullPath1)
                                bundle.putInt(Constants.DOC_MODE,Constants.PDF_MODE)
                                bundle.putString(Constants.RECORD_TYPE, RECORD_TYPE)
                                var fullScreenPDFViewFragment  = FullScreenPDFViewFragmentKt()
                                fullScreenPDFViewFragment.arguments = bundle

                                CommonMethods.addNextFragment(
                                    FourBaseCareApp.activityFromApp,
                                    fullScreenPDFViewFragment,this,false)

                                deleteDirectory()
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

    fun getPDFPath(uri: Uri?): String? {
        val id = DocumentsContract.getDocumentId(uri)
        val contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
        )
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = FourBaseCareApp.activityFromApp.getContentResolver().query(contentUri, projection, null, null, null)
        val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    fun isPDF(file: File?): Boolean {
        if(file !=null && file.exists()){
            val input = Scanner(FileReader(file))
            while (input.hasNextLine()) {
                val checkline: String = input.nextLine()
                if (checkline.contains("%PDF-")) {
                    // a match!
                    return true
                }
            }
        }

        return false
    }


    private fun applyTagFilter(strTag: String) {

        var filteredList: ArrayList<Record> = ArrayList()

        if(strTag.equals("All")){
            filteredList = reportsList
            Log.d("filter_result", "All filtered reports size "+filteredList.size)
        }else{
            for (record in reportsList) {

                for (tag in record.categories.distinct()) {
                    if (record.categories != null) {
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

        if(downloadUrls != null && downloadUrls.size > 0){
            downloadUrls.clear()
            Constants.IS_SELECTION_MODE = false
            binding.linTitleActions.visibility = View.INVISIBLE
        }


        if (!responseObserver.isNullOrEmpty()) {
            if (responseObserver.size > 0) {
                Log.d("delete_tag_log","list "+responseObserver.size)
                recordsList.addAll(responseObserver)
                Log.d("filter_result", "All records size "+recordsList.size)

                for (record in recordsList.asReversed()) {

                    totalSize += record.size.toInt()

                    if (record.recordType.equals("BILL")) {
                        billsList.add(record)
                    } else if (record.recordType.equals("PRESCRIPTION", true)) {
                        prescriptionsList.add(record)
                    } else {
                        reportsList.add(record)
                    }

                }

                Log.d("filter_result", "reports added originally "+reportsList.size)


                Log.d("size_result", "total records "+recordsList.size)
                Log.d("size_result", "reports size "+reportsList.size)
                Log.d("size_result", "pres size "+prescriptionsList.size)
                Log.d("size_result", "bill size "+billsList.size)
                binding.tvReportCOunt.setText(CommonMethods.getStringWithOnePadding(reportsList.size.toString()))
                binding.tvPrescriptionsCount.setText(CommonMethods.getStringWithOnePadding(prescriptionsList.size.toString()))
                binding.tvBillsCount.setText(CommonMethods.getStringWithOnePadding(billsList.size.toString()))


                if(SELECTED_CATEGORY == CATEGORY_REPORTS){
                    if(reportsList.size > 0){
                        Log.d("no_data_show","false")
                        showHideNoData(false)
                        setRecyclerView(reportsList)
                        setFilterTagsListView()
                        binding.linTagsContainer.visibility = View.VISIBLE
                    }else{
                        Log.d("no_data_show","true")
                        showHideNoData(true)
                        Log.d("no_data_show","no tags showing")
                        binding.linTagsContainer.visibility = View.GONE
                   }
                }else if(SELECTED_CATEGORY == CATEGORY_PRESCRIPTIONS){
                    setRecyclerView(prescriptionsList)
                    if(prescriptionsList.size>0){
                        showHideNoData(false)
                    }else{
                        showHideNoData(true)
                    }

                    binding.linTagsContainer.visibility = View.GONE

                }else{
                    setRecyclerView(billsList)
                    if(billsList.size>0){
                        showHideNoData(false)
                    }else{
                        showHideNoData(true)
                    }

                    binding.linTagsContainer.visibility = View.GONE
                }
            }
            else{
                binding.linTitleActions.visibility = View.INVISIBLE
                showHideNoData(true)
                binding.linTagsContainer.visibility = View.GONE
                binding.tvReportCOunt.setText("00")
                binding.tvPrescriptionsCount.setText("00")
                binding.tvBillsCount.setText("00")
            }


        }else{
            Log.d("no_data_show","Came here 1")
            binding.linTitleActions.visibility = View.INVISIBLE
            showHideNoData(true)
            binding.linTagsContainer.visibility = View.GONE
            binding.tvReportCOunt.setText("00")
            binding.tvPrescriptionsCount.setText("00")
            binding.tvBillsCount.setText("00")
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

    override fun onFilterTagSelected(position: Int, item: FilterTag, view: View) {
        //showToast(FourBaseCareApp.activityFromApp,"Clicked "+position)
        item.isSelected = true
        for (filterTag in tagListWithSize) {
            filterTag.isSelected = filterTag.tagName != null && filterTag.tagName.equals(item.tagName)
        }
        tagListWithSize.sortByDescending { it -> it.tagName }
        filterTagListingAdapter.submitList(tagListWithSize.asReversed())
        filterTagListingAdapter.notifyDataSetChanged()
        applyTagFilter(item.tagName)

    }


    private fun uploadPDF() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        startActivityForResult(intent, 112)
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

   /* private fun showBottomAddMemberInputDialogue() {
        val li = LayoutInflater.from(FourBaseCareApp.activityFromApp)
        val myView: View = li.inflate(R.layout.bottom_dialogue_camera_or_gallery, null)

        bottomAddFamilyMemberInputDIalogue = FragmentModalBottomSheet(myView)
        bottomAddFamilyMemberInputDIalogue.show(
            FourBaseCareApp.activityFromApp.supportFragmentManager,
            "BottomSheet Fragment"
        )

        val linGallery: LinearLayout = myView.findViewById(R.id.linGallery)
        val linCamera: LinearLayout = myView.findViewById(R.id.linCamera)

        linGallery.setOnClickListener(View.OnClickListener {
            Log.d("bottom_click_log","Gallery clicked")
            if(!isDoubleClickBottom()){
                uploadPDF()
                Log.d("bottom_click_log","came gallery")
            }
            bottomAddFamilyMemberInputDIalogue.dismiss()
        })

        linCamera.setOnClickListener(View.OnClickListener {
            Log.d("bottom_click_log","Camera clicked")
            if(!isDoubleClickBottom())
                Log.d("bottom_click_log","came camera")
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    ScanReportFragment(), this, false
                )
            bottomAddFamilyMemberInputDIalogue.dismiss()
        })


    }*/

    private fun showHideNoData(shouldSHow: Boolean){
        if(shouldSHow){
            binding.recyclerView.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
            Log.d("no_data_show","no data showing")
        }else{
            binding.recyclerView.visibility = View.VISIBLE
            binding.tvNoData.visibility = View.GONE
            Log.d("no_data_show","no data hidden")

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

    fun isDoubleClickItem(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickItemTime < MAX_CLICK_INTERVAL) {
            Log.d("Returned", "Returned Fragment")
            return true
        }
        Log.d("Success", "Success")
        mLastClickItemTime = SystemClock.elapsedRealtime()
        return false
    }

    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent?) {
            Log.d("multiple_share", "download success ")
            if(intent != null){
                count += 1
                var downloadId = intent.getExtras()?.getLong(DownloadManager.EXTRA_DOWNLOAD_ID)
                Log.d("multiple_share", "download id "+downloadId)
                if(downloadId != null){
                    var uriId = downloadManager.getUriForDownloadedFile(downloadId)
                    Log.d("multiple_share", "uriId "+uriId)
                    shareURIs.add(uriId)
                }else{
                    //showToast(FourBaseCareApp.activityFromApp,"DOwnload id is null")
                }

                Log.d("multiple_share", "count "+count)
                Log.d("multiple_share", "total counts "+totalCounts)
                Log.d("multiple_share", "share mode on "+IS_SHARE_MODE)
                Log.d("multiple_share", "DOwnloadURLs "+downloadUrls.size)

                if(IS_SHARE_MODE && count.equals(totalCounts)){
                    showHideProgress(false, binding.layoutProgress.frProgress)
                    shareMultipleFilesTest(shareURIs)
                    IS_SHARE_MODE = false
                    totalCounts = 0
                    count = 0
                    shareURIs.clear()
                }
                Log.d("multiple_share", "count "+count)
                Log.d("multiple_share", "count "+totalCounts)
                if(count == totalCounts){
                    totalCounts = 0
                    showHideProgress(false, binding.layoutProgress.frProgress)
                }

            }else{
                showHideProgress(false, binding.layoutProgress.frProgress)
                Log.d("multiple_share", "download intent uri null")
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

    override fun onMonthSelected(position: Int, item: MonthyRecords, view: View) {

    }

    private fun showDeleteConfirmDialogue(obj: Record) {
        deleteConfirmationDialogue = Dialog(FourBaseCareApp.activityFromApp)
        deleteConfirmationDialogue.setContentView(R.layout.dialogue_delete_records)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(deleteConfirmationDialogue.window?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = deleteConfirmationDialogue?.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        deleteConfirmationDialogue.window?.setAttributes(lp)
        deleteConfirmationDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent);

        val btnDelete: TextView = deleteConfirmationDialogue.findViewById(R.id.btnDelete)


        btnDelete.setOnClickListener(View.OnClickListener {
            deleteRecordFromServer(obj)
            deleteConfirmationDialogue.dismiss()
        })

        val btnCancel: TextView = deleteConfirmationDialogue.findViewById(R.id.btnCancel)
        btnCancel.setText("Cancel")

        btnCancel.setOnClickListener(View.OnClickListener {
            deleteConfirmationDialogue.dismiss()
        })

        deleteConfirmationDialogue.show()
    }

    private fun deleteRecordFromServer(item: Record) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            recordsViewModel.callDeleteRecord(
                getUserObject().role,
                ""+getPatientObjectByCG()?.id,
                getUserAuthToken(),
                "" + item.id

            )
            Log.d("delete_log", "API called")

        }
    }

    private fun shareFile() {
        try {
            val uri = FileProvider.getUriForFile(
                FourBaseCareApp.activityFromApp,
                BuildConfig.APPLICATION_ID + ".provider",
                pdfFile!!
            )
            //Uri uri = Uri.parse(pdfFile);//FileProvider.getUriForFile(FourBaseCareApp.activityFromApp, BuildConfig.APPLICATION_ID + ".provider",pdfFile);
            val share = Intent()
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            share.action = Intent.ACTION_SEND
            share.type = "application/pdf"
            share.putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                    FourBaseCareApp.activityFromApp,
                    BuildConfig.APPLICATION_ID + ".provider",
                    pdfFile!!
                )
            )
            share.putExtra(Intent.EXTRA_SUBJECT, "File share from Oncobuddy")
            share.putExtra(
                Intent.EXTRA_TEXT,
                """
                Hello! I am sharing this file using Oncobuddy. Please download the app using following link.
                
                https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                """.trimIndent()
            )
            startActivity(share)
        } catch (e: java.lang.Exception) {
            Log.d("share_pdf_log", e.toString())
            Toast.makeText(
                FourBaseCareApp.activityFromApp.applicationContext,
                "There was an issue in sharing this file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun checkPermission(context: Context?): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((context as Activity?)!!, Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    Log.d("permission_log","Coming here")
                    val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(
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
                                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                            ),
                            ADD_DOCUMENT_PERMISSION
                        )
                    }
                    val alert = alertBuilder.create()
                    alert.setCancelable(false)
                    alert.show()
                } else {
                    Log.d("permissions_log","Came here to take permissions")
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                        Log.d("permissions_log","External storage manager "+Environment.isExternalStorageManager())
                        if (Environment.isExternalStorageManager()) {
                            uploadPDF()
                        }else{
                            askAndroidRStoragePermission()
                        }

                    }else{
                        Log.d("permissions_log","Less than R permissions")
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                            ),
                            ADD_DOCUMENT_PERMISSION
                        )
                    }


                }
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    private fun askAndroidRStoragePermission() {
        try {
            Log.d("permissions_log", "Android R")
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(
                java.lang.String.format(
                    "package:%s",
                    FourBaseCareApp.activityFromApp.getPackageName()
                )
            )
            startActivityForResult(intent, Constants.OS_R_STORAGE_PERMISSION)
        } catch (e: java.lang.Exception) {
            Log.d("permissions_log", "Permission Exception " + e.toString())
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            startActivityForResult(intent, Constants.OS_R_STORAGE_PERMISSION)
        }
    }

    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()) deleteRecursive(
            child
        )
        fileOrDirectory.delete()
    }

    private fun deleteDirectory() {
        val dir = File(FourBaseCareApp.activityFromApp.getFilesDir().toString() + "/DirName")
        if (dir.isDirectory) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                File(dir, children[i]).delete()
            }
        }
        if (dir.isDirectory) for (child in dir.listFiles()) deleteRecursive(child)
        dir.delete()
        Log.d("delete_data_log","Directory deleted")
    }


}


