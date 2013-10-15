package gov.noaa.ncdc.crn.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Version {
    public static final String SVN_URL = "$HeadURL: https://conman.ncdc.noaa.gov/svn-repos/CRN/crnshared/trunk/src/java/main/gov/noaa/ncdc/crn/util/Version.java $";

    private static Log LOG = LogFactory.getLog(Version.class);

    public static String getVersion() {
        String retval = null;
        if (SVN_URL.contains("tags/")) {
            LOG.debug("getting version from tag");
            retval = SVN_URL.substring(SVN_URL.indexOf("tags/") + 5);
            retval = retval.substring(retval.indexOf('-') + 1, retval.indexOf('/'));
        }
        return retval;
    }

    public static void main(String[] args) throws IOException {
        String version = getVersion();
        if (version != null) {
            File file = new File("version.properties");
            if (file.exists()) {
                file.delete();
                file = new File("version.properties");
            }
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("app.version.svn=" + version);
            bw.close();
        }
        LOG.debug("CRNShared code base version: " + version);
    }
}
