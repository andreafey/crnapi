package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * Represents a CRN data stream. Provides information about observation frequency, the number of temperature fans, and
 * what phenonmena and diagnostics the stream measures.
 * @author Andrea Fey
 */
@SuppressWarnings("serial")
public class Stream implements Comparable<Object>, Serializable {

    /** The id of the stream in the database */
    private final int streamId;
    /** How many times per hour are observations recorded? Either 4 for 15-minute streams or 12 for 5-minute streams */
    private final int subhourlyFrequency;
    /** How many elements are in this stream? */
    private final int elementCount;
    /**
     * Do stations using this stream measure temperature? All CRN data streams are expected to measure temperature and
     * precipitation.
     */
    private final Boolean measuresTemp;
    /**
     * Do stations using this stream measure precipitation? All CRN data streams are expected to measure temperature and
     * precipitation.
     */
    private final Boolean measuresPrecip;
    /**
     * The number of temperature fans installed at the stations using this stream. Either 3 for triple aspirated shield
     * configurations or 2 for a single shield
     */
    private final int tempFans;
    /** Do stations using this stream provide a time when the maximum and minimum temperature was observed? */
    private final Boolean measuresMaxTime;
    /** Do stations using this stream have a tipping bucket? */
    private final Boolean hasTipBucket;
    /** Do stations using this stream have a wetness sensor? */
    private final Boolean measuresWetness;
    /** Do stations using this stream transmit an hourly RH average? */
    private final Boolean measuresRhHrAvg;
    /** Do stations using this stream measure RH every 5 minutes? */
    private final Boolean measuresRh5Min;
    /** Do stations using this stream measure 1.5m wind speed? */
    private final Boolean measuresWind;
    /** Do stations using this stream measure 1.5m wind speed maximum and minimum? */
    private final Boolean measuresMaxWind;
    /** Do stations using this stream measure 10m wind speed every 5 minutes? */
    private final Boolean measures5MinWindAt10m;
    /** Do stations using this stream measure 1.5m wind speed every 5 minutes? */
    private final Boolean measures5MinWindAt1_5m;
    /** Do stations using this stream measure 6m wind speed every 5 minutes? */
    private final Boolean measures5MinWindAt6m;
    /** Do stations using this stream measure soil moisture? */
    private final Boolean measuresSoil;
    /** Do stations using this stream measure soil moisture at 100cm? */
    private final Boolean measuresSoil1m;
    /** Do stations using this stream measure soil moisture every 5 minutes at 5cm? */
    private final Boolean measuresSoil5MinAt5cm;
    /** Do stations using this stream measure solar radiation? */
    private final Boolean measuresSolarad;
    /** Do stations using this stream measure solar radiation every 5 minutes? */
    private final Boolean measuresSolarad5Min;
    /** Do stations using this stream measure solar radiation maximum? */
    private final Boolean measuresMaxSolarad;
    /** Do stations using this stream measure datalogger voltage? */
    private final Boolean measuresDataloggerVoltage;
    /** Do stations using this stream measure USRCRN-specific diagnostics? */
    private final Boolean measuresHcnmDiagnostics;
    /** Do stations using this stream measure hourly average IR? */
    private final Boolean measuresRawIrHr;
    /** Do stations using this stream measure the hourly average temperature of the IR sensor body? */
    private final Boolean measuresIrSensorTemp;
    /**
     * Do stations using this stream measure the hourly average temperature of the secondary IR sensor body?
     */
    private final Boolean measuresSecondaryIrSensorTemp;
    /** Do stations using this stream measure the surface temperature (IR) every 5 minutes? */
    private final Boolean measuresRawIr5Min;
    /** Do stations using this stream measure a corrected surface temperature (IR)? */
    private final Boolean measuresCorrectedIr5Min;
    /**
     * Do stations using this stream measure corrected surface temperature (IR) for a secondary sensor?
     */
    private final Boolean measuresSecondaryCorrectedIr5Min;
    /** Is the datalogger panel temperature recorded? */
    private final Boolean measuresPanelTemp;
    /** Is the secondary datalogger panel temperature recorded? */
    private final Boolean measuresSecondaryPanelTemp;
    /** are diagnostic temperatures, such as reference resistor average, measured? */
    private final Boolean measuresDiagnosticTemps;
    /**
     * are diagnostic temperatures, such as reference resistor average, measured for a second datalogger?
     */
    private final Boolean measuresSecondaryDiagnosticTemps;
    /** is there a -6666 marker in the last position of the hourly observation? */
    private final Boolean hasEndOfObMarker;
    /** is there a secondary transmitter (sending a near-identical observation)? */
    private final Boolean hasSecondaryTransmitter;

    public Stream(int streamId, int subhourlyFrequency, int elementCount, boolean temp, boolean precip,
            boolean wetness, int tempFans, boolean maxTime, boolean rhHrAvg, boolean rh5min, boolean wind,
            boolean wsMax, boolean wind5min10m, boolean wind5min1_5m, boolean wind5min6m, boolean soil,
            boolean soil_1m, boolean soil_5min_005, boolean solarad, boolean solrad_5min, boolean solrad_mx,
            boolean tip_bucket, boolean bv_dl, boolean hcnm_diag, boolean ir_sensor_temp, boolean ir_sensor_temp_2,
            boolean ir_raw_hr, boolean ir_raw_5min, boolean ir_calibrated, boolean ir_calibrated_2, boolean panel_temp,
            boolean panel_temp_2, boolean refresavg, boolean refresavg_2, boolean sec_tx, boolean end_mark) {
        this.streamId = streamId;
        this.subhourlyFrequency = subhourlyFrequency;
        this.elementCount = elementCount;
        this.measuresTemp = temp;
        this.measuresPrecip = precip;
        this.measuresWetness = wetness;
        this.tempFans = tempFans;
        this.measuresMaxTime = maxTime;
        this.measuresRhHrAvg = rhHrAvg;
        this.measuresRh5Min = rh5min;
        this.measuresWind = wind;
        this.measuresMaxWind = wsMax;
        this.measures5MinWindAt10m = wind5min10m;
        this.measures5MinWindAt1_5m = wind5min1_5m;
        this.measures5MinWindAt6m = wind5min6m;
        this.measuresSoil = soil;
        this.measuresSoil1m = soil_1m;
        this.measuresSoil5MinAt5cm = soil_5min_005;
        this.measuresSolarad = solarad;
        this.measuresSolarad5Min = solrad_5min;
        this.measuresMaxSolarad = solrad_mx;
        this.hasTipBucket = tip_bucket;
        this.measuresDataloggerVoltage = bv_dl;
        this.measuresHcnmDiagnostics = hcnm_diag;
        this.measuresIrSensorTemp = ir_sensor_temp;
        this.measuresSecondaryIrSensorTemp = ir_sensor_temp_2;
        this.measuresRawIrHr = ir_raw_hr;
        this.measuresRawIr5Min = ir_raw_5min;
        this.measuresCorrectedIr5Min = ir_calibrated;
        this.measuresSecondaryCorrectedIr5Min = ir_calibrated_2;
        this.measuresPanelTemp = panel_temp;
        this.measuresSecondaryPanelTemp = panel_temp_2;
        this.measuresDiagnosticTemps = refresavg;
        this.measuresSecondaryDiagnosticTemps = refresavg_2;
        this.hasSecondaryTransmitter = sec_tx;
        this.hasEndOfObMarker = end_mark;
    }

    public int getStreamId() {
        return streamId;
    }

    public int getSubhourlyFrequency() {
        return subhourlyFrequency;
    }

    public Boolean getMeasuresTemp() {
        return measuresTemp;
    }

    public Boolean getMeasuresPrecip() {
        return measuresPrecip;
    }

    public int getTempFans() {
        return tempFans;
    }

    public Boolean getMeasuresMaxTime() {
        return measuresMaxTime;
    }

    public Boolean getHasTipBucket() {
        return hasTipBucket;
    }

    public Boolean getMeasuresWetness() {
        return measuresWetness;
    }

    public Boolean getMeasuresRH() {
        return measuresRh5Min || measuresRhHrAvg;
    }

    public Boolean getMeasuresRh5Min() {
        return measuresRh5Min;
    }

    public Boolean getMeasuresRhHrAvg() {
        return measuresRhHrAvg;
    }

    public Boolean getMeasuresSoil() {
        return measuresSoil;
    }

    public Boolean getMeasuresSoil1m() {
        return measuresSoil1m;
    }

    public Boolean getMeasuresWind() {
        return measuresWind;
    }

    public Boolean getMeasuresMaxWind() {
        return measuresMaxWind;
    }

    public Boolean getMeasures5MinWindAt10m() {
        return measures5MinWindAt10m;
    }

    public Boolean getMeasures5MinWindAt1_5m() {
        return measures5MinWindAt1_5m;
    }

    public Boolean getMeasures5MinWindAt6m() {
        return measures5MinWindAt6m;
    }

    public Boolean getMeasuresSoil5MinAt5cm() {
        return measuresSoil5MinAt5cm;
    }

    public Boolean getMeasuresSolarad5Min() {
        return measuresSolarad5Min;
    }

    public boolean getMeasuresIr() {
        return measuresRawIrHr || measuresRawIr5Min || measuresCorrectedIr5Min;
    }

    public Boolean getMeasuresIr5Min() {
        return measuresRawIr5Min || measuresCorrectedIr5Min;
    }

    public Boolean getMeasuresRawIrHrAvg() {
        return measuresRawIrHr;
    }

    public Boolean getMeasuresRawIr5Min() {
        return measuresRawIr5Min;
    }

    public Boolean getMeasuresCorrectedIr5Min() {
        return measuresCorrectedIr5Min;
    }

    public Boolean getMeasuresSecondaryCorrectedIr5Min() {
        return measuresSecondaryCorrectedIr5Min;
    }

    public Boolean getMeasuresIrSensorTemp() {
        return measuresIrSensorTemp;
    }

    public Boolean getMeasuresSecondaryIrSensorTemp() {
        return measuresSecondaryIrSensorTemp;
    }

    public Boolean getMeasuresSolarad() {
        return measuresSolarad;
    }

    public Boolean getMeasuresMaxSolarad() {
        return measuresMaxSolarad;
    }

    public Boolean getMeasuresDataloggerVoltage() {
        return measuresDataloggerVoltage;
    }

    public Boolean getMeasuresHcnmDiagnostics() {
        return measuresHcnmDiagnostics;
    }

    public Boolean getMeasuresPanelTemp() {
        return measuresPanelTemp;
    }

    public Boolean getMeasuresSecondaryPanelTemp() {
        return measuresSecondaryPanelTemp;
    }

    public Boolean getMeasuresDiagnosticTemps() {
        return measuresDiagnosticTemps;
    }

    public Boolean getMeasuresSecondaryDiagnosticTemps() {
        return measuresSecondaryDiagnosticTemps;
    }

    public Boolean getHasEndOfObMarker() {
        return hasEndOfObMarker;
    }

    public Boolean getHasSecondaryTransmitter() {
        return hasSecondaryTransmitter;
    }

    public int getElementCount() {
        return elementCount;
    }

    @Override
    public int compareTo(Object o) {
        if (o != null && o instanceof Stream) {
            return this.streamId - ((Stream) o).streamId;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o instanceof Stream) {
            Stream s = (Stream) o;
            return ComparisonChain.start().compare(streamId, s.streamId)
                    .compare(subhourlyFrequency, s.subhourlyFrequency).compare(elementCount, s.elementCount)
                    .compare(measuresTemp, s.measuresTemp).compare(measuresPrecip, s.measuresPrecip)
                    .compare(tempFans, s.tempFans).compare(measuresMaxTime, s.measuresMaxTime)
                    .compare(hasTipBucket, s.hasTipBucket).compare(measuresWetness, s.measuresWetness)
                    .compare(measuresRhHrAvg, s.measuresRhHrAvg).compare(measuresRh5Min, s.measuresRh5Min)
                    .compare(measuresWind, s.measuresWind).compare(measuresMaxWind, s.measuresMaxWind)
                    .compare(measures5MinWindAt10m, s.measures5MinWindAt10m)
                    .compare(measures5MinWindAt1_5m, s.measures5MinWindAt1_5m).compare(measuresSoil, s.measuresSoil)
                    .compare(measuresSoil1m, s.measuresSoil1m).compare(measuresSoil5MinAt5cm, s.measuresSoil5MinAt5cm)
                    .compare(measuresSolarad, s.measuresSolarad).compare(measuresSolarad5Min, s.measuresSolarad5Min)
                    .compare(measuresMaxSolarad, s.measuresMaxSolarad)
                    .compare(measuresDataloggerVoltage, s.measuresDataloggerVoltage)
                    .compare(measuresHcnmDiagnostics, s.measuresHcnmDiagnostics)
                    .compare(measuresIrSensorTemp, s.measuresIrSensorTemp)
                    .compare(measuresRawIr5Min, s.measuresRawIr5Min)
                    .compare(measuresCorrectedIr5Min, s.measuresCorrectedIr5Min)
                    .compare(measuresPanelTemp, s.measuresPanelTemp)
                    .compare(measuresDiagnosticTemps, s.measuresDiagnosticTemps)
                    .compare(hasEndOfObMarker, s.hasEndOfObMarker)
                    .compare(hasSecondaryTransmitter, s.hasSecondaryTransmitter).result() == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.streamId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", streamId).add("frequency", subhourlyFrequency).toString();
    }

}
