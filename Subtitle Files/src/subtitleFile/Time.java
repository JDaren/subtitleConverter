package subtitleFile;

public class Time {
	
	/**
	 * Constructor to create a time object.
	 * 
	 * @param format supported formats: "hh:mm:ss,ms", "h:mm:ss.cs" and "h:m:s:f/fps"
	 * @param value  string in the correct format
	 */
	protected Time(String format, String value) {
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
		} else if (format.equalsIgnoreCase("h:m:s:f/fps")){
			int h, m, s, f;
			float fps;
			String[] args = value.split("/");
			fps = Float.parseFloat(args[1]);
			args = args[0].split(":");
			h = Integer.parseInt(args[0]);
			m = Integer.parseInt(args[1]);
			s = Integer.parseInt(args[2]);
			f = Integer.parseInt(args[3]);

			mseconds = (int)(f*1000/fps) + s*1000 + m*60000 + h*3600000;
		}
	}

	// in an integer we can store 24 days worth of milliseconds, no need for a long
	protected int mseconds;
	
	
	/* METHODS */
	
	/**
	 * Method to return a formatted value of the time stored
	 * 
	 * @param string supported formats: "hh:mm:ss,ms", "h:mm:ss.cs" and "hhmmssff/fps"
	 * @return formatted time in a string
	 */
	protected String getTime(String format) {
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
		} else if (format.startsWith("hhmmssff/")){
			//this format is used in EBU's STL
			int h, m, s, f;
			float fps;
			String[] args = format.split("/");
			fps = Float.parseFloat(args[1]);
			//now we concatenate time
			f = (mseconds%1000)*(int)fps/1000;
			time = ""+f;
			//we check if an extra 0 is needed
			if (time.length()==1) time = "0"+time;
			s = (mseconds/1000)%60;
			time += s;
			if (time.length()==3) time = "0"+time;
			m = (mseconds/60000)%60;
			time += m;
			if (time.length()==5) time = "0"+time;
			h =  mseconds/3600000;
			time += h;
			if (time.length()==7) time = "0"+time;
		} else if (format.startsWith("hh:mm:ss:ff/")){
			//this format is used in EBU's STL
			int h, m, s, f;
			float fps;
			String[] args = format.split("/");
			fps = Float.parseFloat(args[1]);
			//now we concatenate time
			f = (mseconds%1000)*(int)fps/1000;
			time = ""+f;
			//we check if an extra 0 is needed
			if (time.length()==1) time = "0"+time;
			s = (mseconds/1000)%60;
			time += ":" + s;
			if (time.length()==4) time = "0"+time;
			m = (mseconds/60000)%60;
			time += ":" + m;
			if (time.length()==7) time = "0"+time;
			h =  mseconds/3600000;
			time += ":" + h;
			if (time.length()==10) time = "0"+time;
		}

		return time;
	}
	
	

}
