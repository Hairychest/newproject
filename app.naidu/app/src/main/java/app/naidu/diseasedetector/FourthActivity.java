package app.naidu.diseasedetector;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import app.naidu.diseasedetector.ml.Potato;
import app.naidu.diseasedetector.R;

public class FourthActivity extends AppCompatActivity {
    private Button button;
    Button camera, gallery,explore;
    ImageView imageView;
    TextView result, confidence,confidencesText;

    int h = 256;
    int w = 256;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);



        explore = findViewById(R.id.e4);
        camera = findViewById(R.id.button41);
        gallery = findViewById(R.id.button42);
//        align1 = findViewById(R.id.align1);
        result = findViewById(R.id.result4);
        confidence = findViewById(R.id.confidence4);
        imageView = findViewById(R.id.imageView4);
        confidencesText = findViewById(R.id.confidencesText4);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });
    }


    public void classifyImage(Bitmap image){
        try {
            Potato model = Potato.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, h, w, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * h * w * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[h * w];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < h; i ++){
                for(int j = 0; j < w; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) );
                    byteBuffer.putFloat((val & 0xFF));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Potato.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes ={"Early Blight", "Healthy", "Invalid", "Late Blight"};
            String s = "";
            for(int i = 0; i < classes.length; i++){
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
            }
            if (classes[maxPos] =="Invalid"){
                s = "Upload Different Image";
                result.setText(String.format("%s\n", classes[maxPos]));
                confidencesText.setText("Error:");
            }
            confidence.setText(s);
            result.setText(String.format("%s: %.1f%%\n", classes[maxPos], maxConfidence * 100));
            if (confidences[maxPos] < 0.75){
                result.setText(String.format("%s: %.1f%%\n%s", classes[maxPos], maxConfidence * 100,"(Verify with more Images)"));

            }

            // Releases model resources if no longer used.
            model.close();
            confidencesText.setText("Percentage:");
            explore.setVisibility(View.VISIBLE);
            explore.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    RelativeLayout d1 = (RelativeLayout)  findViewById(R.id.d41);



                    d1.setVisibility(v.INVISIBLE);

                    WebView myWebView = (WebView) findViewById(R.id.webview4);
                    myWebView.getSettings().setJavaScriptEnabled(true); // enable JavaScript if needed
                    myWebView.getSettings().setLoadWithOverviewMode(true); // enable mobile view
                    myWebView.getSettings().setUseWideViewPort(true);
                    myWebView.getSettings().setBuiltInZoomControls(true);
                    myWebView.getSettings().setDisplayZoomControls(true);
                    myWebView.getSettings().setSupportZoom(true);
                    myWebView.getSettings().setUseWideViewPort(true);
                    myWebView.getSettings().setLoadWithOverviewMode(true);
                    myWebView.loadUrl("https://agritech.tnau.ac.in/crop_protection/crop_prot_crop%20diseases_veg_potato.html");
                    myWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                            view.loadUrl(request.getUrl().toString());
                            return true;
                        }
                    });
                    RelativeLayout d2 = (RelativeLayout) findViewById(R.id.d42);
                    d2.setVisibility(v.VISIBLE);
                    myWebView.setVisibility(v.VISIBLE);
                }
            });
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, h, w, false);
                classifyImage(image);
            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, h, w, false);
                classifyImage(image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}