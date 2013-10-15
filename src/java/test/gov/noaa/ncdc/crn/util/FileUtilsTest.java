package gov.noaa.ncdc.crn.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

public class FileUtilsTest {

    private static String TMP_DIR = "tmp";
    private final String testFile = "src/resources/test/files/fileutilstest1.txt";

    // delete tmp dir before starting tests and after completing
    @BeforeClass
    public static void setup() {
        cleanup();
    }
    @AfterClass
    public static void teardown() {
        cleanup();
    }
    private static void cleanup() {
        File tmpDir = new File(TMP_DIR);
        if (tmpDir.exists() && tmpDir.isDirectory()) {
            FileUtils.deleteDirectory(tmpDir);
        }
    }

    @Test
    public void testReadWholeFile() throws IOException {
        File myfile = new File(TMP_DIR,"mytmpfile.txt");
        String expected = "Hello."+FileUtils.newline()+
                "This is a test of a two-line file read.";
        // overwrites file
        Files.write(expected, myfile, Charset.defaultCharset());
        String results = FileUtils.readWholeFile(myfile.getPath());
        assertEquals("didn't get matching file text",expected,results);
    }

    @Test
    public void testCreateFile() throws IOException {
        String filepath = "tmp/newdir1/fakefile.txt";
        File notexist = new File(filepath);
        assertFalse("file should not yet exist",notexist.exists());
        File myfile = FileUtils.createFile(filepath);
        File parent = new File(myfile.getParent());
        assertTrue("expect parent is dir", parent.isDirectory());
        assertTrue("expect parent dir to exist", parent.exists());
        Files.write("write a line", myfile, Charset.defaultCharset());
        // expect no exception
    }

    @Test
    public void testNewline() {
        assertEquals("newline char differs from system",
                System.getProperty("line.separator"),FileUtils.newline());
    }

    @Test
    public void testListRecursive() throws IOException {
        String testdir = TMP_DIR+"/testlistfiles";
        String file1 = testdir+"/subdir1/bar1.txt";
        String file2 = testdir+"/subdir2/foo2.txt";
        FileUtils.createFile(file1);
        FileUtils.createFile(file2);
        Files.touch(new File(file1));
        Files.touch(new File(file2));
        List<String> files = FileUtils.listRecursive(testdir);
        HashSet<String> expected = new HashSet<String>();
        expected.add("testlistfiles");
        expected.add("subdir1");
        expected.add("subdir2");
        expected.add("bar1.txt");
        expected.add("foo2.txt");
        assertEquals("incorrect # of files",expected.size(),files.size());
        for (String filePath : files) {
            File file = new File(filePath);
            assertTrue("file not in list",expected.contains(file.getName()));
        }
    }

    @Test
    public void testCreateDir() throws IOException {
        // create a directory where none exists
        String dirpath = "tmp/newdir2";
        assertFalse("expect not to exist yet",(new File(dirpath)).exists());
        File mydir = FileUtils.createDir(dirpath);
        assertTrue("expect is dir", mydir.isDirectory());
        assertTrue("expect dir to exist", mydir.exists());

        // when create a directory where a directory already exists, no problem
        File exists = new File(testFile);
        String direxists = exists.getParent();
        assertTrue("directory should exist already", (new File(direxists).exists()));
        assertTrue("existing directory should *be* a directory", (new File(direxists).isDirectory()));

        mydir = FileUtils.createDir(direxists);
        assertTrue("directory should still exist", (new File(direxists).exists()));
        assertTrue("existing directory should still *be* a directory", (new File(direxists).isDirectory()));
    }
    @Test(expected=IOException.class)
    public void testCreateDirX() throws IOException {
        // create a directory where file already exists and is not a directory
        String dirpath = testFile;
        assertTrue("expect file exists already", (new File(dirpath).exists()));
        assertTrue("expect existing file is a file (not a dir)", (new File(dirpath).isFile()));
        // can't creat a directory when file already exists
        FileUtils.createDir(testFile);
    }

    @Test
    public void testDeleteDirectory() throws IOException {
        File myfile = FileUtils.createFile("tmp/newdir3/subdir/fakofile.txt");
        Files.touch(myfile);
        assertTrue("expect subdir file to exist", myfile.exists());
        File dir = myfile.getParentFile().getParentFile();
        FileUtils.deleteDirectory(dir);
        File dir2 = myfile.getParentFile().getParentFile();
        assertFalse("directory was not deleted",dir2.exists());
    }

    @Test
    public void testIsChecksumValid() throws NoSuchAlgorithmException, IOException {
        assertTrue("incorrect md5 received", FileUtils.isChecksumValid(testFile,
                        FileUtils.CHECKSUM.MD5,
                        "d180c6467bbaba95d5898da394c046e8"));
        assertTrue("incorrect adler32 received", FileUtils.isChecksumValid(testFile,
                        FileUtils.CHECKSUM.ADLER32,
                        "a9e70a8c"));
        assertTrue("incorrect sha1 received", FileUtils.isChecksumValid(testFile,
                        FileUtils.CHECKSUM.SHA1,
                        "3633694481553d3a4ed44f77bb6087e93f7589d6"));
    }

}
