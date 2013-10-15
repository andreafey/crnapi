package gov.noaa.ncdc.crn.service;

import gov.noaa.ncdc.crn.dao.NetworkDao;
import gov.noaa.ncdc.crn.domain.Network;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetworkService {
    @Autowired
    private NetworkDao networkDao;

    public Network getNetwork(Integer networkId) {
        return networkDao.getNetwork(networkId);
    }

    public Network getNetwork(String abbr) {
        return networkDao.getNetwork(abbr);
    }

    public Map<Integer, Network> getNetworks() {
        return networkDao.getNetworks();
    }

}
