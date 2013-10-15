package gov.noaa.ncdc.crn.persistence;

import gov.noaa.ncdc.crn.domain.Element;
import gov.noaa.ncdc.crn.domain.ElementGroup;
import gov.noaa.ncdc.crn.domain.ElementValue;
import gov.noaa.ncdc.crn.domain.StationDateElement;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

public interface ElementMapper {

    /**
     * Retrieves from the database a Map{@code <StationDateElement, ElementValue>} based on the parameter map submitted
     * @param params a parameter name-value map with a keys in the following group:
     * <ul>
     * <li>(int) stationId or (Collection{@code <Integer>}) stationIds</li>
     * <li>(int) datetimeId</li>
     * <li>(int) begin and (int) end (both datetimeIds)</li>
     * <li>(int) elementId or (Collection{@code <Integer>}) elementIds</li>
     * </ul>
     * @return a Map{@code <StationDateElement, ElementValue>} based on the parameter map submitted
     * @throws DataAccessException (unchecked)
     */
    @MapKey("staDateElement")
    public Map<StationDateElement, ElementValue> selectElementValues(final Map<String, Object> params)
            throws DataAccessException;

    /**
     * Retrieves from the database a Map{@code <Integer, ElementValue>} of values for a single station/date mapped by
     * elementId.
     * @param datetimeId the datetimeId to retrieve the values for
     * @param stationId the station to retrieve the values for
     * @return a Map{@code <Integer, ElementValue>} of values mapped by elementId
     * @throws DataAccessException (unchecked)
     */
    @MapKey("elementId")
    public Map<Integer, ElementValue> selectElementValues(@Param("datetimeId") final int datetimeId,
            @Param("stationId") final int stationId) throws DataAccessException;

    /**
     * Retrieves from the database a Map{@code <StationDateElement, ElementValue>} based on the parameter map submitted;
     * @param params a parameter name-value map with a keys in the following group:
     * <ul>
     * <li>(int) stationId</li>
     * <li>(String) yyyymm(dd) to get a LST day or month; requires stationId(s)</li>
     * <li>(int) elementId or (List{@code <Integer>}) elementIds</li>
     * </ul>
     * @return a Map{@code <StationDateElement, ElementValue>} based on the parameter map submitted
     * @throws DataAccessException (unchecked)
     */
    @MapKey("staDateElement")
    public Map<StationDateElement, ElementValue> selectElementValuesLST(final Map<String, Object> params)
            throws DataAccessException;

    /**
     * Retrieves from the database a Map{@code <StationDateElement, ElementValue>} based on when the observation was
     * last modified.
     * @param params a parameter name-value map with a keys in the following group:
     * <ul>
     * <li>[Required] (yyyymmddhh String) endhour or (List{@code <String>}) endhours</li>
     * <li>(int) hours - the number of hours to retrieve [defaults to 1]</li>
     * <li>(int) stationId or (List{@code <Integer>}) stationIds</li>
     * <li>(int) elementId or (List{@code <Integer>}) elementIds</li>
     * </ul>
     * @return a Map{@code <StationDateElement, ElementValue>} based on the parameter map submitted
     * @throws DataAccessException (unchecked)
     */
    @MapKey("staDateElement")
    public Map<StationDateElement, ElementValue> selectElementValuesForHours(final Map<String, Object> params)
            throws DataAccessException;

    /**
     * Retrieves a Map{@code <Integer,Element>} of all Elements in the database mapped by elementId.
     * @return a Map{@code <Integer,Element>} of all Elements in the database mapped by elementId
     * @throws DataAccessException (unchecked)
     */
    @MapKey("elementId")
    public Map<Integer, Element> selectElements() throws DataAccessException;

    /**
     * Retrieves a Map{@code <Integer,Element>} of the Elements defined by elementIds in the database mapped by
     * elementId.
     * @param elementIds the ids of the Elements to retrieve
     * @return Map{@code <Integer,Element>} of all Elements in the database mapped by elementId
     * @throws DataAccessException (unchecked)
     */
    @MapKey("elementId")
    public Map<Integer, Element> selectElements(@Param("elementIds") final Collection<Integer> elementIds)
            throws DataAccessException;

    /**
     * Retrieves an ElementValue for a StationDateElement from the database
     * @return an ElementValue for the StationDateElement
     * @throws DataAccessException (unchecked)
     */
    public ElementValue selectElementValue(final StationDateElement sDElement) throws DataAccessException;

    /**
     * Retrieves a Map{@code <String,Element>} of all Elements in the database mapped by Element.name.
     * @return map of all Elements in the database mapped by Element.name
     * @throws DataAccessException (unchecked)
     */
    @MapKey("name")
    public Map<String, Element> selectElementsByName() throws DataAccessException;

    /**
     * Retrieves the Element in the database with this name.
     * @return the Element in the database with this name
     * @throws DataAccessException (unchecked)
     */
    public Element selectElement(@Param("name") final String name) throws DataAccessException;

    /**
     * Retrieves a Map{@code <Integer,ElementValue>} mapping stationId to ElementValue of the current observations of an
     * element
     * @param elementId the element requested
     * @return Map{@code <Integer,ElementValue>} mapping stationId to ElementValue
     * @throws DataAccessException (unchecked)
     */
    @MapKey("stationId")
    public Map<Integer, ElementValue> selectCurrentElementValues(@Param("elementId") final int elementId)
            throws DataAccessException;

    /**
     * Retrieves a Map{@code <Integer,ElementValue>} mapping stationId to ElementValue of the current observations of an
     * element
     * @param elementId the element requested
     * @param networkIds the network(s) requested
     * @return Map{@code <Integer,ElementValue>} mapping stationId to ElementValue
     * @throws DataAccessException (unchecked)
     */
    @MapKey("stationId")
    public Map<Integer, ElementValue> selectCurrentElementValues(@Param("elementId") final int elementId,
            @Param("networkIds") final int... networkIds) throws DataAccessException;

    /**
     * Retrieves a Map{@code <StationDateElement,ElementValue>} mapping StationDateElement to ElementValue of the
     * current observations of a list of elements for a list of networks
     * @param elementIds the elements requested
     * @param networkIds the networks requested
     * @return Map{@code <StationDateElement,ElementValue>} mapping StationDateElement to ElementValue
     * @throws DataAccessException (unchecked)
     */
    @MapKey("staDateElement")
    public Map<StationDateElement, ElementValue> selectCurrentElementValues(
            @Param("elementIds") final Collection<Integer> elementIds,
            @Param("networkIds") final Collection<Integer> networkIds) throws DataAccessException;

    /**
     * Retrieves a Map{@code <StationDateElement,ElementValue>} mapping StationDateElement to ElementValue of the
     * current observations of a list of elements
     * @param elementIds the elements requested
     * @return Map{@code <StationDateElement,ElementValue>} mapping StationDateElement to ElementValue
     * @throws DataAccessException (unchecked)
     */
    @MapKey("staDateElement")
    public Map<StationDateElement, ElementValue> selectCurrentElementValues(
            @Param("elementIds") final Collection<Integer> elementIds) throws DataAccessException;

    /**
     * Retrieves an ordered List{@code <ElementGroup>}
     * @return an ordered List{@code <ElementGroup>}
     * @throws DataAccessException (unchecked)
     */
    public List<ElementGroup> selectElementGroups() throws DataAccessException;

    /**
     * Inserts a new element in the database
     * @param element The element to insert
     * @throws DataAccessException (unchecked)
     */
    public void insertElement(final Element element) throws DataAccessException;

    /**
     * Inserts element value (fact only) for an existing observation into the database. Updates the observation's
     * modification timestamp. There <em>must</em> be an observation in the database for the station/datetime already.
     * @param value The value to insert
     * @throws DataAccessException (unchecked)
     */
    public void insertFact(final ElementValue value) throws DataAccessException;

    /**
     * Updates only the flags of this ElementValue. Inserts flags where they don't exist already, updates the flag's
     * value if the new value differs from the old, and deletes a flag if the new value is zero. Use with caution, for
     * example when you are adding or removing an exception.
     * @param value The ElementValue to update
     * @throws DataAccessException
     */
    public void updateFlag(final ElementValue value) throws DataAccessException;

    /**
     * Updates only the fact portion of this ElementValue. Inserts a new fact where it doesn't exist already, updates
     * the fact's value if the new value differs from the old, and deletes a fact if the new value is null. Use with
     * caution, for example when you are adding or removing an exception.
     * @param value The ElementValue to update
     * @throws DataAccessException
     */
    public void updateFact(final ElementValue value) throws DataAccessException;

    public void deleteFacts(@Param("stationId") final int stationId, @Param("datetimeId") final int datetimeId)
            throws DataAccessException;

    public void deleteFacts(@Param("stationId") final int stationId,
            @Param("beginDatetimeId") final int beginDatetimeId, @Param("endDatetimeId") final int endDatetimeId)
            throws DataAccessException;

    public void deleteCalculatedFacts(@Param("stationId") final int stationId, @Param("datetimeId") final int datetimeId)
            throws DataAccessException;

    public int deleteCalculatedFacts(@Param("stationId") final Integer stationId,
            @Param("beginDatetimeId") final int beginDatetimeId, @Param("endDatetimeId") final int endDatetimeId,
            @Param("elementIds") final int... elementIds) throws DataAccessException;

    public void deleteCalculatedFact(@Param("stationId") final int stationId,
            @Param("datetimeId") final int datetimeId, @Param("elementId") int elementId) throws DataAccessException;

    public void deleteCalculatedFlags(@Param("stationId") int stationId, @Param("datetimeId") final int datetimeId)
            throws DataAccessException;

    public void deleteCalculatedFlag(@Param("stationId") final int stationId,
            @Param("datetimeId") final int datetimeId, @Param("elementId") final int elementId)
            throws DataAccessException;

    // stationId is nullable
    public void deleteCalculatedFlags(@Param("stationId") final Integer stationId,
            @Param("beginDatetimeId") final int beginDatetimeId, @Param("endDatetimeId") final int endDatetimeId,
            @Param("elementIds") final int... elementIds);

    public void deleteFlags(@Param("stationId") final int stationId, @Param("datetimeId") final int datetimeId)
            throws DataAccessException;

    // stationId is nullable
    public void deleteFlags(@Param("stationId") final Integer stationId,
            @Param("beginDatetimeId") final int beginDatetimeId, @Param("endDatetimeId") final int endDatetimeId,
            @Param("elementIds") final int... elementIds) throws DataAccessException;

    public void deleteFlags(@Param("stationId") final int stationId,
            @Param("beginDatetimeId") final int beginDatetimeId, @Param("endDatetimeId") final int endDatetimeId)
            throws DataAccessException;

}
