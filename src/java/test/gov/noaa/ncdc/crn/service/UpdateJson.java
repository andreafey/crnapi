package gov.noaa.ncdc.crn.service;

import gov.noaa.ncdc.crn.domain.Element;
import gov.noaa.ncdc.crn.domain.Station;
import gov.noaa.ncdc.crn.spring.ApplicationContextProvider.Contexts;
import gov.noaa.ncdc.crn.util.JsonUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.springframework.context.ApplicationContext;

public class UpdateJson {

	final static String testDataDir = "src/resources/test/data";
	/**
	 * Run this to update supported metadata pulls into json from unit test db
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ApplicationContext context  = Contexts.UNIT.getApplicationContext();
//		Gson gson = JsonUtils.gson;

		updateStations(context.getBean(StationService.class));
		updateElements(context.getBean(ElementService.class));

	}

	private static void updateStations(StationService service) throws IOException {
		Map<Integer,Station> stations = service.getStations();
		String json = JsonUtils.stationMapToJson(stations);
		File file = new File(testDataDir, "/stations/stations.json");
		BufferedWriter out = new BufferedWriter(new FileWriter(file), 32768);
		out.write(json);
		out.close();
	}
	private static void updateElements(ElementService service) throws IOException {
		Map<String,Element> elements = service.getElementsByName();
		String json = JsonUtils.elementMapToJson(elements);
		File file = new File(testDataDir, "/elements/elements.json");
		BufferedWriter out = new BufferedWriter(new FileWriter(file), 32768);
		out.write(json);
		out.close();
	}

}
