package mygame;

import ch.epfl.chili.chilitags.Chilitags3D;
import ch.epfl.chili.chilitags.ObjectTransform;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static boolean isDesktop = true;
    public CamStream camStream;
    private Chilitags3D chilitags; //TODO move to a Chilitag AppState ?
    private HashMap<String, Matrix4f> config = new HashMap<String, Matrix4f>();

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    public Main() {
        setDisplayStatView(false);
    }

    // FIXME
    private static Matrix4f computeLocalCoords(Matrix4f knownLocal, Matrix4f known, Matrix4f toAdd) {
        return knownLocal.invert()
                .mult(known.invert())
                .mult(toAdd);
    }

    public static Matrix4f toMatrix4f(ObjectTransform ot) {
        return new Matrix4f(
                (float) ot.transform[0][0], (float) ot.transform[0][1],
                (float) ot.transform[0][2], (float) ot.transform[0][3],
                (float) ot.transform[1][0], (float) ot.transform[1][1],
                (float) ot.transform[1][2], (float) ot.transform[1][3],
                (float) ot.transform[2][0], (float) ot.transform[2][1],
                (float) ot.transform[2][2], (float) ot.transform[2][3],
                (float) ot.transform[3][0], (float) ot.transform[3][1],
                (float) ot.transform[3][2], (float) ot.transform[3][3]);
    }

    @Override
    public void simpleInitApp() {
        if (isDesktop) {
            this.camStream = new DesktopCamStream();
        }
        this.stateManager.attach(camStream);
        this.camStream.setEnabled(true);

        cam.setLocation(new Vector3f(0, 0, -100));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        stateManager.getState(FlyCamAppState.class).getCamera().setMoveSpeed(100.0f);

        chilitags = new Chilitags3D(640, 480, 640, 480,
                isDesktop
                ? Chilitags3D.InputType.RGB888
                : Chilitags3D.InputType.YUV_NV21);

        createGrid();
    }

    // TODO move to a control
    private void setTagGeometry(String name, Matrix4f transform) {
        Geometry geometry = (Geometry) rootNode.getChild(name);
        if (geometry == null) {
            geometry = new Geometry(name, new Quad(20.f, 20.f));

            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", name.contains("config") ? ColorRGBA.Blue : ColorRGBA.Red);
            mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
            geometry.setMaterial(mat);

            rootNode.attachChild(geometry);
        }

        geometry.setLocalTransform(new Transform(
                transform.toTranslationVector(),
                transform.toRotationQuat(),
                transform.toScaleVector()));
    }

    public void createGrid() {
        int WS_WIDTH = 640;
        int WS_HEIGHT = 480;

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

    @Override
    public void simpleUpdate(float tpf) {
        // make the cube rotate:
        if (camStream.isEnabled()) {
            ObjectTransform[] tags = chilitags.estimate(camStream.getImageData());
            if (tags.length > 0) {
                if (config.isEmpty()) {
                    ObjectTransform firstTag = tags[0];
                    config.put(firstTag.name, Matrix4f.IDENTITY);
                    setTagGeometry(firstTag.name, toMatrix4f(firstTag));
                    setTagGeometry(firstTag.name + "config", Matrix4f.IDENTITY);
                }

                ObjectTransform knownTag = null;
                LinkedList<ObjectTransform> tagsToAdd = new LinkedList<ObjectTransform>();
                for (ObjectTransform tag : tags) {
                    if (config.containsKey(tag.name)) {
                        knownTag = tag;
                    } else {
                        tagsToAdd.add(tag);
                    }
                    setTagGeometry(tag.name, toMatrix4f(tag));
                }

                if (knownTag != null) {
                    for (ObjectTransform tag : tagsToAdd) {
                        Matrix4f localCoordinates = computeLocalCoords(
                                config.get(knownTag.name),
                                toMatrix4f(knownTag),
                                toMatrix4f(tag));
                        setTagGeometry(tag.name + "config", localCoordinates);
                        config.put(tag.name, localCoordinates);
                        printConfig(System.out);
                    }
                }
            }
        }
    }

    private void printConfig(PrintStream out) {
        out.println("myobject:");
        Pattern tagIdPattern = Pattern.compile("\\d+");
        for (String t : config.keySet()) {
            Matcher matcher = tagIdPattern.matcher(t);
            matcher.find();
            out.println("    - tag:" + matcher.group());

            Matrix4f transform = config.get(t);

            float[] angles = transform.toRotationQuat().toAngles(null);
            out.println(String.format("      rotation: [%f, %f, %f]",
                    angles[0], angles[1], angles[2]));

            Vector3f translation = transform.toTranslationVector();
            out.println(String.format("      translation: [%f, %f, %f]",
                    translation.x, translation.y, translation.z));
        }
    }
}
