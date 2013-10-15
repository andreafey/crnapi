package gov.noaa.ncdc.crn.dao;

import gov.noaa.ncdc.crn.domain.Network;

import java.util.Map;

public interface NetworkDao {

    /**
     * Retrieves a Map<Integer,Network> mapping networkId to Network
     * @return Map<Integer,Network> mapping networkId to Network
     */
    public abstract Map<Integer, Network> getNetworks();

    /**
     * Retrieves the Network with the provided networkId or null if it does not exist
     * @param networkId The id of the Network to retrieve
     * @return the Network with the provided networkId
     */
    public abstract Network getNetwork(int networkId);

    /**
     * Retrieves the Network with the provided String abbreviation or null if it does not exist
     * @param abbr The abbreviation of the Network to retrieve
     * @return the Network with the provided String abbreviation
     */
    public abstract Network getNetwork(String abbr);
}
