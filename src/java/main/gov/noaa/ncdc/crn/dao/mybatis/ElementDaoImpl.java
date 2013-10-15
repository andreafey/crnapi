package gov.noaa.ncdc.crn.dao.mybatis;

import gov.noaa.ncdc.crn.dao.ElementDao;
import gov.noaa.ncdc.crn.domain.Element;
import gov.noaa.ncdc.crn.domain.ElementGroup;
import gov.noaa.ncdc.crn.domain.ElementValue;
import gov.noaa.ncdc.crn.domain.StationDateElement;
import gov.noaa.ncdc.crn.persistence.ElementMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

@Repository
public class ElementDaoImpl implements ElementDao {
    @Autowired
    ElementMapper mapper;
    @Autowired
    SqlSession sqlSession;
    private static int THROTTLE = 100;

    @Override
    public Map<StationDateElement, ElementValue> getElementValues(Map<String, Object> params)
            throws DataAccessException {
        return mapper.selectElementValues(params);
    }

    @Override
    public Map<Integer, ElementValue> getElementValues(int datetimeId, int stationId) throws DataAccessException {
        return mapper.selectElementValues(datetimeId, stationId);
    }

    @Override
    public Map<StationDateElement, ElementValue> getElementValuesLST(Map<String, Object> params)
            throws DataAccessException {
        return mapper.selectElementValuesLST(params);
    }

    @Override
    public Map<StationDateElement, ElementValue> getElementValuesForHours(Map<String, Object> params)
            throws DataAccessException {
        return mapper.selectElementValuesForHours(params);
    }

    @Override
    public Map<Integer, Element> getElements() throws DataAccessException {
        return mapper.selectElements();
    }

    @Override
    public Map<Integer, Element> getElements(Collection<Integer> elementIds) throws DataAccessException {
        return mapper.selectElements(elementIds);
    }

    @Override
    public ElementValue getElementValue(StationDateElement sDElement) throws DataAccessException {
        return mapper.selectElementValue(sDElement);
    }

    @Override
    public Map<String, Element> getElementsByName() throws DataAccessException {
        return mapper.selectElementsByName();
    }

    @Override
    public Element getElement(String name) throws DataAccessException {
        return getElementsByName().get(name);
    }

    @Override
    public Map<String, Element> getElementsByName(Collection<String> names) throws DataAccessException {
        return Maps.filterKeys(mapper.selectElementsByName(), Predicates.in(names));
    }

    @Override
    public Map<Integer, ElementValue> getCurrentElementValues(int elementId) throws DataAccessException {
        return mapper.selectCurrentElementValues(elementId);
    }

    @Override
    public Map<Integer, ElementValue> getCurrentElementValues(int elementId, int... networkIds)
            throws DataAccessException {
        return mapper.selectCurrentElementValues(elementId, networkIds);
    }

    @Override
    public Map<StationDateElement, ElementValue> getCurrentElementValues(Collection<Integer> elementIds,
            Collection<Integer> networkIds) throws DataAccessException {
        if (networkIds == null) {
            return getCurrentElementValues(elementIds);
        }
        return mapper.selectCurrentElementValues(elementIds, networkIds);
    }

    @Override
    public Map<StationDateElement, ElementValue> getCurrentElementValues(Collection<Integer> elementIds)
            throws DataAccessException {
        return mapper.selectCurrentElementValues(elementIds);
    }

    @Override
    public Map<Integer, ElementGroup> getElementGroupMap() throws DataAccessException {
        List<ElementGroup> list = getElementGroups();
        Map<Integer, ElementGroup> mappedGroups = Maps.uniqueIndex(list, new Function<ElementGroup, Integer>() {
            public Integer apply(ElementGroup eg) {
                return eg.getElementGroupId(); // or something else
            }
        });
        return mappedGroups;
    }

    @Override
    public List<ElementGroup> getElementGroups() throws DataAccessException {
        return mapper.selectElementGroups();
    }

    @Override
    public void insertElement(Element element) throws DataAccessException {
        mapper.insertElement(element);
    }

    @Override
    public void insertElementValues(Collection<ElementValue> values) throws DataAccessException {
        /* partitioning to avoid max cursors when values.size() is large */
        Iterable<List<ElementValue>> partitions = Iterables.partition(values, THROTTLE);
        for (List<ElementValue> partition : partitions) {
            Collection<ElementValue> flaggedValues = new ArrayList<>();
            for (ElementValue value : partition) {
                mapper.insertFact(value);
                /*
                 * separating fact from flag inserts to prevent new cursor from being retrieved during batch processing
                 */
                if (value.getFlags().isFlagged()) {
                    flaggedValues.add(value);
                }
            }
            for (ElementValue value : flaggedValues) {
                mapper.updateFlag(value);
            }
            sqlSession.flushStatements();
        }
    }

    @Override
    public void updateFlags(Collection<ElementValue> values) throws DataAccessException {
        /* partitioning to avoid max cursors when values.size() is large */
        Iterable<List<ElementValue>> partitions = Iterables.partition(values, THROTTLE);
        for (List<ElementValue> partition : partitions) {
            for (ElementValue value : partition) {
                mapper.updateFlag(value);
            }
            sqlSession.flushStatements();
        }
    }

}
