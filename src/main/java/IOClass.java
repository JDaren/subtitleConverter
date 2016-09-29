import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * 
 * @author J. David
 * 
 * Class that handles reading and writing files or reads from keyboard
 *
 */
public class IOClass {

	private IOClass() {
	}

	/**
	 * Method to get the file name (or path relative to the directory) and file to write to
	 * in the form of an array of strings where each string represents a line
	 * 
	 * @param fileName name of the file (or path relative to directory)
	 * @param totalFile array of strings where each string represents a line in the file
	 */
	public static void writeFileTxt(String fileName, String[] totalFile){
		FileWriter file = null;
	    PrintWriter pw = null;
	    try
	    {
	        file = new FileWriter(System.getProperty("user.dir")+"/"+fileName);
	        pw = new PrintWriter(file);
	
	        for (int i = 0; i < totalFile.length; i++)
	            pw.println(totalFile[i]);
	
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	       try {
	       // Execute the "finally" to make sure the file is closed
	       if (null != file)
	          file.close();
	       } catch (Exception e2) {
	          e2.printStackTrace();
	       }
			
		   try {
		   	  if (pw != null)
				  pw.close();
		   } catch (Exception e2) {
		      e2.printStackTrace();
		   }
	    }
	}
	
	/**
	 * Method to get the file name (or path relative to the directory) and file to write to
	 * in the form of an array of strings where each string represents a line
	 * 
	 * @param fileName name of the file (or path relative to directory)
	 * @param s array of strings where each string represents a line in the file
	 */
	public static String[] readfileTxt(String fileName){
		
		String [] s = new String [0];
		String direccion = System.getProperty("user.dir")+"/"+ fileName;
		
		// Try to load the file (archive)
		File archive;
        FileReader fr = null;
        BufferedReader br = null;
		try {
	        // Open the file and create BufferedReader in order to
	        // reading easier (disposing the method readLine()).
	        archive = new File (direccion);
	        fr = new FileReader (archive);
	        br = new BufferedReader(fr);

	        // Reading the file
	        String line;
	        while((line=br.readLine())!=null){
	        	int n;
	        	String [] s2 = new String[s.length+1];
	        	for(n=0;n<s.length;n++)s2[n] = s[n];
	        	s2[n]=line.trim();
	        	s=s2;
	        }
	     }catch(Exception e){
	        System.err.println("File not found");
	        System.exit(-1);
	     }finally{
	        // In the "finally" block, try to close the file and ensure
	        // that it closes, otherwise, throw an exception
	        try{                    
	           if( null != fr ){   
	              fr.close();     
	           }                  
	        }catch (Exception e2){ 
	           e2.printStackTrace();
	        }
	        
			try{
			   if( br != null ){
			      br.close();
			   }
			}catch (Exception e2){
			   e2.printStackTrace();
			}
	     }
		return s;
	}
	
	/**
	 * Method to read a line from the keyboard
	 * 
	 * @return Returns the string being read
	 */
	public static String readTeclado(){
		String response = null;
		try{
			InputStreamReader isr = new InputStreamReader (System.in);
			BufferedReader br = new BufferedReader (isr);
			response = br.readLine();
		} catch (IOException e){
			System.err.println("Error reading keyboard, program complete");
			System.exit(-1);
		}
		return response;
	}
}
