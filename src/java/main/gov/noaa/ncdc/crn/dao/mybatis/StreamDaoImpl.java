package gov.noaa.ncdc.crn.dao.mybatis;

import gov.noaa.ncdc.crn.dao.StreamDao;
import gov.noaa.ncdc.crn.domain.Stream;
import gov.noaa.ncdc.crn.domain.StreamElement;
import gov.noaa.ncdc.crn.persistence.StreamMapper;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class StreamDaoImpl implements StreamDao {

    @Autowired
    private StreamMapper mapper;

    @Override
    public Stream getStream(final int streamId) throws DataAccessException {
        return mapper.selectStream(streamId);
    }

    @Override
    public Stream getStream(final String crxVersion) throws DataAccessException {
        return mapper.selectStream(crxVersion);
    }

    @Override
    public Stream getStream(final String crxVersion, final int elementCount) throws DataAccessException {
        return mapper.selectStream(crxVersion, elementCount);
    }

    @Override
    public Map<Integer, Stream> getStreams() throws DataAccessException {
        return mapper.selectStreams();
    }

    @Override
    public List<StreamElement> getStreamElementList(final int streamId) throws DataAccessException {
        return mapper.selectStreamElementList(streamId);
    }

    @Override
    public List<StreamElement> getStreamElementListForPda(final int streamId) throws DataAccessException {
        return mapper.selectStreamElementListForDatalogger(streamId);
    }

}
