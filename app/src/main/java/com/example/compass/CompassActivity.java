package com.example.compass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import static com.example.compass.Camera.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;

/**
 * This activity presents the compass
 * @author James Hanratty
 */
public class CompassActivity extends AppCompatActivity implements Orientation.Listener{

    private Orientation mOrientation;
    private com.example.compass.Animation compassAnimation;

    // compass value in 360 degrees
    private int compassValue;

    // request code when asking permissions for the camera
    private static final int PERMISSION_REQUEST_CAMERA_CODE = 100;

    /**
     * initialises everything this activity needs to do:
     * such as the camera, compass, animation and toolbar
     * @param savedInstanceState    The saved state instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create the orientation java object
        mOrientation = new Orientation(this);
        initialiseAnimation();
        initialiseCamera();
        initialiseToolbar();
    }


    // -- Toolbar Methods --
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
     * @param item   What toolbar icon
     * @return      true
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
     * Does goes to the BatteryActivity
     */
    private void powerClicked(){
        Intent i = new Intent(getApplicationContext(),BatteryActivity.class);
        startActivity(i);
    }
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
     * checks the camera permissions and asks if they are not granted
     * @return
     */
    private boolean checkCameraPermissions(){
        boolean fullAccess = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fullAccess = Camera.checkPermissions(this, this);
        }
        Log.d("Camera",String.format("Permission Granted: %b",fullAccess));
        return fullAccess;
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
            Intent scanFileIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri);
            sendBroadcast(scanFileIntent);
            try {
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                Log.d("Camera", "Cant start activity to use camera");
                Toast.makeText(getApplicationContext(),"Camera failed",Toast.LENGTH_LONG).show();
            }
        }
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
        boolean[] granted = Camera.onRequestPermissionsResult(requestCode, grantResults);
        if (granted[1]) {
            if (granted[0]) {
                cameraClicked();
            } else {
                Toast.makeText(getApplicationContext(), "Camera permissions not granted, can't take a picture", Toast.LENGTH_SHORT).show();
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
            super.onActivityResult(requestCode, resultCode, data);
            String text = Camera.onActivityResult(requestCode, resultCode);
            if (text != null){
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        }
    // ------------------


    // -- Compass Methods --
    /**
     * Does nothiing as we are already in this activity
     */
    private void compassClicked(){
    }

    /**
     * Sets the compassValue to the current orientation
     * along with executing the compass animation
     * @param degree    the current orientation degree between [-180,180]
     */
    @Override
    public void onOrientationChanged(float degree) {
        compassAnimation.animateCompass(degree);
        compassValue = Orientation.convertTo360Degrees(degree);
    }
    // --------------------


    // -- Animation Methods --
    /**
     * Initialises the animation
     */
    private void initialiseAnimation() {
        TextView direction_TV = findViewById(R.id.directiontextView);
        ImageView compass_IV = findViewById(R.id.compassimageview);
        compassAnimation = new com.example.compass.Animation(compass_IV, direction_TV);
    }
    // ----------------------


    // -- Activity Methods --
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
    // --------------------

}
