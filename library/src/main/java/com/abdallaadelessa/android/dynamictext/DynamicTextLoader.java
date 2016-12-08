package com.abdallaadelessa.android.dynamictext;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Abdalla on 28/11/2016.
 */
public class DynamicTextLoader {
    private String key;
    private Parser parser;

    public DynamicTextLoader(String key, Parser parser) {
        this.key = key;
        this.parser = parser;
    }

    public Observable<String> getStringAsObservable() {
        return parser.getValueByKey(key);
    }

    // ------------------>

    public interface Parser {
        Observable<String> getValueByKey(String key);
    }
}
