package mygame;

import ch.epfl.chili.chilitags.Chilitags3D;
import ch.epfl.chili.chilitags.ObjectTransform;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import org.lwjgl.opengl.Display;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


    }
    int WS_WIDTH = 640;
    int WS_HEIGHT = 480;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    private Chilitags3D chilitags;
    private VideoCapture video;
    private Geometry geom;

    @Override
    public void simpleInitApp() {
        initCameras();
        initChilitags();

        createGrid();

        Box b = new Box(20, 20, 20);
        geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);

        rootNode.attachChild(geom);
    }

    public void initCameras() {
        // Setup Perspective view
        cam.setViewPort(.5f, 1f, 0f, 0.5f);
        cam.setFrustum(1f, 1000f, -1f, 1f, 1f, -1f);
        cam.update();
        cam.setLocation(new Vector3f(0, 100, 100));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        viewPort.setBackgroundColor(ColorRGBA.LightGray);

        // Setup Top view
        Camera cam2 = cam.clone();
        cam2.setViewPort(0f, 0.5f, 0f, 0.5f);
        float aspect = (float) Display.getWidth() / Display.getHeight();
        float invZoom = 50f;
        cam2.setParallelProjection(true);
        cam2.setFrustum(0, 1000, -invZoom * aspect, invZoom * aspect, invZoom, -invZoom);
        cam2.update();

        cam2.setLocation(new Vector3f(0, 100, 0));
        cam2.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        ViewPort view2 = renderManager.createMainView("Bottom Left", cam2);
        view2.setBackgroundColor(ColorRGBA.LightGray);
        view2.setClearFlags(true, true, true);
        view2.attachScene(rootNode);

        // Setup Front view
        Camera cam3 = cam.clone();
        cam3.setViewPort(0f, .5f, .5f, 1f);
        cam3.setParallelProjection(true);
        cam3.setFrustum(0, 1000, -invZoom * aspect, invZoom * aspect, invZoom, -invZoom);
        cam3.update();

        cam3.setLocation(new Vector3f(0, 0, 100));
        cam3.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);


        ViewPort view3 = renderManager.createMainView("Top Left", cam3);
        view3.setBackgroundColor(ColorRGBA.LightGray);
        view3.setClearFlags(true, true, true);
        view3.attachScene(rootNode);

        // Setup Side view
        Camera cam4 = cam.clone();
        cam4.setViewPort(.5f, 1f, .5f, 1f);
        cam4.setParallelProjection(true);
        cam4.setFrustum(0, 1000, -invZoom * aspect, invZoom * aspect, invZoom, -invZoom);
        cam4.update();

        cam4.setLocation(new Vector3f(-100, 0, 0));
        cam4.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        ViewPort view4 = renderManager.createMainView("Top Right", cam4);
        view4.setBackgroundColor(ColorRGBA.LightGray);
        view4.setClearFlags(true, true, true);
        view4.attachScene(rootNode);




    }

    public void initChilitags() {
        chilitags = new Chilitags3D(640, 480, 640, 480, Chilitags3D.InputType.RGB888);
        chilitags.setPerformancePreset(Chilitags3D.PerformancePreset.ROBUST);
        //Fake matrix
        double[] cc = {
            270, 0, 640 / 2,
            0, 270, 480 / 2,
            0, 0, 1};
        double[] dc = {};
        chilitags.setCalibration(cc, dc);
        video = new VideoCapture(0);
        if (!video.isOpened()) {
            System.err.println("No Camera");
        }
        /*Set image size to 640x480*/
        video.set(3, 640);
        video.set(4, 480);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void createGrid() {
        Node grid = new Node("Grid");

        for (int i = -WS_WIDTH / 2; i < WS_WIDTH / 2; i += 20) {
            Geometry line_geom = new Geometry();

            Line line_mesh = new Line(new Vector3f(i, 0, -WS_HEIGHT / 2), new Vector3f(i, 0, WS_HEIGHT / 2));
            line_geom.setMesh(line_mesh);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Cyan);
            line_geom.setMaterial(mat);
            grid.attachChild(line_geom);
        }

        for (int i = -WS_HEIGHT / 2; i < WS_HEIGHT / 2; i += 20) {
            Geometry line_geom = new Geometry();

            Line line_mesh = new Line(new Vector3f(-WS_WIDTH / 2, 0, i), new Vector3f(WS_WIDTH / 2, 0, i));
            line_geom.setMesh(line_mesh);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Red);
            line_geom.setMaterial(mat);
            grid.attachChild(line_geom);
        }


        rootNode.attachChild(grid);



    }

    public void simpleUpdate(float tpf) {
        // make the cube rotate:
        Mat img = new Mat();
        video.read(img);
        byte[] return_buff = new byte[(int) (img.total()
                * img.channels())];
        img.get(0, 0, return_buff);
        ObjectTransform[] tags = chilitags.estimate(return_buff);
        Matrix3f wsRot = null;
        Vector3f wsTrans = null;
        /*tag 1023 defines my workspace plane*/
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].name.contains("1023")) {
                Matrix4f m = new Matrix4f(
                        (float) tags[i].transform[0][0], (float) tags[i].transform[0][1],
                        (float) tags[i].transform[0][2], (float) tags[i].transform[0][3],
                        (float) tags[i].transform[1][0], (float) tags[i].transform[1][1],
                        (float) tags[i].transform[1][2], (float) tags[i].transform[1][3],
                        (float) tags[i].transform[2][0], (float) tags[i].transform[2][1],
                        (float) tags[i].transform[2][2], (float) tags[i].transform[2][3],
                        (float) tags[i].transform[3][0], (float) tags[i].transform[3][1],
                        (float) tags[i].transform[3][2], (float) tags[i].transform[3][3]);

                /*TODO: check products! */
                Matrix3f m1 = new Matrix3f(1, 0, 0, 0, 0, 1, 0, 1, 0);
                Vector3f tran = m.toTranslationVector();
                Matrix3f rot = m.toRotationMatrix();
                wsRot = m1.invert().mult(rot.clone().mult(m1));
                wsTrans = m.toTranslationVector();
                wsTrans.y = wsTrans.z;
                wsTrans.z = tran.y;
                break;
            }
        }
        if (wsRot != null) {

            for (int i = 0; i < tags.length; i++) {
                if (!tags[i].name.contains("1023")) {
                    Matrix4f m = new Matrix4f(
                            (float) tags[i].transform[0][0], (float) tags[i].transform[0][1],
                            (float) tags[i].transform[0][2], (float) tags[i].transform[0][3],
                            (float) tags[i].transform[1][0], (float) tags[i].transform[1][1],
                            (float) tags[i].transform[1][2], (float) tags[i].transform[1][3],
                            (float) tags[i].transform[2][0], (float) tags[i].transform[2][1],
                            (float) tags[i].transform[2][2], (float) tags[i].transform[2][3],
                            (float) tags[i].transform[3][0], (float) tags[i].transform[3][1],
                            (float) tags[i].transform[3][2], (float) tags[i].transform[3][3]);
                    Matrix3f m1 = new Matrix3f(1, 0, 0, 0, 0, 1, 0, 1, 0);
                    Vector3f tran = m.toTranslationVector();
                    Matrix3f rot = m.toRotationMatrix();

                    Matrix3f temp = wsRot.invert().mult(m1.invert().mult(rot).mult(m1));
                    geom.setLocalRotation(temp);
                    break;
                }
            }
        }
    }

    public Main() {
        setDisplayStatView(false);
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
    }
}
