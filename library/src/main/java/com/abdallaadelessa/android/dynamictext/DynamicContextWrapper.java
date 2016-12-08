package com.abdallaadelessa.android.dynamictext;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;

/**
 * Created by abdullah on 12/8/16.
 */

public class DynamicContextWrapper extends ContextWrapper {
    private DynamicResources resources;

    public static ContextWrapper wrap(Context base) {
        return new DynamicContextWrapper(base);
    }

    DynamicContextWrapper(Context base) {
        super(base);
    }

    @Override
    public DynamicResources getResources() {
        if(resources == null) {
            resources = new DynamicResources(super.getResources().getAssets(), super.getResources().getDisplayMetrics(), super.getResources().getConfiguration());
        }
        return resources;
    }
}
