import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import subtitleFile.FormatASS;
import subtitleFile.FormatSCC;
import subtitleFile.FormatSRT;
import subtitleFile.FormatSTL;
import subtitleFile.FormatTTML;
import subtitleFile.TimedTextFileFormat;
import subtitleFile.TimedTextObject;


public class Tests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TimedTextObject tto;
		TimedTextFileFormat ttff;
		OutputStream output = null;

		//this is in case anyone may want to use this as stand alone java executable
		if (args != null && args.length == 4){

			try {

				String inputFile = args[0];
				String inputFormat = args[1];
				String outputFormat = args[2];
				String outputFile = args[3];

				if ("SRT".equalsIgnoreCase(inputFormat)){
					ttff = new FormatSRT();
				} else if ("STL".equalsIgnoreCase(inputFormat)){
					ttff = new FormatSTL();
				} else if ("SCC".equalsIgnoreCase(inputFormat)){
					ttff = new FormatSCC();
				} else if ("XML".equalsIgnoreCase(inputFormat)){
					ttff = new FormatTTML();
				} else if ("ASS".equalsIgnoreCase(inputFormat)){
					ttff = new FormatASS();
				} else {
					throw new Exception("Unrecognized input format: "+inputFormat+" only [SRT,STL,SCC,XML,ASS] are possible");
				}

				File file = new File(inputFile);
				InputStream is = new FileInputStream(file);
				tto = ttff.parseFile(file.getName(), is);

				if ("SRT".equalsIgnoreCase(outputFormat)){
					IOClass.writeFileTxt(outputFile, tto.toSRT());
				} else if ("STL".equalsIgnoreCase(outputFormat)){
					output = new BufferedOutputStream(new FileOutputStream(outputFile));
					output.write(tto.toSTL());
				    output.close();
				} else if ("SCC".equalsIgnoreCase(outputFormat)){
					IOClass.writeFileTxt(outputFile, tto.toSCC());
				} else if ("XML".equalsIgnoreCase(outputFormat)){
					IOClass.writeFileTxt(outputFile, tto.toTTML());
				} else if ("ASS".equalsIgnoreCase(outputFormat)){
					IOClass.writeFileTxt(outputFile, tto.toASS());
				} else {
					throw new Exception("Unrecognized input format: "+outputFormat+" only [SRT,STL,SCC,XML,ASS] are possible");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			// normal test use
		} else {

			try {

				//To test the correct implementation of the SRT parser and writer.
				ttff = new FormatSRT();
				File file = new File("../standards/SRT/Avengers.2012.Eng.Subs.srt");
				InputStream is = new FileInputStream(file);
				tto = ttff.parseFile(file.getName(), is);
				IOClass.writeFileTxt("test.srt", tto.toSRT());

				//To test the correct implementation of the ASS/SSA parser and writer.
				ttff = new FormatASS();
				file = new File("../standards/ASS/test.ssa");
				is = new FileInputStream(file);
				tto = ttff.parseFile(file.getName(), is);
				IOClass.writeFileTxt("test.ssa", tto.toASS());

				//To test the correct implementation of the TTML parser and writer.
				ttff = new FormatTTML();
				file = new File("../standards/XML/Debate0_03-03-08.dfxp.xml");
				is = new FileInputStream(file);
				tto = ttff.parseFile(file.getName(), is);
				IOClass.writeFileTxt("test.xml", tto.toTTML());

				//To test the correct implementation of the SCC parser and writer.
				ttff = new FormatSCC();
				file = new File("../standards/SCC/sccTest.scc");
				is = new FileInputStream(file);
				tto = ttff.parseFile(file.getName(), is);
				IOClass.writeFileTxt("prueba.scc", tto.toSCC());

				//To test the correct implementation of the STL parser and writer.
				ttff = new FormatSTL();
				file = new File("../standards/STL/Alsalirdeclasebien.stl");
				is = new FileInputStream(file);
				tto = ttff.parseFile(file.getName(), is);
				try {
					output = new BufferedOutputStream(new FileOutputStream("test.stl"));
					output.write(tto.toSTL());
				} finally {
					output.close();
				}


			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
}
