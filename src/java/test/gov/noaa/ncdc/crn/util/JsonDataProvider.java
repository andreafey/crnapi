package gov.noaa.ncdc.crn.util;

import static gov.noaa.ncdc.crn.domain.CrnDomains.ELEMENT_ID;
import gov.noaa.ncdc.crn.domain.Element;
import gov.noaa.ncdc.crn.domain.Station;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;

public class JsonDataProvider {
	final static String testDataDir = "src/resources/test/data";
	final static File stationsJson = new File(testDataDir,"stations/stations.json");
	final static File elementsJson = new File(testDataDir,"elements/elements.json");
	
	private static Map<Integer,Station> stations = null;
	private static Map<String,Element> elementsByName = null;
	private static Map<Integer,Element> elements = null;
	
	public static Map<Integer,Station> getStations() throws IOException {
		if (stations==null) {
			String json = FileUtils.readWholeFile(stationsJson.getPath());
			stations = JsonUtils.stationMapFromJson(json);
		}
		return stations;
	}
	public static Map<String,Element> getElementsByName() throws IOException {
		if (elementsByName==null) {
			String json = FileUtils.readWholeFile(elementsJson.getPath());
			elementsByName = JsonUtils.elementNameMapFromJson(json);
		}
		return elementsByName;
	}
	public static Map<Integer,Element> getElements() throws IOException {
		if (elements==null) {
			Map<String,Element> elsByName = getElementsByName();
			elements = Maps.uniqueIndex(elsByName.values(), ELEMENT_ID);
		}
		return elements;
	}

}
