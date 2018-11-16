package subtitleFile;

import java.util.Comparator;

/**
 * Provides some tools to create a Caption more easily
 *
 */
public class SubtitleTools {
	
	
	/**
	 * Comparator to order subtitle chronologically 
	 */
	public static class StartTimeComparator implements Comparator<Caption> {
	    @Override
	    public int compare(Caption a, Caption b) {
	    	if(a == null && b == null){
	    		return 0;
	    	}else if (a == null){
	    		return -1;
	    	}else if (b == null){
	    		return 1;
	    	}else if(a.start == null && b.start == null){
	    		return 0;
	    	}else if (a.start == null){
	    		return -1;
	    	}else if (b.start == null){
	    		return 1;
	    	}else if(a.start.mseconds < b.start.mseconds){
	    		return -1;
	    	}else if(a.start.mseconds > b.start.mseconds){
	    		return 1;
	    	}else{
		        return a.end.mseconds < b.end.mseconds ? -1 : a.end.mseconds == b.end.mseconds ? 0 : 1;
	    	}
	    }
	}
	
	/**
	 * Generate a protected TimedTextObject
	 * 
	 * @return an instance of TimedTextObject
	 */
	public static TimedTextObject getTimedTextObjectInstance() {
		return new TimedTextObject();
	}
	
	/**
	 * return the number of millisecond represented by this Time instance
	 * 
	 * @param time
	 * 
	 * @return the protected mseconds value
	 */
	public static int gettimeMsSecond(Time time){
		return time.mseconds;
	}
	
	/**
	 * create an instance of Time with the number of millisecond specified
	 * 
	 * @param millisecond
	 * 
	 * @return new instance of Time
	 */
	public static Time createNewTime(int millisecond) {
		return new Time(millisecond);
	}
	
}











 	
 
 	

