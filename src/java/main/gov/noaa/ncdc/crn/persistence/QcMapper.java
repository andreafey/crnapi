package gov.noaa.ncdc.crn.persistence;

import gov.noaa.ncdc.crn.domain.QcDeltaParam;
import gov.noaa.ncdc.crn.domain.QcRangeParam;

import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

public interface QcMapper {

    @MapKey("elementIds[0]")
    public abstract Map<Integer, QcDeltaParam> selectDeltaParams(@Param("streamId") final int streamId,
            @Param("stationId") final int stationId, @Param("month") final int month);

    @MapKey("elementId")
    public abstract Map<Integer, QcRangeParam> selectRangeParams(@Param("streamId") final int streamId,
            @Param("stationId") final int stationId, @Param("month") final int month);

}
