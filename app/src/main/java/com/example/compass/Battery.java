package com.example.compass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.TextView;

/**
 * This class deals with the battery information
 * @author James Hanratty
 */
public class Battery {

    TextView batteryInfoTextView;

    public Battery(TextView batteryInfoTextView){
        this.batteryInfoTextView = batteryInfoTextView;
    }

    /**
     * Sets the textviews which showcase the battery information
     * @param intent    Battery action intent
     */
    public void SetBatteryValues(Intent intent){
        int BatteryL = intent.getIntExtra("level",0);
        float BatteryV = intent.getIntExtra("voltage",0)/1000;
        float BatteryT = (float) (intent.getIntExtra("temperature",0)*0.1);
        String BatteryTe = intent.getStringExtra("technology");
        String health = getBatteryHealth(intent);
        String status = getBatteryStatus(intent);
        String plugged = getBatteryPlugged(intent);
        // change text views
        batteryInfoTextView.setText("Battery Level: " + BatteryL + "%\n" +
                String.format(
                "Battery Status: %s\n" +
                "Battery Plugged: %s\n" +
                "Battery Health: %s\n" +
                "Battery Voltage: %.1f\n" +
                "Battery Temperature: %.0fºC\n" +
                "Battery Technology: %s\n",status, plugged, health, BatteryV, BatteryT, BatteryTe));

    }

    /**
     * Gets how the battery is plugged in
     * @param intent    The Battery intent
     * @return          String of how the battery is plugged
     */
    private String getBatteryPlugged(Intent intent) {
        switch (intent.getIntExtra("plugged", 0)) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "AC";
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "USB";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "Wireless";
            default:
                return "Unknown";
        }
    }

    /**
     * Gets what the battery health is.
     * @param intent    Battery Intent
     * @return          String of the health of the battery
     */
    private String getBatteryHealth(Intent intent){
        switch(intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN)){
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                return "Unknown";
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Good";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Over Voltage";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "Overheat";
            default:
                return "Unknown";
        }
    }

    /**
     * Provides the status of the battery such as if it is charging etc.
     * @param intent    The battery Intent
     * @return          The status of the battery
     */
    private String getBatteryStatus(Intent intent){
        switch(intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN)){
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "Discharging";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "Not Charging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "Fully Charged";
            default:
                return "Unkown";
        }
    }

}
