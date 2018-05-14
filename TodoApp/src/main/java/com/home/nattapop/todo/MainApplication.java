package com.home.nattapop.todo;

import android.app.Application;

import com.home.nattapop.todo.manager.Contextor;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Contextor.getInstance().init(getApplicationContext());

    }

}
