package com.abdallaadelessa.android.dynamictext;

import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by abdullah on 11/30/16.
 */

public class DynamicTextManager {
    private static DynamicTextManager dynamicTextManager;
    private boolean useMemoryCache = false;
    private DynamicTextCache dynamicTextCache;
    private DynamicTextLoader.Parser defaultParser;

    public static synchronized DynamicTextManager getInstance() {
        if(dynamicTextManager == null) {
            dynamicTextManager = new DynamicTextManager();
        }
        return dynamicTextManager;
    }

    private DynamicTextManager() {
        dynamicTextCache = new DynamicTextCache();
        defaultParser = new DynamicTextLoader.Parser() {
            @Override
            public Observable<String> getValueByKey(final String key) {
                Observable<String> stringObservable = Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext(key);
                        subscriber.onCompleted();
                    }
                });
                //stringObservable = stringObservable.delay(3, TimeUnit.SECONDS);
                return stringObservable;
            }
        };
    }

    public void setDefaultParser(DynamicTextLoader.Parser defaultParser) {
        this.defaultParser = defaultParser;
    }

    public void setUseMemoryCache(boolean useMemoryCache) {
        this.useMemoryCache = useMemoryCache;
    }

    // ----------------------->

    public Observable<String> getStringAsObservable(final String key, DynamicTextLoader.Parser parser) {
        if(dynamicTextCache.containsKey(key) && useMemoryCache) {
            // Get From Memory Cache
            return Observable.just(dynamicTextCache.get(key));
        }
        else {
            // Get From Disk
            return createNewDynamicTextLoader(key, parser).getStringAsObservable().doOnNext(new Action1<String>() {
                @Override
                public void call(String text) {
                    dynamicTextCache.add(key, text);
                }
            });
        }
    }

    public Observable<String> getStringAsObservable(String key) {
        return getStringAsObservable(key, null);
    }

    public String getString(String key, DynamicTextLoader.Parser parser) {
        return getStringAsObservable(key, parser).toBlocking().firstOrDefault(null);
    }

    public String getString(String key) {
        return getString(key, null);
    }

    // -----------------------> Helpers
    
    private DynamicTextLoader createNewDynamicTextLoader(String key, DynamicTextLoader.Parser parser) {
        return new DynamicTextLoader(key, parser == null ? defaultParser : parser);
    }

}
