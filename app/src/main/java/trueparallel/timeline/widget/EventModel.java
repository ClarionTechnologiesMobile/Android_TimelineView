package trueparallel.timeline.widget;

/**
 * Created by Narendrasinh Dodiya on 4/25/2017.
 */

public class EventModel {
    public enum Type {MEETING, REMINDER, TODO, OTHER}

    private long startTime;
    private long endTime;
    private Type eventType;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EventModel(long start, long end, Type t, String title) {
        this.startTime = start;
        this.endTime = end;
        this.eventType = t;
        this.title = title;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Type getEventType() {
        return eventType;
    }

    public void setEventType(Type eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "start: "+getStartTime()+" end: "+getEndTime();
    }
}

