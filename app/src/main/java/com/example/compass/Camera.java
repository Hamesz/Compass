package com.example.compass;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * This class deals with using the camera
 * @author James Hanratty
 */
public class Camera {

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    static final String STORAGE_DIRECTORY= "/storage/sdcard0/EMFdetectingApp/";
    static final String PUBLIC_DIRECTORY= "/EMFdetectingApp/";

    /**
     * Takes a picture and creates the save location for the media file
     * @param cameraIntent  The camera intent
     * @param compass_value The compass value
     */
    public static Uri  takePicture(Intent cameraIntent, int compass_value){
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
}
