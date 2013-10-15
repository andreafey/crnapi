package gov.noaa.ncdc.crn.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.ncdc.crn.domain.Network;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:application-context.xml"})
@ActiveProfiles("unittest")
public class NetworkServiceTest {

	@Autowired
	private NetworkService service;

	@Test
	public void testGetNetworkInteger() {
		for (int i=1; i<=3; i++) {
			assertNotNull("didn't get networkId="+i, service.getNetwork(i));
		}
	}

	@Test
	public void testGetNetworkString() {
		String[] networks = {"CRN","AL USRCRN", "USRCRN"};
		for (String network : networks) {
			assertNotNull("didn't get network="+network, service.getNetwork(network));
		}
	}

	@Test
	public void testGetNetworks() {
		Map<Integer,Network> networks = service.getNetworks();
		assertNotNull("network map is null",networks);
		assertTrue("network map too small",networks.size()>=3);
	}

}
