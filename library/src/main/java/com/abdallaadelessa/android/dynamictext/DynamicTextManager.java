package com.abdallaadelessa.android.dynamictext;

import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by abdullah on 11/30/16.
 */

public class DynamicTextManager {
    public static final String TAG_DYNAMIC_TEXT_MANAGER = "DynamicTextManager";
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
            public Observable<String> loadTextByKey(final String key) {
                Observable<String> stringObservable = Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext(key);
                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.newThread());
                // stringObservable = stringObservable.delay(3, TimeUnit.SECONDS);
                return stringObservable;
            }
        };
    }

    // ----------------------->

    private void getFromMemoryCache(String key, DynamicTextLoader.Listener listener) {
        String text = dynamicTextCache.get(key);
        if(listener != null) {
            listener.onTextLoaded(text);
        }
    }

    private void getFromLoader(String key, DynamicTextLoader.Loader loader, String tag, DynamicTextLoader.Listener listener) {
        if(dynamicTextLoaderMap.containsKey(key)) {
            DynamicTextLoader dynamicTextLoader = dynamicTextLoaderMap.get(key);
            dynamicTextLoader.addDynamicTextListener(tag, listener);
        }
        else {
            DynamicTextLoader dynamicTextLoader = createNewDynamicTextLoader(key, loader);
            dynamicTextLoader.addDynamicTextListener(TAG_DYNAMIC_TEXT_MANAGER, getManagerListener(key));
            dynamicTextLoader.addDynamicTextListener(tag, listener);
            dynamicTextLoaderMap.put(key, dynamicTextLoader);
            dynamicTextLoader.getStringAsync();
        }
    }

    // ----------------------->

    public final void getStringAsync(final String key, final DynamicTextLoader.Loader loader, final String tag, final DynamicTextLoader.Listener listener) {
        if(dynamicTextCache.containsKey(key) && useMemoryCache) {
            // Get From Memory Cache
            getFromMemoryCache(key, listener);
        }
        else {
            // Get From Disk
            getFromLoader(key, loader, tag, listener);
        }
    }

    public void getStringAsync(final String key, String tag, final DynamicTextLoader.Listener listener) {
        getStringAsync(key, null, tag, listener);
    }

    public void getStringAsync(final String key, final TextView textView) {
        if(textView == null) return;
        final WeakReference<TextView> textViewWeakReference = new WeakReference<>(textView);
        getStringAsync(key, null, String.valueOf(textViewWeakReference.get().getId()), new DynamicTextLoader.Listener() {
            @Override
            public void onTextLoaded(String text) {
                super.onTextLoaded(text);
                if(textViewWeakReference.get() != null) {
                    textViewWeakReference.get().setText(text);
                }
            }
        });
    }

    //--->

    public String getString(String key, DynamicTextLoader.Loader loader) {
        return createNewDynamicTextLoader(key, loader).getString();
    }

    public String getString(String key) {
        return new DynamicTextLoader(key, defaultLoader).getString();
    }

    //--->

    public Observable<String> getStringAsObservable(String key, DynamicTextLoader.Loader loader) {
        return createNewDynamicTextLoader(key, loader).getStringAsObservable();
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

    // ----------------------->

    private DynamicTextLoader createNewDynamicTextLoader(String key, DynamicTextLoader.Loader loader) {
        return new DynamicTextLoader(key, loader == null ? defaultLoader : loader);
    }

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

}
