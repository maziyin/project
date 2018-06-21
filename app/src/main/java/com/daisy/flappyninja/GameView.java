package com.daisy.flappyninja;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private float measuredWidth;
    private float measuredHeight;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private Bitmap bitmap;
    private Bitmap bitmap2;

    // The colors
    private  int colorTarget = Color.parseColor("#C75B39");

    // The current score
    private int score = 0;

    public int getScore() {
        return score;
    }

    // For the bird
    private float positionX = 0.0f;
    private float positionY = 0.0f;
    private float velocityX = 0.0f;
    private float velocityY = 0.0f;
    private float accelerationX = 0.0f;
    private float accelerationY = 0.7f;

    // For the targets
    private int iteratorInt = 0;
    private static final int interval = 150;
    private static final float gap = 150.0f;
    private static final float base = 100.0f;
    private float targetWidth = 200.0f;
    private List<Target> targetList;
    private static final float targetVelocity = 3.0f;

    public GameView(Context context) {
        super(context);

        // Initialize
        init();
    }

    public GameView(Context context, AttributeSet a) {
        super(context, a);

        // Initialize
        init();
    }

    public GameView(Context context, AttributeSet a, int b) {
        super(context, a, b);

        // Initialize
        init();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void init() {
        /* Initializes */

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        paint = new Paint();
        paint.setAntiAlias(true);

        // For the bird
        bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_ninja);
        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false);

        // For the targets
        targetList = new ArrayList<Target>();

//        setFocusable(true);
        setKeepScreenOn(true);
    }

    public void update() {
        /* Updates the UI */

        paint.setStyle(Paint.Style.FILL);

        Canvas canvas = surfaceHolder.lockCanvas();

        // Clear the canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // Draw the bird
        canvas.drawBitmap(bitmap, positionX - 100.0f / 2.0f, positionY - 100.0f / 2.0f, null);

        // Draw the targets
        paint.setColor(colorTarget);
        List<Integer> removeList = new ArrayList<Integer>();
        int size = targetList.size();
        for (int index = 0; index < size; index++) {
            Target target = targetList.get(index);
            if (isTargetOut(target)) {
                removeList.add(index);
            } else {
                /*// Draw the upper part of the target
                canvas.drawRect(target.getPositionX() - targetWidth / 2.0f,
                        0.0f,
                        target.getPositionX() + targetWidth / 2.0f,
                        measuredHeight - target.getHeight() - gap,
                        paint);

                // Draw the lower part of the target
                canvas.drawRect(target.getPositionX() - targetWidth / 2.0f,
                        measuredHeight - target.getHeight(),
                        target.getPositionX() + targetWidth / 2.0f,
                        measuredHeight,
                        paint);*/
                //Draw the targets

                if(target.isBomb()) {
                    bitmap2 = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_bomb);
                    canvas.drawBitmap(bitmap2, target.getPositionX() - targetWidth / 2.0f, target.getHeight() - targetWidth / 2.0f, null);
                }else if(target.getStuff()>5) {
                    bitmap2 = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_apple);
                    canvas.drawBitmap(bitmap2, target.getPositionX() - targetWidth / 2.0f, target.getHeight() - targetWidth / 2.0f, null);
                }
                else if(target.getStuff()>3) {
                    bitmap2 = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_kivi);
                    canvas.drawBitmap(bitmap2, target.getPositionX() - targetWidth / 2.0f, target.getHeight() - targetWidth / 2.0f, null);
                }
                else {
                    bitmap2 = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_watermelon);
                    canvas.drawBitmap(bitmap2, target.getPositionX() - targetWidth / 2.0f, target.getHeight() - targetWidth / 2.0f, null);
                }
            }
        }
        removeItemsFromTargetList(removeList);

        surfaceHolder.unlockCanvasAndPost(canvas);

        // Update the data for the bird
        positionX += velocityX;
        positionY += velocityY;
        velocityX += accelerationX;
        velocityY += accelerationY;

        // Update the data for the targets
        for (Target target : targetList) {
            target.setPositionX(target.getPositionX() - targetVelocity);
        }
        if (iteratorInt == interval) {
            addTarget();
            iteratorInt = 0;
        } else {
            iteratorInt++;
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Get the measured size of the view
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();

        // Set the initial position
        setPosition(measuredWidth / 2.0f, measuredHeight / 2.0f);

        // Add the initial Target
        addTarget();
    }

    public void jump() {
        velocityY = -13.0f;
    }

    public void setPosition(float positionX, float positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public boolean isAlive() {
        /* Checks if the bird is still alive */

        // Check if the bird hits the Targets
        for (Target target : targetList) {
            if ((target.getPositionX() >= measuredWidth / 2.0f - targetWidth / 2.0f - 5.0f / 2.0f) &&
                    (target.getPositionX() <= measuredWidth / 2.0f + targetWidth / 2.0f + 120.0f / 2.0f)) {
                if ((positionY >= target.getHeight() - targetWidth / 2.0f) &&
                        (positionY <= target.getHeight() + targetWidth / 2.0f)) {
                    if(target.isBomb())
                        return false;
                    else{
                        target.setPositionX(-targetWidth - 1.0f);
                        score++;
                        // Update the score in MainActivity
                        Context context = getContext();
                        if (context instanceof GameActivity) {
                            ((GameActivity) context).updateScore(score);
                            ((GameActivity) context).playScoreMusic();
                        }
                    } /*else {
                            if (target.getPositionX() - targetVelocity <
                            measuredWidth / 2.0f - targetWidth / 2.0f - 100.0f / 2.0f) {

                        }
                    }*/
                }
            }
        }

        // Check if the bird goes beyond the border
        if ((positionY < 0.0f + 100.0f / 2.0f) || (positionY > measuredHeight - 100.0f / 2.0f)) {
            return false;
        }

        return true;
    }

    private boolean isTargetOut(Target target) {
        /* Checks if the target is out of the screen */

        if (target.getPositionX() + targetWidth / 2.0f < 0.0f) {
            return true;
        } else {
            return false;
        }
    }

    private void removeItemsFromTargetList(List<Integer> removeList) {
        /* Removes all the items at the indices specified by removeList */

        List newList = new ArrayList();
        int size = targetList.size();
        for (int index = 0; index < size; index++) {
            if (!removeList.remove(new Integer(index))) {
                newList.add(targetList.get(index));
            }
        }

        targetList = newList;
    }

    public void resetData() {
        /* Resets all the data of the over game */

        // For the bird
        positionX = 0.0f;
        positionY = 0.0f;
        velocityX = 0.0f;
        velocityY = 0.0f;
        accelerationX = 0.0f;
        accelerationY = 0.7f;

        // For the targets
        iteratorInt = 0;
        targetList = new ArrayList<Target>();

        score = 0;

        // Set the initial position
        setPosition(measuredWidth / 2.0f, measuredHeight / 2.0f);

        // Add the initial target
        addTarget();
    }

    private void addTarget() {
        /* Adds a target into the list of targets */

        targetList.add(new Target(measuredWidth + targetWidth / 2.0f,
                base + (measuredHeight - 2 * base - gap) * new Random().nextFloat(),new Random().nextInt(10)));
    }
}
