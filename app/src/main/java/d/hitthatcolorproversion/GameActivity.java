package d.hitthatcolorproversion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.Display;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.andengine.AndEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.IGameInterface;
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.util.color.Color;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class GameActivity extends LayoutGameActivity {
    protected static final int CAMERA_WIDTH = 480;
    protected static int CAMERA_HEIGHT = 800;
    protected static float SPEED = 2f;
    protected static BigDecimal SPEEDDISTANCE = new BigDecimal("51.4").divide(new BigDecimal("4"));
    Font scoreFnt;
    Scene scene;
    Random randomGenerater = new Random();
    int score = 0;
    boolean died;
    boolean mode2;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest;

    private void requestNewInterstitial() {
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        //3:4, 2:3, 10:16, 3:5, and 9:16
        //0.75, 0.66, 0.625, 0.6, 0.5625
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            CAMERA_HEIGHT = (int) (CAMERA_WIDTH / ((float) size.x / (float) size.y));
        } else {
            CAMERA_HEIGHT = (int) (CAMERA_WIDTH / ((float) display.getWidth() / (float) display.getHeight()));
        }
        Camera mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        //SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        //mode2 = prefs.getBoolean("Mode2", false);
        mode2 = getIntent().getBooleanExtra("Mode2", false);
        //prefs.edit().clear().apply();
        adRequest = new AdRequest.Builder().addTestDevice("DDB504E461FF179D726AD9B5F625CBE1").build();
        adRequest = new AdRequest.Builder().build();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4130770981589196/7520587060");
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), mCamera);
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws
            Exception {
        loadGFX();
        scoreFnt = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 1024, 1024, this.getAssets(), "fnt/ChronicaPro-Bold.ttf", 95f, true, new Color(155f / 255, 155f / 255, 155f / 255).getABGRPackedInt());
        scoreFnt.load();
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    TextureRegion wheel;
    TextureRegion dot;
    TextureRegion usealife;
    TextureRegion backarrow;

    private void loadGFX() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        BitmapTextureAtlas b = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        int x = 0;
        wheel = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "wheel.png", x, 0); //346
        x += wheel.getWidth() + 1;
        dot = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "dotblank.png", x, 0); //47
        x += dot.getWidth() + 1;
        usealife = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "usealife.png", x, 0);
        x += usealife.getWidth() + 1;
        x = 0;
        backarrow = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "arrow.png", x, 500);
        x += backarrow.getWidth() + 1;
        b.load();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
        scene = new Scene();
        scene.setBackground(new Background(new Color(Color.WHITE)));
        scene.setBackgroundEnabled(true);
        mEngine.registerUpdateHandler(new TimerHandler(0.001f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                if (!died) {
                    update();
                    pTimerHandler.reset();
                }
            }
        }
        ));
        /* scene.registerUpdateHandler(new IUpdateHandler() {
            public void reset() {
            }

            public void onUpdate(float pSecondsElapsed) {
                if (gameOn && !died) {
                    update();
                }
            }
        });*/
        pOnCreateSceneCallback.onCreateSceneFinished(scene);
    }

    public void update() {
        //218
        int x = rotation.divide(new BigDecimal("51.4"), 0, RoundingMode.HALF_UP).intValue();
        if (x == 7) {
            x = 0;
        } else if (x == -1) {
            x = 6;
        }

        if (!UpDirection) {
            SPEED += 0.3f;
            dotSprite.setY(dotSprite.getY() + SPEED);
            //if (dotSprite.getY() + dotSprite.getHeight() >= wheelSprite.getY() + wheelSprite.getHeight() - dot.getHeight() - 25f - (float)(x * 1.5)) {
            if (x >= 0 && x <= 7 && dotSprite.getY() + dotSprite.getHeight() >= bottom[x] + 3) {
                SPEED = 11f;
                //dotSprite.setY(wheelSprite.getY() + wheelSprite.getHeight() - dot.getHeight() - 25f - (float)(x * 1.5));
                // dotSprite.setY(bottom[x]);
                test();
            }
        } else {
            SPEED -= 0.38f;
            dotSprite.setY(dotSprite.getY() - SPEED);
            //if (dotSprite.getY() <= wheelSprite.getY() + wheelSprite.getHeight() / 2f - dotSprite.getHeight() / 2) {
            if (SPEED <= 0) {
                SPEED = -0.15f;
                UpDirection = false;
            }
        }
    }

    public void test() {
        if (rotation.compareTo(new BigDecimal("360")) >= 0)
            rotation = rotation.subtract(new BigDecimal("360"));
        else if (rotation.compareTo(new BigDecimal("0")) <= 0)
            rotation = rotation.add(new BigDecimal("360"));
        int x = rotation.divide(new BigDecimal("51.4"), 0, RoundingMode.HALF_UP).intValue();
        if (x == 7) {
            x = 0;
        } else if (x == -1) {
            x = 6;
        }
        Color testColor;
        testColor = colors[x];

        if (testColor == currentColor) {
            UpDirection = true;
            score += 1;
            scoreText.setText(String.valueOf(score));
            scoreText.setPosition(CAMERA_WIDTH / 2f - scoreText.getWidth() / 2f, 71f);
            randomColor();
        } else {
            dotSprite.setY(bottom[x] + 3 - dotSprite.getHeight());
            died = true;
            gameOver();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mInterstitialAd.isLoaded()) {
                        //mInterstitialAd.show();
                    }
                }
            });
        }
    }

    private void gameOver() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if (prefs.getInt("lives", 0) > 0) {
            scene.registerTouchArea(spendButton);
            scene.registerTouchArea(exitButton);
            usealifeSprite.setVisible(true);
            wheelSprite.setVisible(false);
            backArrowSprite.setVisible(true);
            livesCountText.setVisible(true);
            livesCountText.setText("You have " + prefs.getInt("lives", 0) + " lives left.");
            livesCountText.setX(CAMERA_WIDTH / 2 - livesCountText.getWidth() / 2);
        } else {
            Intent i = new Intent(GameActivity.this, MenuActivity.class);
            i.putExtra("Mode2", mode2);
            i.putExtra("score", score);
            startActivity(i);
            finish();
        }
    }

    Sprite wheelSprite;
    Sprite dotSprite;
    Sprite usealifeSprite;
    Sprite backArrowSprite;
    Text scoreText;
    Text livesCountText;
    Color[] colors = new Color[]{
            new Color(51f / 255, 153f / 255, 1f), //blue
            new Color(107f / 255, 119f / 255, 124f / 255), //grey
            new Color(1f, 153f / 255, 51f / 255), //orange
            new Color(1f, 102f / 255, 102f / 255), //red
            new Color(26f / 255, 209f / 255, 163f / 255), //lime
            new Color(118f / 255, 72f / 255, 209f / 255), //purple
            new Color(1f, 204f / 255, 0), //yellow
    };
    Color currentColor = colors[0];
    int[] bottom = new int[]{
            573,//blue
            572,//grey
            569, //orange
            562,//red
            565,//lime
            568, //purple
            572//yellow
    };

    boolean UpDirection = false;

    @Override
    public void onPopulateScene(final Scene pScene, IGameInterface.OnPopulateSceneCallback
            pOnPopulateSceneCallback) throws Exception {
        wheelSprite = new Sprite(CAMERA_WIDTH / 2 - wheel.getWidth() / 2f, 206f, wheel, this.getVertexBufferObjectManager());
        wheelSprite.setRotationCenter(wheel.getWidth() / 2, wheel.getHeight() / 2);
        wheelSprite.setZIndex(1);
        scene.attachChild(wheelSprite);
        dotSprite = new Sprite(wheelSprite.getX() + wheelSprite.getWidth() / 2 - dot.getWidth() / 2, wheelSprite.getY() + wheelSprite.getHeight() / 2 - dot.getWidth() / 2, dot, this.getVertexBufferObjectManager());
        //dotSprite = new Sprite(wheelSprite.getX() + wheelSprite.getWidth() / 2 - dot.getWidth() / 2, wheelSprite.getY() + wheelSprite.getHeight() - dot.getHeight() - 30f, dot, this.getVertexBufferObjectManager());
        startingColor();
        scene.attachChild(dotSprite);
        usealifeSprite = new Sprite(CAMERA_WIDTH / 2 - usealife.getWidth() / 2, wheelSprite.getY() - 60f, usealife, this.getVertexBufferObjectManager());
        usealifeSprite.setVisible(false);
        usealifeSprite.setZIndex(2);
        scene.attachChild(usealifeSprite);
        backArrowSprite = new Sprite(25, usealifeSprite.getY() + usealifeSprite.getHeight() - 50f, backarrow, this.getVertexBufferObjectManager());
        backArrowSprite.setVisible(false);
        backArrowSprite.setZIndex(4);
        scene.attachChild(backArrowSprite);
        livesCountText = new Text(0, 0, scoreFnt, "New High: 123456789", this.getVertexBufferObjectManager());
        livesCountText.setVisible(false);
        livesCountText.setY(usealifeSprite.getY() + 300);
        livesCountText.setScale(0.25f);
        livesCountText.setZIndex(3);
        scene.attachChild(livesCountText);
        scoreText = new Text(0, 0, scoreFnt, "Score: ", this.getVertexBufferObjectManager());
        scoreText.setText(String.valueOf(score));
        scoreText.setPosition(CAMERA_WIDTH / 2f - scoreText.getWidth() / 2f, 71f);
        scoreText.setZIndex(0);
        scene.attachChild(scoreText);
        scene.sortChildren();
        createTouchAreas();
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    BigDecimal rotation;

    Rectangle exitButton;
    Rectangle spendButton;

    private void createTouchAreas() {
        Rectangle r = new Rectangle(0, 155f, CAMERA_WIDTH / 2, CAMERA_HEIGHT, this.getVertexBufferObjectManager()) {

            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_DOWN:
                        if (!died) {
                            wheelAnimation(true);
                        }
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        r.setColor(0, 0, 0, 0);
        scene.attachChild(r);
        scene.registerTouchArea(r);
        Rectangle r2 = new Rectangle(CAMERA_WIDTH / 2, 155f, CAMERA_WIDTH / 2, CAMERA_HEIGHT, this.getVertexBufferObjectManager()) {

            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_DOWN:
                        if (!died) {
                            if (mode2) {
                                wheelAnimation(false);
                            } else {
                                wheelAnimation(true);
                            }
                        }
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        r2.setColor(0, 0, 0, 0);
        scene.attachChild(r2);
        scene.registerTouchArea(r2);
        spendButton = new Rectangle(CAMERA_WIDTH / 2 - 320 / 2, usealifeSprite.getY() + 194, 320, 126, this.getVertexBufferObjectManager()) {

            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_DOWN:
                        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                        prefs.edit().putInt("lives", prefs.getInt("lives", 0) - 1).apply();
                        usealifeSprite.setVisible(false);
                        wheelSprite.setVisible(true);
                        livesCountText.setVisible(false);
                        backArrowSprite.setVisible(false);
                        //scene.unregisterTouchArea(exitButton);
                        //scene.unregisterTouchArea(this);
                        startingColor();
                        dotSprite.setPosition(wheelSprite.getX() + wheelSprite.getWidth() / 2 - dot.getWidth() / 2, wheelSprite.getY() + wheelSprite.getHeight() / 2 - dot.getWidth() / 2);
                        SPEED = 2f;
                        died = false;
                        return false;
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        spendButton.setVisible(false);
        scene.attachChild(spendButton);
        exitButton = new Rectangle(0, usealifeSprite.getY() + usealifeSprite.getHeight() - 50f, 30 * 2 + backArrowSprite.getWidth(), 30 * 2 + backArrowSprite.getHeight(), this.getVertexBufferObjectManager()) {

            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_DOWN:
                        Intent i = new Intent(GameActivity.this, MenuActivity.class);
                        i.putExtra("Mode2", mode2);
                        i.putExtra("score", score);
                        startActivity(i);
                        finish();
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        exitButton.setVisible(false);
        scene.attachChild(exitButton);
    }

    private void wheelAnimation(final boolean left) {
        mEngine.registerUpdateHandler(new TimerHandler(0.001f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                if (!died) {
                    if (left) {
                        rotation = rotation.subtract(SPEEDDISTANCE);
                    } else {
                        rotation = rotation.add(new BigDecimal("12.85"));
                    }
                    wheelSprite.setRotation(rotation.intValue());
                    String s = rotation.divide(new BigDecimal("51.4"), 2, RoundingMode.HALF_UP).toPlainString();
                    s = s.substring(s.indexOf("."));
                    s = String.valueOf(s.charAt(1));
                    if (!s.equals("0")) {
                        pTimerHandler.reset();
                    } else if (rotation.compareTo(new BigDecimal("355")) >= 0) {
                        rotation = new BigDecimal("0");
                    } else if (rotation.compareTo(new BigDecimal("-50")) <= 0) {
                        rotation = new BigDecimal("308.6");
                    }
                }
            }
        }
        ));
    }

    private void randomColor() {
        Color c = colors[randomGenerater.nextInt(7)];
        while (c == currentColor) {
            c = colors[randomGenerater.nextInt(7)];
        }
        currentColor = c;
        dotSprite.setColor(c);
        //TODO: transition color animation
    }

    private void startingColor() {
        int x = randomGenerater.nextInt(7);
        rotation = new BigDecimal("51.4").multiply(new BigDecimal(x));
        wheelSprite.setRotation(rotation.intValue());
        currentColor = colors[x];
        dotSprite.setColor(colors[x]);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.SurfaceViewId;
    }

    AdView adView;

    @Override
    protected void onSetContentView() {
        RelativeLayout relativeLayout = new RelativeLayout(this);
        final RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);

        if (!AndEngine.isDeviceSupported()) {
            //this device is not supported, create a toast to tell the user
            //then kill the activity
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3500);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    } catch (InterruptedException e) {
                    }
                }
            };
            this.toastOnUIThread("This device does not support AndEngine GLES2, so this game will not work. Sorry.");
            finish();
            thread.start();

            this.setContentView(relativeLayout, relativeLayoutParams);
        } else {
            this.mRenderSurfaceView = new RenderSurfaceView(this);
            mRenderSurfaceView.setRenderer(mEngine, this);

            relativeLayout.addView(mRenderSurfaceView, GameActivity.createSurfaceViewLayoutParams());

            try {
                adView = new AdView(this);
                adView.setAdSize(AdSize.SMART_BANNER);
                adView.setAdUnitId("ca-app-pub-6184270616715379/8829367446");
                adView.setTag("adView");
                adView.refreshDrawableState();
                adView.setVisibility(AdView.GONE);

                // Initiate a generic request to load it with an ad
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adView.loadAd(adRequest);
                    }
                });

                adView.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        adView.setVisibility(AdView.VISIBLE);
                    }
                });

                RelativeLayout.LayoutParams adViewParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                //the next line is the key to putting it on the bottom
                adViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                relativeLayout.addView(adView, adViewParams);
            } catch (Exception e) {
                //ads aren't working. oh well
            }
            this.setContentView(relativeLayout, relativeLayoutParams);
        }
    }
}