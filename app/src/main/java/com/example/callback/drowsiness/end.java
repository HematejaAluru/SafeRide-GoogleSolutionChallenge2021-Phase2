package com.example.callback.drowsiness;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class end extends AppCompatActivity{
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> seriesmean;
    LineGraphSeries<DataPoint> seriesspeed;
    Button proceed;
    TextView d;
    TextView tip;
    TextView noofv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        proceed = findViewById(R.id.button2);
        d = findViewById(R.id.AMVV);
        noofv = findViewById(R.id.NoofV2);
        tip = findViewById(R.id.Accztip);
        tip.setText(monitor_menu.Accztip);
        proceed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent next = new Intent(end.this, MainActivity.class);
                next.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(next);
                end.this.finish();
                return false;
            }
        });
        Intent intent2 = getIntent();

        d.setText(String.valueOf(monitor_menu.AMVMean));
        noofv.setText(String.valueOf(monitor_menu.Distance)+" m");
        final GraphView graph = findViewById(R.id.graph);
        final GraphView graphMean = findViewById(R.id.graphmean);
        final GraphView graphSpeed = findViewById(R.id.graphmean3);

        //Drawing all the three graphs...
        series = new LineGraphSeries<>();
        seriesmean = new LineGraphSeries<>();
        seriesspeed = new LineGraphSeries<>();
        double y, x;
        x = 0.0;
        for (int i = 0; i < monitor_menu.tempnum; i++) {
            x = x + 1;
            y = monitor_menu.temp[i];
            series.appendData(new DataPoint(x, y), true, monitor_menu.tempnum);
        }
        graph.addSeries(series);
        double ymean, xmean;
        xmean=0.0;
        for (int i = 0; i < monitor_menu.tempnum; i++) {
            xmean = xmean + 1;
            ymean = monitor_menu.tempmean[i];
            seriesmean.appendData(new DataPoint(xmean, ymean), true, monitor_menu.tempnum);
        }
        graphMean.addSeries(seriesmean);
        double ymean2, xmean2;
        xmean2=0.0;
        for (int i = 0; i < monitor_menu.speednum; i++) {
            xmean2 = xmean2 + 1;
            ymean2 = monitor_menu.speeds[i];
            seriesspeed.appendData(new DataPoint(xmean2, ymean2), true, monitor_menu.speednum);
        }
        graphSpeed.addSeries(seriesspeed);
    }
}
