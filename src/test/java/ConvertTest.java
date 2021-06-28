import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import subtitleFile.FormatASS;
import subtitleFile.FormatSCC;
import subtitleFile.FormatSRT;
import subtitleFile.FormatSTL;
import subtitleFile.FormatTTML;
import subtitleFile.TimedTextFileFormat;
import subtitleFile.TimedTextObject;

public class ConvertTest {

    final Path builddir = Paths.get(System.getProperty("builddir", "./target"));

    @Test
    public void testSRT() throws Exception {
        // To test the correct implementation of the SRT parser and writer.
        TimedTextFileFormat ttff = new FormatSRT();
        File file = new File("SRT/Avengers.2012.Eng.Subs.srt");
        try (InputStream is = getClass().getResourceAsStream(file.getPath())) {
            TimedTextObject tto = ttff.parseFile(file.getName(), is);
            IOClass.writeFileTxt(builddir.resolve("test.srt"), tto.toSRT());
        }
    }

    @Test
    public void testASS() throws Exception {
        // To test the correct implementation of the ASS/SSA parser and writer.
        TimedTextFileFormat ttff = new FormatASS();
        File file = new File("ASS/test.ssa");
        try (InputStream is = getClass().getResourceAsStream(file.getPath())) {
            TimedTextObject tto = ttff.parseFile(file.getName(), is);
            IOClass.writeFileTxt(builddir.resolve("test.ssa"), tto.toASS());
        }
    }

    @Test
    public void testTTML() throws Exception {
        // To test the correct implementation of the TTML parser and writer.
        TimedTextFileFormat ttff = new FormatTTML();
        File file = new File("XML/Debate0_03-03-08.dfxp.xml");
        try (InputStream is = getClass().getResourceAsStream(file.getPath())) {
            TimedTextObject tto = ttff.parseFile(file.getName(), is);
            IOClass.writeFileTxt(builddir.resolve("test.xml"), tto.toTTML());
        }
    }

    @Test
    public void testSCC() throws Exception {
        // To test the correct implementation of the SCC parser and writer.
        TimedTextFileFormat ttff = new FormatSCC();
        File file = new File("SCC/sccTest.scc");
        try (InputStream is = getClass().getResourceAsStream(file.getPath())) {
            TimedTextObject tto = ttff.parseFile(file.getName(), is);
            IOClass.writeFileTxt(builddir.resolve("prueba.scc"), tto.toSCC());
        }
    }

    @Test
    public void testSTL() throws Exception {
        // To test the correct implementation of the STL parser and writer.
        TimedTextFileFormat ttff = new FormatSTL();
        File file = new File("STL/Alsalirdeclasebien.stl");
        try (InputStream is = getClass().getResourceAsStream(file.getPath())) {
            TimedTextObject tto = ttff.parseFile(file.getName(), is);
            try (OutputStream output = new BufferedOutputStream(
                new FileOutputStream(builddir.resolve("test.stl").toFile()))) {
                output.write(tto.toSTL());
            }
        }
    }
}
