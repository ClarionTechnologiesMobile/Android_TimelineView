package trueparallel.timeline.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Narendrasinh Dodiya on 4/19/2017.
 */

public class TimelineView extends View {

    private static final String TAG = TimelineView.class.getSimpleName();

    private String backgroundColor = "#FFFFFF";

    private String selectionAnchorColor = "#304ffe";
    private String axisTextColor = "#000000";

    private int axisTextSize = 40;
    private int axisValueRightPadding = 20;

    private Rect mDrawingAreaRect;
    private Rect mChartAreaRect;

    private float lastTouchX = -1;
    private float lastTouchY = -1;

    private List<EventModel> eventModels;
    private List<Rect> sleepEventRect;
    private Map<Rect, EventModel> eventMap;
    private OnEventClickListener mOnEventClickListener;
    private DisplayMetrics displaymetrics;


    public TimelineView(Context context) {
        super(context);
        init();
    }

    public TimelineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimelineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * initializes fields
     */
    private void init(){
        eventModels = new ArrayList<>();
        eventMap = new HashMap<>();
        displaymetrics = new DisplayMetrics();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        WindowManager w = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        w.getDefaultDisplay().getMetrics(displaymetrics);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                width = displaymetrics.widthPixels;
                break;

        }

        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                height = displaymetrics.heightPixels;
                break;

        }
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        render(canvas);
    }

    /**
     * draws on the Canvas
     * @param canvas
     */
    private void render(Canvas canvas){
        setBackgroundColor(Color.parseColor(backgroundColor));

        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mDrawingAreaRect = new Rect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());

        Rect axisValueBounds = getAxisTextBounds("23 AM", mPaint, axisTextSize);
        int chartRectLeftStart = axisValueRightPadding + axisValueBounds.width();
        int chartRectTopStart = (axisValueBounds.height() / 2);
        int chartRectBottomEnd = axisValueBounds.height() / 2;

        mChartAreaRect = new Rect(mDrawingAreaRect.left + chartRectLeftStart, mDrawingAreaRect.top + chartRectTopStart , mDrawingAreaRect.right, mDrawingAreaRect.bottom - chartRectBottomEnd);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.WHITE);


        int chartHeight = mChartAreaRect.height();
        mPaint.setColor(Color.WHITE);

        Calendar mStartCal = TimeLineHelper.getStartTime(eventModels);
        mStartCal.set(Calendar.MINUTE, 0);

        Calendar mEndCal = Calendar.getInstance();
        if(eventModels.size() > 0){
            EventModel lastEventModel = eventModels.get(eventModels.size() - 1);
            mEndCal.setTimeInMillis(lastEventModel.getEndTime());
            mEndCal.set(Calendar.MINUTE, 0);
            mEndCal.add(Calendar.HOUR_OF_DAY, 1);
        } else {
            mEndCal.add(Calendar.HOUR_OF_DAY, 8);
        }

        long seconds = (mEndCal.getTimeInMillis() - mStartCal.getTimeInMillis()) / 1000;
        int sleepDuration = (int) (seconds / 3600);

        mPaint.setTextSize(axisTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.parseColor(axisTextColor));

        //Draw Axis values
        int counter = 0;
        while(mStartCal.compareTo(mEndCal) <= 0){
            drawAxisTimeValue(canvas, mPaint, getNiceHour(mStartCal), mChartAreaRect.left - axisValueRightPadding, (mChartAreaRect.top  + (counter  * chartHeight / sleepDuration)));
            mStartCal.add(Calendar.HOUR_OF_DAY, 1);
            counter++;
        }

        // start of lines drawing
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(1);
        mStartCal = TimeLineHelper.getStartTime(eventModels);
        mStartCal.set(Calendar.MINUTE, 0);

        counter = 0;
        while(mStartCal.compareTo(mEndCal) <= 0){
            int y = (int)(mChartAreaRect.top  + (counter  * chartHeight / sleepDuration));
            canvas.drawLine( mChartAreaRect.left, y  ,mChartAreaRect.right, y, mPaint);
            mStartCal.add(Calendar.HOUR_OF_DAY, 1);
            counter++;
        }
        //end of lines drawing

        sleepEventRect = new ArrayList<>();

        mStartCal = TimeLineHelper.getStartTime(eventModels);
        mStartCal.set(Calendar.MINUTE, 0);

        //set horizontal bar colors
        for(EventModel eventModel : eventModels){
            Calendar mSessionStartCal = Calendar.getInstance();
            mSessionStartCal.setTimeInMillis(eventModel.getStartTime());


            Calendar mSessionEndCal = Calendar.getInstance();
            mSessionEndCal.setTimeInMillis(eventModel.getEndTime());


            long start = getDiffInMinutes(mStartCal, mSessionStartCal);
            long end = getDiffInMinutes(mStartCal, mSessionEndCal);

            int paintStart = mChartAreaRect.top + (int)(start * chartHeight / (sleepDuration * 60));
            int paintEnd = mChartAreaRect.top + (int)(end * chartHeight / (sleepDuration * 60));

            Rect r = new Rect(mChartAreaRect.left + 20, paintStart, mChartAreaRect.right, paintEnd);
            sleepEventRect.add(r);
            eventMap.put(r, eventModel);
            switch (eventModel.getEventType()){
                case MEETING:
                    mPaint.setColor(Color.parseColor("#058CCE"));
                    break;
                case TODO:
                    mPaint.setColor(Color.parseColor("#BB3EA9"));
                    break;
                case REMINDER:
                    mPaint.setColor(Color.parseColor("#d84315"));
                    break;
                case OTHER:
                    mPaint.setColor(Color.parseColor("#78909c"));
                    break;
            }

            canvas.drawRect(r, mPaint);
            mPaint.setColor(Color.WHITE);
            mPaint.setTextAlign(Paint.Align.LEFT);
            drawBottomAlignedText(canvas, mPaint, eventModel.getTitle(), r.left + 20, r.top + 20);
        }

        if(lastTouchX == -1 && lastTouchY == -1){
            return;
        }


        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.parseColor(selectionAnchorColor));
        canvas.drawLine(mChartAreaRect.left, lastTouchY, mChartAreaRect.left + mChartAreaRect.width()+20, lastTouchY, mPaint);
        point(mChartAreaRect.left, lastTouchY, canvas, mPaint);
    }

    /**
     * returns difference between two Calendar objects in minutes
     * @param mStartCal
     * @param mEndCal
     * @return
     */
    private long getDiffInMinutes(Calendar mStartCal, Calendar mEndCal){
        long seconds = (mEndCal.getTimeInMillis() - mStartCal.getTimeInMillis()) / 1000;
        int sleepDurationInMinutes = (int) (seconds / 60);
        return Math.abs(sleepDurationInMinutes);
    }


    /**
     * draws point on Canvas
     * @param x
     * @param y
     * @param canvas
     * @param mPaint
     */
    public void point(float x,float y,Canvas canvas,Paint mPaint)
    {
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, 20, mPaint);
    }

    private Rect drawCenteredText(Canvas canvas, Paint mPaint, String text, int x, int y){
        Rect textBounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, x - textBounds.exactCenterX(), y - textBounds.exactCenterY(), mPaint);
        return textBounds;
    }

    private Rect drawAxisTimeValue(Canvas canvas, Paint mPaint, String text, int x, int y){
        Rect textBounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, x - textBounds.exactCenterX(), y - textBounds.exactCenterY(), mPaint);
        return textBounds;
    }

    private void drawBottomAlignedText(Canvas canvas, Paint mPaint, String text, int x, int y){
        Rect textBounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, x , y + textBounds.height(), mPaint);
    }

    private void drawTopAlignedText(Canvas canvas, Paint mPaint, String text, int x, int y){
        Rect textBounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, x , y - textBounds.height(), mPaint);
    }


    private Rect drawHorizontalLeftAlignedText(Canvas canvas, Paint mPaint, String text, int x, int y){
        Rect textBounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, x , y - textBounds.exactCenterY(), mPaint);
        return textBounds;
    }


    private Rect drawHorizontalRightAlignedText(Canvas canvas, Paint mPaint, String text, int x, int y){
        Rect textBounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, x - textBounds.width(), y - textBounds.exactCenterY(), mPaint);
        return textBounds;
    }


    /**
     * handles all touch events
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        lastTouchX = event.getX();
        lastTouchY = event.getY();

        switch(action){
            case MotionEvent.ACTION_DOWN:
                if(mChartAreaRect.contains((int)lastTouchX, (int)lastTouchY)){
//                    Toast.makeText(getContext(), "inside", Toast.LENGTH_SHORT).show();
                    detectEventRect(lastTouchX, lastTouchY);
                    invalidate();
                } else {
//                    Toast.makeText(getContext(), "outside", Toast.LENGTH_SHORT).show();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(mChartAreaRect.contains((int)lastTouchX, (int)lastTouchY)){
                    invalidate();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * rect based approach for detecting touch event
     * @param x
     * @param y
     */
    private void detectEventRect(float x, float y){
        for(Rect r : sleepEventRect){
            if(r.contains((int)x, (int)y)){
                EventModel e = eventMap.get(r);
                if(mOnEventClickListener != null){
                    mOnEventClickListener.onEventClick(e);
                }
            }
        }
    }

    /**
     * allows to set event models data
     * @param eventModels
     */
    public void setSessionData(List<EventModel> eventModels){
        this.eventModels.clear();
        this.eventModels.addAll(eventModels);
        invalidate();
    }

    /**
     * allows to set event click listener, notifies when tap on event occurs
     * @param listener
     */
    public void setOnEventClickListener(OnEventClickListener listener){
        this.mOnEventClickListener = listener;
    }

    /**
     * returns human readable text for hours
     * @param mStartCal
     * @return
     */
    private String getNiceHour(Calendar mStartCal){
        int hour = mStartCal.get(Calendar.HOUR_OF_DAY);

        String amPm;
        if(hour == 0){
            hour = 12;
            amPm = "AM";
        } else if(hour == 12){
            amPm = "PM";
        } else if(hour > 12){
            hour = hour - 12;
            amPm = "PM";
        } else {
            amPm = "AM";
        }
        return new StringBuilder().append(hour).append(" ").append(amPm).toString();
    }

    /**
     * returns size occupied by text on Canvas
     * @param text
     * @param mPaint
     * @param textSize
     * @return
     */
    private Rect getAxisTextBounds(String text, Paint mPaint, int textSize){
        Rect textBounds = new Rect();
        mPaint.setTextSize(textSize);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.getTextBounds(text, 0, text.length(), textBounds);
        return textBounds;
    }

    public void setSelectionAnchorColor(String selectionBarColor){
        this.selectionAnchorColor = selectionBarColor;
    }

    public void setAxisLabelsTextSize(int size){
        axisTextSize = size;
    }

    public void setBackgroundColor(String bgColor){
        this.backgroundColor = bgColor;
    }

    private void setAxisTextColor(String color){
        this.axisTextColor = color;
    }

}

