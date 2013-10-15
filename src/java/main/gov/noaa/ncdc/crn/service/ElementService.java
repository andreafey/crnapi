package gov.noaa.ncdc.crn.service;

import gov.noaa.ncdc.crn.dao.ElementDao;
import gov.noaa.ncdc.crn.domain.Element;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

@Service
public class ElementService {
    @Autowired
    private ElementDao elementDao;

    /**
     * Retrieves a Map{@code <Integer,Element>} of all Elements in the database mapped by elementId.
     * @return a Map{@code <Integer,Element>} of all Elements in the database mapped by elementId
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Element> getElements() throws DataAccessException {
        return elementDao.getElements();
    }

    /**
     * Retrieves a Map{@code <Integer,Element>} of all Elements in the database mapped by elementId.
     * @param elementIds the ids of the elements to retrieve
     * @return a Map{@code <Integer,Element>} of all Elements in the database mapped by elementId
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Element> getElements(Collection<Integer> elementIds) throws DataAccessException {
        return Maps.filterKeys(elementDao.getElements(), Predicates.in(elementIds));
    }

    /**
     * Retrieves a Map{@code <String,Element>} of all Elements in the database mapped by Element.name.
     * @return Map{@code <String,Element>} in the database mapped by Element.name
     * @throws DataAccessException (unchecked)
     */
    public Map<String, Element> getElementsByName() throws DataAccessException {
        return elementDao.getElementsByName();
    }

    /**
     * Retrieves a Map{@code <String,Element>} of all Elements in the database mapped by Element.name.
     * @param names the names of the elements to retrieve
     * @return Map{@code <String,Element>} in the database mapped by Element.name
     * @throws DataAccessException (unchecked)
     */
    public Map<String, Element> getElementsByName(Collection<String> names) throws DataAccessException {
        return Maps.filterKeys(elementDao.getElementsByName(), Predicates.in(names));
    }

    /**
     * Inserts new elements in the database and associates them with a single NetcdfVariable.
     * @param elements The elements to insert
     * @throws DataAccessException (unchecked)
     */
    @Transactional
    public void insertElements(Element... elements) throws DataAccessException {
        for (Element element : elements) {
            elementDao.insertElement(element);
        }
    }

}
