package com.oncobuddy.app.views.fragments

import android.Manifest
import com.oncobuddy.app.utils.CommonMethods.addNextFragment
import android.annotation.SuppressLint
import com.oncobuddy.app.utils.background_task.GetPdfDetailsTask.GetPdfDetailsInterface
import android.graphics.pdf.PdfRenderer
import com.oncobuddy.app.models.pojo.PDFExtractedData
import androidx.recyclerview.widget.RecyclerView
import com.oncobuddy.app.views.adapters.PdfPagesAdapter
import android.graphics.Bitmap
import com.oncobuddy.app.R
import com.oncobuddy.app.models.pojo.login_response.LoginDetails
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import android.provider.MediaStore
import androidx.core.content.FileProvider
import android.app.DownloadManager
import androidx.annotation.RequiresApi
import kotlin.Throws
import retrofit2.Retrofit
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url
import android.content.pm.PackageManager
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.oncobuddy.app.utils.background_task.TaskRunner
import com.oncobuddy.app.utils.background_task.GetPdfDetailsTask
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.databinding.RawPdfImageBinding
import com.oncobuddy.app.models.injectors.RecordsInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.PdfImage
import com.oncobuddy.app.models.pojo.extract_doc_response.UploadDocResponse
import com.oncobuddy.app.models.pojo.img_to_pdf.ImageToPDFOptions
import com.oncobuddy.app.models.pojo.tags_response.Tag
import com.oncobuddy.app.utils.*
import com.oncobuddy.app.utils.FileUtils
import com.oncobuddy.app.utils.background_task.AwsTextractTask
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.utils.custom_views.FragmentModalBottomSheet
import com.oncobuddy.app.view_models.RecordsViewModel
import com.oncobuddy.app.views.adapters.GalleryAdapterNew
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import android.content.IntentFilter

import androidx.localbroadcastmanager.content.LocalBroadcastManager




@SuppressLint("ValidFragment")
class FullScreenPDFViewFragmentKt : BaseFragment(),
    GetPdfDetailsInterface,
    OnPDFCreatedInterface,
    AwsTextractTask.ExtractTextInterface,
    PdfPagesAdapter.Interaction{

    private lateinit var relProgress: RelativeLayout
    private lateinit var tvStatus: TextView
    var ivBack: ImageView? = null
    var imgPdf: ImageView? = null
    private lateinit var imageViewPdf: TouchOrZoomImageView
    var tvNext: TextView? = null
    var ivShare: ImageView? = null
    var ivDownload: ImageView? = null
    var ivAddNewImage: ImageView? = null
    var linShare: LinearLayout? = null
    var prePageButton: ImageView? = null
    var nextPageButton: ImageView? = null
    private var linTitleContainer: LinearLayout? = null
    private val TAG = "download_pdf"
    private var FILENAME: String? = null
    private var FILE_URL: String? = ""
    private var USER_ID: String? = ""
    private var FILE_PATH: String? = "/storage/emulated/0/Download/Blood Report.pdf"

    var folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    var folderName = "Oncobuddy_Docs"
    var pdfFile: File? = null
    private val matchedTags: ArrayList<Tag>? = null
    private var pageIndex = 0
    private var pdfRenderer: PdfRenderer? = null
    private lateinit var currentPage: PdfRenderer.Page
    private var parcelFileDescriptor: ParcelFileDescriptor? = null
    private val filePath = ""
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
    private var pdfExtractedData: PDFExtractedData? = null
    private var startTime: Long = 0
    private var hasCompletedExtraction = false
    private val MAX_CLICK_INTERVAL: Long = 2000
    private var lastCLickTIme: Long = 0
    private lateinit var confirmNextDilogue: Dialog
    private var rvPdfPages: RecyclerView? = null
    private var pdfPagesAdapter: PdfPagesAdapter? = null
    private var sampleBitmap: Bitmap? = null
    private var CAMERA_MODE = 0
    var pdfImagesList = ArrayList<PdfImage>()
    private lateinit var progressDialog: ProgressDialog
    private var mHomePath = ""
    private lateinit var pdfFileList : ArrayList<File>
    private var mPdfOptions: ImageToPDFOptions? = null
    private var IS_CAMERA_SELECTED = false
    var mCurrentPhotoPath: String? = null
    var currentSelectedPage = 0
    private var bottomCameraOrGalleryDIalogue: FragmentModalBottomSheet? = null
    private var RECORD_TYPE = Constants.RECORD_TYPE_REPORT
    private lateinit var recordsViewModel: RecordsViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private var lastSelectedItem = 6


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.fragment_full_screen_pdf_view, container,
            false
        )

        Log.d("item_click_1", "opened here")
        findViewsById(rootView)
        pdfExtractedData = PDFExtractedData()
        pageIndex = 0
        mHomePath = FourBaseCareApp.activityFromApp.getCacheDir().getAbsolutePath() + Constants.PDF_FILES
        setClickListeners()
        getArgumentData()
        setupVM()
        setupObservers()

        return rootView
    }

    private fun setupVM() {
        recordsViewModel = ViewModelProvider(
            this,
            RecordsInjection.provideViewModelFactory()
        ).get(RecordsViewModel::class.java)
    }

    private fun setupObservers() {
        recordsViewModel.uploadDocData.observe(this, uploadDocObserver)
        recordsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        recordsViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val uploadDocObserver =
        androidx.lifecycle.Observer<UploadDocResponse> { responseObserver ->
            //binding.loginModel = loginResponseData
            if (responseObserver.isSuccess()) {
                  Log.d("upload_doc_log","Document uploaded")
                FILE_URL = responseObserver.payLoad.documentUrl
                Log.d("upload_doc_log","FILE URL  "+FILE_URL)
                if(responseObserver.payLoad.dateExtract != null){
                if(responseObserver.payLoad.dateExtract.date.size > 0){
                    pdfExtractedData?.consulationDate = responseObserver.payLoad.dateExtract.date.get(0).formattedDate
                    Log.d("upload_doc_log","new consultation date  "+pdfExtractedData?.consulationDate)
                }

                if(responseObserver.payLoad.dateExtract.tags != null){

                    var matchedTags = ArrayList<Tag>()
                    var matchedPrimaryTags = ArrayList<Tag>()
                    for(preObj in  responseObserver.payLoad.dateExtract.tags.primaryPredictions){
                        matchedPrimaryTags.add(Tag(0, preObj.label))
                    }
                    matchedTags.add(Tag(0, responseObserver.payLoad.dateExtract.tags.secondaryPredictions.label))
                    pdfExtractedData?.tags = matchedTags
                    pdfExtractedData?.primaryTags = matchedPrimaryTags
                    Log.d("upload_doc_log","new consultation Date "+pdfExtractedData?.consulationDate)
                    Log.d("upload_doc_log","new Secondary Tag "+pdfExtractedData?.tags?.size)
                    Log.d("upload_doc_log","new Primary Tag "+pdfExtractedData?.primaryTags?.size)
                }

            }
                Log.d("upload_doc_log","Next screen from here")
                gotoNextScreen()
            }else{
                showToast(FourBaseCareApp.activityFromApp,"There is an issue in uploading document")
            }
        }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean> { isLoading ->
        if (isLoading) relProgress.visibility = View.VISIBLE
        else relProgress.visibility = View.GONE

    }
    private val errorMessageObserver = androidx.lifecycle.Observer<String> { message ->
        Log.d("upload_log", "Error " + message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val loginObject: LoginDetails
        private get() {
            val gson = Gson()
            val userJson = FourBaseCareApp.sharedPreferences.getString(
                Constants.PREF_USER_OBJ,
                ""
            )
            return gson.fromJson(userJson, LoginDetails::class.java)
        }

    private fun showPDF() {
        try {
            openRenderer(FourBaseCareApp.activityFromApp)
            //getImagesListFromPdf()
            //showPage(pageIndex)
            prePageButton!!.visibility = View.VISIBLE
            nextPageButton!!.visibility = View.VISIBLE


        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "Error  $e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun findViewsById(rootView: View) {
        relProgress = rootView.findViewById(R.id.relProgress)
        tvStatus = rootView.findViewById(R.id.tvStatus)
        relProgress.setVisibility(View.GONE)
        linTitleContainer = rootView.findViewById(R.id.linTitleContainer)
        imageViewPdf = rootView.findViewById(R.id.pdf_image)
        prePageButton = rootView.findViewById(R.id.button_pre_doc)
        nextPageButton = rootView.findViewById(R.id.button_next_doc)
        tvNext = rootView.findViewById(R.id.tvNext)
        ivShare = rootView.findViewById(R.id.ivShare)
        ivDownload = rootView.findViewById(R.id.ivDownload)
        linShare = rootView.findViewById(R.id.linShareContainer)
        ivBack = rootView.findViewById(R.id.ivBack)
        rvPdfPages = rootView.findViewById(R.id.rvPreviews)
        imgPdf = rootView.findViewById(R.id.imgPdf)
        ivAddNewImage = rootView.findViewById(R.id.ivAddNewImage)

    }

    private fun getArgumentData() {
            if (arguments != null) {
                USER_ID = arguments!!.getString(Constants.USER_ID)
                RECORD_TYPE = arguments!!.getString(Constants.RECORD_TYPE).toString()
                if (arguments!!.getString(Constants.SOURCE) != null && arguments!!.getString(
                        Constants.SOURCE
                    ).equals(
                        Constants.EDIT_RECORD_FRAGMENT, ignoreCase = true
                    )
                ) {
                    Log.d("update_doc_log", "1")
                    FILE_URL = arguments!!.getString(Constants.SERVER_FILE_URL)
                    Log.d("update_doc_log", "FILE_URL $FILE_URL")
                    checkLocalFile()
                    tvNext!!.visibility = View.GONE
                    if (loginObject.role != Constants.ROLE_DOCTOR) {
                        linShare!!.visibility = View.VISIBLE
                    }
                    ivAddNewImage?.visibility = View.GONE
                } else {
                    tvNext!!.visibility = View.VISIBLE
                    linShare!!.visibility = View.GONE
                    CAMERA_MODE = arguments!!.getInt(Constants.DOC_MODE)
                    if (CAMERA_MODE == Constants.PDF_MODE) {
                        deleteDirectory()
                        Log.d("open_pdf","0")
                        FILE_PATH = arguments!!.getString(Constants.PDF_PATH)
                        val seperated = FILE_PATH!!.split("/").toTypedArray()
                        FILENAME = seperated[seperated.size - 1]
                        Log.d("open_pdf", "file path $FILE_PATH")
                        Log.d("open_pdf", "file name $FILENAME")
                        showPDF()
                        ivAddNewImage?.visibility = View.GONE

                    } else {
                        Log.d(
                            "camera_img_log",
                            "path " + arguments!!.getString(Constants.IMAGE_PATH)
                        )
                        try {
                            val argUri = Uri.parse(arguments!!.getString(Constants.IMAGE_PATH))
                            Log.d("camera_img_log", "arguri "+argUri.path)
                            pdfImagesList = ArrayList()
                            val pdfImage = PdfImage(0)
                            pdfImage.setPath(argUri.toString())
                            pdfImage.setUri(argUri.toString())
                            pdfImage.setSelected(true)
                            pdfImagesList.add(pdfImage)
                            setupPDFRecyclerView()
                            Log.d("camera_img_log", "uri done list size "+pdfImagesList.size)
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                FourBaseCareApp.activityFromApp.contentResolver,
                                argUri
                            )
                            createDirectoryAndSaveFile(bitmap,getFileName(argUri))
                            Log.d("camera_img_log", "bitmp done done")
                            imageViewPdf.setImageBitmap(bitmap)
                            ivAddNewImage?.visibility = View.VISIBLE
                            argUri.path?.let { awsTexttractExtenstion(it) }
                            Log.d("camera_img_log", "img load done")
                        } catch (e: IOException) {
                            Log.d("camera_img_log", "err $e")
                            e.printStackTrace()
                        }
                    }
                }
                if (arguments!!.containsKey(Constants.SHOULD_SHOW_TITLE)) {
                    if (!arguments!!.getBoolean(Constants.SHOULD_SHOW_TITLE)) {
                        linTitleContainer!!.visibility = View.INVISIBLE
                    }
                }
            }
        }

    private fun setClickListeners() {

        ivAddNewImage!!.setOnClickListener(View.OnClickListener {
        /*    if (IS_CAMERA_SELECTED) openCameraIntent()
            else openGalleryForImage()*/
            showUploadRecordDialogue()
        })
        //ivBack!!.setOnClickListener { v: View? -> if (getFragmentManager() != null) getFragmentManager()!!.popBackStack() }
        ivBack!!.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                deleteDirectory()
                if (getFragmentManager() != null){
                    getFragmentManager()!!.popBackStack()
                }

            }

        })
        ivDownload!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (!isDoubleClick()) {
                    if (checkPermission(activity)) {
                        downloadFile()
                    }
                }
            }
        })
        ivShare!!.setOnClickListener { v: View? ->
            if (!isDoubleClick()) {
                if (checkPermission(FourBaseCareApp.activityFromApp)) shareFile()
            }
        }
        tvNext!!.setOnClickListener { view: View? ->
            if(!isDoubleClick() && relProgress.visibility != View.VISIBLE){
            try {
                var mSelectedImages: ArrayList<String>? = getSavedImages()
                Log.d("camera_img_log", "click time list size "+pdfImagesList.size)
                if (pdfImagesList.size > 0) {
                        if(RECORD_TYPE.equals(Constants.RECORD_TYPE_BILL) || RECORD_TYPE.equals(Constants.RECORD_TYPE_PRESCRIPTION)){
                            if(hasCompletedExtraction) createPdf(mSelectedImages)
                            else showNextScreenDialogue(mSelectedImages)
                        }else{
                            createPdf(mSelectedImages)
                        }
                    //createPdf(mSelectedImages)


                } else {
                    CommonMethods.showToast(
                        FourBaseCareApp.activityFromApp,
                        "Please upload document"
                    )
                }
            } catch (e: java.lang.Exception) {
                Log.d("camera_img_log", "click time Err "+e.toString())
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, "There was some issue while uploading a document")
            }
            }

        }
        prePageButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
              /*  if (currentPage!!.index > 0) showPage(currentPage!!.index - 1) else Toast.makeText(
                    activity, "You have reached at the starting of the document", Toast.LENGTH_SHORT
                ).show()*/

                if(currentSelectedPage > 0){
                    currentSelectedPage -= 1
                    var selectedPdfImage  = pdfImagesList.get(currentSelectedPage)
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        FourBaseCareApp.activityFromApp.contentResolver,
                        Uri.parse(selectedPdfImage.path)
                    )
                    imageViewPdf.setImageBitmap(bitmap)
                    setupPDFRecyclerView(currentSelectedPage)

                }else{
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp,"You have reached at the starting of the document")
                }
            }
        })
        nextPageButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                /*if (currentPage!!.index < pageCount) showPage(currentPage!!.index + 1) else Toast.makeText(
                    activity, "You have reached at the ending of the document", Toast.LENGTH_SHORT
                ).show()*/
                if(currentSelectedPage < pdfImagesList.size - 1){
                    currentSelectedPage += 1
                    var selectedPdfImage  = pdfImagesList.get(currentSelectedPage)
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        FourBaseCareApp.activityFromApp.contentResolver,
                        Uri.parse(selectedPdfImage.path)
                    )
                    imageViewPdf.setImageBitmap(bitmap)
                    setupPDFRecyclerView(currentSelectedPage)

                }else{
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp,"You have reached at the ending of the document")
                }
            }
        })
    }

    private fun gotoNextScreen() {
      //  Log.d("pdf_extract_log", "1")
        //Log.d("etxract_date_log", "Date next screen " + pdfExtractedData!!.consulationDate)
        val b = Bundle()
        b.putParcelable(Constants.PDF_CLASS_DATA, pdfExtractedData)
        b.putString(Constants.PDF_PATH, FILE_PATH)
        b.putString(Constants.SERVER_FILE_URL, FILE_URL)
        b.putString(Constants.RECORD_TYPE, RECORD_TYPE)
        val addOrEditRecordFragment = AddOrEditRecordFragment()
        addOrEditRecordFragment.arguments = b
        addNextFragment(
            FourBaseCareApp.activityFromApp,
            addOrEditRecordFragment,
            this@FullScreenPDFViewFragmentKt,
            false
        )
        deleteDirectory()
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
                """Hello! I am sharing this file using Oncobuddy. Please download the app using following link.
                        https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"""
            )
            startActivity(share)
        } catch (e: Exception) {
            Log.d("share_pdf_log", e.toString())
            Toast.makeText(
                FourBaseCareApp.activityFromApp.applicationContext,
                "There was an issue in sharing this file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun downloadFile() {
        try {
            val downloadmanager =
                FourBaseCareApp.activityFromApp.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(FILE_URL)
            val request = DownloadManager.Request(uri)
            request.setTitle("File $FILENAME")
            request.setDescription("Downloading...")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setVisibleInDownloadsUi(true)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "OncoDocs/File $FILENAME"
            )
            downloadmanager.enqueue(request)
            Toast.makeText(
                FourBaseCareApp.activityFromApp,
                "Downloading started! Please check status bar for progress.",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                FourBaseCareApp.activityFromApp,
                "Downloading failed! With err $e",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    private fun checkLocalFile() {
        val seperated = FILE_URL!!.split("/").toTypedArray()
        FILENAME = seperated[seperated.size - 1]
        val cw = ContextWrapper(FourBaseCareApp.activityFromApp)
        folder = cw.getDir("oncobuddyPdfs", Context.MODE_PRIVATE)
        pdfFile = File(folder, FILENAME)
        val path = FourBaseCareApp.activityFromApp.filesDir.absolutePath
        FILE_PATH = pdfFile!!.absolutePath
        if (checkPermission(activity)) {
            if (!pdfFile!!.exists()) {
                startDownload()
            } else {
                try {
                    showPDF()
                } catch (e: Exception) {
                    Toast.makeText(activity, "Error local file  $e", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onStop() {
        try {
            closeRenderer()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        super.onStop()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(IOException::class)
    private fun openRenderer(context: Context?) {
        // In this sample, we read a PDF from the assets directory.
        //File file = new File(context.getCacheDir(), FILENAME);
        try {
            /*File d = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);  // -> filename = maven.pdf
            File file = new File(d, FILENAME);*/
            val file = File(FILE_PATH)
            Log.d("open_pdf","File path "+FILE_PATH)
            Log.d("open_pdf","file exists "+file.exists())
            if (!file.exists()) {
                // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
                // the cache directory.
                val asset = context!!.assets.open((FILENAME)!!)
                val output = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var size: Int
                while ((asset.read(buffer).also { size = it }) != -1) {
                    output.write(buffer, 0, size)
                }
                asset.close()
                output.close()
            }
            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            // This is the PdfRenderer we use to render the PDF.
            if (parcelFileDescriptor != null) {
                pdfRenderer = PdfRenderer(parcelFileDescriptor!!)
                getImagesListFromPdf()
            }
        } catch (e: Exception) {
            //Toast.makeText(activity, "Rending Error  $e", Toast.LENGTH_SHORT).show() Log.d("open_pdf","Err 1 "+e.toString())
            e.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(IOException::class)
    private fun closeRenderer() {
        try {
            if (null != currentPage) {
                currentPage!!.close()
            }
            if (pdfRenderer != null) pdfRenderer!!.close()
            if (parcelFileDescriptor != null) parcelFileDescriptor!!.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getImagesListFromPdf(){
        pdfImagesList = ArrayList()
        if(pdfRenderer != null){
                for(i in 0..pageCount - 1){
                    if (::currentPage.isInitialized && currentPage != null) {
                        currentPage!!.close()
                    }
                    currentPage = pdfRenderer!!.openPage(i)


                    val bitmap1 = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap1)
                    canvas.drawColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.white))
                    canvas.drawBitmap(bitmap1, 0f, 0f, null)


                    currentPage.render(bitmap1, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    Log.d("pdf_con_log", "imageview pdf set")

                    var uri = getImageUri(bitmap1)

                    Log.d("pdf_con_log", "Uri "+uri)
                    val pdfImage = PdfImage(i)
                    pdfImage.setPath(uri.toString())
                    pdfImage.setUri(uri.toString())
                    pdfImage.setSelected(false)
                    pdfImagesList.add(pdfImage)
                    //Log.d("pdf_con_log", "page added")
                    createDirectoryAndSaveFile(bitmap1, uri?.let { getFileName(it) })
                }
                setupPDFRecyclerView()
                Log.d("pdf_con_log", "Size "+pdfImagesList.size)
                if(pdfImagesList.size > 0){
                    var selectedPdfImage  = pdfImagesList.get(0)
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        FourBaseCareApp.activityFromApp.contentResolver,
                        Uri.parse(selectedPdfImage.path)
                    )
                    imageViewPdf.setImageBitmap(bitmap)
                    var uri = getImageUri(bitmap)
                    uri?.path.let {
                        if (it != null) {
                            awsTexttractExtenstion(it)
                        }
                    }
                }
                Log.d("pdf_con_log", "List set size "+pdfImagesList.size)
            }
    }

    fun getUriFromBitmp(inContext: Context, inImage: Bitmap): Uri? {
       /* val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
*/

        return null
    }

    fun getImageUri(src: Bitmap): Uri? {
        /*val os = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.PNG, 100, os)
        val path = MediaStore.Images.Media.insertImage(FourBaseCareApp.activityFromApp.contentResolver, src, "title", null)
        return Uri.parse(path)*/

        val file = File(FourBaseCareApp.activityFromApp.cacheDir,"Temp "+System.currentTimeMillis()) //Get Access to a local file.
        file.delete() // Delete the File, just in Case, that there was still another File
        file.createNewFile()
        val fileOutputStream = file.outputStream()
        val byteArrayOutputStream = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.PNG,90,byteArrayOutputStream)
        val bytearray = byteArrayOutputStream.toByteArray()
        fileOutputStream.write(bytearray)
        fileOutputStream.flush()
        fileOutputStream.close()
        byteArrayOutputStream.close()
        Log.d("pdf_con_log", "uri got "+file.toUri())
        return file.toUri()
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun showPage(index: Int) {
        try {
            if (pdfRenderer!!.pageCount <= index) {
                return
            }
            // Make sure to close the current page before opening another one.
            if (null != currentPage) {
                currentPage!!.close()
            }
            // Use `openPage` to open a specific page in PDF.
            currentPage = pdfRenderer!!.openPage(index)
            // Important: the destination bitmap must be ARGB (not RGB).
            val bitmap = Bitmap.createBitmap(
                currentPage.getWidth(), currentPage.getHeight(),
                Bitmap.Config.ARGB_8888
            )
            sampleBitmap = bitmap
            // Here, we render the page onto the Bitmap.
            // To render a portion of the page, use the second and third parameter. Pass nulls to get
            // the default result.
            // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            // We are ready to show the Bitmap to user.
            imageViewPdf.setImageBitmap(bitmap)
            updateUi()
        } catch (e: Exception) {
            Toast.makeText(activity, "show page Error  $e", Toast.LENGTH_SHORT).show()
            prePageButton!!.visibility = View.GONE
            nextPageButton!!.visibility = View.GONE
            e.printStackTrace()
        }
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun updateUi() {
        val index = currentPage!!.index
        val pageCount = pdfRenderer!!.pageCount
        prePageButton!!.isEnabled = 0 != index
        nextPageButton!!.isEnabled = index + 1 < pageCount
    }

    @get:RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    val pageCount: Int get() = if(pdfRenderer != null) pdfRenderer!!.pageCount else 0

    // Retrofit try
    private fun startDownload() {
        relProgress!!.visibility = View.VISIBLE
        Log.v("retro_download", "download started")
        val i = FILE_URL!!.lastIndexOf("/")
        val temp = arrayOf(FILE_URL!!.substring(0, i), FILE_URL!!.substring(i))
        Log.v("retro_download", "retro base url " + temp[0])
        val retrofit = Retrofit.Builder()
            .baseUrl(temp[0] + "/")
            .build()
        val handlerService = retrofit.create(
            FileHandlerService::class.java
        )
        val call = handlerService.downloadFile(FILENAME)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                relProgress!!.visibility = View.GONE
                Log.d("update_doc_log", "5 " + response.isSuccessful)
                Log.d("update_doc_log", "5.1 message " + response.message())
                Log.d("update_doc_log", "5.1 " + response.code())
                Log.d("update_doc_log", "5.1 " + response.errorBody())
                Log.d("update_doc_log", "5.1 " + response.body())
                if (response.isSuccessful) {
                    if (writeResponseBodyToDisk(response.body())) {
                        //listener.onFileLoaded(file);
                        Log.v("retro_download", "download completed")
                        Log.d("update_doc_log", "6 download done")
                        try {
                            showPDF()
                        } catch (e: Exception) {
                            Log.d("update_doc_log", "7 $e")
                            Toast.makeText(activity, "downloadd pdf  $e", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    }
                } else {
                    //listener.onDownloadFailed("Resource not Found");
                    Log.v("retro_download", "download Error ")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                relProgress!!.visibility = View.GONE
                //listener.onDownloadFailed("Download Failed");
                Log.v("retro_download", "download failed " + t.message)
                t.printStackTrace()
            }
        })
    }

    override fun onPdfObjectCreated(pdfExtractedData: PDFExtractedData?) {
        this.pdfExtractedData = pdfExtractedData
        hasCompletedExtraction = true
        val a = (SystemClock.elapsedRealtime() - startTime / 1000).toDouble()
        Log.d("bg_log", "Finished in $a seconds")
    }

    internal interface FileHandlerService {
        @GET
        fun downloadFile(@Url url: String?): Call<ResponseBody>
    }

    private fun writeResponseBodyToDisk(body: ResponseBody?): Boolean {
        return try {
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body!!.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(pdfFile)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
                true
            } catch (e: Exception) {
                Toast.makeText(activity, "Write file Error  $e", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: Exception) {
            Toast.makeText(activity, "Write file parent Error  $e", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> try {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!pdfFile!!.exists()) {
                        Log.v("retro_download", "File not present")
                        startDownload()
                    } else {
                        try {
                            Log.v("retro_download", "Showing local file")
                            Log.d("upload_log", "File name " + pdfFile!!.name)

                            /* openRenderer(getActivity());
                                showPage(pageIndex);*/checkLocalFile()
                        } catch (e: Exception) {
                            Toast.makeText(activity, "Permission Error  $e", Toast.LENGTH_SHORT)
                                .show()
                            e.printStackTrace()
                        }
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Please provide needed permissions first from app permission settings",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(activity, "Permission Error 2  $e", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun extractpdf(pdfPath: String?) {
        try {
            var parsedText = ""
            val reader = PdfReader(pdfPath)
            val n = reader.numberOfPages
            for (i in 0 until n) {
                parsedText = parsedText + PdfTextExtractor.getTextFromPage(reader, i + 1)
                    .trim { it <= ' ' } + "\n" //Extracting the content from the different pages
            }
            Log.d("bg_log", "started")
            //pdfExtractedData = CommonMethods.getPDfObject(parsedText);
            startTime = SystemClock.elapsedRealtime()
            val taskRunner = TaskRunner()
            taskRunner.executeAsync(GetPdfDetailsTask(parsedText, this))


            /*   taskRunner.executeAsync(new GetPdfDetailsTask(parsedText, this::onPdfObjectCreated){

            });*/
        } catch (e: Exception) {
            Log.d("pdf_read", "Error $e")
            Toast.makeText(activity, "Extract pdf Error  $e", Toast.LENGTH_SHORT).show()
            println(e)
        }
    }

    fun isDate(word: String?): Boolean {
        val df = SimpleDateFormat("dd/MM/yyyy")
        return try {
            df.parse(word)
            true
        } catch (e: ParseException) {
            false
        }
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
                    alertBuilder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                        )
                    }
                    val alert = alertBuilder.create()
                    alert.setCancelable(false)
                    alert.show()
                } else {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
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


    private fun showNextScreenDialogue(mImagesUri: ArrayList<String>?) {
        confirmNextDilogue = Dialog(FourBaseCareApp.activityFromApp)
        confirmNextDilogue!!.setContentView(R.layout.tags_confirm_dialogue)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(confirmNextDilogue!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation
        val window = confirmNextDilogue!!.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        confirmNextDilogue!!.window!!.attributes = lp
        confirmNextDilogue!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val ivLogo = confirmNextDilogue!!.findViewById<ImageView>(R.id.ivLogo)
        val tvMsg = confirmNextDilogue!!.findViewById<TextView>(R.id.tvTitleText)
        val btnNo = confirmNextDilogue!!.findViewById<Button>(R.id.btnNo)
        val btnYes = confirmNextDilogue!!.findViewById<Button>(R.id.btnYes)
        tvMsg.text = "Your extraction process is still in progress. Do you want to proceed further?"
        btnNo.text = "Wait"
        btnYes.text = "Next"
        ivLogo.setImageDrawable(
            ContextCompat.getDrawable(
                FourBaseCareApp.activityFromApp,
                R.drawable.ic_cancel_alert
            )
        )
        btnYes.setBackgroundResource(R.drawable.rounded_red_bg)
        btnYes.setOnClickListener {
            createPdf(mImagesUri)
            confirmNextDilogue!!.dismiss()
        }
        btnNo.setOnClickListener { confirmNextDilogue!!.dismiss() }
        confirmNextDilogue!!.show()
    }

    private fun setupPDFRecyclerView(position: Int = 0) {

        FourBaseCareApp.activityFromApp.runOnUiThread(Runnable {
            pdfPagesAdapter = PdfPagesAdapter(FourBaseCareApp.activityFromApp, this, position)

            pdfPagesAdapter!!.submitList(pdfImagesList)

           layoutManager = LinearLayoutManager(
                FourBaseCareApp.activityFromApp,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            rvPdfPages!!.layoutManager = layoutManager

            rvPdfPages!!.adapter = pdfPagesAdapter

            //rvPdfPages!!.addOnScrollListener(recyclerViewOnScrollListener)

            lastSelectedItem+1
            if(currentSelectedPage > 5) {
                rvPdfPages!!.smoothScrollToPosition(currentSelectedPage)
            }

        })

    }

    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(::layoutManager.isInitialized){
                    if(dx>0){
                        val visibleItemCount: Int = layoutManager.getChildCount()
                        val totalItemCount: Int = layoutManager.getItemCount()
                        val firstVisibleItemPosition: Int =
                            layoutManager.findFirstVisibleItemPosition()
                        val lastItem = firstVisibleItemPosition + visibleItemCount
                        Log.d("pdf_adap_log_", "visible item count "+visibleItemCount)
                        Log.d("pdf_adap_log_", "total item count "+totalItemCount)
                        Log.d("pdf_adap_log_", "last item "+lastItem)
                        Log.d("pdf_adap_log_", "f "+layoutManager.findFirstVisibleItemPosition())
                        Log.d("pdf_adap_log_", "fc "+layoutManager.findFirstCompletelyVisibleItemPosition())
                        Log.d("pdf_adap_log_", "l "+layoutManager.findLastVisibleItemPosition())
                        Log.d("pdf_adap_log_", "lc "+layoutManager.findLastCompletelyVisibleItemPosition())
                    }

                }

            }
        }

    private fun showProgressDialogWithTitle() {
        progressDialog = ProgressDialog(FourBaseCareApp.activityFromApp)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setMessage("Converting Images to Pdf \nPlease Wait ...")
        progressDialog.show()
    }
    private fun resetValues() {
        mPdfOptions = ImageToPDFOptions()
        mPdfOptions?.setPasswordProtected(false)
        mPdfOptions?.setWatermarkAdded(false)
        mPdfOptions?.setPageSize("A4")
        mPdfOptions?.setImageScaleType("maintain_aspect_ratio")
        mPdfOptions?.setPageNumStyle("pg_num_style_page_x_of_n")
        mPdfOptions?.setPageColor(Color.WHITE)
        mPdfOptions?.setMargins(0, 0, 0, 0)
    }

    override fun onPDFCreationStarted() {
        showProgressDialogWithTitle()
    }

    override fun onPDFCreated(success: Boolean, path: String?) {
        if (progressDialog != null && progressDialog.isShowing) progressDialog.dismiss()

        if (!success) {
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Error on creating application folder.")
            return
        }
        FILE_PATH = path
        mHomePath = path!!
        pdfFileList = java.util.ArrayList()
        pdfFileList.add(File(path))

        if (pdfFileList.size > 0) {
            // updateMedicalRecord()
            //CommonMethods.showToast(FourBaseCareApp.activityFromApp, "PDF File created")
            Log.d("etxract_date_log", "Date going to full screen " + pdfExtractedData?.consulationDate)
            if(RECORD_TYPE.equals(Constants.RECORD_TYPE_REPORT)){
                uploadDOcumentForExtraction()
            }else{
                gotoNextScreen()
            }

        }
        //tvAddDocument.setVisibility(View.GONE)
        resetValues()
        deleteDirectory()
    }

    override fun onTextExtracted(result: String?) {
        val runner = TaskRunner()
        runner.executeAsync(
            result?.let {
                GetPdfDetailsTask(
                    it,
                    this@FullScreenPDFViewFragmentKt
                )
            }
        )
    }

    private fun awsTexttractExtenstion(filepath: String) {
        //extractTextFromImageAsync = ExtractTextFromImageAsync(filepath, this@ScanReportFragment)
        //extractTextFromImageAsync.execute()
        Log.d("extract_text","file path "+filepath)
        startTime = SystemClock.elapsedRealtime()
        val runner = TaskRunner()
        runner.executeAsync(
            AwsTextractTask(
                filepath,
                this@FullScreenPDFViewFragmentKt, FourBaseCareApp.sharedPreferences.getString(Constants.PREF_AWS_ACCESS_KEY, ""), FourBaseCareApp.sharedPreferences.getString(Constants.PREF_AWS_SECRET_KEY, "")
            )
        )
    }

    private fun getSavedImages(): ArrayList<String>? {
        val directory = File(FourBaseCareApp.activityFromApp.getFilesDir().toString() + "/DirName")
        val selectedImageList =
            ArrayList<String>()
        val files: Array<File> = directory.listFiles()
        for (file in files) {
            selectedImageList.add(file.path)
        }
        return selectedImageList
    }

    fun createPdf(mImagesUri: ArrayList<String>?) {
        mPdfOptions = ImageToPDFOptions()
        mPdfOptions?.setImagesUri(mImagesUri)
        mPdfOptions?.setPageSize("A4")
        mPdfOptions?.setImageScaleType("maintain_aspect_ratio")
        mPdfOptions?.setPageNumStyle("pg_num_style_page_x_of_n")
        mPdfOptions?.setPageColor(Color.WHITE)
        val cal = Calendar.getInstance()
        val filename = cal.timeInMillis.toString() + ""
        if (!isFileExist("$filename.pdf")) {
            mPdfOptions?.setOutFileName(filename)
            CreatePdf(
                mPdfOptions, mHomePath,
                this@FullScreenPDFViewFragmentKt
            ).execute()
            //deleteDirectory()
        } else {
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Error in loading Images.")
        }
    }

    fun isFileExist(mFileName: String): Boolean {
        val path: String = mHomePath + mFileName
        val file = File(path)
        return file.exists()
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

    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()) deleteRecursive(
            child
        )
        fileOrDirectory.delete()
    }

    private fun createDirectoryAndSaveFile(
        imageToSave: Bitmap?,
        fileName: String?
    ) {
        val direct = File(FourBaseCareApp.activityFromApp.getFilesDir().toString() + "/DirName")

        if (!direct.exists()) {
            val wallpaperDirectory =
                File(FourBaseCareApp.activityFromApp.getFilesDir().toString() + "/DirName/")
            wallpaperDirectory.mkdirs()
        }
        val file =
            File(File(FourBaseCareApp.activityFromApp.getFilesDir().toString() + "/DirName/"), fileName)
        if (file.exists()) {
            file.delete()
        }

        try {
            val out = FileOutputStream(file)
            imageToSave?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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
                    }
                } catch (e: Exception) {
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
                        Log.d("camera_img_log","Got crop uri")
                        val pdfImage = PdfImage(0)
                        pdfImage.setPath(resultUri.toString())
                        pdfImage.setUri(resultUri.toString())
                        pdfImage.setSelected(false)
                        pdfImagesList.add(pdfImage)
                        setupPDFRecyclerView(pdfImagesList.size - 1)

                        currentSelectedPage = pdfImagesList.size - 1
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            FourBaseCareApp.activityFromApp.contentResolver,
                            Uri.parse(resultUri.toString())
                        )
                        imageViewPdf.setImageBitmap(bitmap)
                        deleteDirectory()
                        for (pdfImage in pdfImagesList) {
                            var bitmap1: Bitmap? = null
                            try {
                                bitmap1 = MediaStore.Images.Media.getBitmap(
                                    FourBaseCareApp.activityFromApp.getContentResolver(),
                                    Uri.parse(pdfImage.uri)
                                )
                                try {
                                    if (bitmap1 != null) {
                                        Log.d("camera_img_log","bitmap1 is not null")
                                        while (bitmap1!!.height > 2048 || bitmap1.width > 2048) {
                                            bitmap1 = getResizedBitmap(bitmap1)
                                        }
                                    }else{
                                        Log.d("camera_img_log","bitmap1 is null")
                                    }
                                } catch (e: java.lang.Exception) {
                                    Log.d("camera_img_log","e2 "+e.toString())
                                    e.printStackTrace()
                                }

                                createDirectoryAndSaveFile(bitmap1, getFileName(Uri.parse(pdfImage.uri)))
                                Log.d("camera_img_log","director created and file saved")

                            } catch (e: Exception) {
                                Log.d("camera_img_log","e3 "+e.toString())
                                e.printStackTrace()
                            }
                        }

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        val error: java.lang.Exception = result.getError()
                        CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Error")
                    }
                }
            }
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

    override fun onPageSelected(position: Int, item: PdfImage, view: View) {
        /*if(view.id ==  R.id.relContainer){
            setupPDFRecyclerView(position)
        }else{

        }*/
        Log.d("pdf_adap_log", "pdf image "+item.uri)
        val bitmap = MediaStore.Images.Media.getBitmap(
            FourBaseCareApp.activityFromApp.contentResolver,
            Uri.parse(item.path)
        )
        imageViewPdf.setImageBitmap(bitmap)
        currentSelectedPage = position
        setupPDFRecyclerView(position)


    }

    private fun showUploadRecordDialogue() {
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
        }
        linCamera.setOnClickListener {
            if(!isDoubleClick()){
                openCameraIntent()
            }
            bottomCameraOrGalleryDIalogue!!.dismiss()
        }
    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {

    }

    private fun uploadDOcumentForExtraction() {
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
            recordsViewModel.callDocUpload("", getUserAuthToken(), body)
            Log.d("upload_log", "2 " + FILE_PATH)

        }
    }


    // Broadcast receiver to capture uploading notifications

    override fun onResume() {
        super.onResume()
        val lbm = LocalBroadcastManager.getInstance(FourBaseCareApp.activityFromApp)
        lbm.registerReceiver(onNotice, IntentFilter("action_upload_status"))
        Log.d("notification_data_log", "Receiver created")
    }

    override fun onPause() {
        super.onPause()
         if(onNotice != null){
             LocalBroadcastManager.getInstance(FourBaseCareApp.activityFromApp).unregisterReceiver(onNotice);
             Log.d("notification_data_log", "Receiver destroyed")
         }
     }

    private val onNotice: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // intent can contain anydata
            Log.d("notification_data_log", "Received at pdf doc screen!")
            if (intent != null) {
                var str = intent.getStringExtra("progress_status")

                if(str.equals("UPLOADING")){
                    str = "Uploading (1 of 2)\nThis may take a while"
                }else{
                    str = "Processing (2 of 2)\nThis may take a while"
                }
                tvStatus.text =  str

                Log.d("notification_data_log", "status "+str)
            }else{
                Log.d("notification_data_log", "intent null")
            }
        }
    }


}