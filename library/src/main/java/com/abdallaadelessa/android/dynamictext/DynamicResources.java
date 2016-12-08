package com.abdallaadelessa.android.dynamictext;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import rx.Observable;

/**
 * Created by abdullah on 12/8/16.
 */

public class DynamicResources extends Resources {

    /**
     * Create a new Resources object on top of an existing set of assets in an
     * AssetManager.
     *
     * @param assets  Previously created AssetManager.
     * @param metrics Current display metrics to consider when
     *                selecting/computing resource values.
     * @param config  Desired device configuration to consider when
     */
    public DynamicResources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
        super(assets, metrics, config);
    }

    @NonNull
    @Override
    public String getString(int id) throws NotFoundException {
        return super.getString(id);
    }

    @NonNull
    public String getString(String key, final DynamicTextLoader.Parser parser) throws NotFoundException {
        return DynamicTextManager.getInstance().getString(key, parser);
    }

    @NonNull
    public String getString(String key) throws NotFoundException {
        return getString(key, null);
    }

    @NonNull
    public Observable<String> getStringAsObservable(final String key, final DynamicTextLoader.Parser parser) throws NotFoundException {
        return DynamicTextManager.getInstance().getStringAsObservable(key, parser);
    }

    @NonNull
    public Observable<String> getStringAsObservable(String key) throws NotFoundException {
        return getStringAsObservable(key, null);
    }
}
