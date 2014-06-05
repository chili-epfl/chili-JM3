package com.mycompany.mygame;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import java.io.IOException;

import mygame.CamStream;

/**
 *
 * @author quentin
 */
public class AndroidCamStream implements CamStream, SurfaceHolder.Callback {

    static {
        System.loadLibrary("opencv_core");
        System.loadLibrary("opencv_imgproc");
        System.loadLibrary("opencv_highgui");
        System.loadLibrary("opencv_flann");
        System.loadLibrary("opencv_features2d");
        System.loadLibrary("opencv_calib3d");
        System.loadLibrary("opencv_video");
            //TODO move to a Chilitag class (AppState ?)
        System.loadLibrary("chilitags");
        System.loadLibrary("chilitags_jni_bindings");
    }
    private Camera camera;
    private byte[] imgData;
    private boolean initialized;
    private boolean enabled;

    public AndroidCamStream() {
        this.initialized = false;
        this.enabled = false;
        //FIXME: hard coded values
        imgData = new byte[(int) (640 * 480 * 1.5)];
    }

    public byte[] getImageData() {
        return imgData;
    }

    public void initialize(AppStateManager stateManager, Application app) {
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

    public void stateAttached(AppStateManager stateManager) {
    }

    public void stateDetached(AppStateManager stateManager) {
    }

    public void update(float tpf) {
        //image updates happens in the PreviewCallback instantiated in surfaceCreated
    }

    public void render(RenderManager rm) {
    }

    public void postRender() {
    }

    public void cleanup() {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        camera.setParameters(params);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            camera.release();
            camera = null;
        }

        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                //FIXME: synchronisation ? never heard of that.
                imgData = data;
            }
        });

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}
