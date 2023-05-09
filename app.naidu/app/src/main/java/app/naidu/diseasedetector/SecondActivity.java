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
import android.webkit.WebResourceRequest;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import app.naidu.diseasedetector.ml.Rice;
import app.naidu.diseasedetector.R;


public class SecondActivity extends AppCompatActivity {
    private Button button;
    Button camera, gallery,explore;
    ImageView imageView;
    TextView result, confidence,confidencesText;
//    LinearLayout align1;

    int h = 512;
    int w = 512;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        explore = findViewById(R.id.e1);
        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);
//        align1 = findViewById(R.id.align1);
        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        confidencesText = findViewById(R.id.confidencesText);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explore.setVisibility(View.INVISIBLE);
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
                explore.setVisibility(View.INVISIBLE);
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);

            }
        });
    }


    public void classifyImage(Bitmap image){
        try {
            Rice model = Rice.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, h, w, 3},DataType.FLOAT32);
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
            Rice.Outputs outputs = model.process(inputFeature0);
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
            String[] classes = {"Brown Spot","Healthy","Invalid","Leaf Blast"};
            result.setText(String.format("%s: %.1f%%\n", classes[maxPos], maxConfidence * 100));
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



            // Releases model resources if no longer used.
            model.close();
//            align1.setVisibility(View.INVISIBLE);
            explore.setVisibility(View.VISIBLE);
            explore.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    RelativeLayout d1 = (RelativeLayout)  findViewById(R.id.d1);



                    d1.setVisibility(v.INVISIBLE);

                    WebView myWebView = (WebView) findViewById(R.id.webview);
                    myWebView.getSettings().setJavaScriptEnabled(true); // enable JavaScript if needed
                    myWebView.getSettings().setLoadWithOverviewMode(true); // enable mobile view
                    myWebView.getSettings().setUseWideViewPort(true);
                    myWebView.getSettings().setBuiltInZoomControls(true);
                    myWebView.getSettings().setDisplayZoomControls(true);
                    myWebView.getSettings().setSupportZoom(true);
                    myWebView.getSettings().setUseWideViewPort(true);
                    myWebView.getSettings().setLoadWithOverviewMode(true);
                    myWebView.loadUrl("https://agritech.tnau.ac.in/crop_protection/crop_prot_crop%20diseases_cereals_paddy.html");
                    myWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                            view.loadUrl(request.getUrl().toString());
                            return true;
                        }
                    });

                    RelativeLayout d2 = (RelativeLayout) findViewById(R.id.d2);
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