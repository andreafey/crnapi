package gov.noaa.ncdc.crn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class FileUtils {

    private static final Log LOGGER = LogFactory.getLog(FileUtils.class);

    public enum CHECKSUM {
        SHA1, MD5, ADLER32
    }

    /**
     * Returns a String that contains the entire contents of a text file. Note that this method does not consider the
     * case where the file is too large to be contained within memory and as such should be used with caution.
     * @param filename The file to get the contents of.
     * @return String containing the contents of the file
     * @throws IOException
     */
    public static String readWholeFile(String filename) throws IOException {
        return Files.toString(new File(filename), Charset.defaultCharset());
    }

    /**
     * Creates a file and its parent directory structure
     * @param fileName The full path of the file to create
     * @return The file which was created
     */
    public static File createFile(String fileName) throws IOException {
        File file = new File(fileName);
        Files.createParentDirs(file);
        return file;
    }

    /**
     * Returns the system line separator String
     * @return The system line separator String.
     */
    public static String newline() {
        return System.getProperty("line.separator");
    }

    /**
     * Recursively lists file names
     * @param file The root of the directory/file to recurse
     * @return List of files
     */
    public static List<String> listRecursive(String file) {
        List<String> list = new ArrayList<String>();
        return listRecursive(file, list);
    }

    /**
     * Recursively adds file names to list
     * @param fileName The root of the directory/file to recurse
     * @param list The list to recursively add files to
     */
    private static List<String> listRecursive(String fileName, List<String> list) {
        File file = new File(fileName);
        if (file.exists() || file.isDirectory()) {
            list.add(file.getAbsolutePath());
            if (file.isDirectory()) {
                String[] contents = file.list();
                for (String listing : contents) {
                    listRecursive(file.getAbsolutePath() + '/' + listing, list);
                }
            }
        }
        return list;
    }

    /**
     * Creates a directory and its parent directory structure
     * @param dirName The full path of the directory to create
     * @return The file which was created
     * @throws IOException
     */
    public static File createDir(String dirName) throws IOException {
        // creating a file object for a file which does not exist so parent directories can be created
        File fakeChild = new File(dirName, "fake_child_file");
        Files.createParentDirs(fakeChild);
        return fakeChild.getParentFile();
    }

    /**
     * Delete a directory and all of its contents. Assumes dir is a directory; if it is a file, an
     * IllegalArgumentException will be thrown.
     * @param dir The directory to delete
     */
    public static void deleteDirectory(File dir) {
        if (dir.exists()) {
            Preconditions.checkArgument(dir.isDirectory(), "Cannot call deleteDirectory on a file.");
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                    LOGGER.debug("deleted " + file.getAbsolutePath());
                }
            }
            dir.delete();
            LOGGER.debug("deleted " + dir.getAbsolutePath());
        }
    }

    /**
     * Delete a directory and its contents. Assumes dir is a directory; if it is a file, an IllegalArgumentException
     * will be thrown.
     * @param dir The directory to delete
     * @deprecated Use identical deleteDirectory instead
     */
    @Deprecated
    public static void deleteDirectoryContents(File dir) {
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("deleted " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the checksum based on a supported algorithm of a file
     * @param filepath The path of the file to check
     * @param checksum The checksum algorithm to use
     * @return the checksum hash of the file
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String checksum(String filepath, CHECKSUM checksum) throws NoSuchAlgorithmException, IOException {
        File file = new File(filepath);
        switch (checksum) {
        case SHA1:
            return getSHA1Checksum(file);
        case MD5:
            return getMD5Checksum(file);
        case ADLER32:
            return getAdler32Checksum(file);
        default:
            throw new IllegalArgumentException("Unsupported checksum");
        }
    }

    /**
     * Returns true if an expected file checksum hash equals the calculated file checksum
     * @param filepath The path of the file to check
     * @param checksum The checksum algorithm to use
     * @param expectedHash The expected value of the file's checksum
     * @return true if the expected value equals the calculated file checksum
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static boolean isChecksumValid(String filepath, CHECKSUM checksum, String expectedHash)
            throws NoSuchAlgorithmException, IOException {
        return expectedHash.equalsIgnoreCase(checksum(filepath, checksum));
    }

    /**
     * Computes the MD5 checksum of a file.
     * @param file The file on which to compute the checksum
     * @return the checksum in hex
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private static String getMD5Checksum(File file) throws NoSuchAlgorithmException, IOException {
        HashCode hash = Files.hash(file, Hashing.md5());
        // convert to hex
        BigInteger bigInt = new BigInteger(1, hash.asBytes());
        return bigInt.toString(16);
    }

    /**
     * Compute a checksum using Adler32 (common for zip files).
     * @param file the file to calculate the checksum on
     * @return the checksum in hex
     * @throws IOException
     */
    private static String getAdler32Checksum(File file) throws IOException {
        HashCode hash = Files.hash(file, Hashing.adler32());
        // convert to hex
        return Long.toHexString(hash.padToLong());
    }

    /**
     * Compute a checksum using SHA1
     * @param file the file to calculate the checksum on
     * @return the checksum in hex
     * @throws IOException
     */
    private static String getSHA1Checksum(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        try (FileInputStream fis = new FileInputStream(file);) {
            byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            ;
        }
        byte[] mdbytes = md.digest();

        // convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

}
