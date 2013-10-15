package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StationName implements Serializable {
    private final String state;
    private final String location;
    private final String vector;

    public StationName(final String state, final String location, final String vector) {
        this.state = state;
        this.location = location;
        this.vector = vector;
    }

    public String getState() {
        return state;
    }

    public String getLocation() {
        return location;
    }

    public String getVector() {
        return vector;
    }

    public String toString() {
        return state + " " + location + " " + vector;
    }
}
