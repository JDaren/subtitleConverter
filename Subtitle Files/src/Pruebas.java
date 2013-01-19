import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import subtitleFile.FormatASS;
import subtitleFile.FormatSRT;
import subtitleFile.TimedTextFileFormat;
import subtitleFile.TimedTextObject;


public class Pruebas {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TimedTextObject tto;
		
		try {

			TimedTextFileFormat ttff;
			
			//To test the correct implementation of the SRT parser and writter.
			ttff = new FormatSRT();
			File file = new File("standards\\SRT\\Avengers.2012.Eng.Subs.srt");
			InputStream is = new FileInputStream(file);
			tto = ttff.parseFile(file.getName(), is);
			IOClass.escribirFicheroTxt("prueba.txt", ((FormatSRT)ttff).toFile(tto));
			
			//To test the correct implementation of the ASS/SSA parser and writter.
			ttff = new FormatASS();
			file = new File("standards\\ASS\\test.ass");
			is = new FileInputStream(file);
			tto = ttff.parseFile(file.getName(), is);
			IOClass.escribirFicheroTxt("prueba.txt", ((FormatASS)ttff).toFile(tto));



		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
