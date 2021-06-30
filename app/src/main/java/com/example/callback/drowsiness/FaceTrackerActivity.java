package com.example.callback.drowsiness;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.example.callback.drowsiness.ui.camera.CameraSourcePreview;
import com.example.callback.drowsiness.ui.camera.GraphicOverlay;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static com.example.callback.drowsiness.monitor_menu.accidentoccured;

public final class FaceTrackerActivity extends AppCompatActivity {
    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    private Button end_button;
    private TextView tv_1;
    static int count = 0, count1 = 0;
    private MediaPlayer mp;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private String start_2;
    private String key = "facetrackeractivity";
    private String key_2 = "callbackcat's project";
    private String key_3 = "hello";
    private String key_4 = "senstivity";
    private int s_status, s_time;
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    public int flag = 0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setIcon(R.drawable.portrait)
                .setTitle("Information")
                .setMessage("Please use this feature in Portrait Mode")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(FaceTrackerActivity.this,"Please use this feature in Portrait Mode", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);
        end_button = findViewById(R.id.button);
        tv_1 = findViewById(R.id.textView4);
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int c = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (c == 0) {
            Toast.makeText(getApplicationContext(), "Volume is MUTE", Toast.LENGTH_LONG).show();
        }
        Intent intent_2 = getIntent();
        final String start = intent_2.getStringExtra(key_2);
        start_2 = start;
        String time_info = intent_2.getStringExtra(key_4);
        s_status = Integer.parseInt(time_info);

        //setting the time sensitivity for drowsiness detection
        if (s_status == 1) {
            s_time = 1000;
        } else if (s_status == 2) {
            s_time = 2000;
        } else if (s_status == 3) {
            s_time = 3000;
        } else if (s_status == 4) {
            s_time = 4000;
        }

        View decorview = getWindow().getDecorView(); //hide navigation bar
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorview.setSystemUiVisibility(uiOptions);

        end_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent next = new Intent(FaceTrackerActivity.this, end.class);
                count = 0;
                count1 = 0;
                next.putExtra(key_3, start);
                next.putExtra(key, tv_1.getText());
                next.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(next);
                FaceTrackerActivity.this.finish();
                return false;
            }
        });


        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
        final Activity thisActivity = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());
        if (!detector.isOperational()) {
            Toast.makeText(getApplicationContext(), "Dependencies are not yet available. ", Toast.LENGTH_LONG).show();
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        //setting the camera source
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(60.0f)
                .build();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
        stop_playing();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ALERT")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    private void startCameraSource() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    public static int incrementer() {
        count++;
        return (count);
    }

    public static int incrementer_1() {
        count1++;
        return (count1);
    }

    public static int get_incrementer() {
        return (count);
    }

    public void play_media() {
        stop_playing();
        mp = MediaPlayer.create(this, R.raw.alarm);
        mp.start();
    }

    public void stop_playing() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public void alert_box() {
        play_media();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                play_media();
                AlertDialog dig;
                dig = new AlertDialog.Builder(FaceTrackerActivity.this)
                        .setTitle("Drowsyness Alert")
                        .setMessage("You are Drowsy, you should not be driving - Please stop and take REST")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                stop_playing();
                                flag = 0;
                            }
                        }).setIcon(R.drawable.ic_dialog_alert)
                        .show();
                dig.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        stop_playing();
                        flag = 0;
                    }
                });
            }
        });
    }


    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;
        int state_i, state_f = -1;
        long start, end = System.currentTimeMillis();
        long begin, stop;
        int c;
        private Map<Integer, PointF> mPreviousProportions = new HashMap<>();
        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            if (accidentoccured == true) {
                onBackPressed();
            }
            mOverlay.add(mFaceGraphic);
            updatePreviousProportions(face);
            mFaceGraphic.updateFace(face);
            PointF leftPosition = getLandmarkPosition(face, Landmark.LEFT_EYE);
            PointF rightPosition = getLandmarkPosition(face, Landmark.RIGHT_EYE);
            float leftOpenScore = face.getIsLeftEyeOpenProbability();
            boolean isLeftOpen;
            float rightOpenScore = face.getIsRightEyeOpenProbability();
            boolean isRightOpen;
            if (leftOpenScore > 0.50) {
                isLeftOpen = true;
            } else {
                isLeftOpen = false;
            }
            if (rightOpenScore > 0.50) {
                isRightOpen = true;
            } else {
                isRightOpen = false;
            }
            if (flag == 0) {
                if (leftOpenScore < 0.50 && rightOpenScore < 0.50) {
                    state_i = 0;
                } else {
                    state_i = 1;
                }
                if (state_i != state_f) {
                    start = System.currentTimeMillis();
                    if (state_f == 0) {
                        c = incrementer_1();

                    }
                    end = start;
                    stop = System.currentTimeMillis();
                } else if (state_i == 0 && state_f == 0) {
                    begin = System.currentTimeMillis();
                    if (begin - stop > s_time) {
                        c = incrementer();
                        alert_box();
                        flag = 1;
                    }
                    begin = stop;
                }
                state_f = state_i;
                status();
            }
            mFaceGraphic.updateEyes(leftPosition, isLeftOpen, rightPosition, isRightOpen);
        }

        private void updatePreviousProportions(Face face) {
            for (Landmark landmark : face.getLandmarks()) {
                PointF position = landmark.getPosition();
                float xProp = (position.x - face.getPosition().x) / face.getWidth();
                float yProp = (position.y - face.getPosition().y) / face.getHeight();
                mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
            }
        }

        private PointF getLandmarkPosition(Face face, int landmarkId) {
            for (Landmark landmark : face.getLandmarks()) {
                if (landmark.getType() == landmarkId) {
                    return landmark.getPosition();
                }
            }
            PointF prop = mPreviousProportions.get(landmarkId);
            if (prop == null) {
                return null;
            }
            float x = face.getPosition().x + (prop.x * face.getWidth());
            float y = face.getPosition().y + (prop.y * face.getHeight());
            return new PointF(x, y);
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
            setText(tv_1, "Missing");
        }

        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }

        private void setText(final TextView text, final String value) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text.setText(value);
                }
            });
        }

        public void status() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int s = get_incrementer();
                    if (s < 5) {
                        setText(tv_1, "Active");
                        tv_1.setTextColor(Color.GREEN);
                        tv_1.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                    if (s > 4) {
                        setText(tv_1, "Sleepy");
                        tv_1.setTextColor(Color.YELLOW);
                        tv_1.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                    if (s > 8) {
                        setText(tv_1, "Drowsy");
                        tv_1.setTextColor(Color.RED);
                        tv_1.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (accidentoccured == true) {
            super.onBackPressed();
        } else {
            Intent next = new Intent(FaceTrackerActivity.this, end.class);
            count = 0;
            count1 = 0;
            next.putExtra(key_3, start_2);
            next.putExtra(key, tv_1.getText());
            next.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(next);
            FaceTrackerActivity.this.finish();
        }
    }


}
