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


public class Pruebas {

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

				String inputFile = args[1];
				String inputFormat = args[0];
				String outputFormat = args[3];
				String outputFile = args[4];

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
					IOClass.escribirFicheroTxt(outputFile, tto.toSRT());
				} else if ("STL".equalsIgnoreCase(outputFormat)){
					output = new BufferedOutputStream(new FileOutputStream(outputFile));
					output.write(tto.toSTL());
				} else if ("SCC".equalsIgnoreCase(outputFormat)){
					IOClass.escribirFicheroTxt(outputFile, tto.toSCC());
				} else if ("XML".equalsIgnoreCase(outputFormat)){
					IOClass.escribirFicheroTxt(outputFile, tto.toTTML());
				} else if ("ASS".equalsIgnoreCase(outputFormat)){
					IOClass.escribirFicheroTxt(outputFile, tto.toASS());
				} else {
					throw new Exception("Unrecognized input format: "+outputFormat+" only [SRT,STL,SCC,XML,ASS] are possible");
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				output.close();
			}

			// normal test use
		} else {

			try {

				//To test the correct implementation of the SRT parser and writer.
				ttff = new FormatSRT();
				File file = new File("standards\\SRT\\Avengers.2012.Eng.Subs.srt");
				InputStream is = new FileInputStream(file);
				tto = ttff.parseFile(file.getName(), is);
				IOClass.escribirFicheroTxt("prueba.txt", tto.toSRT());

				//To test the correct implementation of the ASS/SSA parser and writer.
				ttff = new FormatASS();
				file = new File("standards\\ASS\\test.ssa");
				is = new FileInputStream(file);
				tto = ttff.parseFile(file.getName(), is);
				IOClass.escribirFicheroTxt("prueba.txt", tto.toASS());

				//To test the correct implementation of the TTML parser and writer.
				ttff = new FormatTTML();
				file = new File("standards\\XML\\Debate0_03-03-08.dfxp.xml");
				is = new FileInputStream(file);
				tto = ttff.parseFile(file.getName(), is);
				IOClass.escribirFicheroTxt("prueba.txt", tto.toTTML());

				//To test the correct implementation of the SCC parser and writer.
				ttff = new FormatSCC();
				file = new File("standards\\SCC\\sccTest.scc");
				is = new FileInputStream(file);
				tto = ttff.parseFile(file.getName(), is);
				IOClass.escribirFicheroTxt("prueba.txt", tto.toSCC());

				//To test the correct implementation of the STL parser and writer.
				ttff = new FormatSTL();
				file = new File("standards\\STL\\Alsalirdeclasebien.stl");
				is = new FileInputStream(file);
				tto = ttff.parseFile(file.getName(), is);
				try {
					output = new BufferedOutputStream(new FileOutputStream("prueba.txt"));
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
