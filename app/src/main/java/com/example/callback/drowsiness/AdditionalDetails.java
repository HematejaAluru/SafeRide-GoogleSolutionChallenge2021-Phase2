package com.example.callback.drowsiness;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.callback.drowsiness.monitor_menu.AMVMean;
import static com.example.callback.drowsiness.monitor_menu.GAforGraph;
import static com.example.callback.drowsiness.monitor_menu.GMVMean;
import static com.example.callback.drowsiness.monitor_menu.MAforGraph;
import static com.example.callback.drowsiness.monitor_menu.currentAmvValue;
import static com.example.callback.drowsiness.monitor_menu.currentLatitude;
import static com.example.callback.drowsiness.monitor_menu.currentLongitude;

public class AdditionalDetails extends Fragment {
    private static LineChart mChart,mChart2;
    private Thread thread;
    private boolean plotData = true;
    private boolean plotData2=true;
    public static LineData data,data2;
    public static Legend l,l2;
    TextView amvValue;
    TextView gmvValue;
    TextView location;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root;
        root = inflater.inflate(R.layout.activity_additional_details,container,false);
        amvValue = root.findViewById(R.id.amv);
        gmvValue = root.findViewById(R.id.gmv);
        location = root.findViewById(R.id.location);
        amvValue.setText("AMV mean value: "+ String.valueOf(AMVMean));
        gmvValue.setText("GMV mean value: "+ String.valueOf(GMVMean ));
        updateamvandgmv();
        location.setText("Latitude: " + currentLatitude + "\nLongitude: " + currentLongitude );

        mChart = (LineChart) root.findViewById(R.id.chart1);
        mChart2=(LineChart) root.findViewById(R.id.chart2);
        mChart.getDescription().setEnabled(false);
        mChart2.getDescription().setEnabled(false);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        mChart2.setTouchEnabled(true);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        mChart2.setDragEnabled(true);
        mChart2.setScaleEnabled(true);
        mChart2.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart2.setPinchZoom(false);
        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);
        mChart2.setBackgroundColor(Color.WHITE);

        data = new LineData();
        data.setValueTextColor(Color.WHITE);

        data2 = new LineData();
        data2.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);
        l = mChart.getLegend();

        mChart2.setData(data2);
        l2 = mChart2.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.rgb(244, 86, 87));
        l2.setForm(Legend.LegendForm.LINE);
        l2.setTextColor(Color.rgb(244, 86, 87));

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(50f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getXAxis().setDrawGridLines(true);
        mChart.setDrawBorders(true);

        XAxis xl2 = mChart2.getXAxis();
        xl2.setTextColor(Color.BLACK);
        xl2.setDrawGridLines(true);
        xl2.setAvoidFirstLastClipping(true);
        xl2.setEnabled(true);

        YAxis leftAxis2 = mChart2.getAxisLeft();
        leftAxis2.setTextColor(Color.BLACK);
        leftAxis2.setDrawGridLines(false);
        leftAxis2.setAxisMaximum(50f);
        leftAxis2.setAxisMinimum(0f);
        leftAxis2.setDrawGridLines(true);

        YAxis rightAxis2 = mChart2.getAxisRight();
        rightAxis2.setEnabled(false);

        mChart2.getAxisLeft().setDrawGridLines(true);
        mChart2.getXAxis().setDrawGridLines(true);
        mChart2.setDrawBorders(true);
        try {
            feedMultiple();
            feedMultiple2();
            addEntry(MAforGraph);
            addEntry2(GAforGraph);
        }
        catch (Exception e){
            Log.d("Nothing", "There is a problem with graphs please reopen the app");
        }
        return root;
    }
    public static void addEntry(double AMV) {
        try{
        LineData data = mChart.getData();

        if (data != null || !(data.toString().isEmpty())) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            // data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data.addEntry(new Entry(set.getEntryCount(), (float)(AMV)), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                addEntry(MAforGraph);
            }
        },60);
        }
        catch (Exception e){
            Log.d("Nothing","There is a problem with graphs please reopen the app");
        }
    }

    private static LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Accident Monitoring Value (using Accelerometer)");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.rgb(62, 95, 230));
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    public void updateamvandgmv(){
        amvValue.setText("AMV mean value: "+ String.valueOf(AMVMean));
        gmvValue.setText("GMV mean value: "+ String.valueOf(GMVMean ));
        location.setText("Latitude: " + currentLatitude + "\nLongitude: " + currentLongitude );
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateamvandgmv();
            }
        },3000);
    }

    public static void addEntry2(double GMV) {
        try{
        LineData data = mChart2.getData();

        if (data != null || !(data.toString().isEmpty())) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet2();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), (float)(GMV)), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart2.notifyDataSetChanged();

            // limit the number of visible entries
            mChart2.setVisibleXRangeMaximum(150);

            // move to the latest entry
            mChart2.moveViewToX(data.getEntryCount());

        }
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                addEntry2(GAforGraph);
            }
        },60);
        }
        catch (Exception e){
            Log.d("Nothing","There is a problem with graphs please reopen the app");
        }
    }
    private static LineDataSet createSet2() {

        LineDataSet set = new LineDataSet(null, "Accident Monitoring Value (using Gyroscope)");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.rgb(62, 95, 230));
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }
    private void feedMultiple2() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){

                    plotData2 = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }
    @Override
    //Pressed return button - returns to the results menu
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    Intent next = new Intent(getContext(), MainActivity.class);
                    next.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(next);
                    return true;
                }
                return false;
            }
        });
    }
}
