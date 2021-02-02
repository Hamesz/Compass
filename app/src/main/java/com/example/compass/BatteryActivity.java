package com.example.compass;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import static com.example.compass.Camera.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;

/**
 * This activity presents the battery information
 * @author James Hanratty
 */
public class BatteryActivity extends AppCompatActivity implements Orientation.Listener{

    private Battery battery;
    Orientation mOrientation;
    private static final int PERMISSION_REQUEST_CAMERA_CODE = 100;
    private int compassValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_sctivity);
        mOrientation = new Orientation(this);
        initialiseToolbar();
        initialiseCamera();
        initialiseBattery();
    }


    // -- Battery Methods --

    /**
     * Intialises the battery by registering as a listener for
     * any changes to the battery
     */
    private void initialiseBattery() {
        IntentFilter ifilter = new IntentFilter((Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(batteryReciever, ifilter);

        TextView batteryTV = findViewById(R.id.batteryTV);;
        battery = new Battery(batteryTV);
    }

    /**
     * cretaes the battery reciever which will call the Battery.SetBatteryValues when
     * there is new information about the battery
     * @see Battery
     */
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
    // --------------------


    // -- Camera Methods --
    /**
     * Initialises the camera by setting the VM builder to
     * allow android studio to use the camera
     */
    private void initialiseCamera() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    /**
     * This method checks the camera permissions and
     * if their is sufficient permissions then takes a photo
     */
    private void cameraClicked(){
        if (checkCameraPermissions()) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Log.d("Camera", "Taking picture...");
            Uri fileUri = Camera.takePicture(cameraIntent, compassValue);
            try {
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                Log.d("Camera", "Cant start activity to use camera");
                Toast.makeText(getApplicationContext(),"Camera failed",Toast.LENGTH_LONG).show();
            }
        }
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

    /**
     * This method takes a picture with the camera if all
     * permissions needed are granted
     * @param requestCode   the request code
     * @param permissions   the permissions asked
     * @param grantResults  the permissions granted or not granted
     */
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

    /**
     * This method will alert the user on the success of the picture
     * being taken
     * @param requestCode   Array of request codes
     * @param resultCode    Array of results
     * @param data          Intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            String text;
            switch (resultCode) {
                case RESULT_OK:
                    text = "Image saved!";
                    break;
                case RESULT_CANCELED:
                    text = "cancelled";
                    break;
                default:
                    text = "failed";
                    break;
            }
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }
    }
    // ------------------


    // -- Toolbar methods --
    /**
     * Initialises the toolbar by inflating it and displaying it
     */
    private void initialiseToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
//        Drawable d = myToolbar.getContext().getResources().getDrawable(R.drawable.ic_baseline_battery);
//        myToolbar.setOverflowIcon(d);
    }

    /**
     * Inflates the toolbar showing all of the icons
     * @param menu  The xml name for the toolbar
     * @return      true
     */
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
        switch(id){
            case  R.id.camera:
                cameraClicked();
                break;
            case R.id.power:
                powerClicked();
                break;
            case R.id.compass:
                compassClicked();
                break;
        }
        return true;
    }

    /**
     * Method does nothing as we are already in battery activity
     */
    private void powerClicked(){
    }
    // ------------------


    // -- Compass Methods --
    /**
     * Method moves to the compass activity
     */
    private void compassClicked(){
        Intent i = new Intent(getApplicationContext(),CompassActivity.class);
        startActivity(i);
    }

    /**
     * Sets the compassValue to the current orientation
     * along with executing the compass animation
     * @param degree    the current orientation degree between [-180,180]
     */
    @Override
    public void onOrientationChanged(float degree) {
        compassValue = Orientation.convertTo360Degrees(degree);
    }
    // ---------------------


    // -- Default Activity Methods --
    /**
     * un registers the orientation listener
     */
    @Override
    protected void onPause(){
        super.onPause();
        mOrientation.stopListening();
    }

    /**
     * registers itself to the orientation listener
     */
    @Override
    protected void onResume(){
        super.onResume();
        mOrientation.startListening(this);
    }
    // -----------------------------
}