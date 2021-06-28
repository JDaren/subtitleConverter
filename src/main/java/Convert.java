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


public class Convert {

	private Convert() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TimedTextObject tto;
		TimedTextFileFormat ttff;
		OutputStream output;

		//this is in case anyone may want to use this as stand alone java executable
		if (args != null && args.length >= 4){

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

                if (args.length >= 6) {
                    tto.removeWords(args[5]);
                }
                
				if (args.length >= 5 && "merge".equals(args[4])) {
				    tto.stripTags();
				    tto.mergeSingleWords();
				}
				
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
                System.out.println("Usage: java Convert input-file input-format output-format output-file [merge] [delWords]");
                System.out.println("The 'merge' option merges single words into the previous string.");
                System.out.println("The 'merge' option will strip any <...>-tags.");
                System.out.println("'delWords' is a an optional, comma-separated list of words to remove.");
                System.out.println("Arguments are identified by their position. Use 'nomerge' if you want to remove words");
                System.out.println("  without merging single words.");
		}

	}
}
