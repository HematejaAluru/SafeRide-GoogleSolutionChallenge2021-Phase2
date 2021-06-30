package com.example.callback.drowsiness;

import android.Manifest;
import com.google.android.material.card.MaterialCardView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.androdocs.httprequest.HttpRequest;
import org.json.JSONObject;
import org.tensorflow.lite.examples.detection.DetectorActivity;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import static android.content.Context.SENSOR_SERVICE;
import static com.example.callback.drowsiness.MainActivity.countofexceeds;
import static com.example.callback.drowsiness.Settings.PN1;
import static com.example.callback.drowsiness.Settings.PN2;
import static com.example.callback.drowsiness.Settings.PN3;
import static com.example.callback.drowsiness.Settings.PN4;
import static com.example.callback.drowsiness.Settings.PN5;
import static com.example.callback.drowsiness.Settings.getmobilenumbers;

public class monitor_menu extends Fragment implements SensorEventListener {
    private LocationManager mLocationManagerNetwork;
    public double xgyrot1=0;
    public double ygyrot1=0;
    public double zgyrot1=0;
    public double xgyrot2=0;
    public double ygyrot2=0;
    public double zgyrot2=0;
    double currentTime;
    double startTime;
    public double thresholdAMV = 49.05;  //49.05
    public static double MAforGraph;
    public static double GAforGraph;
    public double MG,MGchange,currGVal;
    public TextView xGyro,yGyro,zGyro;
    public double thresholdGMV=16.25;  //16.25
    private android.location.LocationListener mLocationListenerNetwork;
    public static double[] temp=new double[100000];
    public static double[] tempmean=new double[100000];
    public static double[] speeds=new double[100000];
    public static boolean accidentoccured=false;
    public static boolean falloccured=false;
    public static double accelerationMean;
    public static String accelerationTip;
    public static double Acczmean;
    public static String Accztip;
    public static String Phone1;
    public static String Phone2;
    public static String Phone3;
    public static String Phone4;
    public static String Phone5;
    public static int tempnum=0;
    public static int speednum=0;
    public static double currspeed=0;
    private boolean countBool=true;
    public boolean disspeed=true;
    private int FalseA=0;
    private boolean Ontracking=false;
    private boolean Ontracking2=false;
    public static double currentLatitude;
    public static double currentLongitude;
    public static double currentAmvValue;
    public static int noofspeedwarnings;

    //For sending message
    private EditText txtMessage;
    private Button btnSms;

    //Location
    TextView LocationText1;
    TextView LocationText2;

    //Sensor
    private SensorManager sensorManager;
    Sensor accelerometer;
    Sensor gyroscopmeter;
    TextView Sensortext;
    RadioButton R2;
    RadioGroup RG1;

    //End sensor
    LinearLayout b,b1;
    TextView ttv;
    String selectedTime = "2";
    public static int SelectedSim = 1;
    private String key_2 = "callbackcat's project";
    private String key_4 = "senstivity";
    private final static long ACC_CHECK_INTERVAL = 100;
    private final static long GYRO_CHECK_INTERVAL=100;
    private long lastAccCheck;
    private long lastgyroCheck;
    private boolean countbool=true;
    double oldVal=0;
    public double oldGVal=0;
    public static double AMVMean;
    public static double GMVMean;
    public int countGMV;
    public double GMV;
    public static double AMVCount;
    public static float Distance;
    Location locationA;
    boolean isFirstSet = true;

    //for weather forcast
    public String API = "7f31e8bc04594ac830cef65ee5b12b7a"; //API KEY (openweatherapi)
    Button B1;
    TextView windtext,pressuretext,humiditytext,sunrisetext,sunsettext,temptext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View root;
        lastAccCheck = System.currentTimeMillis();
        root = inflater.inflate(R.layout.activity_monitor_menu_4, container, false);

        windtext = (TextView) root.findViewById(R.id.windtext);
        pressuretext = (TextView) root.findViewById(R.id.pressuretext);
        humiditytext = (TextView)root.findViewById(R.id.humidtext);
        sunrisetext = (TextView)root.findViewById(R.id.sunrisetext);
        sunsettext = (TextView)root.findViewById(R.id.sunsettext);
        temptext = (TextView)root.findViewById(R.id.temptext);

        //To get the location of the device
        RG1 = root.findViewById(R.id.radiog1);
        RG1.clearCheck();
        Sensortext = (TextView) root.findViewById(R.id.textView2);
        sensorManager = (SensorManager) requireContext().getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyroscopmeter = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener((SensorEventListener) this, accelerometer, 2000000);

        if(!isAirplaneModeOn(getContext())){
            //do nothing
        }
        else{
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Please turn off Airplane mode").setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            }).show();
        }

        if(gyroscopmeter!=null){
            sensorManager.registerListener((SensorEventListener)this,gyroscopmeter,SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("onCreate: ","Gyro Registered!");
        }
        else{
            Log.d("onCreate: ","Gyro Registration failed!");
        }

        R2 = root.findViewById(R.id.radio2);
        R2.setChecked(true);
        b = root.findViewById(R.id.card2layout);
        b1= root.findViewById(R.id.card3layout);
        RG1.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(RadioGroup group,int checkedId){
                        RadioButton radioButton=(RadioButton)group.findViewById(checkedId);
                        selectedTime=radioButton.getTag().toString();
                    }
                }
        );

        //if drowsiness is clicked...
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(root.getContext(), FaceTrackerActivity.class);
                i.putExtra(key_4, "" + selectedTime);
                i.putExtra(key_2, DateFormat.getDateTimeInstance().format(new Date()));
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                getActivity().finish();
            }
        });
        Intent myIntent = new Intent(monitor_menu.this.getContext(), DetectorActivity.class);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(myIntent);
            }
        });

        if(isLocationEnabled(getContext())){
            // do nothing
        }else{
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Please turn on Location").setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            }).show();
        }

        getPositionNetwork();
        getPositionNetwork();

        return root;
    }

    private static boolean isAirplaneModeOn(Context context) {

        return Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;

    }

    //Accident Detection Algorithm
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            Log.d("SensorDATA ACC : ", Double.toString(event.values[0]) + " " + Double.toString(event.values[1]) + " " + Double.toString(event.values[2]));
            double MA;
            long currTime = System.currentTimeMillis();
            MA = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));
            MAforGraph=MA;
            double currVal = MA;
            double MAchange;
            AMVCount = AMVCount + 1;
            AMVMean = ((AMVMean + MA) / AMVCount);
            Acczmean = ((Acczmean + event.values[2]) / AMVCount);
            if (20 < AMVMean && AMVMean < 30) {
                Accztip = "You are driving too rash";
            } else if (10<AMVMean && AMVMean< 20) {
                Accztip = "You are driving uniformly";
            } else if (0<AMVMean && AMVMean < 10) {
                Accztip = "You are driving too smoothly";
            } else if (AMVMean > 25) {
                Accztip = "Your driving may harm you";
            }
            if (isFirstSet) {
                startTime = System.currentTimeMillis();
                isFirstSet = false;
            }
            if (currTime - lastAccCheck > ACC_CHECK_INTERVAL) {
                MAchange = Math.abs(oldVal - currVal);

                lastAccCheck = System.currentTimeMillis();
                oldVal = MA;
                if (MA > thresholdAMV && (!Ontracking)) {
                    Ontracking = true;
                    accidentoccured = true;
                    //Sending messages to all the phone numbers...
                    String msg3="";
                    String msgbg="";
                    final ArrayList<Integer> simCardList = new ArrayList<>();
                    try {
                        String msg2 = readFromFilefordiseases(getContext());
                        String[] msg2array=msg2.split(":");
                        for (int i=0;i<msg2array.length;i++){
                            msg3=msg3+"\n"+msg2array[i];
                        }
                    }
                    catch(Exception e){
                        //Do nothing
                    }
                    try{
                        msgbg=readFromFileforblood(getContext());
                    }
                    catch (Exception e){
                        //Do nothing
                    }

                    String msg = "Accident happened at Latitude: " + String.valueOf(currentLatitude) + "   -   Longitude: " + String.valueOf(currentLongitude)+" \nLong Term Diseases: \n"+msg3+"\nBlood Group: "+msgbg;
                    SubscriptionManager subscriptionManager;
                    if (getContext() != null) {
                        subscriptionManager = SubscriptionManager.from(getContext());
                        @SuppressLint("MissingPermission") final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                            int subscriptionId = subscriptionInfo.getSubscriptionId();
                            simCardList.add(subscriptionId);
                        }

                        int smsToSendFrom = simCardList.get(SelectedSim); //assign your desired sim to send sms, or user selected choice

                        //For stopping the messages...
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setIcon(R.drawable.ic_dialog_alert);
                        builder.setTitle("Is it a False Alarm?");
                        builder.setMessage("Is it a False Alarm?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FalseA = 1;
                                Toast.makeText(getContext(), "SMS not sent", Toast.LENGTH_SHORT).show();
                            }

                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setCancelable(true);
                        final AlertDialog dlg = builder.create();

                        dlg.show();

                        final Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            public void run() {
                                dlg.dismiss(); // when the task active then close the dialog
                                if ((FalseA == 1)) {
                                    FalseA = 0;
                                } else {
                                    getmobilenumbers();
                                    if(!PN1.isEmpty()){
                                        SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom).sendTextMessage(PN1, null, msg, null, null); //use your phone number, message and pending intents
                                    }
                                    if(!PN2.isEmpty()){
                                        SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom).sendTextMessage(PN2, null, msg, null, null); //use your phone number, message and pending intents
                                    }
                                    if(!PN3.isEmpty()){
                                        SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom).sendTextMessage(PN3, null, msg, null, null); //use your phone number, message and pending intents
                                    }
                                    if(!PN4.isEmpty()){
                                        SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom).sendTextMessage(PN4, null, msg, null, null); //use your phone number, message and pending intents
                                    }
                                    if(!PN5.isEmpty()){
                                        SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom).sendTextMessage(PN5, null, msg, null, null); //use your phone number, message and pending intents
                                    }
                                }
                                Ontracking = false;
                                t.cancel(); // to cancel the timer thread...
                            }
                        }, 5000);
                    }
                    accidentoccured = false;
                } else if (MA >= (thresholdAMV - (0.05 * thresholdAMV)) && falloccured && (Ontracking2 != true)) {
                    Ontracking2 = true;
                    // Sending messages to all the phone numbers...
                    final ArrayList<Integer> simCardList = new ArrayList<>();
                    String msg = "Accident happened at Latitude: " + String.valueOf(currentLatitude) + "   -   Longitude: " + String.valueOf(currentLongitude);
                    SubscriptionManager subscriptionManager;
                    if (getContext() != null) {
                        subscriptionManager = SubscriptionManager.from(getContext());
                        @SuppressLint("MissingPermission") final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                            int subscriptionId = subscriptionInfo.getSubscriptionId();
                            simCardList.add(subscriptionId);
                        }

                        int smsToSendFrom = simCardList.get(SelectedSim); // assign your desired sim to send sms, or user selected choice

                        // For stopping the messages...
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setIcon(R.drawable.ic_dialog_alert);
                        builder.setTitle("Is it a False Alarm?");
                        builder.setMessage("Is it a False Alarm?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FalseA = 1;
                                Toast.makeText(getContext(), "SMS not sent", Toast.LENGTH_SHORT).show();
                            }

                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setCancelable(true);
                        final AlertDialog dlg = builder.create();

                        dlg.show();

                        final Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            public void run() {
                                dlg.dismiss(); // when the task active then close the dialog
                                if ((FalseA == 1)) {
                                    FalseA = 0;
                                } else {
                                    getmobilenumbers();
                                    if(!PN1.isEmpty()){
                                        SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom).sendTextMessage(PN1, null, msg, null, null); //use your phone number, message and pending intents
                                    }
                                    if(!PN2.isEmpty()){
                                        SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom).sendTextMessage(PN2, null, msg, null, null); //use your phone number, message and pending intents
                                    }
                                    if(!PN3.isEmpty()){
                                        SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom).sendTextMessage(PN3, null, msg, null, null); //use your phone number, message and pending intents
                                    }
                                    if(!PN4.isEmpty()){
                                        SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom).sendTextMessage(PN4, null, msg, null, null); //use your phone number, message and pending intents
                                    }
                                    if(!PN5.isEmpty()){
                                        SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom).sendTextMessage(PN5, null, msg, null, null); //use your phone number, message and pending intents
                                    }
                                }
                                Ontracking2 = false;
                                t.cancel();// to cancel the timer thread...
                            }
                        }, 5000);
                    }
                    falloccured = false;
                }
            }
            currentTime = System.currentTimeMillis();
            temp[tempnum] = event.values[2]; // values for acceleration graph
            tempmean[tempnum] = AMVMean; // values for AMV graph
            tempnum = tempnum + 1; // values of time
        }
        else if(sensor.getType()==Sensor.TYPE_GYROSCOPE){
            long currTime2 = System.currentTimeMillis();
            long timediff = currTime2-lastgyroCheck;
            MG = Math.sqrt(Math.pow((event.values[0]-xgyrot1), 2) + Math.pow((event.values[1]-ygyrot1), 2) + Math.pow((event.values[2]-zgyrot1), 2));
            currGVal = MG;
            countGMV=countGMV+1;
            GMV=(GMV+MG);
            GMVMean=GMV/countGMV;
            GAforGraph=MG;
            if (currTime2 - lastgyroCheck > GYRO_CHECK_INTERVAL) {
                xgyrot1 =event.values[0];
                ygyrot1 =event.values[1];
                zgyrot1 =event.values[2];
                if(MG>thresholdGMV) {
                    falloccured=true;
                    if(getContext()!=null){
                        Toast.makeText(getContext(),"Fall Detected",Toast.LENGTH_SHORT).show();
                    }
                }
                lastgyroCheck = System.currentTimeMillis();
                oldGVal = MG;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getPositionNetwork() {
        if(getContext()!=null) {
            mLocationManagerNetwork = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

            mLocationListenerNetwork = new android.location.LocationListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onLocationChanged(Location location) {

                    if (countbool == true) {
                        locationA = location;
                        countbool = false;
                    }
                    Distance = Distance + location.distanceTo(locationA);
                    if(getContext()!=null&& disspeed&&(countofexceeds>0)){
                       // Toast.makeText(getContext(), "Distance: " + String.valueOf(Distance) + " m", Toast.LENGTH_SHORT).show();
                    }
                    currspeed = getSpeedown(location, locationA);
                    if(getContext()!=null && disspeed&&(countofexceeds>0)){
                      //  Toast.makeText(getContext(), "Current Speed: " + String.valueOf(currspeed) + " m/s", Toast.LENGTH_SHORT).show();
                    }
                    if(Distance>0){
                        countofexceeds=countofexceeds-1;
                        disspeed=false;
                    }
                    if(currspeed>0){
                        countofexceeds=countofexceeds-1;
                        disspeed=false;
                    }
                    speeds[speednum] = currspeed; //values for speed graph
                    speednum = speednum + 1;
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                    locationA = location;
                    new weatherTask().weatherloop();

                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    requestLocationPermission();
                }

                public void onProviderEnabled(String provider) {
                    Toast.makeText(getActivity(), "Location is enabled", Toast.LENGTH_SHORT).show();
                }

                public void onProviderDisabled(String provider) {
                    requestLocationPermission();
                }
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermission();
                } else {
                    mLocationManagerNetwork.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, mLocationListenerNetwork);
                }
            }
        }
    }

    private void requestLocationPermission() {
        if(getActivity()!=null) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                } else {
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            getPositionNetwork();
                            new weatherTask().weatherloop();
                        }
                    };

                    Handler h = new Handler();
                    h.postDelayed(r, 2000);
                }
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mLocationManagerNetwork != null) {
            mLocationManagerNetwork.removeUpdates(mLocationListenerNetwork);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // for calculating the speed using lat and long...
    public static double getSpeedown(Location currentLocation, Location oldLocation)
    {
        double newLat = currentLocation.getLatitude();
        double newLon = currentLocation.getLongitude();
        double oldLat = oldLocation.getLatitude();
        double oldLon = oldLocation.getLongitude();
        if(currentLocation.hasSpeed()){
            return currentLocation.getSpeed();
        } else {
            double radius = 6371000;
            double dLat = Math.toRadians(newLat-oldLat);
            double dLon = Math.toRadians(newLon-oldLon);
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(Math.toRadians(newLat)) * Math.cos(Math.toRadians(oldLat)) *
                            Math.sin(dLon/2) * Math.sin(dLon/2);
            double c = 2 * Math.asin(Math.sqrt(a));
            double distance =  Math.round(radius * c);
            double timeDifferent = currentLocation.getTime() - oldLocation.getTime();
            return ((distance/timeDifferent)/1000);
        }
    }

    // to read the file of phone numbers....
    private String readFromFile(Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("config.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
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

    // to write the file of phone numbers...
    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            Toast.makeText(getContext(),"File write failed: " + e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    class weatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            Log.d("Async","Async: This is working"+" Lon: "+currentLongitude+" Lat: "+currentLatitude);
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/onecall?lat=" +currentLatitude + "&lon="+currentLongitude+"&units=metric&appid=" + API);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Log.d("Async2","Async2: This is also working"+result);
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("current");
                Long updatedAt = main.getLong("dt");
                String temperature = main.getString("temp");
                String humi_dity = main.getString("humidity");
                String pre = main.getString("pressure");
                String windspeed = main.getString("wind_speed");
                Long rise = main.getLong("sunrise");
                String sunrise = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(rise * 1000));
                Long set = main.getLong("sunset");
                String sunset = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(set * 1000));




                // SET ALL VALUES IN TEXTBOX :
                sunrisetext.setText("Sunrise: "+sunrise);
                sunsettext.setText("Sunset:  "+sunset);
                humiditytext.setText(humi_dity+" %");
                pressuretext.setText(pre+" atm");
                windtext.setText(windspeed+" m/s");
                temptext.setText(temperature);

            } catch (Exception e) {

            }
        }
        public void weatherloop(){
            new weatherTask().execute();
        }
    }

    private String readFromFilefordiseases(Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("longTermDisease.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
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
    private String readFromFileforblood(Context context) {
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
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
}
