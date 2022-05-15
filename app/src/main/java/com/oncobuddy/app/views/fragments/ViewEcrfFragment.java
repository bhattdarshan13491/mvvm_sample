package com.oncobuddy.app.views.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oncobuddy.app.FourBaseCareApp;
import com.oncobuddy.app.R;
import com.oncobuddy.app.models.pojo.tags_response.Tag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
class ViewEcrfFragment extends Fragment {

    private ProgressBar pb_per;
    ImageView imageViewPdf, ivBack;
    TextView tvNext, tvTitle;
    FloatingActionButton prePageButton;
    FloatingActionButton nextPageButton;
    private LinearLayout linTitleContainer;
    private String TAG = "download_pdf";
    private String FILENAME;
    private String FILE_URL = "blob:http://52.66.98.251:9501/";
    private String FILE_PATH = "/storage/emulated/0/Download/Blood Report.pdf";
    File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    File pdfFile;

    private ArrayList<Tag> matchedTags;
    private int pageIndex;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;
    private String filePath = "";
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;



    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_full_screen_pdf_view, container,
                false);

        findViewsById(rootView);

        getArgumentData();

        setClickListeners();

        return rootView;
    }

    private void checkLocalFile() {
        /*val separated = recordObj.link.split("/".toRegex()).toTypedArray()
        val separatedDash = separated[separated.size - 1].split("-".toRegex()).toTypedArray()*/

         String[] seperated = FILE_URL.split("/");
         FILENAME = seperated[seperated.length -1];
         Log.d("update_doc_log","FILE_NAME "+FILENAME);
         pdfFile = new File(folder, FILENAME);
         FILE_PATH = pdfFile.getAbsolutePath();
        Log.d("update_doc_log","FILE_PATH "+FILE_PATH);

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
                    openRenderer(getActivity());
                    showPage(pageIndex);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showPDF() {
        try {
            openRenderer(getActivity());
            showPage(pageIndex);
        } catch (IOException e) {
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
        tvTitle = rootView.findViewById(R.id.tvTitle);
        ivBack = rootView.findViewById(R.id.ivBack);

        tvTitle.setText("Patient Ecrf summary");
        tvNext.setVisibility(View.GONE);

    }

    private void getArgumentData() {
        /*if(getArguments() != null){

            if(getArguments().getString(Constants.SOURCE) != null && getArguments().getString(Constants.SOURCE).equalsIgnoreCase(Constants.EDIT_RECORD_FRAGMENT)){
                Log.d("update_doc_log","1");
                FILE_URL = getArguments().getString(Constants.SERVER_FILE_URL);
                Log.d("update_doc_log","FILE_URL "+FILE_URL);
                checkLocalFile();
                tvNext.setVisibility(View.GONE);

            } else{
                FILE_PATH = getArguments().getString(Constants.PDF_PATH);
                String[] seperated = FILE_PATH.split("/");
                FILENAME = seperated[seperated.length -1];
                Log.d("add_doc_log","file path "+ FILE_PATH);
                Log.d("add_doc_log","file name "+ FILENAME);
                showPDF();
            }

            if(getArguments().containsKey(Constants.SHOULD_SHOW_TITLE)){
                if(!getArguments().getBoolean(Constants.SHOULD_SHOW_TITLE)){
                    linTitleContainer.setVisibility(View.INVISIBLE);
                }
            }


        }*/

        checkLocalFile();
    }

    private void setClickListeners() {

     /*   tvNext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });*/

        ivBack.setOnClickListener(v -> {
                       if(getFragmentManager()!=null)
                       getFragmentManager().popBackStack();
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
            Toast.makeText(getActivity(),"R Error  "+e.toString(),Toast.LENGTH_SHORT).show();
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
            // Here, we render the page onto the Bitmap.
            // To render a portion of the page, use the second and third parameter. Pass nulls to get
            // the default result.
            // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            // We are ready to show the Bitmap to user.
            imageViewPdf.setImageBitmap(bitmap);
            updateUi();
        } catch (Exception e) {
            Toast.makeText(getActivity(),"sp Error  "+e.toString(),Toast.LENGTH_SHORT).show();
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.66.98.251:9501/")
                .build();

        FileHandlerService handlerService = retrofit.create(FileHandlerService.class);

        //Toast.makeText(FourBaseCareApp.activityFromApp, "URL : https://s3.ap-south-1.amazonaws.com/4care-dev/"+FILENAME, Toast.LENGTH_SHORT).show();
        Call<ResponseBody> call = handlerService.downloadFile("66715f77-f667-4bea-ae86-28429a214737");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pb_per.setVisibility(View.GONE);

                Log.d("update_doc_log","5 "+response.isSuccessful());
                Log.d("update_doc_log","5.1 "+response.message());
                Log.d("update_doc_log","5.1 "+response.code());
                Log.d("update_doc_log","5.1 "+response.errorBody());
                Log.d("update_doc_log","5.1 "+response.body());

                if (response.isSuccessful()) {
                    //Toast.makeText(FourBaseCareApp.activityFromApp, "Came here", Toast.LENGTH_SHORT).show();
                    if (writeResponseBodyToDisk(response.body())) {
                        //listener.onFileLoaded(file);
                        Log.v("retro_download", "download completed");
                        Log.d("update_doc_log","6 download done");
                        try {
                            openRenderer(getActivity());
                            showPage(pageIndex);

                        } catch (Exception e) {
                            Log.d("update_doc_log","7 "+e.toString());
                            Toast.makeText(FourBaseCareApp.activityFromApp, "Err "+e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }

                    }
                } else {
                    //listener.onDownloadFailed("Resource not Found");

                    Log.v("retro_download", "download Error");

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
            e.printStackTrace();
            Toast.makeText(FourBaseCareApp.activityFromApp,"Err 2 "+e.toString(), Toast.LENGTH_SHORT);
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
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please provide needed permissions first from app permission settings", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
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
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.setCancelable(false);
                    alert.show();

                  /*  requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);*/
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
}
