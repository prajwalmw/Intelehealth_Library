package com.circle.ayu;

import android.content.Context;
import android.content.Intent;

import com.circle.ayu.activities.vitals.VitalsActivity;


public class Ayu {

    public static void launch(Context context) {
        Intent intent = new Intent(context, VitalsActivity.class);
        context.startActivity(intent);
    }
}
