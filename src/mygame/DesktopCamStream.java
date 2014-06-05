/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import com.jme3.system.JmeSystem;
import com.jme3.system.Natives;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

/**
 *
 * @author quentin
 */
public class DesktopCamStream implements CamStream {
    
    static {
        String sysName = JmeSystem.getPlatform().name();
        try {
            Natives.extractNativeLib(sysName, "opencv_core", true, false);
            Natives.extractNativeLib(sysName, "opencv_imgproc", true, false);
            Natives.extractNativeLib(sysName, "opencv_highgui", true, false);
            Natives.extractNativeLib(sysName, "opencv_flann", true, false);
            Natives.extractNativeLib(sysName, "opencv_features2d", true, false);
            Natives.extractNativeLib(sysName, "opencv_calib3d", true, false);
            Natives.extractNativeLib(sysName, "opencv_video", true, false);
            Natives.extractNativeLib(sysName, "opencv_java248", true, false);
            //TODO move to a Chilitag class (AppState ?)
            Natives.extractNativeLib(sysName, "chilitags", true, false);
            Natives.extractNativeLib(sysName, "chilitags_jni_bindings", true, false);
        } catch (IOException ex) {
            //Natives.extractNativeLib already warns about loading error
        }
    }
    
    private VideoCapture video;
    private Mat img;
    private byte[] imgData;
    private boolean initialized;
    private boolean enabled;

    public DesktopCamStream() {
        this.initialized = false;
        this.enabled = false;
        this.video = null;
        this.img = null;
    }

    public byte[] getImageData() {
        return imgData;
    }

    public void initialize(AppStateManager stateManager, Application app) {
        video = new VideoCapture(0);
        if (!video.isOpened()) {
            System.err.println("No Camera");
        }
        /*Set image size to 640x480*/
        //FIXME: hardcoded values
        video.set(3, 640);
        video.set(4, 480);
        img = new Mat();
        video.read(img);
        imgData = new byte[(int) (img.total()  * img.channels())];
        img.get(0, 0, imgData);
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setEnabled(boolean active) {
        enabled = active;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void stateAttached(AppStateManager stateManager) {}

    public void stateDetached(AppStateManager stateManager) {}

    public void update(float tpf) {
        if (enabled) {
            video.read(img);
            img.get(0, 0, imgData);
        }
    }

    public void render(RenderManager rm) {}

    public void postRender() {}

    public void cleanup() {}
    
}
