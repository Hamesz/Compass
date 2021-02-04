package com.example.compass;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PermissionsUnitTest {
    @Test
    public void wrong_code() {
        boolean expected_result = false;
        // actual result
        int request_code = -1;
        int[] grantResults = null;
        boolean[] actual_results = Camera.onRequestPermissionsResult(request_code, grantResults);
        assertEquals(expected_result, actual_results[0]);
        assertEquals(expected_result, actual_results[1]);
    }

    @Test
    public void correct_code_no_permission() {
        boolean expected_permission_result = false;
        boolean expected_code_result = true;
        // actual result
        int request_code = Camera.PERMISSION_REQUEST_CAMERA_CODE;
        int permission_granted = 0;
        int permission_not_granted = -1;
        int[] grantResults = {permission_granted, permission_not_granted, permission_granted};
        boolean[] actual_results = Camera.onRequestPermissionsResult(request_code, grantResults);
        assertEquals(expected_permission_result, actual_results[0]);
        assertEquals(expected_code_result, actual_results[1]);
    }

    @Test
    public void correct_code_yes_permission() {
        boolean expected_permission_result = true;
        boolean expected_code_result = true;
        // actual result
        int request_code = Camera.PERMISSION_REQUEST_CAMERA_CODE;
        int permission_granted = 0;
        int[] grantResults = {permission_granted, permission_granted, permission_granted};
        boolean[] actual_results = Camera.onRequestPermissionsResult(request_code, grantResults);
        assertEquals(expected_permission_result, actual_results[0]);
        assertEquals(expected_code_result, actual_results[1]);
    }
}