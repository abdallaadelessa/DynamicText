package com.abdallaadelessa.android.dynamictext;

import android.util.Log;

/**
 * Created by abdullah on 12/8/16.
 */

public class DynamicErrorHandler {
    private static final String TAG = "TAG";

    public static void onError(Throwable throwable) {
        Log.e(TAG, "Error", throwable);
    }
}
