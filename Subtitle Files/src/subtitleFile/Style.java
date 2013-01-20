package subtitleFile;

public class Style {
	
	private static int styleCounter;

	/**
	 * Constructor that receives a String to use a its identifier
	 * 
	 * @param styleName = identifier of this style
	 */
	protected Style(String styleName) {
		this.iD = styleName;
	}
	
	protected Style(String styleName, Style style) {
		this.iD = styleName;
		this.font = style.font;
		this.fontSize = style.fontSize;
		this.color = style.color;
		this.backgroundColor = style.backgroundColor;
		this.textAlign = style.textAlign;
		this.italic = style.italic;
		this.underline = style.underline;
		this.bold = style.bold;
		
	}

	/* ATTRIBUTES */
	protected String iD;
	protected String font;
	protected String fontSize;
	/**colors are stored as 6 chars long RGB*/
	protected String color;
	protected String backgroundColor;
	protected String textAlign;
	
	protected boolean italic;
	protected boolean bold;
	protected boolean underline;
	
	/* METHODS */
	
	/**
	 * returns a string with the correctly formated colors
	 * @param useASSInsteadOfSSA true if formated for ASS
	 * @return the colors in the decimal format
	 */
	protected String getColorsForASS(boolean useASSInsteadOfSSA) {
		String colors;
		if(useASSInsteadOfSSA)
			//primary color(BBGGRR) with Alpha level (00) in front + 00FFFFFF + 00000000 + background color(BBGGRR) with Alpha level (80) in front
			colors=Integer.parseInt("00"+ this.color.substring(4)+this.color.substring(2, 4)+this.color.substring(0, 2), 16)+",16777215,0,"+Long.parseLong("80"+ this.backgroundColor.substring(4)+this.backgroundColor.substring(2, 4)+this.backgroundColor.substring(0, 2), 16)+",";
		else
			//primary color(BBGGRR) + FFFFFF + 000000 + background color(BBGGRR)
			colors=Integer.parseInt(this.color.substring(4)+this.color.substring(2, 4)+this.color.substring(0, 2), 16)+",16777215,0,"+Integer.parseInt(this.backgroundColor.substring(4)+this.backgroundColor.substring(2, 4)+this.backgroundColor.substring(0, 2), 16)+",";	
		return colors;
	}
	
	/**
	 * returns a string with the correctly formated options
	 * @param useASSInsteadOfSSA
	 * @return
	 */
	protected String getOptionsForASS(boolean useASSInsteadOfSSA) {
		String options;
		if (bold)
			options="-1,";
		else
			options="0,";
		if (italic)
			options+="-1,";
		else
			options+="0,";
		if(useASSInsteadOfSSA){
			if (underline)
				options+="-1,";
			else
				options+="0,";
			options+="0,100,100,0,0,";
		}	
		return options;
	}
	
	/**
	 * To get the string containing the hex value to put into color or background color
	 * 
	 * @param format supported: "#RRGGBB", "&HBBGGRR", "&HAABBGGRR", "decimalCodedBBGGRR", "decimalCodedAABBGGRR"
	 * @param value RRGGBB string
	 * @return
	 */
	protected static String getRGBValue(String format, String value){
		String color = null;
		if (format.equalsIgnoreCase("#RRGGBB")){
			//standard color format from W3C
			color = value.substring(1);
		} else if (format.equalsIgnoreCase("&HBBGGRR")){
			//hex format from SSA
			color = value.substring(2);
			color = new StringBuffer(color).reverse().toString();
		} else if (format.equalsIgnoreCase("&HAABBGGRR")){
			//hex format from ASS
			color = value.substring(4);
			color = new StringBuffer(color).reverse().toString();
		} else if (format.equalsIgnoreCase("decimalCodedBBGGRR")){
			//normal format from SSA
			color = Integer.toHexString(Integer.parseInt(value));
			//any missing 0s are filled in
			while(color.length()<6)color="0"+color;
			//order is reversed
			color = color.substring(4)+color.substring(2,4)+color.substring(0,2);
		}  else if (format.equalsIgnoreCase("decimalCodedAABBGGRR")){
			//normal format from ASS
			color = Long.toHexString(Long.parseLong(value));
			//any missing 0s are filled in
			while(color.length()<8)color="0"+color;
			//order is reversed
			color = color.substring(6)+color.substring(4,6)+color.substring(2,4);
		}
		 return color;
	}

	protected static String defaultID() {
		return "default"+styleCounter++;
	}

	
}
