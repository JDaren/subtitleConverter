package subtitleFile;

public class Time {
	
	/**
	 * Constructor to create a time object.
	 * 
	 * @param format supported formats: "hh:mm:ss,ms", "h:mm:ss.cs"
	 * @param value  string in the correct format
	 */
	public Time(String format, String value) {
		if (format.equalsIgnoreCase("hh:mm:ss,ms")){
			// this type of format:  01:02:22,501 (used in .SRT)
			int h, m, s, ms;
			h = Integer.parseInt(value.substring(0, 2));
			m = Integer.parseInt(value.substring(3, 5));
			s = Integer.parseInt(value.substring(6, 8));
			ms = Integer.parseInt(value.substring(9, 12));
			
			mseconds = ms + s*1000 + m*60000 + h*3600000;
			
		} else if (format.equalsIgnoreCase("h:mm:ss.cs")){
			// this type of format:  1:02:22.51 (used in .ASS/.SSA) 
			int h, m, s, cs;
			h = Integer.parseInt(value.substring(0, 1));
			m = Integer.parseInt(value.substring(2, 4));
			s = Integer.parseInt(value.substring(5, 7));
			cs = Integer.parseInt(value.substring(8, 10));

			mseconds = cs*10 + s*1000 + m*60000 + h*3600000;
		}
	}

	// in an integer we can store 24 days worth of milliseconds, no need for a long
	int mseconds;
	
	
	/* METHODS */
	
	/**
	 * Method to return a formatted value of the time stored
	 * 
	 * @param string supported formats: "hh:mm:ss,ms", "h:mm:ss.cs"
	 * @return formatted time in a string
	 */
	public String getTime(String format) {
		String time = null;
		if(format.equalsIgnoreCase("hh:mm:ss,ms")){
			// this type of format:  01:02:22,501 (used in .SRT)
			int h, m, s, ms;
			ms = mseconds%1000;
			s = (mseconds/1000)%60;
			m = (mseconds/60000)%60;
			h =  mseconds/3600000;
			time = "" + h + ":" + m + ":" + s + "," + ms;
		} else if(format.equalsIgnoreCase("h:mm:ss.cs")){
			// this type of format:  1:02:22.51 (used in .ASS/.SSA)
			int h, m, s, cs;
			cs = (mseconds/10)%100;
			s = (mseconds/1000)%60;
			m = (mseconds/60000)%60;
			h =  mseconds/3600000;
			time = "" + h + ":" + m + ":" + s + "." + cs;
		}
		
		return time;
	}
	
	

}
