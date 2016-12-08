package com.abdallaadelessa.android.dynamictext.ui_components;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.abdallaadelessa.android.dynamictext.DynamicContextWrapper;
import com.abdallaadelessa.android.dynamictext.DynamicResources;

/**
 * Created by abdullah on 12/8/16.
 */

public class DynamicApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(DynamicContextWrapper.wrap(base));
    }

    @Override
    public DynamicContextWrapper getBaseContext() {
        return (DynamicContextWrapper) super.getBaseContext();
    }

    @Override
    public DynamicContextWrapper getApplicationContext() {
        return (DynamicContextWrapper) super.getApplicationContext();
    }

    @Override
    public DynamicResources getResources() {
        return (DynamicResources) super.getResources();
    }

}
