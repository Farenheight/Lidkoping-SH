package se.chalmers.lidkopingsh.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class App extends Application {

    private static App instance;

    public App() {
    	Log.d("App", "App created");
    	instance = this;
    }

    public static Context getContext() {
    	return instance;
    }

}