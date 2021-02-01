package com.example.compass;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.example.compass.Camera.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;

public class CompassActivity extends AppCompatActivity implements Orientation.Listener{

    private Orientation mOrientation;
    private com.example.compass.Animation compassAnimation;
    private Camera camera;
    private Battery battery;

    private int compassValue;
    private static final int PERMISSION_REQUEST_CAMERA_CODE = 100;
    private static final int PERMISSION_REQUEST_WRITE_STORAGE_CODE = 1;
    private static final int PERMISSION_REQUEST_READ_STORAGE_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create the orientation java object
        mOrientation = new Orientation(this);
        initialiseAnimation();
        initialiseCamera();
        initialiseToolbar();
        // having the battery on this thread means saving the picture
        // takes too long
        initialiseBattery();

    }

    private void initialiseBattery() {
        IntentFilter ifilter = new IntentFilter((Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(batteryReciever, ifilter);

        TextView batteryChargeTV = findViewById(R.id.chargeTV);
        TextView batteryVoltageTV = findViewById(R.id.voltageTV);
        TextView batteryTempTV = findViewById(R.id.tempTV);
        battery = new Battery(batteryChargeTV, batteryVoltageTV, batteryTempTV);
    }

    private BroadcastReceiver batteryReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                Log.i("Battery","notification");
                battery.SetBatteryValues(intent);
            }
        }
    };

    private void initialiseToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Drawable d = myToolbar.getContext().getResources().getDrawable(R.drawable.ic_baseline_battery);
        myToolbar.setOverflowIcon(d);
    }

    private void initialiseCamera() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    /**
     * checks the camera permissions and asks if they are not granted
     * @return
     */
    private boolean checkCameraPermissions(){
        boolean fullAccess = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fullAccess = true;
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA_CODE);
                fullAccess = false;
            }
            Log.d("Camera",String.format("Permissions:\ncamera: %b, write: %b, read: %b, full access:%b",(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED),
                    (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED),
                    (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED),
                    fullAccess));
        }
        Log.d("Camera",String.format("Permission Granted: %b",fullAccess));
        return fullAccess;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    /**
     * is called when the toolbar icons are clicked
     * @param item: What toolbar icon
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        String text = "";
        if (id == R.id.camera) {
            cameraClicked();
        }
        return true;
    }

    /**
     * This method checks the camera permissions and
     * if their is sufficient permissions then takes a photo
     */
    private void cameraClicked(){
        if (checkCameraPermissions()) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Log.d("Camera", "Taking picture...");
            Camera.takePicture(cameraIntent, compassValue);
            try {
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                Log.d("Camera", "Cant start activity to use camera");
            }
        }
    }

    /**
     * Initialises the animation
     */
    private void initialiseAnimation() {
        TextView direction_TV = findViewById(R.id.directiontextView);
        ImageView compass_IV = findViewById(R.id.compassimageview);
        compassAnimation = new com.example.compass.Animation(compass_IV, direction_TV);
    }

    @Override
    public void onOrientationChanged(float degree) {
        compassAnimation.animateCompass(degree);
        compassValue = Orientation.convertTo360Degrees(degree);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mOrientation.stopListening();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mOrientation.startListening(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                cameraClicked();
            } else {
                Toast.makeText(getApplicationContext(), "Camera permission denied, please accept to use the camera", Toast.LENGTH_LONG).show();
            }
        }
    }
}
