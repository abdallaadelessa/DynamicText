package com.abdallaadelessa.android.dynamictext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abdullah on 11/30/16.
 */

public class DynamicTextCache {
    private Map<String, String> cache;

    public DynamicTextCache() {
        cache = Collections.synchronizedMap(new HashMap<String, String>());
    }

    public String get(String key) {
        return cache.get(key);
    }

    public String add(String key, String value) {
        return cache.put(key, value);
    }

    public String remove(String key) {
        return cache.remove(key);
    }

    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }
}
