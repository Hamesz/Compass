package com.example.compass;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * This class deals with using the camera
 * @author James Hanratty
 */
public class Camera {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    public static final int PERMISSION_REQUEST_CAMERA_CODE = 100;
    public static final String STORAGE_DIRECTORY= "/storage/sdcard0/EMFdetectingApp/";
    public static final String PUBLIC_DIRECTORY= "/EMFdetectingApp/";

    /**
     * Takes a picture and creates the save location for the media file
     * @param cameraIntent  The camera intent
     * @param compass_value The compass value
     */
    public static Uri takePicture(Intent cameraIntent, int compass_value){
        Log.d("Camera","inside takPicture()");
        File mediaStorageDirectory = createMediaStorageDirectory();
        File mediaFile = createMediaFile(MEDIA_TYPE_IMAGE, mediaStorageDirectory, compass_value);
        Uri fileUri = Uri.fromFile(mediaFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        Log.d("Camera",String.format("Saved Picture to: %s", mediaFile.getAbsolutePath()));
        return fileUri;
    }

    /**
     * Create the mediaFile
     * @param type                      The type of file
     * @param mediaStorageDirectory     Directoy where the image should be stored
     * @param compass_value             The current compass value
     * @return                          The media file
     * @see File
     */
    private static File createMediaFile(int type, File mediaStorageDirectory, int compass_value){
        File mediaFile = null;
        String timestamp  = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = String.format("%d_%s.jpg", compass_value, timestamp);
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDirectory.getPath() + File.separator + filename);
        }
        return mediaFile;
    }

    /**
     * Creates the mdeia storage directory,
     * if an SD card is enabled then it is stored in
     * the SD_DIRECTORY else it is stored in the public directory
     * PUBLIC_DIRECTORY
     * @return  Media storgae directory as a File
     * @see File
     */
    private static File createMediaStorageDirectory(){
        final File mediaStorageDirectory;
        // doesnt work when connected to the computer
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            mediaStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PUBLIC_DIRECTORY);
            Log.d("EMF","Media detected!");
        }else{
            mediaStorageDirectory = new File(STORAGE_DIRECTORY);
        }
        // check if saved pictures directoy exists
        if (! mediaStorageDirectory.exists()){
            if (! mediaStorageDirectory.mkdirs()){
                Log.d("EMF",String.format("failed to create directory: %s",STORAGE_DIRECTORY));
                return null;
            }
        }
        return mediaStorageDirectory;
    }

    /**
     * This method will alert the user on the success of the picture
     * being taken and return the approproate text
     * @param requestCode   Array of request codes
     * @param resultCode    Array of results
     * @return text describing the success
     */
    public static String onActivityResult(int requestCode, int resultCode){
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            String text;
            switch (resultCode) {
                case RESULT_OK:
                    text = "Image saved in directory:\n" + PUBLIC_DIRECTORY;
                    break;
                case RESULT_CANCELED:
                    text = "Cancelled";
                    break;
                default:
                    text = "Failed";
                    break;
            }
            return text;
        }
        return null;
    }

    /**
     * checks the camera permissions and asks if they are not granted
     * Requires API 25 or above so that permission granting can happen automatically.
     * @param context   The context of the activity that called this method
     * @param activity  The Activity
     * @return          true if all permissions are granted
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkPermissions(Context context, Activity activity){
        boolean fullAccess = true;
        if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA_CODE);
            fullAccess = false;
        }
        return fullAccess;
    }

    /**
     * This method determines if the appropriate permissions were granted to use the camera
     * and will return all true if they were
     * @param requestCode   the request code
     * @param grantResults  the permissions granted or not granted
     * @result permissionsGranted a boolean array where
     * 1st is if permissions were granted and 2nd index is if request code was correct
     */
    public static boolean[] onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults){
        // first is if permissions were granted, 2nd is if requestcode was correct
        boolean[] permissionsGranted = {false, false};
        if (requestCode == PERMISSION_REQUEST_CAMERA_CODE) {
            permissionsGranted[1] = true;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted[0] = true;
            } else {
                permissionsGranted[0] = false;
            }
        }
        return permissionsGranted;
    }
}
