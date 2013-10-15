package gov.noaa.ncdc.crn.dao;

import static org.junit.Assert.assertEquals;
import gov.noaa.ncdc.crn.spring.ApplicationContextProvider.Contexts;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class DataImporter {

	/**
	 * Imports data from the production database to the unit test database.
	 * Not for large scale data imports; more for adding a few rows to the unit 
	 * test database for testing.
	 */
	final JdbcTemplate sourceTemplate;
	final DataSource sinkDataSource;
	
	/**
	 * Contexts must be registered read/write database contexts and not the 
	 * production context. 
	 * @param source
	 * @param sink
	 */
	public DataImporter (Contexts source, Contexts sink)
	{
		try {
			assert(sink==Contexts.UNIT);
		} catch (Exception e) {
			throw new RuntimeException("Unsupported server for updates; don't use this on production.");
		}
		DataSource sourceDataSource = source.getApplicationContext().getBean(DataSource.class);
		sourceTemplate = new JdbcTemplate(sourceDataSource);
		sinkDataSource =
				sink.getApplicationContext().getBean(DataSource.class);
	}
	
	/**
	 * Imports datetimes from the source database into the sink database. Note
	 * queries and inserts one at a time.
	 * NOTE: Renamed table/col names info for publication
	 * @param datetimeIds The datetimes to insert
	 */
	public void importDatetimes(Integer... datetimeIds)
	{
    	SimpleJdbcInsert sinkDatetimeInsert =
    			new SimpleJdbcInsert(sinkDataSource).withTableName("HIDDEN");
    	Map<String,Object> params = new HashMap<>();
		for (Integer datetimeId : datetimeIds) {
			Map<String,Object> sourceDatetime = 
					sourceTemplate.queryForMap(
							"select * from TABLE1 where id=?",
							datetimeId);
			params.clear();
	    	params.put("ID", sourceDatetime.get("ID"));
	    	params.put("COL1", sourceDatetime.get("COL1"));
	    	params.put("COL2", sourceDatetime.get("COL2"));
	    	params.put("COL3", sourceDatetime.get("COL3"));
	    	params.put("COL4", sourceDatetime.get("COL4"));
	    	params.put("COL5", sourceDatetime.get("COL5"));
	    	int rows = sinkDatetimeInsert.execute(params);
	    	assertEquals(1,rows);
		}
	}
	
	/**
	 * Imports stations from the source database into the sink database. Note
	 * queries and inserts one at a time.
	 * NOTE: REMOVED database info for publication
	 * @param stationIds The stations to insert
	 */
	public void importStations(Integer... stationIds)
	{
		// first update stations
    	SimpleJdbcInsert sinkStationInsert =
    			new SimpleJdbcInsert(sinkDataSource).withTableName("TABLE1");
    	SimpleJdbcInsert sinkStationDataInsert =
    			new SimpleJdbcInsert(sinkDataSource).withTableName("TABLE2");

		for (Integer stationId : stationIds) {
			Map<String,Object> sourceStation = sourceTemplate.queryForMap(
							"select * from TABLE2 where id=?",
							stationId);
			
	    	// insert new station into TABLE1 and then TABLE2
			
	    	Map<String,Object> params = new HashMap<>();
	    	
			// first insert table1
	    	params.put("ID", sourceStation.get("ID"));
	    	int rows = sinkStationInsert.execute(params);
	    	assertEquals(1,rows);
	    	
	    	// then table2; keep param map
	    	params.put("ID2", sourceStation.get("ID2"));
	    	params.put("ID3", sourceStation.get("ID3"));
	    	params.put("ID4", sourceStation.get("ID4"));
	    	params.put("ID5", sourceStation.get("ID5"));
	    	params.put("NAME", sourceStation.get("NAME"));
	    	params.put("LONGITUDE", sourceStation.get("LONGITUDE"));
	    	params.put("LATITUDE", sourceStation.get("LATITUDE"));
	    	params.put("ELEVATION", sourceStation.get("ELEVATION"));
	    	params.put("TIMEZONE", sourceStation.get("TIMEZONE"));
	    	params.put("STATUS", sourceStation.get("STATUS"));
	    	params.put("REL_ID", sourceStation.get("REL_ID"));
	    	rows = sinkStationDataInsert.execute(params);
	    	assertEquals(1,rows);
		}
	}


}
