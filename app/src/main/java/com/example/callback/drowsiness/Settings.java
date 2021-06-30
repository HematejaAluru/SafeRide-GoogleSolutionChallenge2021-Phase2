package com.example.callback.drowsiness;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Settings extends AppCompatActivity {
    private EditText PP1, PP2, PP3, PP4, PP5;
    public static String PN1="";
    public static String PN2="";
    public static String PN3="";
    public static String PN4="";
    public static String PN5="";
    RadioGroup simGroup;
    public static Context c1;
    public int simselected;
    private Button save;
    private TextView saveAgainComp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        PP1 = findViewById(R.id.phonenum1);
        PP2 = findViewById(R.id.phonenum2);
        PP3 = findViewById(R.id.phonenum3);
        PP4 = findViewById(R.id.phonenum4);
        PP5 = findViewById(R.id.phonenum5);
        save = findViewById(R.id.save);
        simGroup = findViewById(R.id.simgroup);
        saveAgainComp = findViewById(R.id.status_phonnum_again);
        c1=getApplicationContext();
        try {
            // if phone numbers already stored...
            String OutputPh = readFromFile(getApplicationContext());
            String[] KK=OutputPh.split(":");
            if(!KK[0].trim().isEmpty()) {
                PP1.setText(KK[0]);
                PN1=KK[0];
                save.setEnabled(true);
            }
            if(!KK[1].trim().isEmpty()) {
                PP2.setText(KK[1]);
                PN2=KK[1];
                save.setEnabled(true);
            }
            if(!KK[2].trim().isEmpty()) {
                PP3.setText(KK[2]);
                PN3=KK[2];
                save.setEnabled(true);
            }
            if(!KK[3].trim().isEmpty()) {
                PP4.setText(KK[3]);
                PN4=KK[3];
                save.setEnabled(true);
            }
            if(!KK[4].trim().isEmpty()) {
                PP5.setText(KK[4]);
                PN5=KK[4];
                save.setEnabled(true);
            }
        }
        catch (Exception e){
            // do nothing if phone numbers are not stored...
        }
        simGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton=(RadioButton)group.findViewById(checkedId);
                simselected=Integer.parseInt(radioButton.getTag().toString());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!checkIfContactsAreValid(PP1, PP2, PP3, PP4, PP5)) {
                    Toast.makeText(getApplicationContext(),"Please enter valid phone number" ,Toast.LENGTH_SHORT).show();
                } else {
                    writeToFile(PP1.getText().toString() + ":" + PP2.getText().toString() + ":" + PP3.getText().toString() + ":" + PP4.getText().toString() + ":" + PP5.getText().toString(), getApplicationContext());
                    saveAgainComp.setVisibility(View.VISIBLE);
                    saveAgainComp.setText("Phone Numbers Saved");
                    monitor_menu.SelectedSim=simselected;
                    // for selecting sim slot....
                    Intent monitorMenuIntent = new Intent(Settings.this, MainActivity.class);
                    startActivity(monitorMenuIntent);
                    Settings.this.finish();
                }
            }
            private void writeToFile(String data, Context context) {
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
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
    public static void getmobilenumbers(){
        try {
            //if phone numbers already stored...
            String OutputPh = readFromFile(c1);
            Log.d("Nothing","This is working"+ OutputPh);
            String[] KK=OutputPh.split(":");
            if(!KK[0].trim().isEmpty()) {
                PN1=KK[0];
                Log.d("Nothing","This is pn1: "+PN1);
            }
            if(!KK[1].trim().isEmpty()) {
                PN2=KK[1];
                Log.d("Nothing","This is pn1: "+PN2);
            }
            if(!KK[2].trim().isEmpty()) {
                PN3=KK[2];
                Log.d("Nothing","This is pn1: "+PN3);
            }
            if(!KK[3].trim().isEmpty()) {
                PN4=KK[3];
                Log.d("Nothing","This is pn1: "+PN4);
            }
            if(!KK[4].trim().isEmpty()) {
                PN5=KK[4];
                Log.d("Nothing","This is pn1: "+PN5);
            }
        }
        catch (Exception e){
            //do nothing if phone numbers are not stored...
        }
    }

    //to read the file of phone numbers....
    public static String readFromFile(Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("config.txt");

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

    private boolean checkIfContactsAreValid(EditText PP1, EditText PP2, EditText PP3, EditText PP4, EditText PP5) {
        // checks if atleast one number is entered
        if(PP1.getText().length()==0 && PP2.getText().length()==0 && PP3.getText().length()==0 && PP4.getText().length()==0 && PP5.getText().length()==0) {
            Log.d("Nothing","PP1 is working1");
            return false;
        }
        //checks if number entered is having 10 digits or not
        if((PP1.getText().length()>0 && PP1.getText().length()!=10) ||
                (PP2.getText().length()>0 && PP2.getText().length()!=10) ||
                (PP3.getText().length()>0 && PP3.getText().length()!=10)|| (PP4.getText().length()>0 && PP4.getText().length()!=10) ||
                (PP5.getText().length()>0 && PP5.getText().length()!=10)) {
            Log.d("Nothing","PP1 is working2");
            return false;
        }
        // checks if number entered is having only numerals
        int p1=0;
        int p2=0;
        int p3=0;
        int p4=0;
        int p5=0;
        try {
            if ((PP1.getText().length() == 10)&& isNumeric(PP1.getText().toString())){
                p1=1;
            }
            else{
                p1=2;
            }
            if ((PP2.getText().length() == 10)&&isNumeric(PP2.getText().toString())){
                p2=1;
            }
            else{
                p2=2;
            }
            if ((PP3.getText().length() == 10)&&isNumeric(PP3.getText().toString())){
                p3=1;
            }
            else{
                p3=2;
            }
            if ((PP4.getText().length() == 10)&&(isNumeric(PP4.getText().toString()))){
                p4=1;
            }
            else{
                p4=2;
            }
            if ((PP5.getText().length() == 10)&&isNumeric(PP5.getText().toString())){
                p5=1;
            }
            else{
                p5=2;
            }
            if(checkit(p1)&&checkit(p2)&&checkit(p3)&&checkit(p4)&&checkit(p5)){
                return true;
            }
            return true;
        } catch(Exception e){
            return false;
        }
    }
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
    public boolean checkit(int p){
        if(p==0){
            return true;
        }
        else{
            if(p==1){
                return true;
            }
            else{
               return false;
            }
        }
    }
}
