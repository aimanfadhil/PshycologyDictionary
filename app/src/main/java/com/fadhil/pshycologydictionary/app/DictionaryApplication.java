package com.fadhil.pshycologydictionary.app;

import android.app.Application;

import com.fadhil.pshycologydictionary.model.KamusObserver;

/**
 * Created by Student10 on 2/11/2016.
 */
public class DictionaryApplication extends Application {
    KamusObserver kamusObserver;
    @Override
    public void onCreate() {
        super.onCreate();
        kamusObserver = new KamusObserver();
    }

    public KamusObserver getKamusObserver(){
        return kamusObserver;
    }
}

