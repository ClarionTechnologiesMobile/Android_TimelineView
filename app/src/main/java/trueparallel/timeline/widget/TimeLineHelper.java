package trueparallel.timeline.widget;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Narendrasinh Dodiya on 5/11/2017.
 */

public class TimeLineHelper {

    public static Calendar getStartTime(List<EventModel> eventModelList){
        Calendar mCal = Calendar.getInstance();
        if(eventModelList != null && eventModelList.size() > 0){
            mCal.setTimeInMillis(eventModelList.get(0).getStartTime());
            return mCal;
        }
        return mCal;
    }

    public static Calendar getEndTime(){
        Calendar mCal = Calendar.getInstance();
        mCal.set(2017, 5, 11, 8, 0, 0);
        return mCal;
    }

    public static Calendar getCalendarFromTimeStamp(long timeInMills){
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(timeInMills);
        return  mCalendar;
    }
}
