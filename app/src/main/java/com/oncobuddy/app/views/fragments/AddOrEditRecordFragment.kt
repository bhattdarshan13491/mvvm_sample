package com.oncobuddy.app.views.fragments


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.text.format.DateFormat
import android.text.format.Time
import android.util.Log
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.cmexpertise.dabcustomtagslibrary.models.TagModel
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentAddOrUpdateRecordsNewBinding
import com.oncobuddy.app.models.injectors.RecordsInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.PDFExtractedData
import com.oncobuddy.app.models.pojo.records_list.AddRecordInput
import com.oncobuddy.app.models.pojo.records_list.Record
import com.oncobuddy.app.models.pojo.response_categories.ReportCategory
import com.oncobuddy.app.models.pojo.response_categories.ResponseCategories
import com.oncobuddy.app.models.pojo.response_sub_categories.CategoryIds
import com.oncobuddy.app.models.pojo.response_sub_categories.ResponseSubCategories
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.RecordsViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class AddOrEditRecordFragment : BaseFragment() {

    private lateinit var binding: FragmentAddOrUpdateRecordsNewBinding
    private lateinit var pdfExtractedData: PDFExtractedData
    private lateinit var tagModelListMain: ArrayList<TagModel>
    private lateinit var tagModelListCategories: ArrayList<TagModel>
    private lateinit var tagModelListSubCategories: ArrayList<TagModel>
    private lateinit var recordsViewModel: RecordsViewModel
    private lateinit var deleteConfirmationDialogue: Dialog
    private lateinit var recordSavedDialogue: Dialog
    private lateinit var confirmTagsDilogue: Dialog
    private var IS_UPDATE_MODE = false
    private var selectedDate = ""
    private lateinit var recordObj: Record
    private lateinit var FILE_PATH: String
    private lateinit var FILE_URL: String
    private lateinit var FILE_NAME: String
    private var FILE_SIZE = 1
    private var selectTab = 1
    private val FIRST_TAB = 1
    private val SECOND_TAB = 2
    private val THIRD_TAB = 3
    private val FOURTH_TAB = 4
    private var IS_VIEWING_DOCUMENT = false
    private val bills_tag_list = arrayOf("Test/Scan", "Consultation", "Treatment", "Medicine")
    private var reportName = ""
    private var RECORD_TYPE = Constants.RECORD_TYPE_REPORT

    private lateinit var categoriesList: ArrayList<ReportCategory>
    private lateinit var subCategoriesList: ArrayList<String>

    private fun getCategoriesList() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            recordsViewModel.callGetCategories(getUserAuthToken())
        }
    }

    private fun getSubCategoriesList(categoryIds: CategoryIds) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            var categoryIds = CategoryIds()
            categoryIds.categoryIds = intArrayOf(1,2,3)
            recordsViewModel.callGetSubCategories(getUserAuthToken(), categoryIds)
        }
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

    private fun setupVM() {
        recordsViewModel = ViewModelProvider(
            this,
            RecordsInjection.provideViewModelFactory()
        ).get(RecordsViewModel::class.java)
    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_or_update_records_new, container, false
        )

        setupVM()

        setupObservers()

        setupArgumentData()

        setupText()

        setupClickListeners()

        showHideBottomButton()
    }

    private fun setupArgumentData() {
        if (arguments != null) {

            if (arguments!!.containsKey(Constants.RECORD_OBJ)) {
                Log.d("new_record_log", "update true")
                IS_UPDATE_MODE = true
                binding.ivBack.visibility = View.VISIBLE
                binding.tvTitle.setText(getString(R.string.update_record))
                recordObj = arguments!!.getParcelable(Constants.RECORD_OBJ)!!
                selectedDate = CommonMethods.utcTOLocalDate(recordObj.recordDate)
                binding.etReportDate.setText(selectedDate)
                //selectedDate = ""+recordObj.recordDatea
                FILE_URL = recordObj.link
                val separated = recordObj.link.split("/".toRegex()).toTypedArray()
                val separatedDash =
                    separated[separated.size - 1].split("-".toRegex()).toTypedArray()
                FILE_NAME = separatedDash[separatedDash.size - 1]
                FILE_SIZE = recordObj.size.toInt()
                binding.tvFileName.text = FILE_NAME
                Log.d("new_record_log", "update " + FILE_NAME)


                     Log.d("new_record_log", "record type "+recordObj.recordType)
                     RECORD_TYPE = recordObj.recordType
                     Log.d("new_record_log", "update " + RECORD_TYPE)
                     binding.tvReportType.text = RECORD_TYPE

                     if (RECORD_TYPE.equals(Constants.RECORD_TYPE_REPORT)) {
                         binding.linTagsContainer.visibility = View.VISIBLE
                         getCategoriesList()
                     } else if (RECORD_TYPE.equals(Constants.RECORD_TYPE_BILL)) {
                         binding.linBillTag.visibility = View.VISIBLE
                         Log.d("new_record_log", "update bill tags visible "+recordObj.tags.get(0))
                         binding.etReportName.setText(recordObj.title)
                         binding.etBillTag.setText(recordObj.tags.get(0))
                         binding.linReportsNameContainer.visibility = View.VISIBLE
                         binding.linTagsContainer.visibility = View.GONE
                     } else {
                         binding.linReportsNameContainer.visibility = View.VISIBLE
                         binding.linTagsContainer.visibility = View.GONE
                         binding.linBillTag.visibility = View.GONE
                         binding.etReportName.setText(recordObj.title)
                     }






            } else {
                IS_UPDATE_MODE = false
                Log.d("new_record_log", "update false")
                //binding.ivBack.visibility = View.INVISIBLE
                //   binding.linDeleteReccord.visibility = View.GONE
                //  binding.linDocument.visibility = View.VISIBLE
                FILE_PATH = arguments!!.getString(Constants.PDF_PATH, "")
                FILE_URL = arguments!!.getString(Constants.SERVER_FILE_URL, "")
                Log.d("update_log", "path " + FILE_PATH)
                val file = File(FILE_PATH)
                FILE_NAME = file.name
                FILE_SIZE = getFileSize(file)
                Log.d("update_log", "file name " + FILE_NAME)
                binding.tvFileName.text = FILE_NAME
                if (checkIFDoctor()) {
                    setSelectedTab(SECOND_TAB)   // If doctor logged in, He/she will most probably add prescriptions
                    var strTodayDate = CommonMethods.getDateFromTimeStamp(System.currentTimeMillis())
                    binding.etReportDate.setText(
                        CommonMethods.getReportDateForPresciption(
                            strTodayDate
                        )
                    )
                }

                if (arguments!!.containsKey(Constants.RECORD_TYPE)) {
                    RECORD_TYPE =
                        arguments!!.getString(Constants.RECORD_TYPE, Constants.RECORD_TYPE_REPORT)
                    Log.d("new_record_log","0 "+RECORD_TYPE)
                    binding.tvReportType.text = RECORD_TYPE

                    if(RECORD_TYPE.equals(Constants.RECORD_TYPE_REPORT)){
                        binding.linTagsContainer.visibility = View.VISIBLE
                        getCategoriesList()
                    }else if(RECORD_TYPE.equals(Constants.RECORD_TYPE_BILL)){
                        uploadFileToS3()
                        binding.linBillTag.visibility = View.VISIBLE
                        Log.d("new_record_log","1 bill tags visible ")
                        binding.linReportsNameContainer.visibility = View.VISIBLE
                        binding.linTagsContainer.visibility = View.GONE
                    }else{
                        uploadFileToS3()
                        binding.linReportsNameContainer.visibility = View.VISIBLE
                        binding.linTagsContainer.visibility = View.GONE
                        binding.linBillTag.visibility = View.GONE
                    }


                }

            }

        }
    }

    private fun setupObservers() {
        recordsViewModel.responseDeleteRecord.observe(this, deleteResponseObserver)
        recordsViewModel.responseCategoriesData.observe(this, categoriesResponseData)
        recordsViewModel.responseSubCategoriesData.observe(this, subCategoriesResponseData)
        recordsViewModel.responseRecordAddOrUpdate.observe(this, addOrUpdateresponseObserver)
        recordsViewModel.responseFileUploadData.observe(this, responseObserver)
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
                    Constants.IS_LIST_UPDATED = true
                    fragmentManager?.popBackStack()
                    binding.executePendingBindings()
                    binding.invalidateAll()
                }
            }

       }

    private val categoriesResponseData =
        androidx.lifecycle.Observer<ResponseCategories?> { responseObserver ->

            Log.d("new_record_log", "categories API")
            if (responseObserver != null) {
                if (responseObserver.isSuccess) {
                     categoriesList = ArrayList()
                     categoriesList.addAll(responseObserver.payLoad)
                     Log.d("new_style_log", "categories found "+categoriesList.size)
                     var categoryIds = CategoryIds()
                     categoryIds.categoryIds = intArrayOf(1,2,3)
                     getSubCategoriesList(categoryIds)
                     setupTagCategoriesData(responseObserver)
                     binding.executePendingBindings()
                     binding.invalidateAll()

                }
            }

        }

    private val subCategoriesResponseData =
        androidx.lifecycle.Observer<ResponseSubCategories?> { responseObserver ->

            if (responseObserver != null) {
                if (responseObserver.isSuccess) {
                    subCategoriesList = ArrayList()
                    subCategoriesList.addAll(responseObserver.payLoad)
                    setupSubCategoriesData(responseObserver)
                    Log.d("new_style_log", "sub categories found "+subCategoriesList.size)
                    binding.executePendingBindings()
                    binding.invalidateAll()
                }
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
                        CommonMethods.showLog("button_height", "button hidden")
                       // binding.relBottom.setVisibility(View.GONE)

                    } else {
                        //ok now we know the keyboard is down...
                        CommonMethods.showLog("button_height", "button visible")
                        //binding.relBottom.setVisibility(View.VISIBLE)
                    }
                }
            })
    }

    private fun setupClickListeners() {

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

        binding.tvViewReport.setOnClickListener(View.OnClickListener {

            if(!isDoubleClick()) {
                showToast(FourBaseCareApp.activityFromApp, "Opening document")
                showPDFFragment(FILE_URL)
            }
        })

        val spinner_bill_tags = ArrayAdapter(
            FourBaseCareApp.activityFromApp,
            android.R.layout.simple_spinner_dropdown_item,
            bills_tag_list
        )

        binding.etBillTag.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                AlertDialog.Builder(FourBaseCareApp.activityFromApp)
                    .setTitle("Select Tag")
                    .setAdapter(spinner_bill_tags,
                        DialogInterface.OnClickListener { dialog, which ->
                            binding.etBillTag.setText(bills_tag_list[which])
                            dialog.dismiss()
                        }).create().show()
            }
        })


        binding.ivBack.setOnClickListener(View.OnClickListener {
            handleBackPress()
        })


        binding.etReportDate.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) showDatePickerDialogue()
        })

        binding.relSave.setOnClickListener(View.OnClickListener {

            if (!isDoubleClick()) {
                if (::FILE_URL.isInitialized && !FILE_URL.isEmpty())
                    addRecordToServer()
            }

        })
    }

    private fun uploadFileToS3() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            Log.d("upload_log", "File path1.2 " + FILE_PATH)

            val body: MultipartBody.Part
            val file = File(FILE_PATH)

            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

            Log.d("f_n_log", "old_name " + file.name)
            var fileName =
                file.name.replace("[\\p{Punct}&&[^_]]+|^_+|\\p{Punct}+(?=_|$)".toRegex(), "")
            fileName = fileName.replace("+", "_")
            Log.d("f_n_log", "new_name " + fileName)
            body = MultipartBody.Part.createFormData("file", fileName + ".pdf", requestFile)

            recordsViewModel.callFileUpload(
                getUserAuthToken(), body
            )
            Log.d("upload_log", "2 " + FILE_PATH)

        }
    }

    private fun addRecordToServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp) && isValidInput()) {

            var addRecordInput = AddRecordInput()
            addRecordInput.title = getTrimmedText(binding.etReportName)
            addRecordInput.link = FILE_URL
            if (getUserObject().role.equals(Constants.ROLE_PATIENT)) {
                Log.d("upload_tag_log", "patient id " + Constants.PATIENT_ID_FOR_RECORDS)
                addRecordInput.patientId = getUserObject().userIdd
            }else if(getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER) && getPatientObjectByCG() != null){
                addRecordInput.patientId = getPatientObjectByCG()?.id
                Log.d("upload_tag_log", " cg patient id " + addRecordInput.patientId)
            }
            else {
                Log.d("upload_tag_log", "patient id " + Constants.PATIENT_ID_FOR_RECORDS)
                addRecordInput.patientId = Constants.PATIENT_ID_FOR_RECORDS.toInt()
            }

            addRecordInput.recordDate = selectedDate
            addRecordInput.recordType = RECORD_TYPE
            addRecordInput.size = FILE_SIZE
            //addRecordInput.title = reportName//getTrimmedText(binding.etReportName)

            var subCategoriesList = ArrayList<String>()
            var categoriesList = ArrayList<String>()
            Log.d("upload_tag_log", "initialized list")

          /*  if (selectTab == THIRD_TAB) {
                recordsList.add(getTrimmedText(binding.etBillTag))
                Log.d("upload_tag_log", "Added bill tag")
            } else {
                if (!binding.tagsEditText.tagModelListRemoved.isNullOrEmpty()) {
                    Log.d("upload_tag_log", "selection not empty")
                    for (tag in binding.tagsEditText.tagModelListRemoved) {
                        if (!recordsList.contains(tag.name)) {
                            recordsList.add(tag.name)
                            Log.d("upload_tag_log", "Added " + tag.name)
                        }

                    }
                }
            }*/

            if(RECORD_TYPE.equals(Constants.RECORD_TYPE_BILL)){
                subCategoriesList.add(getTrimmedText(binding.etBillTag))
            }else{
            if (!binding.tagsEditText.tagModelListRemoved.isNullOrEmpty()) {
                Log.d("new_style_log", "selection not empty")
                for (tag in binding.tagsEditText.tagModelListRemoved) {
                    if (!subCategoriesList.contains(tag.name)) {
                        subCategoriesList.add(tag.name)
                        Log.d("new_style_log", "Added " + tag.name)
                    }

                }
            }
            }


            if (!binding.tagsEditTextPrimary.tagModelListRemoved.isNullOrEmpty()) {
                Log.d("new_style_log", "selection not empty")
                for (tag in binding.tagsEditTextPrimary.tagModelListRemoved) {
                    if (!categoriesList.contains(tag.name)) {
                        categoriesList.add(tag.name)
                        Log.d("upload_tag_log", "Added " + tag.name)
                    }

                }
            }
            addRecordInput.extractedTags = subCategoriesList
            addRecordInput.tags = subCategoriesList
            addRecordInput.categories = categoriesList

            Log.d("upload_log", "update mode " + IS_UPDATE_MODE)

            if(RECORD_TYPE.equals(Constants.RECORD_TYPE_REPORT)){
                showConfirmTagsDialogue(addRecordInput)
            }else{
                saveRecordOnServer(addRecordInput)
            }

        }
    }

    private fun saveRecordOnServer(addRecordInput: AddRecordInput) {
        if (IS_UPDATE_MODE) {
            if(getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
                recordsViewModel.callupdateRecord(
                    getUserObject().role, addRecordInput, getUserAuthToken(),""+getPatientObjectByCG()?.id,
                    getUserIdd().toString(), "" + recordObj.id
                )
            }else{
                recordsViewModel.callupdateRecord(
                    getUserObject().role, addRecordInput, getUserAuthToken(),"",
                    getUserIdd().toString(), "" + recordObj.id
                )
            }

        } else {
            recordsViewModel.callAddRecord(getUserObject().role, addRecordInput, getUserAuthToken(), getUserIdd().toString())
        }
    }

    private fun setupText() {
        if (arguments != null) {

            if (arguments!!.getParcelable<Parcelable?>(Constants.PDF_CLASS_DATA) != null) {

                pdfExtractedData = arguments!!.getParcelable(Constants.PDF_CLASS_DATA)!!

                Log.d("new_style_log", "Date from Arguments " + pdfExtractedData.consulationDate)
                Log.d("new_style_log", "Primary tags from arguments " + pdfExtractedData.primaryTags.size)
                Log.d("etxract_date_log", "Secondary Tags from args " + pdfExtractedData.tags.size)

                /*var dateObj = pdfExtractedData.consulationDate
                if(!CommonMethods.getDateFormat(pdfExtractedData.consulationDate).equals("yyyy-MM-dd",true)){
                    dateObj = CommonMethods.convertDateToSaveRecords(pdfExtractedData.consulationDate)
                }*/
                binding.etReportDate.setText(CommonMethods.convertDateToSaveRecords(pdfExtractedData.consulationDate))

                selectedDate = "" + CommonMethods.convertDateToSaveRecords(pdfExtractedData.consulationDate)

                if (pdfExtractedData.reportType != null) {
                    if (pdfExtractedData.reportType.equals("report", true)) {
                        setSelectedTab(FIRST_TAB)
                    } else if (pdfExtractedData.reportType.equals("prescription", true)) {
                        setSelectedTab(SECOND_TAB)
                    } else {
                        setSelectedTab(THIRD_TAB)
                    }
                }

                /*if (!pdfExtractedData.hospitalName.isNullOrEmpty()) {
                    var foundHospital = false
                    for (hospital in hospitalList) {
                        if (hospital.name.contains(pdfExtractedData.hospitalName, true)) {
                            foundHospital = true
                            Log.d("hospital_log", "Yes " + hospital.name)
                        } else {
                            Log.d("hospital_log", "No")
                        }
                    }
                    if (!foundHospital) {
                        Log.d("hospital_log", "Outside hospital " + pdfExtractedData.hospitalName)
                    }
                }*/


            } else {
                Log.d("pdf_extract_log ", "4 null ")
                Log.d("etxract_date_log", "Data is null")
                pdfExtractedData = PDFExtractedData()
            }

            /*setupInitialListData()
            setUpTagEditText()*/


        }
    }

    private fun showDatePickerDialogue() {
        val calendar = Calendar.getInstance()
        val yy = calendar[Calendar.YEAR]
        val mm = calendar[Calendar.MONTH]
        val dd = calendar[Calendar.DAY_OF_MONTH]
        val datePicker =
            DatePickerDialog(
                FourBaseCareApp.activityFromApp,
                DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val chosenDate = Time()
                    chosenDate[dayOfMonth, monthOfYear] = year
                    val dtDob = chosenDate.toMillis(true)
                    var strDate = DateFormat.format("yyyy-MM-dd", dtDob)
                    selectedDate = strDate.toString()
                    binding.etReportDate.setText(strDate)

                }, yy, mm, dd
            )

        datePicker.datePicker.maxDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    private fun setupTagCategoriesData(responseCategories: ResponseCategories){
        tagModelListCategories = ArrayList<TagModel>()

        val tagsResponse = responseCategories

        val tagsList = tagsResponse?.payLoad

        if (tagsList != null) {
            for (tag in tagsList) {
                tagModelListCategories.add(TagModel("" + tag.id, tag.name))
            }
        }

        Log.d("new_style_log", "categories Added after matching " + tagModelListCategories.size)


        binding.tagsEditTextPrimary.setTagsWithSpacesEnabled(true)

        binding.tagsEditTextPrimary.initializeAdapter(
            FourBaseCareApp.activityFromApp,
            R.layout.row_tag,
            tagModelListCategories
        )

        if(IS_UPDATE_MODE){
            if(!recordObj.categories.isNullOrEmpty()){
                val myarray = arrayOfNulls<String>(recordObj.categories.size)

                Log.d("new_record_log", "update mode here found " + recordObj.size);

                for (i in recordObj.categories.indices) {
                    if(recordObj.categories.get(i) != null){
                        myarray[i] = recordObj.categories.get(i)
                        Log.d("new_record_log", "Added in removed "+myarray[i])

                        binding.tagsEditTextPrimary.tagModelListRemoved.add(TagModel("01", recordObj.categories.get(i)))
                        Log.d("new_record_log", "Added in array " + recordObj.categories.get(i))
                    }else{
                        Log.d("new_record_log", "Null captured")
                    }

                }
                binding.tagsEditTextPrimary.setTags(myarray)
            }

        }else{
  if (::pdfExtractedData.isInitialized) {
            if (pdfExtractedData != null && pdfExtractedData.primaryTags != null) {

                val myarray = arrayOfNulls<String>(pdfExtractedData.primaryTags.size)

                Log.d("new_style_log", "here found " + pdfExtractedData.primaryTags.size);

                for (i in pdfExtractedData.primaryTags.indices) {
                    if(pdfExtractedData.primaryTags.get(i).getName() != null){
                        myarray[i] = pdfExtractedData.primaryTags.get(i).getName()
                        Log.d("new_style_log", "Added in removed "+myarray[i])

                        binding.tagsEditTextPrimary.tagModelListRemoved.add(TagModel("01", pdfExtractedData.primaryTags.get(i).getName()))
                        Log.d("new_style_log", "Added in array " + pdfExtractedData.primaryTags.get(i).name)
                    }else{
                        Log.d("new_style_log", "Null captured")
                    }

                }
                binding.tagsEditTextPrimary.setTags(myarray)
            }
        }

        }

    }

    private fun setupSubCategoriesData(responseSubCategories: ResponseSubCategories) {
        tagModelListSubCategories = ArrayList<TagModel>()

        val tagsResponse = responseSubCategories

        val tagsList = tagsResponse?.payLoad

        if (tagsList != null) {
            for (tag in tagsList) {
                tagModelListCategories.add(TagModel("0", tag))
            }
        }

        Log.d(
            "new_record_log_s",
            "SUbcategories Added after matching" + tagModelListSubCategories.size
        )


        binding.tagsEditText.setTagsWithSpacesEnabled(true)

        binding.tagsEditText.initializeAdapter(
            FourBaseCareApp.activityFromApp,
            R.layout.row_tag,
            tagModelListCategories
        )

        if (IS_UPDATE_MODE) {
            if (!recordObj.tags.isNullOrEmpty()) {
                val myarray = arrayOfNulls<String>(recordObj.tags.size)

                Log.d("new_record_log", "update mode here found " + recordObj.size);

                for (i in recordObj.tags.indices) {
                    if (recordObj.tags.get(i) != null) {
                        myarray[i] = recordObj.tags.get(i)
                        Log.d("new_record_log", "Added in removed " + myarray[i])

                        binding.tagsEditText.tagModelListRemoved.add(
                            TagModel(
                                "01",
                                recordObj.tags.get(i)
                            )
                        )
                        Log.d("new_record_log", "Added in array " + recordObj.tags.get(i))
                    } else {
                        Log.d("new_record_log", "Null captured")
                    }

                }
                binding.tagsEditText.setTags(myarray)
            }

        } else {
            if (::pdfExtractedData.isInitialized) {
                if (pdfExtractedData != null && pdfExtractedData.tags != null) {

                    val myarray = arrayOfNulls<String>(pdfExtractedData.tags.size)

                    Log.d("new_style_log", "here found " + pdfExtractedData.tags.size);

                    for (i in pdfExtractedData.tags.indices) {
                        if (pdfExtractedData.tags.get(i).getName() != null) {
                            myarray[i] = pdfExtractedData.tags.get(i).getName()
                            Log.d("new_style_log", "Added in removed " + myarray[i])
                            binding.tagsEditText.tagModelListRemoved.add(
                                TagModel(
                                    "01",
                                    pdfExtractedData.tags.get(i).getName()
                                )
                            )
                            Log.d(
                                "new_style_log",
                                "Added in array " + pdfExtractedData.tags.get(i).name
                            )
                        } else {
                            Log.d("new_style_log", "Null captured")
                        }

                    }
                    binding.tagsEditText.setTags(myarray)
                }
            }


        }
    }

    private fun setupInitialListData() {

        tagModelListMain = ArrayList<TagModel>()

        val tagsResponse = CommonMethods.getTagsFromAssets()

        val tagsList = tagsResponse?.data

        if (tagsList != null) {
            for (tag in tagsList) {
                tagModelListMain.add(TagModel("" + tag.id, tag.name))
            }
        }

        Log.d("tag_list", "Added " + tagModelListMain.size)


        binding.tagsEditText.setTagsWithSpacesEnabled(true)

        binding.tagsEditText.initializeAdapter(
            FourBaseCareApp.activityFromApp,
            R.layout.row_tag,
            tagModelListMain
        )

        binding.tagsEditTextPrimary.initializeAdapter(
            FourBaseCareApp.activityFromApp,
            R.layout.row_tag,
            tagModelListMain
        )

        if (IS_UPDATE_MODE) {
            var existingTags = ArrayList<String>()
            existingTags.addAll(recordObj.tags)
            Log.d("tags_log", "0")
            if (existingTags.size > 0) {
                Log.d("tags_log", "size " + existingTags.size)
                val tagsArray = arrayOfNulls<String>(existingTags.size)
                for (i in existingTags.indices) {
                    tagsArray[i] = existingTags.get(i)
                    Log.d("upload_tag_log", "Added in removed in edit mode")
                    binding.tagsEditText.tagModelListRemoved.add(
                        TagModel(
                            "01",
                            existingTags.get(i)
                        )
                    )
                }

                if (selectTab == THIRD_TAB) {
                    binding.etBillTag.setText(tagsArray[0])
                } else {
                    //binding.tagsEditText.setTags(tagsArray)
                    binding.tagsEditText.setTags(tagsArray[1])
                  //  binding.tagsEditTextPrimary.setTags(tagsArray[0])
                    reportName = tagsArray.get(0).toString()
                    Log.d("report_name_log","1 "+tagsArray[0])
                }

            }
        } else {
            if (::pdfExtractedData.isInitialized) {
                if (pdfExtractedData != null && pdfExtractedData.tags != null) {

                    val myarray = arrayOfNulls<String>(pdfExtractedData.tags.size)

                    Log.d("report_name_log", "here found " + pdfExtractedData.tags.size);

                    for (i in pdfExtractedData.tags.indices) {
                        myarray[i] = pdfExtractedData.tags.get(i).getName()
                        Log.d("report_name_log", "Added in removed "+myarray[i])

                        binding.tagsEditText.tagModelListRemoved.add(TagModel("01", pdfExtractedData.tags.get(i).getName()))
                        binding.tagsEditTextPrimary.tagModelListRemoved.add(
                            TagModel(
                                "01",
                                pdfExtractedData.tags.get(i).getName()
                            )
                        )
                    }

                    if(myarray.size > 1){
                        binding.tagsEditText.setTags(myarray[0])
                    }
                    if(myarray.size> 0){
                      //  binding.tagsEditTextPrimary.setTags(myarray[0])
                        reportName = myarray.get(0).toString()
                    }

                }
            }
        }


    }

    private fun setSelectedTab(activeTab: Int) {

        selectTab = activeTab
        Log.d("edit_log", "selected tab " + activeTab)
        //binding.spReportType.setSelection(selectTab - 1)
        if (selectTab == THIRD_TAB) {
            binding.linBillTag.visibility = View.VISIBLE
            Log.d("new_record_log","1 bill tags gone")
            binding.tagsEditText.visibility = View.GONE
        } else {
            binding.linBillTag.visibility = View.GONE
            Log.d("new_record_log","1 bill tags gone")
            binding.tagsEditText.visibility = View.VISIBLE
        }

    }

    fun backToRecords() {

        val fm: FragmentManager = FourBaseCareApp.activityFromApp.getSupportFragmentManager()
        val count = fm.backStackEntryCount

        if (checkIFDoctor()) {
            for (i in 0 until count) {
                fm.popBackStackImmediate()
                Log.d("back_log", "remove i " + count)
                Log.d("back_log", "remove COunt " + count)
            }
        } else if (checkIFCareCOmpanion()) {
            for (i in 0 until count - 1) {
                fm.popBackStackImmediate()
                Log.d("back_log", "remove i " + count)
                Log.d("back_log", "remove COunt " + count)
            }
        } else {
            for (i in 0 until count) {
                fm.popBackStackImmediate()
                Log.d("back_log", "remove i " + count)
                Log.d("back_log", "remove COunt " + count)
            }
        }

    }

    private fun showRecordAddedDialogue() {
        recordSavedDialogue = Dialog(FourBaseCareApp.activityFromApp)
        recordSavedDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        recordSavedDialogue.setContentView(R.layout.dialogue_cancel_appointment)

        val ivLogo: ImageView = recordSavedDialogue.findViewById(R.id.ivLogo)
        ivLogo.setImageDrawable(FourBaseCareApp.activityFromApp.getDrawable(R.drawable.logo_record_saved_confirmation))

        val tvTitleText: TextView = recordSavedDialogue.findViewById(R.id.tvTitleText)
        /*if(Constants.IS_FROM_ADD_APPOINTMENT){
            tvTitleText.setText(getString(R.string.record_added_successfully))
        }else{
            tvTitleText.setText(getString(R.string.record_added_successfully))
        }*/
        tvTitleText.setText(getString(R.string.record_added_successfully))

        val btnNo: TextView = recordSavedDialogue.findViewById(R.id.btnNo)
        btnNo.visibility = View.GONE

        val linNo: LinearLayout = recordSavedDialogue.findViewById(R.id.linNo)
        linNo.visibility = View.GONE

        val btnYes: TextView = recordSavedDialogue.findViewById(R.id.btnYes)

        if (Constants.IS_FROM_HOME_SCREEN) {
            btnYes.setText(getString(R.string.go_back_to_home))
            Constants.IS_FROM_HOME_SCREEN = false
        } else if (Constants.IS_FROM_CONSULTATION_SCREEN) {
            btnYes.setText(getString(R.string.go_back_to_consultation))
            Constants.IS_FROM_CONSULTATION_SCREEN = false
        }else if (Constants.IS_FROM_PATIENT_LIST_SCREEN) {
            btnYes.setText(getString(R.string.go_back_to_patient_list))
            Constants.IS_FROM_PATIENT_LIST_SCREEN = false
        }  else {
            btnYes.setText(getString(R.string.go_back_to_record_listings))
        }


        btnYes.setOnClickListener(View.OnClickListener {
            recordSavedDialogue.dismiss()
            //fragmentManager?.popBackStack()
            backToRecords()
        })

        recordSavedDialogue.show()
    }

    private val responseObserver = androidx.lifecycle.Observer<BaseResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        FILE_URL = responseObserver.message
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val addOrUpdateresponseObserver =
        androidx.lifecycle.Observer<BaseResponse> { responseObserver ->
            //binding.loginModel = loginResponseData
            if (responseObserver.success) {
                Constants.IS_LIST_UPDATED = true
                showRecordAddedDialogue()
            }

            binding.executePendingBindings()
            binding.invalidateAll()


        }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean> { isLoading ->
        if (isLoading) showHideProgress(true, binding.layoutProgress.frProgress)
        else showHideProgress(false, binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = androidx.lifecycle.Observer<String> { message ->
        Log.d("upload_log", "Error " + message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private fun getFileSize(file: File): Int {
        val l = file.length() / 1024 / 1024
        val fileSize = l.toString()
        var finalFileSize = fileSize.toInt()

        if (finalFileSize == 0) {
            finalFileSize = 1 // because size 0 will create error at api side
        }

        return finalFileSize
    }

    private fun isValidInput(): Boolean {
        if (!RECORD_TYPE.equals(Constants.RECORD_TYPE_REPORT) && getTrimmedText(binding.etReportName).isNullOrBlank()) {
            showToast(FourBaseCareApp.activityFromApp, "Please enter report title")
            return false
        }else if (selectedDate.isNullOrEmpty()) {
            showToast(FourBaseCareApp.activityFromApp, "Please enter report date")
            return false
        }
        return true
    }

    private fun showConfirmTagsDialogue(addRecordInput: AddRecordInput) {

        confirmTagsDilogue = Dialog(FourBaseCareApp.activityFromApp)
        confirmTagsDilogue.setContentView(R.layout.tags_confirm_dialogue)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(confirmTagsDilogue.window?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = confirmTagsDilogue?.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        confirmTagsDilogue.window?.setAttributes(lp)
        confirmTagsDilogue.window?.setBackgroundDrawableResource(android.R.color.transparent);

        val btnNo: TextView = confirmTagsDilogue.findViewById(R.id.btnNo)
        val btnYes: TextView = confirmTagsDilogue.findViewById(R.id.btnYes)

        btnYes.setOnClickListener(View.OnClickListener {
            confirmTagsDilogue.dismiss()
            saveRecordOnServer(addRecordInput)

        })

        btnNo.setOnClickListener(View.OnClickListener {
            confirmTagsDilogue.dismiss()
        })

        confirmTagsDilogue.show()

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
            if(getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
                recordsViewModel.callDeleteRecord(
                    getUserObject().role,
                    ""+getPatientObjectByCG()?.id,
                    getUserAuthToken(),
                    "" + item.id)
            }else{
                recordsViewModel.callDeleteRecord(
                    getUserObject().role,
                    "",
                    getUserAuthToken(),
                    "" + item.id)
            }

            Log.d("delete_log", "API called")

        }
    }

    private fun showPDFFragment(link: String) {
        try {
          /*  Timer().schedule(object : TimerTask(){
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

            }, 1)*/

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

            CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
        } catch (e: Exception) {
            Log.d("open_exec","err "+e.toString())
        }
    }


    fun handleBackPress(){
        if (!isDoubleClick()) {
            Log.d("back_press_log","0")
            if(IS_VIEWING_DOCUMENT){
                Log.d("back_press_log","viewing doc")
                try {
                        binding.relFragmentContainer.visibility = View.GONE
                        IS_VIEWING_DOCUMENT = false
                    } catch (e: Exception) {
                        Log.d("open_exec","err 2 "+e.toString())
                    Log.d("back_press_log","view doc Err "+e.toString())
                    }
            }else{
                Log.d("back_press_log","not viwing doc")
                //fragmentManager?.popBackStack()
                //fragmentManager?.popBackStack()
                val fm: FragmentManager = FourBaseCareApp.activityFromApp.getSupportFragmentManager()
                val count: Int = fm.getBackStackEntryCount()
                Constants.IS_LIST_UPDATED = true
                for (i in 0 until count) {
                    fm.popBackStackImmediate()
                }
            }
        }
    }



}