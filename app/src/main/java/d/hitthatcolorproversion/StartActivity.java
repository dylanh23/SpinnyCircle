package d.hitthatcolorproversion;

import android.content.Intent;
import android.graphics.Point;
import android.view.Display;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

public class StartActivity extends BaseGameActivity {
    protected static final int CAMERA_WIDTH = 480;
    protected static int CAMERA_HEIGHT = 800;
    Scene scene;

    @Override
    public EngineOptions onCreateEngineOptions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            CAMERA_HEIGHT = (int) (CAMERA_WIDTH / ((float) size.x / (float) size.y));
        } else {
            CAMERA_HEIGHT = (int) (CAMERA_WIDTH / ((float) display.getWidth() / (float) display.getHeight()));
        }
        //3:4, 2:3, 10:16, 3:5, and 9:16
        //0.75, 0.66, 0.625, 0.6, 0.5625
        CAMERA_HEIGHT = (int) (CAMERA_WIDTH / ((float) size.x / (float) size.y));
        Camera mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), mCamera);
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
        loadGFX();
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }


    TextureRegion sc;

    private void loadGFX() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        BitmapTextureAtlas b = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        int x = 0;
        sc = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "SC.png", x, 0); //346
        x += sc.getWidth() + 1;
        b.load();
    }


    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
        scene = new Scene();
        scene.setBackground(new Background(new Color(Color.WHITE)));
        scene.setBackgroundEnabled(true);
        pOnCreateSceneCallback.onCreateSceneFinished(scene);
    }

    Sprite scSprite;

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
        scSprite = new Sprite(CAMERA_WIDTH / 2 - sc.getWidth() / 2, 200f, sc, this.getVertexBufferObjectManager());
        scene.attachChild(scSprite);
        Rectangle r = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, this.getVertexBufferObjectManager()) {

            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_DOWN:
                        Intent i = new Intent(StartActivity.this, GameActivity.class);
                        i.putExtra("Mode2", false);
                        startActivity(i);
                        finish();
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        r.setColor(0, 0, 0, 0);
        scene.attachChild(r);
        scene.registerTouchArea(r);
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }
}
