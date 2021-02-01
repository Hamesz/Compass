package com.example.compass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.TextView;

public class Battery {

    TextView    batteryLevelTV,
                batteryVoltageTV,
                batteryTempTV;

    public Battery(TextView batteryLevelTV, TextView batteryVoltageTV, TextView batteryTempTV){
        this.batteryLevelTV = batteryLevelTV;
        this.batteryVoltageTV = batteryVoltageTV;
        this.batteryTempTV = batteryTempTV;
    }

    /**
     * Sets the textviews which showcase the battery
     * @param intent: Battery action intent
     */
    public void SetBatteryValues(Intent intent){
        int BatteryL = intent.getIntExtra("level",0);
        int BatteryV = intent.getIntExtra("voltage",0);
        int BatteryT = intent.getIntExtra("temperature",0);

        float voltage = BatteryV/1000;
        // change text views
        batteryLevelTV.setText(BatteryL + "%");
        batteryVoltageTV.setText(String.format("%.1fv", voltage));
        batteryTempTV.setText(String.format("%.0fº",BatteryT*0.1));
    }

}
