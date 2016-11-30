package com.abdallaadelessa.android.dynamictext.ui_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.abdallaadelessa.android.dynamictext.DynamicTextLoader;
import com.abdallaadelessa.android.dynamictext.DynamicTextManager;
import com.abdallaadelessa.android.dynamictext.R;

/**
 * Created by abdullah on 11/29/16.
 */

public class DynamicEditText extends EditText {
    private static final String TAG = "TAG";
    private String textKey;
    private String hintKey;

    public DynamicEditText(Context context) {
        super(context);
    }

    public DynamicEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributeSet(context, attrs, -1);
    }

    public DynamicEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttributeSet(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        DynamicTextManager.getInstance().stop(textKey, String.valueOf(getId()));
        DynamicTextManager.getInstance().stop(hintKey, String.valueOf(getId()));
        super.onDetachedFromWindow();
    }

    // ----------------------->

    private void readAttributeSet(Context context, AttributeSet attrs, int defStyle) {
        try {
            TypedArray a;
            if(defStyle != -1) {
                a = context.obtainStyledAttributes(attrs, R.styleable.dynamicText, defStyle, 0);
            }
            else {
                a = context.obtainStyledAttributes(attrs, R.styleable.dynamicText);
            }
            String dynamicTextKey = a.getString(R.styleable.dynamicText_text);
            String dynamicHintKey = a.getString(R.styleable.dynamicText_hint);
            if(!TextUtils.isEmpty(dynamicTextKey)) {
                setDynamicText(dynamicTextKey);
            }
            if(!TextUtils.isEmpty(dynamicHintKey)) {
                setDynamicHint(dynamicHintKey);
            }
            a.recycle();
        }
        catch(Exception e) {
            onError(e);
        }
    }

    public void setDynamicText(final String key) {
        if(TextUtils.isEmpty(key)) return;
        this.textKey = key;
        DynamicTextManager.getInstance().getStringAsync(key, this);
    }

    public void setDynamicHint(final String key) {
        if(TextUtils.isEmpty(key)) return;
        this.hintKey = key;
        DynamicTextManager.getInstance().getStringAsync(key, String.valueOf(getId()), new DynamicTextLoader.Listener() {
            @Override
            public void onTextLoaded(String text) {
                setHint(text);
            }
        });
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error", throwable);
    }

    // ----------------------->
}
