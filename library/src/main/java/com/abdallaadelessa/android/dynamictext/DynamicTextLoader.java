package com.abdallaadelessa.android.dynamictext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Abdalla on 28/11/2016.
 */
public class DynamicTextLoader {
    private String key;
    private Loader loader;
    private Map<String, Listener> dynamicTextListenersMap;
    private Subscription subscription;

    public DynamicTextLoader(String key, Loader loader) {
        this.key = key;
        this.loader = loader;
        this.dynamicTextListenersMap = Collections.synchronizedMap(new HashMap<String, Listener>());
    }

    public boolean hasListener(String tag) {
        return dynamicTextListenersMap.containsKey(tag);
    }

    public void addDynamicTextListener(String tag, Listener listener) {
        if(tag != null && listener != null) this.dynamicTextListenersMap.put(tag, listener);
    }

    public void removeDynamicTextListener(String tag) {
        if(tag != null) this.dynamicTextListenersMap.remove(tag);
    }

    public void clearDynamicTextListeners() {
        this.dynamicTextListenersMap.clear();
    }

    public String getString() {
        String text = null;
        try {
            text = loader.loadTextByKey(key).toBlocking().firstOrDefault(null);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public void getStringAsync() {
        unSubscribe();
        subscription = getStringAsObservable().subscribe(new Action1<String>() {
            @Override
            public void call(String text) {
                if(dynamicTextListenersMap != null && !dynamicTextListenersMap.isEmpty()) {
                    for(String tag : dynamicTextListenersMap.keySet()) {
                        dynamicTextListenersMap.get(tag).onTextLoaded(text);
                    }
                    clearDynamicTextListeners();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if(dynamicTextListenersMap != null && !dynamicTextListenersMap.isEmpty()) {
                    for(String tag : dynamicTextListenersMap.keySet()) {
                        dynamicTextListenersMap.get(tag).onTextFailure(throwable);
                    }
                    clearDynamicTextListeners();
                }
            }
        });
    }

    public Observable<String> getStringAsObservable() {
        Observable<String> observable = loader.loadTextByKey(key);
        observable = observable.observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public boolean stop(String tag) {
        boolean isStopped = true;
        if(tag == null || dynamicTextListenersMap.size() <= 2) {
            clearDynamicTextListeners();
            unSubscribe();
        }
        else {
            removeDynamicTextListener(tag);
            isStopped = false;
        }
        return isStopped;
    }

    private void unSubscribe() {
        if(subscription != null) {
            subscription.unsubscribe();
        }
    }

    // ------------------>

    public interface Loader {
        Observable<String> loadTextByKey(String key);
    }

    public static abstract class Listener {
        public void onTextLoaded(String text) {
        }

        public void onTextFailure(Throwable throwable) {
        }
    }
}
