package com.example.compass;

import org.junit.Test;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.compass.Camera.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CameraResultUnitTest {
    @Test
    public void wrong_code() {
        String expected_result = null;
        // actual result
        int requestCode = -1;
        int resultCode = -1;
        String actual_result = Camera.onActivityResult(requestCode, resultCode);
        assertEquals(expected_result, actual_result);
    }

    @Test
    public void correct_code_failed_result() {
        String expected_result = "Failed";
        // actual result
        int requestCode = CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
        int resultCode = 5;
        String actual_result = Camera.onActivityResult(requestCode, resultCode);
        assertEquals(expected_result, actual_result);
    }

    @Test
    public void correct_code_success_result() {
        String expected_result = "Image saved in directory:\n" + Camera.PUBLIC_DIRECTORY;
        // actual result
        int requestCode = CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
        int resultCode = RESULT_OK;
        String actual_result = Camera.onActivityResult(requestCode, resultCode);
        assertEquals(expected_result, actual_result);
    }

    @Test
    public void correct_code_cancelled_result() {
        String expected_result = "Cancelled";
        // actual result
        int requestCode = CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
        int resultCode = RESULT_CANCELED;
        String actual_result = Camera.onActivityResult(requestCode, resultCode);
        assertEquals(expected_result, actual_result);
    }
}