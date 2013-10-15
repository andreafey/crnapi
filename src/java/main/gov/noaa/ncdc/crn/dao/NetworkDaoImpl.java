package gov.noaa.ncdc.crn.dao;

import gov.noaa.ncdc.crn.domain.Network;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class NetworkDaoImpl implements NetworkDao {

    private static final Map<Integer, Network> NETWORKS = buildNetworks();

    public NetworkDaoImpl() {}

    private static Map<Integer, Network> buildNetworks() {
        Network crn = new Network(1, "Climate Reference Network", "CRN");
        Network alusrcrn = new Network(2, "AL U.S. Regional Climate Reference Network", "AL USRCRN");
        Network usrcrn = new Network(3, "U.S. Regional Climate Reference Network", "USRCRN");
        Map<Integer, Network> networks = new HashMap<>();
        networks.put(crn.getNetworkId(), crn);
        networks.put(alusrcrn.getNetworkId(), alusrcrn);
        networks.put(usrcrn.getNetworkId(), usrcrn);
        return networks;
    }

    @Override
    public Network getNetwork(final int networkId) {
        return NETWORKS.get(networkId);
    }

    @Override
    public Network getNetwork(final String abbr) {
        for (Network network : NETWORKS.values()) {
            if (network.getAbbr().equalsIgnoreCase(abbr)
                    || network.getAbbr().replaceAll(" ", "").equalsIgnoreCase(abbr)) {
                return network;
            }
        }
        return null;
    }

    @Override
    public Map<Integer, Network> getNetworks() {
        return NETWORKS;
    }

}
