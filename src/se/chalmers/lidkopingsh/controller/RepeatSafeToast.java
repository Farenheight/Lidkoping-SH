package se.chalmers.lidkopingsh.controller;

import java.util.HashMap;
import java.util.Map;

import se.chalmers.lidkopingsh.app.App;

import android.content.Context;
import android.widget.Toast;

public class RepeatSafeToast {

    private static final int DURATION = 4000;

    private static final Map<Object, Long> lastShown = new HashMap<Object, Long>();

    private static boolean isRecent(Object obj) {
        Long last = lastShown.get(obj);
        if (last == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (last + DURATION < now) {
            return false;
        }
        return true;
    }

    public static synchronized void show(int resId) {
        if (isRecent(resId)) {
            return;
        }
        Toast.makeText(App.getContext(), resId, Toast.LENGTH_LONG).show();
        lastShown.put(resId, System.currentTimeMillis());
    }

    public static synchronized void show(String msg) {
        if (isRecent(msg)) {
            return;
        }
        Toast.makeText(App.getContext(), msg, Toast.LENGTH_LONG).show();
        lastShown.put(msg, System.currentTimeMillis());
    }
}