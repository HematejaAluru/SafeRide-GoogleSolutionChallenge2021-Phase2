package com.example.callback.drowsiness;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Objects;

public class LongTermDisease extends AppCompatActivity {

    private TextView displayText;
    private EditText diseaseText;
    private Button addButton;
    private TextView Displaytexthome;
    public String diseases="";
    public String bloodgroup="";
    private EditText bloodgroupedit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.long_term_disease);
        diseaseText = findViewById(R.id.diseaseText);
        addButton = findViewById(R.id.addButton);
        displayText = findViewById(R.id.displayText);
        Displaytexthome=findViewById(R.id.Displaytexthome);

        bloodgroupedit=findViewById(R.id.bloodgroup);

        try {
            //if diseases already stored...
            diseases = readFromFile(getApplicationContext());
            bloodgroup=readFromFile2(getApplicationContext());
            bloodgroupedit.setText(bloodgroup);
        }
        catch (Exception e){
            //do nothing if diseases are not stored...
        }
        previousdiseases();

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(diseaseText.getText() == null && Objects.requireNonNull(diseaseText.getText()).toString().length()==0){
                    Toast.makeText(getApplicationContext(), "Please enter disease name", Toast.LENGTH_LONG).show();
                }
                writeToFile(diseases +":"+diseaseText.getText().toString(), getApplicationContext());
                writeToFile2(bloodgroupedit.getText().toString(),getApplicationContext());
                displayText.setVisibility(View.VISIBLE);
                if(diseaseText.getText().length()==0){
                    displayText.setText(bloodgroupedit.getText()+" Successfully Added");
                }
                else {
                    displayText.setText(diseaseText.getText() + " & " + bloodgroupedit.getText() + " Successfully Added");
                }
                try {
                    //if diseases already stored...
                    diseases = readFromFile(getApplicationContext());
                }
                catch (Exception e){
                    //do nothing if diseases are not stored...
                }
                previousdiseases();
            }

            private void writeToFile(String data, Context context) {
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("longTermDisease.txt", Context.MODE_PRIVATE));
                    outputStreamWriter.write(data);
                    outputStreamWriter.close();
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                    Toast.makeText(getApplicationContext(),"File write failed: " + e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
            private void writeToFile2(String data, Context context) {
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("bloodgroup.txt", Context.MODE_PRIVATE));
                    outputStreamWriter.write(data);
                    outputStreamWriter.close();
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                    Toast.makeText(getApplicationContext(),"File write failed: " + e.toString(),Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
    private void previousdiseases(){
        String diseasesasarray[]=diseases.split(":");
        String remakestring="";
        for(int i=0;i<diseasesasarray.length;i++){
            remakestring=remakestring+"\n"+diseasesasarray[i];
        }
        Displaytexthome.setText("Previously entered: \n"+remakestring);
    }

    //to read the file of long diseases....
    private String readFromFile(Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("longTermDisease.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }
    private String readFromFile2(Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("bloodgroup.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

}
