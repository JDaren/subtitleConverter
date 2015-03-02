package subtitleFile;

public class Caption {
	
	public Style style;
	public Region region;
	
	public Time start;
	public Time end;
	
	public String content="";

    @Override
    public String toString() {
        return "Caption{" +
                start + ".." + end +
                ", " + (style != null ? style.iD : null) + ", " + region + ": " + content +
                '}';
    }
}
