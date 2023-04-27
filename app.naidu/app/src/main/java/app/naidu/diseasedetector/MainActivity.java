/*
 * Created by ishaanjav
 * github.com/ishaanjav
 */

package app.naidu.diseasedetector;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {
    private ImageButton button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.imageButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivity();
            }
        });



    }
    public void openSecondActivity(){
       Intent i = new Intent(MainActivity.this,SecondActivity.class);
       startActivity(i);
    }

        }