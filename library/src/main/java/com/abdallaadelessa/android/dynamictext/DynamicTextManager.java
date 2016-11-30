package com.abdallaadelessa.android.dynamictext;

import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by abdullah on 11/30/16.
 */

public class DynamicTextManager {
    private static DynamicTextManager dynamicTextManager;
    private boolean useMemoryCache = false;
    private Map<String, DynamicTextLoader> dynamicTextLoaderMap;
    private DynamicTextCache dynamicTextCache;
    private DynamicTextLoader.Loader defaultLoader;

    public static synchronized DynamicTextManager getInstance() {
        if(dynamicTextManager == null) {
            dynamicTextManager = new DynamicTextManager();
        }
        return dynamicTextManager;
    }

    private DynamicTextManager() {
        dynamicTextCache = new DynamicTextCache();
        dynamicTextLoaderMap = Collections.synchronizedMap(new HashMap<String, DynamicTextLoader>());
        defaultLoader = new DynamicTextLoader.Loader() {
            @Override
            public Observable<String> loadTextByKey(String key) {
                return Observable.just(key).delay(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
            }
        };
    }

    // ----------------------->

    private DynamicTextLoader.Listener getManagerListener(final String key) {
        return new DynamicTextLoader.Listener() {
            @Override
            public void onTextLoaded(String text) {
                dynamicTextLoaderMap.remove(key);
                dynamicTextCache.add(key, text);
            }

            @Override
            public void onTextFailure(Throwable throwable) {
                dynamicTextLoaderMap.remove(key);
            }
        };
    }

    private void getFromMemoryCache(String key, TextView textView, DynamicTextLoader.Listener listener) {
        String text = dynamicTextCache.get(key);
        if(textView != null) {
            textView.setText(text);
        }
        if(listener != null) {
            listener.onTextLoaded(text);
        }
    }

    private void getFromDisk(String key, DynamicTextLoader.Loader loader, final TextView textView, String tag, DynamicTextLoader.Listener listener) {
        if(dynamicTextLoaderMap.containsKey(key)) {
            DynamicTextLoader dynamicTextLoader = dynamicTextLoaderMap.get(key);
            if(textView != null) {
                dynamicTextLoader.addDynamicTextListener(String.valueOf(textView.getId()), new DynamicTextLoader.Listener() {
                    @Override
                    public void onTextLoaded(String text) {
                        textView.setText(text);
                    }
                });
            }
            dynamicTextLoader.addDynamicTextListener(tag, listener);
        }
        else {
            DynamicTextLoader dynamicTextLoader = new DynamicTextLoader(key, loader == null ? defaultLoader : loader);
            dynamicTextLoader.addDynamicTextListener(String.valueOf(System.currentTimeMillis()), getManagerListener(key));
            if(textView != null) {
                dynamicTextLoader.addDynamicTextListener(String.valueOf(textView.getId()), new DynamicTextLoader.Listener() {
                    @Override
                    public void onTextLoaded(String text) {
                        textView.setText(text);
                    }
                });
            }
            dynamicTextLoader.addDynamicTextListener(tag, listener);
            dynamicTextLoaderMap.put(key, dynamicTextLoader);
            dynamicTextLoader.getStringAsync();
        }
    }

    // ----------------------->

    public void getStringAsync(final String key, final DynamicTextLoader.Loader loader, final TextView textView, final String tag, final DynamicTextLoader.Listener listener) {
        if(dynamicTextCache.containsKey(key) && useMemoryCache) {
            // Get From Memory Cache
            getFromMemoryCache(key, textView, listener);
        }
        else {
            // Get From Disk
            getFromDisk(key, loader, textView, tag, listener);
        }
    }

    public void getStringAsync(final String key, final TextView textView) {
        getStringAsync(key, null, textView, null, null);
    }

    public void getStringAsync(final String key, String tag, final DynamicTextLoader.Listener listener) {
        getStringAsync(key, null, null, tag, listener);
    }

    //--->

    public String getString(String key, DynamicTextLoader.Loader loader) {
        return new DynamicTextLoader(key, loader == null ? defaultLoader : loader).getString();
    }

    public String getString(String key) {
        return new DynamicTextLoader(key, defaultLoader).getString();
    }

    // ----------------------->

    public void setUseMemoryCache(boolean useMemoryCache) {
        this.useMemoryCache = useMemoryCache;
    }

    // ----------------------->

    public boolean isRunning(final String key, final String tag) {
        return dynamicTextLoaderMap.containsKey(key) && dynamicTextLoaderMap.get(key).hasListener(tag);
    }

    public boolean isRunning(final String key) {
        return dynamicTextLoaderMap.containsKey(key);
    }

    // ----------------------->

    public void stop(String key, String tag) {
        if(key == null) return;
        DynamicTextLoader dynamicTextLoader = dynamicTextLoaderMap.get(key);
        if(dynamicTextLoader != null && dynamicTextLoader.stop(tag)) {
            dynamicTextLoaderMap.remove(key);
        }
    }

    public void stopAll() {
        if(dynamicTextLoaderMap != null && !dynamicTextLoaderMap.isEmpty()) {
            for(String key : dynamicTextLoaderMap.keySet()) {
                stop(key, null);
            }
        }

    }
}
