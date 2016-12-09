package d.hitthatcolorproversion;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.view.Display;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.GMSBaseGameActivity;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyV4VCAd;
import com.jirbo.adcolony.AdColonyV4VCListener;
import com.jirbo.adcolony.AdColonyV4VCReward;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
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
import org.andengine.util.color.Color;

import java.util.Random;

public class MenuActivity extends GMSBaseGameActivity {
    protected static final int CAMERA_WIDTH = 480;
    protected static int CAMERA_HEIGHT = 800;
    protected static float SPEED = 0;
    String APPID;
    String ADVERTID;
    boolean mode2;
    String leaderboard;
    boolean UpDirection = false;
    Scene scene;
    Font scoreFnt;
    Random randomGenerater = new Random();
    InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    GoogleApiClient mGoogleApiClient;
    AdColonyV4VCAd ad;
    git remote add origin https://github.com/dylanh23/SpinnyCircle.git

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
        APPID = getResources().getString(R.string.adcolony);
        ADVERTID = getResources().getString(R.string.zoneid);
        if (AdColony.isConfigured()) {
            AdColony.resume(MenuActivity.this);
        } else {
            AdColony.configure(MenuActivity.this, "version:1.0,store:google", APPID, ADVERTID);
        }
        ad = new AdColonyV4VCAd();
        mode2 = getIntent().getBooleanExtra("Mode2", false);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        final int bestScore;
        if (mode2) {
            bestScore = prefs.getInt("mode2score", 0);
            leaderboard = getResources().getString(R.string.leaderboard_top_scores_two_way_mode);
        } else {
            bestScore = prefs.getInt("modescore", 0);
            leaderboard = getResources().getString(R.string.leaderboard_top_scores_original_mode);
        }

        if (checkPlayServices()) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                            //.addConnectionCallbacks(this)
                            //.addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
            Games.Leaderboards.loadCurrentPlayerLeaderboardScore(mGoogleApiClient,
                    leaderboard,
                    LeaderboardVariant.TIME_SPAN_ALL_TIME,
                    LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(
                    new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                        final int bs = bestScore;
                        final GoogleApiClient gac = mGoogleApiClient;

                        @Override
                        public void onResult(Leaderboards.LoadPlayerScoreResult arg0) {
                            LeaderboardScore c = arg0.getScore();
                            if (c != null) {
                                if (bs > c.getRawScore()) {
                                    Games.Leaderboards.submitScore(gac, leaderboard, bestScore);
                                }
                            }
                        }
                    });
        }

        int score = getIntent().getIntExtra("score", 0);
        if (score > bestScore)

        {
            if (mode2) {
                prefs.edit().putInt("mode2score", score).apply();
            } else {
                prefs.edit().putInt("modescore", score).apply();
            }
            if (checkPlayServices() && mGoogleApiClient != null) {
                //Games.Leaderboards.submitScore(mGoogleApiClient, leaderboard, score);
            }
        }

        return new

                EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), mCamera

        );
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return resultCode == ConnectionResult.SUCCESS;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws
            Exception {
        loadGFX();
        scoreFnt = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 1024, 1024, this.getAssets(), "fnt/ChronicaPro-Bold.ttf", 95f, true, new Color(155f / 255, 155f / 255, 155f / 255).getABGRPackedInt());
        scoreFnt.load();
        //TODO: only numbers
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    TextureRegion extralife;
    TextureRegion mode;
    TextureRegion modetwo;
    TextureRegion restart;
    TextureRegion scores;
    TextureRegion score;
    TextureRegion highscore;
    TextureRegion dot;
    TextureRegion rate;

    private void loadGFX() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        BitmapTextureAtlas b = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        int x = 0;
        extralife = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "extralife.png", x, 0);
        x += extralife.getWidth() + 1;
        mode = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "mode.png", x, 0);
        x += mode.getWidth() + 1;
        modetwo = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "mode2.png", x, 0);
        x += modetwo.getWidth() + 1;
        restart = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "restart.png", x, 0);
        x += restart.getWidth() + 1;
        x = 0;
        scores = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "scores.png", x, 100);
        x += scores.getWidth() + 1;
        score = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "score.png", x, 100);
        x += score.getWidth() + 1;
        highscore = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "highscore.png", x, 100);
        x += highscore.getWidth() + 1;
        dot = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "dotblank.png", x, 100); //47
        x += dot.getWidth() + 1;
        rate = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "rate.png", x, 100);
        x += rate.getWidth() + 1;
        b.load();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
        scene = new Scene();
        scene.setBackground(new Background(new Color(1f, 1f, 1f)));
        scene.setBackgroundEnabled(true);
        mEngine.registerUpdateHandler(new TimerHandler(0.001f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                update();
                pTimerHandler.reset();
            }
        }
        ));
        pOnCreateSceneCallback.onCreateSceneFinished(scene);
    }

    public void update() {
        //218

        if (!UpDirection) {
            SPEED += 0.60f;
            dotSprite.setY(dotSprite.getY() + SPEED);
            if (dotSprite.getY() + dotSprite.getHeight() > CAMERA_HEIGHT - 20f) {
                SPEED = 16f;
                UpDirection = true;
                randomColor();
            }
        } else {
            SPEED -= 0.80f;
            dotSprite.setY(dotSprite.getY() - SPEED);
            if (SPEED < 0) {
                SPEED = -0.1f;
                UpDirection = false;
            }
        }
    }

    Sprite extralifeSprite;
    Sprite modeSprite;
    Sprite modetwoSprite;
    Sprite restartSprite;
    Sprite scoresSprite;
    Sprite scoreSprite;
    Sprite highscoreSprite;
    Sprite dotSprite;
    Sprite rateSprite;
    Text scoreText;
    Text highScoreText;

    @Override
    public void onPopulateScene(final Scene pScene, OnPopulateSceneCallback
            pOnPopulateSceneCallback) throws Exception {
        //250
        scoresSprite = new Sprite(CAMERA_WIDTH / 2 - scores.getWidth() / 2, 250f, scores, this.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_UP:
                        if (checkPlayServices() && mGoogleApiClient != null) {
                            //startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, leaderboard), 100);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MenuActivity.this, "Google Play Games not connected!", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        scene.registerTouchArea(scoresSprite);
        scene.attachChild(scoresSprite);
        modeSprite = new Sprite(CAMERA_WIDTH / 2 - mode.getWidth() / 2, scoresSprite.getY() + scoresSprite.getHeight() + 8f, mode, this.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_UP:
                        modeSprite.setVisible(false);
                        scene.unregisterTouchArea(modeSprite);
                        modetwoSprite.setVisible(true);
                        scene.registerTouchArea(modetwoSprite);
                        mode2 = true;
                        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                        highScoreText.setText(String.valueOf(prefs.getInt("mode2score", 0)));
                        highScoreText.setX(highscoreSprite.getX() + highscoreSprite.getWidth() / 2 - highScoreText.getWidth() / 2);
                        if (getIntent().getBooleanExtra("Mode2", false)) {
                            scoreText.setText(String.valueOf(getIntent().getIntExtra("score", 1)));
                            scoreText.setX(scoreSprite.getX() + scoreSprite.getWidth() / 2 - scoreText.getWidth() / 2);
                        } else {
                            scoreText.setText("0");
                            scoreText.setX(scoreSprite.getX() + scoreSprite.getWidth() / 2 - scoreText.getWidth() / 2);
                        }
                        leaderboard = getResources().getString(R.string.leaderboard_top_scores_two_way_mode);
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        scene.attachChild(modeSprite);
        modetwoSprite = new Sprite(CAMERA_WIDTH / 2 - modetwo.getWidth() / 2, scoresSprite.getY() + scoresSprite.getHeight() + 8f, modetwo, this.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_UP:
                        modetwoSprite.setVisible(false);
                        scene.unregisterTouchArea(modetwoSprite);
                        modeSprite.setVisible(true);
                        scene.registerTouchArea(modeSprite);
                        mode2 = false;
                        modetwoSprite.setVisible(false);
                        scene.registerTouchArea(modeSprite);
                        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                        highScoreText.setText(String.valueOf(prefs.getInt("modescore", 0)));
                        highScoreText.setX(highscoreSprite.getX() + highscoreSprite.getWidth() / 2 - highScoreText.getWidth() / 2);
                        if (!getIntent().getBooleanExtra("Mode2", false)) {
                            scoreText.setText(String.valueOf(getIntent().getIntExtra("score", 0)));
                            scoreText.setX(scoreSprite.getX() + scoreSprite.getWidth() / 2 - scoreText.getWidth() / 2);
                        } else {
                            scoreText.setText("0");
                            scoreText.setX(scoreSprite.getX() + scoreSprite.getWidth() / 2 - scoreText.getWidth() / 2);
                        }
                        leaderboard = getResources().getString(R.string.leaderboard_top_scores_original_mode);
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        scene.attachChild(modetwoSprite);
        restartSprite = new Sprite(CAMERA_WIDTH / 2 - restart.getWidth() / 2, modeSprite.getY() + modeSprite.getHeight() + 8f, restart, this.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_UP:
                        Intent i = new Intent(MenuActivity.this, GameActivity.class);
                        i.putExtra("Mode2", mode2);
                        startActivity(i);
                        finish();
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        scene.registerTouchArea(restartSprite);
        scene.attachChild(restartSprite);
        rateSprite = new Sprite(CAMERA_WIDTH / 2 - rate.getWidth() / 2, restartSprite.getY() + restartSprite.getHeight() + 8f, rate, this.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_UP:
                        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                        }
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        scene.attachChild(rateSprite);
        scene.registerTouchArea(rateSprite);
        extralifeSprite = new Sprite(CAMERA_WIDTH / 2 - extralife.getWidth() / 2, rateSprite.getY() + rateSprite.getHeight() + 8f, extralife, this.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_UP:
                        if (ad.isReady()) {
                            ad.show();
                            //AdColony.resume(MenuActivity.this);
                        }
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        AdColonyV4VCListener listener = new AdColonyV4VCListener() {
            public void onAdColonyV4VCReward(AdColonyV4VCReward reward) {
                if (reward.success()) {
                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                    prefs.edit().putInt("lives", prefs.getInt("lives", 0) + 1).apply();
                    ad = new AdColonyV4VCAd();
                }
            }
        };

        AdColony.addV4VCListener(listener);
        scoreSprite = new Sprite(108f, 75f, score, this.getVertexBufferObjectManager());
        scene.attachChild(scoreSprite);
        highscoreSprite = new Sprite(267f, 75f, highscore, this.getVertexBufferObjectManager());
        scene.attachChild(highscoreSprite);
        scene.registerTouchArea(extralifeSprite);
        scene.attachChild(extralifeSprite);
        dotSprite = new Sprite(CAMERA_WIDTH / 2 - dot.getWidth() / 2, extralifeSprite.getY() + extralifeSprite.getHeight() + 50f, dot, this.getVertexBufferObjectManager());
        randomColor();
        scene.attachChild(dotSprite);
        scoreText = new Text(scoreSprite.getX() + scoreSprite.getWidth() / 2, 105f, scoreFnt, "Score: ", this.getVertexBufferObjectManager());
        scoreText.setText(String.valueOf(getIntent().getIntExtra("score", 0)));
        scoreText.setX(scoreSprite.getX() + scoreSprite.getWidth() / 2 - scoreText.getWidth() / 2);
        scene.attachChild(scoreText);
        highScoreText = new Text(highscoreSprite.getX() + highscoreSprite.getWidth() / 2, 105f, scoreFnt, "Score: ", this.getVertexBufferObjectManager());
        scene.attachChild(highScoreText);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if (!mode2) {
            modetwoSprite.setVisible(false);
            scene.registerTouchArea(modeSprite);
            highScoreText.setText(String.valueOf(prefs.getInt("modescore", 0)));
            highScoreText.setX(highscoreSprite.getX() + highscoreSprite.getWidth() / 2 - highScoreText.getWidth() / 2);
        } else {
            modeSprite.setVisible(false);
            scene.registerTouchArea(modetwoSprite);
            highScoreText.setText(String.valueOf(prefs.getInt("mode2score", 0)));
            highScoreText.setX(highscoreSprite.getX() + highscoreSprite.getWidth() / 2 - highScoreText.getWidth() / 2);
        }
        SPEED = 0;
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    Color[] colors = new Color[]{
            new Color(0, 252f / 255, 255f / 255), //aqua
            new Color(0f, 102f / 255, 255f / 255), //blue
            new Color(68f / 255, 0f, 183f / 255), //purple
            new Color(255f / 255, 0f, 234f / 255), //violet
            new Color(234f / 255, 0f, 0f), //red
            new Color(255f / 255, 168f / 255, 0f), //orange
            new Color(255f / 255, 240f / 255, 0f), //yellow
            new Color(138f / 255, 234f / 255, 0f), //green
    };
    //will never be aqua to begin
    Color currentColor = colors[0];

    private void randomColor() {
        Color c = colors[randomGenerater.nextInt(7)];
        while (c == currentColor) {
            c = colors[randomGenerater.nextInt(7)];
        }
        currentColor = c;
        dotSprite.setColor(c);
        //TODO: transition color animation
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }
}