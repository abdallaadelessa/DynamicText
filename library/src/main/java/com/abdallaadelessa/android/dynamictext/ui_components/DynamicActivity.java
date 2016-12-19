package com.abdallaadelessa.android.dynamictext.ui_components;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.abdallaadelessa.android.dynamictext.DynamicContextWrapper;
import com.abdallaadelessa.android.dynamictext.DynamicResources;
import com.abdallaadelessa.android.dynamictext.DynamicTextLoader;
import com.abdallaadelessa.android.dynamictext.DynamicTextManager;

import rx.Observable;

/**
 * Created by abdullah on 12/8/16.
 */

public class DynamicActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(DynamicContextWrapper.wrap(newBase));
    }

    @Override
    public DynamicContextWrapper getBaseContext() {
        return (DynamicContextWrapper) super.getBaseContext();
    }

    @Override
    public DynamicResources getResources() {
        return (DynamicResources) super.getResources();
    }

    // ----------------------------->

    @NonNull
    public String getString(String key, final DynamicTextLoader.Parser parser) throws Resources.NotFoundException {
        return DynamicTextManager.getInstance().getString(key, parser);
    }

    @NonNull
    public String getString(String key) throws Resources.NotFoundException {
        return getString(key, null);
    }

    @NonNull
    public Observable<String> getStringAsObservable(final String key, final DynamicTextLoader.Parser parser) throws Resources.NotFoundException {
        return DynamicTextManager.getInstance().getStringAsObservable(key, parser);
    }

    @NonNull
    public Observable<String> getStringAsObservable(String key) throws Resources.NotFoundException {
        return getStringAsObservable(key, null);
    }
}
