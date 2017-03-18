package com.webianks.task.edforastudio;

import android.app.Application;
import in.myinnos.customfontlibrary.TypefaceUtil;

/**
 * Created by R Ankit on 18-03-2017.
 */

public class StudioApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // custom font for entire App
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Montserrat-Regular.ttf");
    }
}
