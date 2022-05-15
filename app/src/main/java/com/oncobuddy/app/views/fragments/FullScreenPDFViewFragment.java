package com.oncobuddy.app.views.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.oncobuddy.app.BuildConfig;
import com.oncobuddy.app.FourBaseCareApp;
import com.oncobuddy.app.R;
import com.oncobuddy.app.models.pojo.PDFExtractedData;
import com.oncobuddy.app.models.pojo.PdfImage;
import com.oncobuddy.app.models.pojo.login_response.LoginDetails;
import com.oncobuddy.app.models.pojo.tags_response.Tag;
import com.oncobuddy.app.utils.CommonMethods;
import com.oncobuddy.app.utils.Constants;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.oncobuddy.app.utils.TouchOrZoomImageView;
import com.oncobuddy.app.utils.background_task.GetPdfDetailsTask;
import com.oncobuddy.app.utils.background_task.TaskRunner;
import com.oncobuddy.app.views.adapters.PdfPagesAdapter;


import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;


@SuppressLint("ValidFragment")
public
class FullScreenPDFViewFragment extends Fragment implements GetPdfDetailsTask.GetPdfDetailsInterface {

    private ProgressBar pb_per;
    ImageView ivBack, imgPdf;
    TouchOrZoomImageView imageViewPdf;
    TextView tvNext;
    ImageView ivShare, ivDownload;
    ImageView ivAddNewImage;
    LinearLayout linShare;
    ImageView prePageButton;
    ImageView nextPageButton;
    private LinearLayout linTitleContainer;
    private String TAG = "download_pdf";
    private String FILENAME;
    private String FILE_URL = "";
    private String USER_ID = "";
    private String FILE_PATH = "/storage/emulated/0/Download/Blood Report.pdf";
    File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    String folderName = "Oncobuddy_Docs";
    File pdfFile;
    private ArrayList<Tag> matchedTags;
    private int pageIndex;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;
    private String filePath = "";
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private PDFExtractedData pdfExtractedData;
    private long startTime = 0;
    private boolean hasCompletedExtraction = false;
    private long MAX_CLICK_INTERVAL = 2000;
    private long lastCLickTIme = 0;
    private Dialog confirmNextDilogue;
    private RecyclerView rvPdfPages;
    private PdfPagesAdapter pdfPagesAdapter;
    private Bitmap sampleBitmap;
    private int CAMERA_MODE = 0;
    ArrayList<PdfImage> pdfImagesList = new ArrayList<>();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_full_screen_pdf_view, container,
                false);

        findViewsById(rootView);

        pdfExtractedData = new PDFExtractedData();
        pageIndex = 0;

        getArgumentData();

        setClickListeners();
        //FourBaseCareApp.activityFromApp.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        //setupRecyclerView();
        //extractDataFromFile();
        return rootView;
    }

    private LoginDetails getLoginObject(){
        Gson gson = new Gson();
        String userJson = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "");
        LoginDetails userObj  = gson.fromJson(userJson, LoginDetails.class);
        return userObj;
    }



    private void showPDF() {
        try {
            openRenderer(getActivity());
            showPage(pageIndex);
            prePageButton.setVisibility(View.VISIBLE);
            nextPageButton.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"Error  "+e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private void findViewsById(View rootView) {
        pb_per = rootView.findViewById(R.id.progressBar);
        pb_per.setVisibility(View.GONE);
        linTitleContainer = rootView.findViewById(R.id.linTitleContainer);
        imageViewPdf = rootView.findViewById(R.id.pdf_image);
        prePageButton = rootView.findViewById(R.id.button_pre_doc);
        nextPageButton = rootView.findViewById(R.id.button_next_doc);
        tvNext = rootView.findViewById(R.id.tvNext);
        ivShare = rootView.findViewById(R.id.ivShare);
        ivDownload = rootView.findViewById(R.id.ivDownload);
        linShare = rootView.findViewById(R.id.linShareContainer);
        ivBack = rootView.findViewById(R.id.ivBack);
        rvPdfPages = rootView.findViewById(R.id.rvPreviews);
        imgPdf = rootView.findViewById(R.id.imgPdf);
        ivAddNewImage = rootView.findViewById(R.id.ivAddNewImage);
    }

    private void getArgumentData() {
        if(getArguments() != null){
            USER_ID = getArguments().getString(Constants.USER_ID);
            if(getArguments().getString(Constants.SOURCE) != null && getArguments().getString(Constants.SOURCE).equalsIgnoreCase(Constants.EDIT_RECORD_FRAGMENT)){
                Log.d("update_doc_log","1");
                FILE_URL = getArguments().getString(Constants.SERVER_FILE_URL);
                Log.d("update_doc_log","FILE_URL "+FILE_URL);
                checkLocalFile();
                tvNext.setVisibility(View.GONE);
                if(!getLoginObject().getRole().equals(Constants.ROLE_DOCTOR)) {
                    linShare.setVisibility(View.VISIBLE);
                }

            } else{
                tvNext.setVisibility(View.VISIBLE);
                linShare.setVisibility(View.GONE);
                CAMERA_MODE = getArguments().getInt(Constants.DOC_MODE);

                if(CAMERA_MODE == Constants.PDF_MODE){
                    FILE_PATH = getArguments().getString(Constants.PDF_PATH);
                    String[] seperated = FILE_PATH.split("/");
                    FILENAME = seperated[seperated.length -1];
                    Log.d("add_doc_log","file path "+ FILE_PATH);
                    Log.d("add_doc_log","file name "+ FILENAME);
                    hasCompletedExtraction = getArguments().getParcelable(Constants.PDF_CLASS_DATA) != null;
                    Log.d("etxract_date_log", "Date in full acreen "+ hasCompletedExtraction);
                    Log.d("etxract_date_log", "Date in full acreen "+ hasCompletedExtraction);
                    if(hasCompletedExtraction){
                        pdfExtractedData = getArguments().getParcelable(Constants.PDF_CLASS_DATA);
                        Log.d("etxract_date_log","Date "+pdfExtractedData.getConsulationDate());
                        hasCompletedExtraction = true;
                        Log.d("scan_output", "Full screen start "+pdfExtractedData.getReportName());
                    }else{
                        Log.d("etxract_date_log","Not parsed data");
                        Log.d("extract_time","Data not parsed");
                        extractpdf(FILE_PATH);

                    }
                    showPDF();
                }else{
                    Log.d("camera_img_log","path "+getArguments().getString(Constants.IMAGE_PATH));
                    try {
                        Uri argUri = Uri.parse(getArguments().getString(Constants.IMAGE_PATH));
                        pdfImagesList = new ArrayList<>();
                        PdfImage pdfImage = new PdfImage(0);
                        pdfImage.setPath(argUri.toString());
                        pdfImage.setSelected(true);
                        pdfImagesList.add(pdfImage);
                        setupPDFRecyclerView();

                        Log.d("camera_img_log","uri done");
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(FourBaseCareApp.activityFromApp.getContentResolver(), argUri);
                        Log.d("camera_img_log","bitmp done done");
                        imageViewPdf.setImageBitmap(bitmap);

                        /*       Glide
                       .with(FourBaseCareApp.activityFromApp)
                       .load(bitmap)
                       .into(imageViewPdf);
                 */       Log.d("camera_img_log","img load done");
                    } catch (IOException e) {
                        Log.d("camera_img_log","err "+e.toString());
                        e.printStackTrace();
                    }
                }


            }

            if(getArguments().containsKey(Constants.SHOULD_SHOW_TITLE)){
                if(!getArguments().getBoolean(Constants.SHOULD_SHOW_TITLE)){
                    linTitleContainer.setVisibility(View.INVISIBLE);
                }
            }


        }
    }

    private void setClickListeners() {

     /*   tvNext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });*/

        ivAddNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ivBack.setOnClickListener(v -> {
            if(getFragmentManager()!=null)
                getFragmentManager().popBackStack();
        });

        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shareFile();
                if(!isDoubleClick()){
                    if(checkPermission(getActivity())) {
                        downloadFile();
                    }
                }
                //shareFile();
            }
        });

        ivShare.setOnClickListener(v ->{
            if(!isDoubleClick()){
                if(checkPermission(FourBaseCareApp.activityFromApp))
                    shareFile();
                //shareFile();
            }
        });




        tvNext.setOnClickListener(view -> {
            if(hasCompletedExtraction){
                gotoNextScreen();
            }else{
                showNextScreenDialogue();
            }
            //Log.d("pdf_extract_log","2 "+pdfExtractedData.getConsulationDate());

        });


        prePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage.getIndex() > 0)
                    showPage(currentPage.getIndex() - 1);
                else
                    Toast.makeText(getActivity(), "You have reached at the starting of the document", Toast.LENGTH_SHORT).show();
            }
        });
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage.getIndex() < getPageCount())
                    showPage(currentPage.getIndex() + 1);
                else
                    Toast.makeText(getActivity(), "You have reached at the ending of the document", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void gotoNextScreen() {
        Log.d("pdf_extract_log","1");
        Log.d("etxract_date_log","Date next screen "+pdfExtractedData.getConsulationDate());
        Bundle b = new Bundle();
        b.putParcelable(Constants.PDF_CLASS_DATA,pdfExtractedData);
        b.putString(Constants.PDF_PATH, FILE_PATH);
        b.putString(Constants.SOURCE,"pdf_view");
        AddOrEditRecordFragment addOrEditRecordFragment = new AddOrEditRecordFragment();
        addOrEditRecordFragment.setArguments(b);
        CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp, addOrEditRecordFragment, FullScreenPDFViewFragment.this, false);
    }

    private void shareFile() {
        try{
            Uri uri = FileProvider.getUriForFile(FourBaseCareApp.activityFromApp,BuildConfig.APPLICATION_ID + ".provider",pdfFile);
            Log.d("share_log",uri.getPath());
            //Uri uri = Uri.parse(pdfFile);//FileProvider.getUriForFile(FourBaseCareApp.activityFromApp, BuildConfig.APPLICATION_ID + ".provider",pdfFile);
            Intent share = new Intent();
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(FourBaseCareApp.activityFromApp,BuildConfig.APPLICATION_ID + ".provider",pdfFile));
            share.putExtra(Intent.EXTRA_SUBJECT,"File share from Oncobuddy");
            share.putExtra(Intent.EXTRA_TEXT,"Hello! I am sharing this file using Oncobuddy. Please download the app using following link.\n\nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            startActivity(share);
        }catch (Exception e){
            Log.d("share_pdf_log",e.toString());
            Toast.makeText(FourBaseCareApp.activityFromApp.getApplicationContext(),"There was an issue in sharing this file", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile(){
        try {
            DownloadManager downloadmanager = (DownloadManager) FourBaseCareApp.activityFromApp.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(FILE_URL);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("File "+FILENAME);
            request.setDescription("Downloading...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "OncoDocs/File "+FILENAME);
            downloadmanager.enqueue(request);
            Toast.makeText(FourBaseCareApp.activityFromApp, "Downloading started! Please check status bar for progress.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(FourBaseCareApp.activityFromApp, "Downloading failed! With err "+e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void checkLocalFile() {
        /*val separated = recordObj.link.split("/".toRegex()).toTypedArray()
        val separatedDash = separated[separated.size - 1].split("-".toRegex()).toTypedArray()*/

        String[] seperated = FILE_URL.split("/");
        FILENAME = seperated[seperated.length -1];
        Log.d("update_doc_log","FILE_NAME "+FILENAME);
        ContextWrapper cw = new ContextWrapper(FourBaseCareApp.activityFromApp);
        folder = cw.getDir("oncobuddyPdfs", Context.MODE_PRIVATE);
        pdfFile = new File(folder, FILENAME);
        String path = FourBaseCareApp.activityFromApp.getFilesDir().getAbsolutePath();
        FILE_PATH = pdfFile.getAbsolutePath();
        Log.d("update_doc_log","FILE_PATH "+FILE_PATH);
        Log.d("update_doc_log","path "+path);



        if(checkPermission(getActivity())){
            if (!pdfFile.exists()) {
                Log.d("update_doc_log","3 file not present");
                Log.v("retro_download", "File not present");
                startDownload();
            } else {
                try {
                    Log.v("retro_download", "Showing local file");
                    Log.d("update_doc_log","Showing local file");
                    Log.d("upload_log","File name2 "+pdfFile.getName());
                    Log.d("upload_log","File path2 "+FILE_PATH);
                    showPDF();
                } catch (Exception e) {
                    Toast.makeText(getActivity(),"Error local file  "+e.toString(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStop() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        //File file = new File(context.getCacheDir(), FILENAME);
        try {
            /*File d = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);  // -> filename = maven.pdf
            File file = new File(d, FILENAME);*/
            File file = new File(FILE_PATH);

            if (!file.exists()) {
                // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
                // the cache directory.
                InputStream asset = context.getAssets().open(FILENAME);
                FileOutputStream output = new FileOutputStream(file);
                final byte[] buffer = new byte[1024];
                int size;
                while ((size = asset.read(buffer)) != -1) {
                    output.write(buffer, 0, size);
                }
                asset.close();
                output.close();
            }
            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            // This is the PdfRenderer we use to render the PDF.
            if (parcelFileDescriptor != null) {
                pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(),"Rending Error  "+e.toString(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeRenderer() throws IOException {
        try {
            if (null != currentPage) {
                currentPage.close();
            }
            if(pdfRenderer != null)pdfRenderer.close();
            if(parcelFileDescriptor != null)parcelFileDescriptor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        try {
            if (pdfRenderer.getPageCount() <= index) {
                return;
            }
            // Make sure to close the current page before opening another one.
            if (null != currentPage) {
                currentPage.close();
            }
            // Use `openPage` to open a specific page in PDF.
            currentPage = pdfRenderer.openPage(index);
            // Important: the destination bitmap must be ARGB (not RGB).
            Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                    Bitmap.Config.ARGB_8888);
            sampleBitmap = bitmap;
            // Here, we render the page onto the Bitmap.
            // To render a portion of the page, use the second and third parameter. Pass nulls to get
            // the default result.
            // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            // We are ready to show the Bitmap to user.
            imageViewPdf.setImageBitmap(bitmap);

            updateUi();
        } catch (Exception e) {
            Toast.makeText(getActivity(),"show page Error  "+e.toString(),Toast.LENGTH_SHORT).show();
            prePageButton.setVisibility(View.GONE);
            nextPageButton.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateUi() {
        int index = currentPage.getIndex();
        int pageCount = pdfRenderer.getPageCount();
        prePageButton.setEnabled(0 != index);
        nextPageButton.setEnabled(index + 1 < pageCount);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getPageCount() {
        return pdfRenderer.getPageCount();
    }

    // Retrofit try


    private void startDownload() {
        pb_per.setVisibility(View.VISIBLE);
        Log.v("retro_download", "download started");

        int i = FILE_URL.lastIndexOf("/");
        String[] temp =  {FILE_URL.substring(0, i), FILE_URL.substring(i)};
        Log.v("retro_download", "retro base url "+temp[0]);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(temp[0]+"/")
                .build();

        FileHandlerService handlerService = retrofit.create(FileHandlerService.class);

        Call<ResponseBody> call = handlerService.downloadFile(FILENAME);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pb_per.setVisibility(View.GONE);

                Log.d("update_doc_log","5 "+response.isSuccessful());
                Log.d("update_doc_log","5.1 message "+response.message());
                Log.d("update_doc_log","5.1 "+response.code());
                Log.d("update_doc_log","5.1 "+response.errorBody());
                Log.d("update_doc_log","5.1 "+response.body());

                if (response.isSuccessful()) {
                    if (writeResponseBodyToDisk(response.body())) {
                        //listener.onFileLoaded(file);
                        Log.v("retro_download", "download completed");
                        Log.d("update_doc_log","6 download done");
                        try {
                            showPDF();
                        } catch (Exception e) {
                            Log.d("update_doc_log","7 "+e.toString());
                            Toast.makeText(getActivity(),"downloadd pdf  "+e.toString(),Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }

                    }
                } else {
                    //listener.onDownloadFailed("Resource not Found");

                    Log.v("retro_download", "download Error ");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pb_per.setVisibility(View.GONE);
                //listener.onDownloadFailed("Download Failed");

                Log.v("retro_download", "download failed " + t.getMessage());
                t.printStackTrace();
            }
        });

    }

    @Override
    public void onPdfObjectCreated(@Nullable PDFExtractedData pdfExtractedData) {
        this.pdfExtractedData = pdfExtractedData;
        hasCompletedExtraction = true;
        double a = SystemClock.elapsedRealtime() - startTime / 1000;
        Log.d("bg_log","Finished in "+a+" seconds");
    }


    interface FileHandlerService {

        @GET
        Call<ResponseBody> downloadFile(@Url String url);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(pdfFile);


                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;

                }
                outputStream.flush();
                return true;
            } catch (Exception e) {
                Toast.makeText(getActivity(),"Write file Error  "+e.toString(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(),"Write file parent Error  "+e.toString(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (!pdfFile.exists()) {
                            Log.v("retro_download", "File not present");
                            startDownload();
                        } else {
                            try {
                                Log.v("retro_download", "Showing local file");
                                Log.d("upload_log","File name "+pdfFile.getName());

                               /* openRenderer(getActivity());
                                showPage(pageIndex);*/
                                checkLocalFile();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(),"Permission Error  "+e.toString(),Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please provide needed permissions first from app permission settings", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(),"Permission Error 2  "+e.toString(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
        }
    }

    private void extractpdf(String pdfPath){
        try {
            String parsedText="";
            PdfReader reader = new PdfReader(pdfPath);
            int n = reader.getNumberOfPages();
            for (int i = 0; i <n ; i++) {
                parsedText   = parsedText+ PdfTextExtractor.getTextFromPage(reader, i+1).trim()+"\n"; //Extracting the content from the different pages
            }
            Log.d("bg_log","started");
            //pdfExtractedData = CommonMethods.getPDfObject(parsedText);
            startTime = SystemClock.elapsedRealtime();
            TaskRunner taskRunner = new TaskRunner();
            taskRunner.executeAsync(new GetPdfDetailsTask(parsedText,this));


         /*   taskRunner.executeAsync(new GetPdfDetailsTask(parsedText, this::onPdfObjectCreated){

            });*/


        } catch (Exception e) {
            Log.d("pdf_read","Error "+e.toString());
            Toast.makeText(getActivity(),"Extract pdf Error  "+e.toString(),Toast.LENGTH_SHORT).show();
            System.out.println(e);
        }
    }

    public boolean isDate(String word) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            df.parse(word);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(R.string.permission_necessary);
                    alertBuilder.setMessage(R.string.external_storage_permission_needed);
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.setCancelable(false);
                    alert.show();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private boolean isDoubleClick(){
        if (SystemClock.elapsedRealtime() - lastCLickTIme < MAX_CLICK_INTERVAL) {
            return true;
        }
        lastCLickTIme = SystemClock.elapsedRealtime();
        return false;
    }

    private void showNextScreenDialogue() {

        confirmNextDilogue = new Dialog(FourBaseCareApp.activityFromApp);
        confirmNextDilogue.setContentView(R.layout.tags_confirm_dialogue);

        WindowManager.LayoutParams lp =new WindowManager.LayoutParams();


        lp.copyFrom(confirmNextDilogue.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.DialogAnimation;

        Window window = confirmNextDilogue.getWindow();

        window.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        window.setBackgroundDrawableResource(android.R.color.transparent);

        confirmNextDilogue.getWindow().setAttributes(lp);
        confirmNextDilogue.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView ivLogo = confirmNextDilogue.findViewById(R.id.ivLogo);
        TextView tvMsg = confirmNextDilogue.findViewById(R.id.tvTitleText);
        Button btnNo = confirmNextDilogue.findViewById(R.id.btnNo);
        Button btnYes = confirmNextDilogue.findViewById(R.id.btnYes);

        tvMsg.setText("Your extraction process is still in progress. Do you want to proceed further?");
        btnNo.setText("Wait");
        btnYes.setText("Next");


        ivLogo.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.ic_cancel_alert));

        btnYes.setBackgroundResource(R.drawable.rounded_red_bg);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                gotoNextScreen();
                confirmNextDilogue.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmNextDilogue.dismiss();
            }
        });

        confirmNextDilogue.show();

    }

    private void setupPDFRecyclerView(){
        pdfPagesAdapter = new PdfPagesAdapter(FourBaseCareApp.activityFromApp,null, 0);
        pdfPagesAdapter.submitList(pdfImagesList);
        rvPdfPages.setLayoutManager(new LinearLayoutManager(FourBaseCareApp.activityFromApp, LinearLayoutManager.HORIZONTAL, false));
        rvPdfPages.setAdapter(pdfPagesAdapter);
        Log.d("pdf_adap_log","1");
    }




}
