package gov.noaa.ncdc.crn.dao;

import gov.noaa.ncdc.crn.domain.Element;
import gov.noaa.ncdc.crn.domain.ElementGroup;
import gov.noaa.ncdc.crn.domain.ElementValue;
import gov.noaa.ncdc.crn.domain.StationDateElement;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface ElementDao {

    /**
     * Retrieves from the database a Map<StationDateElement, ElementValue> based on the parameter map submitted
     * @param params a parameter name-value map with a keys in the following group:
     * <ul>
     * <li>(int) stationId or (Collection<Integer>) stationIds</li>
     * <li>(int) datetimeId</li>
     * <li>(int) begin and (int) end (both datetimeIds)</li>
     * <li>(int) elementId or (Collection<Integer>) elementIds</li>
     * </ul>
     * @return a Map<StationDateElement, ElementValue> based on the parameter map submitted
     * @throws DataAccessException (unchecked)
     */
    public Map<StationDateElement, ElementValue> getElementValues(Map<String, Object> params)
            throws DataAccessException;

    /**
     * Retrieves from the database a Map<Integer, ElementValue> of values for a single station/date mapped by elementId.
     * @param datetimeId the datetimeId to retrieve the values for
     * @param stationId the station to retrieve the values for
     * @return a Map<Integer, ElementValue> of values mapped by elementId
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, ElementValue> getElementValues(int datetimeId, int stationId) throws DataAccessException;

    /**
     * Retrieves from the database a Map<StationDateElement, ElementValue> based on the parameter map submitted;
     * @param params a parameter name-value map with a keys in the following group:
     * <ul>
     * <li>(int) stationId</li>
     * <li>(String) yyyymm(dd) to get a LST day or month; requires stationId(s)</li>
     * <li>(int) elementId or (List<Integer>) elementIds</li>
     * </ul>
     * @return a Map<StationDateElement, ElementValue> based on the parameter map submitted
     * @throws DataAccessException (unchecked)
     */
    public Map<StationDateElement, ElementValue> getElementValuesLST(Map<String, Object> params)
            throws DataAccessException;

    /**
     * Retrieves from the database a Map<StationDateElement, ElementValue> based on when the observation was last
     * modified.
     * @param params a parameter name-value map with a keys in the following group:
     * <ul>
     * <li>[Required] (yyyymmddhh String) endhour or (List<String>) endhours</li>
     * <li>(int) hours - the number of hours to retrieve [defaults to 1]</li>
     * <li>(int) stationId or (List<Integer>) stationIds</li>
     * <li>(int) elementId or (List<Integer>) elementIds</li>
     * </ul>
     * @return a Map<StationDateElement, ElementValue> based on the parameter map submitted
     * @throws DataAccessException (unchecked)
     */
    public Map<StationDateElement, ElementValue> getElementValuesForHours(Map<String, Object> params)
            throws DataAccessException;

    /**
     * Retrieves an ElementValue for a StationDateElement from the database
     * @return an ElementValue for the StationDateElement
     * @throws DataAccessException (unchecked)
     */
    public ElementValue getElementValue(StationDateElement sDElement) throws DataAccessException;

    /**
     * Retrieves a Map<Integer,Element> of all Elements in the database mapped by elementId.
     * @return a Map<Integer,Element> of all Elements in the database mapped by elementId
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Element> getElements() throws DataAccessException;

    /**
     * Retrieves a Map<Integer,Element> of the Elements defined by elementIds in the database mapped by elementId.
     * @param elementIds the ids of the Elements to retrieve
     * @return Map<Integer,Element> of all Elements in the database mapped by elementId
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, Element> getElements(Collection<Integer> elementIds) throws DataAccessException;

    /**
     * Retrieves a Map<String,Element> of all Elements in the database mapped by Element.name.
     * @return map of all Elements in the database mapped by Element.name
     * @throws DataAccessException (unchecked)
     */
    public Map<String, Element> getElementsByName() throws DataAccessException;

    /**
     * Retrieves the Element in the database with this name.
     * @return the Element in the database with this name
     * @throws DataAccessException (unchecked)
     */
    public Element getElement(String name) throws DataAccessException;

    /**
     * Retrieves a Map<String,Element> of the Elements defined by names in the database mapped by Element.name.
     * @param names the names of the Elements to retrieve
     * @return map of all Elements in the database mapped by Element.name
     * @throws DataAccessException (unchecked)
     */
    public Map<String, Element> getElementsByName(Collection<String> names) throws DataAccessException;

    /**
     * Retrieves a Map<Integer,ElementValue> mapping stationId to ElementValue of the current observations of an element
     * @param elementId the element requested
     * @return Map<Integer,ElementValue> mapping stationId to ElementValue
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, ElementValue> getCurrentElementValues(int elementId) throws DataAccessException;

    /**
     * Retrieves a Map<Integer,ElementValue> mapping stationId to ElementValue of the current observations of an element
     * @param elementId the element requested
     * @param networkIds the network(s) requested
     * @return Map<Integer,ElementValue> mapping stationId to ElementValue
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, ElementValue> getCurrentElementValues(int elementId, int... networkIds)
            throws DataAccessException;

    /**
     * Retrieves a Map<StationDateElement,ElementValue> mapping StationDateElement to ElementValue of the current
     * observations of a list of elements for a list of networks
     * @param elementIds the elements requested
     * @param networkIds the networks requested
     * @return Map<StationDateElement,ElementValue> mapping StationDateElement to ElementValue
     * @throws DataAccessException (unchecked)
     */
    public Map<StationDateElement, ElementValue> getCurrentElementValues(Collection<Integer> elementIds,
            Collection<Integer> networkIds) throws DataAccessException;

    /**
     * Retrieves a Map<StationDateElement,ElementValue> mapping StationDateElement to ElementValue of the current
     * observations of a list of elements
     * @param elementIds the elements requested
     * @return Map<StationDateElement,ElementValue> mapping StationDateElement to ElementValue
     * @throws DataAccessException (unchecked)
     */
    public Map<StationDateElement, ElementValue> getCurrentElementValues(Collection<Integer> elementIds)
            throws DataAccessException;

    /**
     * Retrieves a complete Map<Integer,ElementGroup> mapping elementGroupId to its ElementGroup
     * @return a complete Map<Integer,ElementGroup>
     * @throws DataAccessException (unchecked)
     */
    public Map<Integer, ElementGroup> getElementGroupMap() throws DataAccessException;

    /**
     * Retrieves an ordered List<ElementGroup>
     * @return an ordered List<ElementGroup>
     * @throws DataAccessException (unchecked)
     */
    public List<ElementGroup> getElementGroups() throws DataAccessException;

    /**
     * Inserts a new element in the database
     * @param element The element to insert
     * @throws DataAccessException (unchecked)
     */
    public void insertElement(Element element) throws DataAccessException;

    /**
     * Inserts element values (facts and flags) for an existing observation into the database. Updates the observation's
     * modification timestamp. There <em>must</em> be an observation in the database for the station/datetime already.
     * @param values The values to insert
     * @throws DataAccessException (unchecked)
     */
    public void insertElementValues(Collection<ElementValue> values) throws DataAccessException;

    /**
     * Updates only the flags of these ElementValues. Inserts flags where they don't exist already, updates the flag's
     * value if the new value differs from the old, and deletes a flag if the new value is zero. Use with caution, for
     * example when you are adding or removing an exception.
     * @param values The ElementValues to update
     * @throws DataAccessException
     */
    public void updateFlags(Collection<ElementValue> values) throws DataAccessException;
}
