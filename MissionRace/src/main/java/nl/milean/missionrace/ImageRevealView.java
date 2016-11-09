package nl.milean.missionrace;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Tchakkazulu on 28/12/2015.
 */
public class ImageRevealView extends View {

    private int tilesW;
    private int tilesH;
    private boolean[][] revealed;
    private Drawable img;

    public ImageRevealView(Context context) { this(context, 4, 3); }

    public ImageRevealView(Context context, int tilesW, int tilesH) { super(context); createSelf(context, tilesW, tilesH);}

    public ImageRevealView(Context context, AttributeSet attrs) {
        this(context, attrs, 4, 3);
    }

    public ImageRevealView(Context context, AttributeSet attrs, int tilesW, int tilesH) {
        super(context, attrs);
        createSelf(context, tilesW, tilesH);
    }

    private void createSelf(Context context, int tilesW, int tilesH) {

        this.tilesW = tilesW;
        this.tilesH = tilesH;

        setBackgroundColor(Color.TRANSPARENT);
        setWillNotDraw(false);

        revealed = new boolean[tilesH][tilesW];

        for (int row = 0; row < tilesH; row++) {
            for (int col = 0; col < tilesW; col++) {
                revealed[row][col] = false;
            }
        }
    }

    public boolean[][] getMask() {
        return revealed;
    }

    public void setMask(boolean[][] mask) {
        this.revealed = mask;
        postInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.print("Haantje");

        canvas.drawColor(Color.TRANSPARENT);

        Paint black = new Paint();
        black.setColor(Color.BLACK);

        Rect canvasRect = canvas.getClipBounds();

        for (int row = 0; row < tilesH; row++) {
            for (int col = 0; col < tilesW; col++) {
                if (!revealed[row][col]) {
                    int myLeft = canvasRect.left + (col * canvasRect.width()) / tilesW;
                    int myRight = canvasRect.left + ((col + 1) * canvasRect.width()) / tilesW;
                    int myTop = canvasRect.top + (row * canvasRect.height()) / tilesH;
                    int myBot = canvasRect.top + ((row+1) * canvasRect.height()) / tilesH;
                    Rect myRect = new Rect(myLeft, myTop, myRight, myBot);
                    canvas.drawRect(myRect, black);
                }
            }
        }
    }

    public void reveal(int row, int col) {
        revealed[row][col] = true;
        postInvalidate();
    }

}
