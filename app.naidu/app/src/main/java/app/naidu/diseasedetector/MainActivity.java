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
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private Button rice,tomato,wheat,potato;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rice = findViewById(R.id.rice);
        tomato = findViewById(R.id.tomato);
        wheat = findViewById(R.id.wheat);
        potato = findViewById(R.id.potato);

        rice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondActivity();
            }
        });


        wheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThirdActivity();
            }
        });

        potato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFourthActivity();
            }
        });


        tomato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFifthActivity();
            }
        });



    }
    public void openSecondActivity(){
       Intent i = new Intent(MainActivity.this,SecondActivity.class);
       startActivity(i);
    }


    public void openThirdActivity(){
        Intent i = new Intent(MainActivity.this,ThirdActivity.class);
        startActivity(i);
    }


public void openFourthActivity(){
        Intent i = new Intent(MainActivity.this,FourthActivity.class);
        startActivity(i);
        }


public void openFifthActivity(){
        Intent i = new Intent(MainActivity.this,FifthActivity.class);
        startActivity(i);
        }

        }