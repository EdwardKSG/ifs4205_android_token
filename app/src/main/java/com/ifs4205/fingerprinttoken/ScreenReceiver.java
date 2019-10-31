package com.ifs4205.fingerprinttoken;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {
    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Intent userIntent = new Intent(context, TimeoutActivity.class);
            context.startActivity(userIntent);
            wasScreenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Intent userIntent = new Intent(context, TimeoutActivity.class);
            context.startActivity(userIntent);
            wasScreenOn = true;
        }
    }
}
