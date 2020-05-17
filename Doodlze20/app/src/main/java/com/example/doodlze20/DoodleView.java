package com.example.doodlze20;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.provider.MediaStore;
import android.support.v4.print.PrintHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class DoodleView extends View {

    private static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap; // Область малювання
    private Canvas bitmapCanvas; // Для малювання на bitmap
    private final Paint paintScreen; // Для виводу bitmap
    private final Paint paintLine; // Для малювання ліній в bitmap

    // Дані намальованих контурів Path і точок,які в ній зберігаються
    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap = new HashMap<>();

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintScreen = new Paint();

        paintLine = new Paint();
        paintLine.setAntiAlias(true); // Згладжування
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE); // Суцільна лінія
        paintLine.setStrokeWidth(5);
        paintLine.setStrokeCap(Paint.Cap.SQUARE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    public void clear() {
        pathMap.clear();
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Зміна фону
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);

        // Для кожної лінії
        for (Integer key : pathMap.keySet()) {
            canvas.drawPath(pathMap.get(key), paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked(); // Тип події(натиснув, відпустив, тд...)
        int actionIndex = event.getActionIndex();

        if(action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex),
                    event.getPointerId(actionIndex));
        } else if(action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        }
        else {
            touchMoved(event);
        }

        invalidate();
        return true;
    }

    private void touchMoved(MotionEvent event) {
        for(int i = 0; i < event.getPointerCount(); i++) {
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);

            if(pathMap.containsKey(pointerID)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerID);
                Point point = previousPointMap.get(pointerID);

                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                if(deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    path.quadTo(point.x, point.y, (newX + point.x) / 2, (newY + point.y) / 2);

                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    private void touchStarted(float x, float y, int lineID) {
        Path path; // для зберігання контуру з заданим id
        Point point; // остання точка в контурі

        if(pathMap.containsKey(lineID)) {
            path = pathMap.get(lineID);
            path.reset();
            point = previousPointMap.get(lineID);
        }
        else {
            path = new Path();
            pathMap.put(lineID, path);
            point = new Point();
            previousPointMap.put(lineID, point);
        }

        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    private void touchEnded(int lineID) {
        Path path = pathMap.get(lineID);
        bitmapCanvas.drawPath(path, paintLine);
        path.reset();
    }

    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    public int getDrawingColor() {
        return paintLine.getColor();
    }

    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }

    public void saveImage() {
        final String name = "Doodlz" + System.currentTimeMillis() + ".jpg";
        String description = "Doodlz Drawing";

        String location = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                bitmap, name, description);

        if (location != null) {
            Toast.makeText(getContext(), R.string.message_saved, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getContext(),
                    R.string.message_error_saving, Toast.LENGTH_LONG).show();
        }
    }

    public void printImage() {
        if (PrintHelper.systemSupportsPrint()) {
            PrintHelper printHelper = new PrintHelper(getContext());

            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            printHelper.printBitmap("Doodlz Image", bitmap);
        }
        else {
            Toast.makeText(getContext(),
                    R.string.message_error_printing, Toast.LENGTH_SHORT).show();
        }
    }
}