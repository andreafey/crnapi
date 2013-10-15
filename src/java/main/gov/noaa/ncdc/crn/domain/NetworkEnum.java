package gov.noaa.ncdc.crn.domain;

import com.google.common.base.Objects;

public enum NetworkEnum {
    CRN(1, "Climate Reference Network", "CRN"),
    ALUSRCRN(2, "AL U.S. Regional Climate Reference Network", "AL USRCRN"),
    USRCRN(3, "U.S. Regional Climate Reference Network", "USRCRN");

    private int networkId;
    /** The networkId */
    private String name;
    /** The network name; not nullable */
    private String abbr;

    /** An abbreviated name; not nullable */
    /**
     * Creates a new {@link Network} from it's components
     * @param networkId The netwowrkId
     * @param name The name of the {@link Network}
     * @param abbr The abbreviated name
     */
    private NetworkEnum(int networkId, String name, String abbr) {
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
    public String toString() {
        return Objects.toStringHelper(this).addValue(networkId).addValue(name).addValue("(" + abbr + ")").toString();
    }

}
