package com.oncobuddy.app.utils.background_task;

import android.graphics.Bitmap;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.textract.AmazonTextractClient;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;
import com.amazonaws.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/***
 * THis class is responsible for sending media image to the aws server
 */

public class AwsTextractTask extends BaseTask {

    //private final iOnDataFetched listener;//listener in fragment that shows and hides ProgressBar
    private ExtractTextInterface extractTextInterface;
    private String filePath;
    AmazonTextractClient amazonTextractClient;
    private String accessKey;
    private String secretKey;


    public AwsTextractTask(String filePath, ExtractTextInterface extractTextInterface, String accessKey, String secretKey) {
        this.filePath = filePath;
        this.extractTextInterface = extractTextInterface;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        Log.d("aws_credentials", "access key "+accessKey);
        Log.d("aws_credentials", "secret key "+secretKey);


        BasicAWSCredentials basicAWSCredentials =
                new BasicAWSCredentials(accessKey,secretKey);
        StaticCredentialsProvider staticCredentialsProvider = new StaticCredentialsProvider(basicAWSCredentials);

        amazonTextractClient = new AmazonTextractClient(staticCredentialsProvider);

    }

    @Override
    public Object call() throws Exception {
     
       String result = "";
       result = extractText();
       return result;
    }

    @Override
    public void setUiForLoading() {
        //listener.showProgressBar();
        Log.d("extract_text","Pre execute");
    }

    @Override
    public void setDataAfterLoading(Object result) {
        /*listener.setDataInPageWithResult(result);
        listener.hideProgressBar();*/
        Log.d("extract_text_log",result.toString());
        extractTextInterface.onTextExtracted(result.toString());

    }

    public interface ExtractTextInterface{
        void onTextExtracted(String result);
    }

    private Bitmap getBitmap(Buffer buffer, int width, int height) {
        buffer.rewind();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return bitmap;
    }

    private String extractText(){
        Log.d("extract_text","in background");
        try {
            ByteBuffer imageBytes;
            try (InputStream inputStream = new FileInputStream(filePath)) {
                imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
            }

            Log.d("extract_text","0 "+filePath);
            //Log.d("extract_text","0 "+b.toString());
            // 2. Setup textract client
            BasicAWSCredentials basicAWSCredentials =
                    new BasicAWSCredentials(accessKey,secretKey);
            StaticCredentialsProvider staticCredentialsProvider = new StaticCredentialsProvider(basicAWSCredentials);
            AmazonTextractClient amazonTextractClient = new AmazonTextractClient(staticCredentialsProvider);

            Log.d("extract_text","1");
            // 3. Start text extraction procedure
            DetectDocumentTextRequest request = new DetectDocumentTextRequest()
                    .withDocument(new Document().withBytes(imageBytes));


            Log.d("extract_text","2");
            // 4. Get Result
            DetectDocumentTextResult result = amazonTextractClient.detectDocumentText(request);
            String extractedText = "";
            for(int i = 0 ;i<result.getBlocks().size();i++){
                Log.d("textract text ", "Message "+result.getBlocks().get(i).getText());
                extractedText+=result.getBlocks().get(i).getText()+" ";
            }
            Log.d("extract_text","3");
            Log.d("extract_text","Total blocks "+result.getBlocks().size());
            Log.d("extract_text","Result "+result.toString());
            Log.d("extract_text","Version "+result.getDetectDocumentTextModelVersion());
            Log.d("extract_text","final "+extractedText);
            return extractedText;
        } catch (Exception e) {
            Log.d("extract_text","Ext. Error "+e.toString());
            e.printStackTrace();
            return "";
        }
    }
}
