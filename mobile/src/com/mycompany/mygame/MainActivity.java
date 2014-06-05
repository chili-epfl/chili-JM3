package com.mycompany.mygame;
 
import android.content.pm.ActivityInfo;
import android.view.Gravity;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import com.jme3.app.AndroidHarness;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;
import java.util.logging.Level;
import java.util.logging.LogManager;
import mygame.Main;
 
public class MainActivity extends AndroidHarness {
    
    static {
        Main.isDesktop = false;
    }
 
    /*
     * Note that you can ignore the errors displayed in this file,
     * the android project will build regardless.
     * Install the 'Android' plugin under Tools->Plugins->Available Plugins
     * to get error checks and code completion for the Android project files.
     */
    
    private SurfaceView surfaceView;
    private AndroidCamStream camStream;
 
    public MainActivity(){
        // Set the application class to run
        appClass = "mygame.Main";
        // Try ConfigType.FASTEST; or ConfigType.LEGACY if you have problems
        eglConfigType = ConfigType.BEST;
        // Exit Dialog title & message
        exitDialogTitle = "Exit?";
        exitDialogMessage = "Press Yes";
        // Enable verbose logging
        eglConfigVerboseLogging = false;
        // Choose screen orientation
        screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        // Enable MouseEvents being generated from TouchEvents (default = true)
        mouseEventsEnabled = true;
        // Set the default logging level (default=Level.INFO, Level.ALL=All Debug Info)
        LogManager.getLogManager().getLogger("").setLevel(Level.INFO);
        
        camStream = new AndroidCamStream();
    }
    
    @Override
    public void initialize() {
        ((Main) app).camStream = camStream;
        super.initialize();
    }
    
    @Override
    public void layoutDisplay() {
        super.layoutDisplay();
        surfaceView = new SurfaceView(this);
        surfaceView.getHolder().addCallback(camStream);
        FrameLayout fl = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
        fl.addView(surfaceView);
        fl.bringChildToFront(surfaceView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
        layoutParams.width = 160;
        layoutParams.height = 120;
        layoutParams.topMargin = 0;
        layoutParams.leftMargin = 0;
        layoutParams.gravity = Gravity.BOTTOM + Gravity.RIGHT;
    }
 
}
