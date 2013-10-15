package gov.noaa.ncdc.crn.util;

import gov.noaa.ncdc.crn.domain.Element;
import gov.noaa.ncdc.crn.domain.Station;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {

    public final static Gson GSON = new Gson();
    public final static Type STATION_MAP_TYPE = new TypeToken<Map<Integer, Station>>() {
    }.getType();
    public final static Type ELEMENT_MAP_TYPE = new TypeToken<Map<String, Element>>() {
    }.getType();

    public static String stationMapToJson(Map<Integer, Station> stationMap) {
        return GSON.toJson(stationMap, STATION_MAP_TYPE);
    }

    public static Map<Integer, Station> stationMapFromJson(String json) {
        return GSON.fromJson(json, STATION_MAP_TYPE);
    }

    public static String elementMapToJson(Map<String, Element> elements) {
        return GSON.toJson(elements, ELEMENT_MAP_TYPE);
    }

    public static Map<String, Element> elementNameMapFromJson(String json) {
        return GSON.fromJson(json, ELEMENT_MAP_TYPE);
    }
}
