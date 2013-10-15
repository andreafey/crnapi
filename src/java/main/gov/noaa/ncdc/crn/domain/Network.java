package gov.noaa.ncdc.crn.domain;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

@SuppressWarnings("serial")
public class Network implements Comparable<Network>, Serializable {

    private final int networkId;
    /** The networkId */
    private final String name;
    /** The network name; not nullable */
    private final String abbr;

    /** An abbreviated name; not nullable */
    /**
     * Creates a new {@code Network} from it's components
     * @param networkId The netwowrkId
     * @param name The name of the Network
     * @param abbr The abbreviated name
     */
    public Network(final int networkId, final String name, final String abbr) {
        this.networkId = networkId;
        this.name = name;
        this.abbr = abbr;
    }

    /**
     * Returns the network name
     * @return the network name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the networkId
     * @return the networkId
     */
    public int getNetworkId() {
        return networkId;
    }

    /**
     * Returns the abbreviated name
     * @return the abbreviated name
     */
    public String getAbbr() {
        return abbr;
    }

    @Override
    public int compareTo(Network network) {
        return this.networkId - network.networkId;
    }

    @Override
    public boolean equals(Object o) {
        // true if these refer to the same object
        if (this == o) {
            return true;
        }
        // true if o is a Network and all fields are equal
        if (o instanceof Network) {
            Network n = (Network) o;
            return ComparisonChain.start().compare(networkId, n.networkId).compare(name, n.name).compare(abbr, n.abbr)
                    .result() == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(networkId, name, abbr);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(networkId).addValue(name).addValue("(" + abbr + ")").toString();
    }

}
