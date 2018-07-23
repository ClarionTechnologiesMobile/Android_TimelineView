package trueparallel.timeline.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import trueparallel.timeline.R;
import trueparallel.timeline.widget.EventModel;
import trueparallel.timeline.widget.OnEventClickListener;
import trueparallel.timeline.widget.TimelineView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    List<EventModel> eventModeList;
    TimelineView mTimelineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimelineView = (TimelineView) findViewById(R.id.timelineview);
        eventModeList = new ArrayList<>();

        addDummyData();
        mTimelineView.setSessionData(eventModeList);
        mTimelineView.setOnEventClickListener(new OnEventClickListener(){

            @Override
            public void onEventClick(EventModel event) {
                Log.i(TAG, "event is:"+event.toString());
            }
        });
    }

    /**
     * adds dummy event data
     */
    private void addDummyData() {
        Random rand = new Random();
        Calendar mStartCal = Calendar.getInstance();
        mStartCal.set(2017, 5, 10, 10, 0, 0);

        Calendar mEndCal = Calendar.getInstance();
        mEndCal.set(2017, 5, 10, 19, 0, 0);

        int max = 59;
        int min = 15;

        for (int i = 0; i < 5; i++) {
            int ranMinutes = rand.nextInt((max - min) + 1) + min;
            mStartCal.add(Calendar.MINUTE, ranMinutes);

            if (mEndCal.before(mStartCal)) {
                break;
            }

            long start = mStartCal.getTimeInMillis();

            ranMinutes = rand.nextInt((max - min) + 1) + min;
            mStartCal.add(Calendar.MINUTE, ranMinutes);

            long end = mStartCal.getTimeInMillis();


            int randEvent = rand.nextInt((4 - 1) + 1) + 1;
            EventModel.Type type;
            switch (randEvent){
                case 1:
                    type = EventModel.Type.MEETING;
                    break;
                case 2:
                    type = EventModel.Type.TODO;
                    break;
                case 3:
                    type = EventModel.Type.REMINDER;
                    break;
                default:
                    type = EventModel.Type.OTHER;
                    break;
            }
            EventModel eventModel = new EventModel(start, end, type, " Title "+i);
            eventModeList.add(eventModel);
        }
    }

}
