package subtitleFile;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;

public class TimedTextObject {
	
	/*
	 * Attributes
	 * 
	 */
	//meta info
	public String title;
	public String description;
	public String copyrigth;
	public String author;
	public String fileName;
	public String language;
	
	//list of styles (id, reference)
	public Hashtable<String, Style> styling;
	
	//list of layouts (id, reference)
	public Hashtable<String, Region> layout;
	
	//list of captions (begin time, reference)
	//represented by a tree map to maintain order
	public TreeMap<Integer, Caption> captions;
	
	//to store non fatal errors produced during parsing
	public String warnings;
	
	//options
	//to know whether file should be saved as .ASS or .SSA
	public boolean useASSInsteadOfSSA = false;
	//to delay or advance the subtitles, parsed into +/- milliseconds
	public int offset = 0;
	
	
	
	/**
	 * Constructor
	 */
	public TimedTextObject(){
		
		styling = new Hashtable<String, Style>();
		layout = new Hashtable<String, Region>();
		captions = new TreeMap<Integer, Caption>(); 
		
		warnings = "List of non fatal errors produced during parsing:\n\n";
		
	}
	
	/*
	 * Parsing Methods
	 * 
	 */
	/**
	 * Method to parse the .SRT file
	 * 
	 * @param file a String array where each String represents a line of the file.
	 * 
	 */
	public void parseSRT(String[] file, String name){
		
		Caption caption = new Caption();
		int captionNumber = 1;
		boolean allGood;
		this.fileName = name;

		try {
			for (int i = 0; i < file.length; i++) {
				//if its a blank line, ignore it
				if (!file[i].isEmpty()){
					allGood = false;
					//the first thing should be an increasing number
					try {
						int num = Integer.parseInt(file[i]);
						if (num != captionNumber)
							throw new Exception();
						else {
							captionNumber++;
							allGood = true;
						}
					} catch (Exception e) {
						warnings+= captionNumber + " expected at line " + i;
						warnings+= "\n skipping to next line\n\n";
					}
					if (allGood){
						//we go to next line, here the begin and end time should be found
						try {
							i++;
							String line = file[i].trim();
							String start = line.substring(0, 12);
							String end = line.substring(line.length()-12, line.length());
							Time time = new Time("hh:mm:ss,ms",start);
							caption.start = time;
							time = new Time("hh:mm:ss,ms",end);
							caption.end = time;
						} catch (Exception e){
							warnings += "incorrect time format at line "+i;
							allGood = false;
						}
					}
					if (allGood){
						//we go to next line where the caption text starts
						i++;
						String text = "";
						while (!file[i].isEmpty()){
							text+=file[i]+"<br />";
							i++;
						}
						caption.content = text;
						int key = caption.start.mseconds;
						//in case the key is already there, we increase it by a millisecond, since no duplicates are allowed
						while (this.captions.containsKey(key)) key++;
						if (key != caption.start.mseconds)
							warnings+= "caption with same start time found...\n\n";
						//we add the caption.
						this.captions.put(key, caption);
					}
					//we go to next blank
					while (!file[i].isEmpty()) i++;
					caption = new Caption();
				}
			}
		} catch (IndexOutOfBoundsException e){
			warnings+= "unexpected end of file, maybe last caption is not complete.\n\n";
		}
	}
	
	/**
	 * Method to parse the .ASS file
	 * 
	 * @param file a String array where each String represents a line of the file.
	 *
	 */
	public void parseASS(String[] file, String name){
		Caption caption = new Caption();
		Style style;
		this.fileName = name;
		int line = 0;
		String [] styleFormat;
		String [] dialogueFormat;
		float timer = 100;
		boolean isASS = false;
		
		try{
			//we scour the file
			while (line < file.length){
				//we skip any line until we find a section [section name]
				if(file[line].startsWith("[")){
					//now we must identify the section
					if(file[line].trim().equalsIgnoreCase("[Script info]")){
						//its the script info section section
						line++;
						//Each line is scanned for useful info until a new section is detected
						while (!file[line].startsWith("[")){
							if(file[line].startsWith("Title:"))
								//We have found the title
								this.title = file[line].split(":")[1].trim();
							else if (file[line].startsWith("Original Script:"))
								//We have found the author
								this.author = file[line].split(":")[1].trim();
							else if (file[line].startsWith("Script Type:"))
								//we have found the version
								if(file[line].split(":")[1].trim().equalsIgnoreCase("v4.00+"))isASS = true;
							else if (file[line].startsWith("Timer:"))
								//We have found the timer
								timer = Float.parseFloat(file[line].split(":")[1].trim());
							//we go to the next line
							line++;
						}
						// since a new section has been reached we go back so it can be identified
						line--;

					} else if (file[line].trim().equalsIgnoreCase("[v4 Styles]") 
							|| file[line].trim().equalsIgnoreCase("[v4 Styles+]") 
							|| file[line].trim().equalsIgnoreCase("[v4+ Styles]")){
						//its the Styles description section
						if(file[line].contains("+")&&isASS==false){
							//its ASS and it had not been noted
							isASS=true;
							this.warnings+="ScriptType should be set to v4:00+ in the [Script Info] section.\n\n";
						}
						line++;
						//the first line should define the format
						if(!file[line].startsWith("Format:")){
							//if not, we scan for the format.
							warnings+="Format: (format definition) expected at line "+line+" for the styles section\n\n";
							while (!file[line].startsWith("Format:"))
								line++;
						}
						// we recover the format's fields
						styleFormat = file[line++].split(":")[1].trim().split(",");
						// we parse each style until we reach a new section
						while (!file[line].startsWith("[")){
							//we check it is a style
							if (file[line].startsWith("Style:")){
								//we parse the style
								style = parseStyleForASS(file[line].split(":")[1].trim().split(","),styleFormat,line,isASS);
								//and save the style
								this.styling.put(style.iD, style);
							}
							//next line
							line++;
						}
						// since a new section has been reached we go back so it can be identified
						line--;
					} else if (file[line].trim().equalsIgnoreCase("[Events]")){
						//its the events specification section
						line++;
						warnings+="Only dialogue events are considered, all other events are ignored.";
						//the first line should define the format of the dialogues
						if(!file[line].startsWith("Format:")){
							//if not, we scan for the format.
							warnings+="Format: (format definition) expected at line "+line+" for the events section\n\n";
							while (!file[line].startsWith("Format:"))
								line++;
						}
						// we recover the format's fields
						dialogueFormat = file[line++].split(":")[1].trim().split(",");
						// we parse each style until we reach a new section
						while (!file[line].startsWith("[")){
							//we check it is a dialogue
							//WARNING: all other events are ignored.
							if (file[line].startsWith("Dialogue:")){
								//we parse the dialogue
								caption = parseDialogueForASS(file[line].split(":",2)[1].trim().split(",",10),dialogueFormat,timer);
								//and save the caption
								int key = caption.start.mseconds;
								//in case the key is already there, we increase it by a millisecond, since no duplicates are allowed
								while (this.captions.containsKey(key)) key++;
								this.captions.put(caption.start.mseconds, caption);
							}
							//next line
							line++;
						}
						// since a new section has been reached we go back so it can be identified
						line--;

					} else if (file[line].trim().equalsIgnoreCase("[Fonts]") || file[line].trim().equalsIgnoreCase("[Graphics]")){
						//its the custom fonts or embedded graphics section
						//these are not supported
						warnings+= "The section "+file[line].trim()+" is not supported for conversion, all information there will be lost.\n\n";
					} else {
						warnings+= "Unrecognized section: "+file[line].trim()+" all information there is ignored.";
					}	
				}
				line++;
			}
			// parsed styles that are not used should be eliminated
			cleanUnusedStyles();
		} catch (IndexOutOfBoundsException e){
			warnings+= "unexpected end of file, parsing error has ocurred. Maybe the file is not complete or contains errors.\n\n";
		}
	
	}
	

	/**
	 * Method to parse the .STL file
	 */
	public void parseSTL(String path){

		File file = new File(path);
		InputStream is = null;
		
		this.fileName = file.getName();
		
		byte [] gsiBlock = new byte [1024];
		byte [] ttiBlock = new byte [128];

		if(file.length()<1152) {
			//the file must contain at least a GSI block and a TTI block
			//this is a fatal parsing error.

		} else {
			//we read the file
			try {
				is = new BufferedInputStream(new FileInputStream(file));
				int bytesRead;
				//the GSI block is loaded
				bytesRead = is.read(gsiBlock);
				//CPC : code page number 0..2
				//DFC : disk format code 3..10
				//save the number of frames per second
				byte[] dfc = {gsiBlock[7],gsiBlock[8]};
				int fps = Integer.parseInt(new String(dfc));
				//DSC : Display Standard Code 11
				//CCT : Character Code Table number 12..13
				//LC : Language Code 14..15
				//OPT : Original Programme Title 16..47
				byte[] opt = new byte [32];
				System.arraycopy(gsiBlock, 16, opt, 0, 32);
				String title = new String(opt);
				//OEP : Original Episode Title 48..79
				byte[] oet = new byte [32];
				System.arraycopy(gsiBlock, 48, oet, 0, 32);
				String episodeTitle = new String(oet);
				//TPT : Translated Programme Title 80..111
				//TEP : Translated Episode Title 112..143
				//TN : Translator's Name 144..175
				//TCD : Translators Contact Details 176..207
				//SLR : Subtitle List Reference code 208..223
				//CD : Creation Date 224..229
				//RD : Revision Date 230..235
				//RN : Revision Number 236..237
				//TNB : Total Number of TTI Blocks 238..242
				byte[] tnb = {gsiBlock[238],gsiBlock[239],gsiBlock[240],gsiBlock[241],gsiBlock[242]};
				int numberOfTTIBlocks = Integer.parseInt(new String(tnb));
				//TNS : Total Number of Subtitles 243..247
				byte[] tns = {gsiBlock[243],gsiBlock[244],gsiBlock[245],gsiBlock[246],gsiBlock[247]};
				int numberOfSubtitles = Integer.parseInt(new String(tns));
				//TNG : Total Number of Subtitle Groups 248..250
				//MNC : Max Number of characters in row 251..252
				//MNR : Max number of rows 253..254
				//TCS : Time Code: Status 255
				//TCP : Time Code: Start-of-Programme 256..263
				//TCF : Time Code: First In-Cue 264..271
				//TND : Total Number of Disks 272
				//DSN : Disk Sequence Number 273
				//CO : Country of Origin 274..276
				//PUB : Publisher 277..308
				//EN : Editor's Name 309..340
				//ECD : Editor's Contact Details 341..372
				// Spare bytes 373..447
				//UDA : User-Defined Area 448..1023
				
				this.title = (title.trim()+" "+episodeTitle.trim()).trim();
				
				int subtitleNumber = 0;
				//the TTI blocks are read
				for (int i = 0; i < numberOfTTIBlocks; i++) {
					//the TTI block is loaded
					bytesRead = is.read(ttiBlock);
					if (bytesRead < 1){
						//unexpected end of file
						this.warnings += "Unexpected end of file, "+i+" blocks read, expecting "+numberOfTTIBlocks+" blocks in total.";
						break;
					}
					//SGN
					//SN
					//EBN
					//CS
					//TCI
					//TCO
					//VP
					//JC
					//CF
					//TF
				}



			} catch (Exception e){
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * Method to parse the .SCC file
	 */
	public void parseSCC(){
	
	}
	
	/**
	 * Method to parse the .TTML file
	 * 
	 * @param path to the XML file
	 * 
	 */
	public void parseTTML(String path){
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(path);
			doc.getDocumentElement().normalize();
			
			
			
			
			// parsed styles that are not used should be eliminated
			cleanUnusedStyles();
			
			
		} catch (Exception e) {
			// Unexpected error
			e.printStackTrace();
		}
	}
	
	
	
	
	/*
	 * Writing Methods
	 * 
	 */
	/**
	 * Method to generate the .SRT file
	 * 
	 * @return an array of strings where each String represents a line
	 */
	public String[] toSRT(){
	
		//we will write the lines in an ArrayList,
		int index = 0;
		//the minimum size of the file is 4*number of captions, so we'll take some extra space.
		ArrayList<String> file = new ArrayList<String>(5*captions.size());
		//we iterate over our captions collection, they are ordered since they come from a TreeMap
		Collection<Caption> c = this.captions.values();
	    Iterator<Caption> itr = c.iterator();
	    int captionNumber = 1;

	    while(itr.hasNext()){
	    	//new caption
	    	Caption current = itr.next();
	    	//number is written
	    	file.add(index++,""+captionNumber++);
	    	//we check for offset value:
	    	if(offset != 0){
	    		current.start.mseconds += offset;
	    		current.end.mseconds += offset;
	    	}
	    	//time is written
	    	file.add(index++,current.start.getTime("hh:mm:ss,ms")+" --> "+current.end.getTime("hh:mm:ss,ms"));
	    	//offset is undone
	    	if(offset != 0){
	    		current.start.mseconds -= offset;
	    		current.end.mseconds -= offset;
	    	}
	    	//text is added
	    	String[] lines = cleanTextForSRT(current);
	    	int i=0;
	    	while(i<lines.length)
	    		file.add(index++,""+lines[i++]);
	    	//we add the next blank line
	    	file.add(index++,"");
	    }
		
	    String[] toReturn = new String [file.size()];
	    for (int i = 0; i < toReturn.length; i++) {
			toReturn[i] = file.get(i);
		}
		return toReturn;
	}

	
	/**
	 * Method to generate the .ASS file
	 * 
	 * @return an array of strings where each String represents a line
	 */
	public String[] toASS(){
		
		//we will write the lines in an ArrayList 
		int index = 0;
		//the minimum size of the file is the number of captions and styles + lines for sections and formats and the script info, so we'll take some extra space.
		ArrayList<String> file = new ArrayList<String>(30+styling.size()+captions.size());
		
	//header is placed
		file.add(index++,"[Script Info]");
		//title next
		String title = "Title: ";
		if (this.title == null || this.title.isEmpty())
			title += this.fileName;
		else title += this.title;
		file.add(index++,title);
		//author next
		String author = "Original Script: ";
		if (this.author == null || this.author.isEmpty())
			author += "Unknown";
		else author += this.author;
		file.add(index++,author);
		//additional info
		if (this.copyrigth != null && !this.copyrigth.isEmpty())
			file.add(index++,"; "+this.copyrigth);
		if (this.description != null && !this.description.isEmpty())
			file.add(index++,"; "+this.description);
		file.add(index++,"; Converted by the Online Subtitle Converter developed by J. David Requejo");
		//mandatory info
		if (useASSInsteadOfSSA)
			file.add(index++,"Script Type: V4.00+");
		else file.add(index++,"Script Type: V4.00");
		file.add(index++,"Collisions: Normal");
		file.add(index++,"Timer: 100,0000");
		if (useASSInsteadOfSSA)
			file.add(index++,"WrapStyle: 1");
		//an empty line is added
		file.add(index++,"");
		
	//Styles section
		if (useASSInsteadOfSSA)
			file.add(index++,"[V4+ Styles]");
		else file.add(index++,"[V4 Styles]");
		//define the format
		if (useASSInsteadOfSSA)
			file.add(index++,"Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding");
		else file.add(index++,"Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, TertiaryColour, BackColour, Bold, Italic, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, AlphaLevel, Encoding");
		//Next we iterate over the styles
		Iterator<Style> itrS = styling.values().iterator();
		while(itrS.hasNext()){
			String styleLine = "Style: ";
			//new style
	    	Style current = itrS.next();
	    	//name
	    	styleLine+= current.iD+",";
	    	styleLine+= current.font+",";
	    	styleLine+= current.fontSize+",";
	    	styleLine+= current.getColorsForASS(useASSInsteadOfSSA);
	    	styleLine+= current.getOptionsForASS(useASSInsteadOfSSA);
	    	//BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV
	    	styleLine+= "1,2,2,2,";
	    	//AlphaLevel
	    	if(!useASSInsteadOfSSA)styleLine+= "0,";
	    	//Encoding
	    	styleLine+= "0";

			//and we add the style definition line
			file.add(index++,styleLine);
		}
		//an empty line is added
		file.add(index++,"");
		
	//Events section
		file.add(index++,"[Events]");
		//define the format
		if (useASSInsteadOfSSA)
			file.add(index++,"Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text");
		else file.add(index++,"Format: Marked, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text");
		//Next we iterate over the captions
		Iterator<Caption> itrC = captions.values().iterator();
		while(itrC.hasNext()){
			//for each caption
			String line = "Dialogue: 0,";
			//new caption
	    	Caption current = itrC.next();
	    	//offset is applied
	    	if(offset != 0){
	    		current.start.mseconds += offset;
	    		current.end.mseconds += offset;
	    	}
	    	//start time
	    	line+= current.start.getTime("h:mm:ss.cs")+",";
	    	//end time
	    	line+= current.end.getTime("h:mm:ss.cs")+",";
	    	//offset is undone
	    	if(offset != 0){
	    		current.start.mseconds -= offset;
	    		current.end.mseconds -= offset;
	    	}
	    	//style
	    	if (current.style != null)
	    		line+= current.style.iD;
	    	else
	    		line+="Default";
	    	//default margins are used, no name or effect is recognized
	    	line+=",,0000,0000,0000,,";
	    	
	    	//we add the caption text with \N as line breaks  and clean of XML
			line +=  current.content.replaceAll("<br />","\\N").replaceAll("\\<.*?\\>", "");
			//and we add the caption line
			file.add(index++,line);
		}
		//an empty line is added
		file.add(index++,"");
		
		//we return the expected file as an array of String
		String[] toReturn = new String [file.size()];
	    for (int i = 0; i < toReturn.length; i++) {
			toReturn[i] = file.get(i);
		}
		return toReturn;
	}
	
	/**
	 * Method to generate the .STL file
	 */
	public void toSTL(){
	
	}
	
	/**
	 * Method to generate the .SCC file
	 * @return 
	 */
	public String[] toSCC(){
	
		//we will write the lines in an ArrayList 
		int index = 0;
		//the minimum size of the file is double the number of captions since lines are double spaced.
		ArrayList<String> file = new ArrayList<String>(20 + 2*captions.size());

		
		
		//an empty line is added
		file.add(index++,"");

		//we return the expected file as an array of String
		String[] toReturn = new String [file.size()];
		for (int i = 0; i < toReturn.length; i++) {
			toReturn[i] = file.get(i);
		}
		return toReturn;
	}
	
	/**
	 * Method to generate the .XML file
	 * @return 
	 */
	public String[] toTTML(){
		
		//we will write the lines in an ArrayList 
		int index = 0;
		//the minimum size of the file is the number of captions and styles + lines for sections and formats and the metadata, so we'll take some extra space.
		ArrayList<String> file = new ArrayList<String>(30+styling.size()+captions.size());
		
		//identification line is placed
		file.add(index++,"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		//root element is placed
		file.add(index++,"<tt xml:lang=\""+this.language+"\" xmlns=\"http://www.w3.org/ns/ttml\" xmlns:tts=\"http://www.w3.org/ns/ttml#styling\">");
	//head
		file.add(index++,"\t<head>");
		//metadata
		file.add(index++,"\t\t<metadata xmlns:ttm=\"http://www.w3.org/ns/ttml#metadata\">");
		//title
		String title;
		if (this.title == null || this.title.isEmpty())
			title = this.fileName;
		else title = this.title;
		file.add(index++,"\t\t\t <ttm:title>"+title+"</ttm:title>");
		//Copyright
		if (this.copyrigth != null && !this.copyrigth.isEmpty())
			file.add(index++,"\t\t\t <ttm:copyright>"+this.copyrigth+"</ttm:copyright>");
		//additional info
		String desc = "Converted by the Online Subtitle Converter developed by J. David Requejo\n";
		if (this.author != null && !this.author.isEmpty())
			desc+="\n Original file by: "+this.author;
		file.add(index++,"\t\t\t <ttm:desc>"+desc+"</ttm:desc>");
		
		//metadata closes
		file.add(index++,"\t\t</metadata>");
	//styling opens
		file.add(index++,"\t\t<styling>");

		//Next we iterate over the styles
		Iterator<Style> itrS = styling.values().iterator();
		while(itrS.hasNext()){
			
			
			
		}

		//styling closes
		file.add(index++,"\t\t</styling>");
		
		//head closes
		file.add(index++,"\t</head>");
	//body opens
		file.add(index++,"\t<body>");
		//unique div opens
		file.add(index++,"\t\t<div>");
		
		//Next we iterate over the captions
		Iterator<Caption> itrC = captions.values().iterator();
		while(itrC.hasNext()){
			
			
			
		}

		//unique div closes
		file.add(index++,"\t\t</div>");
		//body closes
		file.add(index++,"\t</body>");
		//root closes
		file.add(index++,"\t</tt>");
		
		//an empty line is added
		file.add(index++,"");

		//we return the expected file as an array of String
		String[] toReturn = new String [file.size()];
		for (int i = 0; i < toReturn.length; i++) {
			toReturn[i] = file.get(i);
		}
		return toReturn;
	}
	
	/* 
	 * PRIVATE METHODS 
	 * 
	 */
	
	
	/**
	 * This method cleans caption.content of XML and parses line breaks.
	 * 
	 */
	private String[] cleanTextForSRT(Caption current) {
		String[] lines;
		String text = current.content;
		//add line breaks
		lines = text.split("<br />");
		//clean XML
		for (int i = 0; i < lines.length; i++){
			//this will destroy all remaining XML tags
			lines[i].replaceAll("\\<.*?\\>", "");
		}
		return lines;
	}
	
	
	/**
	 * This methods transforms a format line from ASS according to a format definition into an Style object.
	 * 
	 * @param line the format line without its declaration
	 * @param styleFormat the list of attributes in this format line
	 * @return a new Style object.
	 */
	private Style parseStyleForASS(String[] line, String[] styleFormat, int index, boolean isASS) {

		Style newStyle = new Style(Style.defaultID());
		if (line.length != styleFormat.length){
			//both should have the same size
			this.warnings+="incorrectly formated line at "+index+"\n\n";
		} else {
			for (int i = 0; i < styleFormat.length; i++) {
				//we go through every format parameter and save the interesting values
				if (styleFormat[i].trim().equalsIgnoreCase("Name")){
					//we save the name
					newStyle.iD=line[i].trim();
				} else if (styleFormat[i].trim().equalsIgnoreCase("Fontname")){
					//we save the font
					newStyle.font=line[i].trim();
				} else if (styleFormat[i].trim().equalsIgnoreCase("Fontsize")){
					//we save the size
					newStyle.fontSize=line[i].trim();
				}else if (styleFormat[i].trim().equalsIgnoreCase("PrimaryColour")){
					//we save the color
					String color =line[i].trim();
					if(isASS){
						if(color.startsWith("&H")) newStyle.color=Style.getRGBValue("&HAABBGGRR", color);
						else  newStyle.color=Style.getRGBValue("decimalCodedAABBGGRR", color);
					} else {
						if(color.startsWith("&H")) newStyle.color=Style.getRGBValue("&HBBGGRR", color);
						else  newStyle.color=Style.getRGBValue("decimalCodedBBGGRR", color);
					}
				}else if (styleFormat[i].trim().equalsIgnoreCase("BackColour")){
					//we save the background color
					String color =line[i].trim();
					if(isASS){
						if(color.startsWith("&H")) newStyle.backgroundColor=Style.getRGBValue("&HAABBGGRR", color);
						else  newStyle.backgroundColor=Style.getRGBValue("decimalCodedAABBGGRR", color);
					} else {
						if(color.startsWith("&H")) newStyle.backgroundColor=Style.getRGBValue("&HBBGGRR", color);
						else  newStyle.backgroundColor=Style.getRGBValue("decimalCodedBBGGRR", color);
					}
				}else if (styleFormat[i].trim().equalsIgnoreCase("Bold")){
					//we save if bold
					newStyle.bold=Boolean.parseBoolean(line[i].trim());
				}else if (styleFormat[i].trim().equalsIgnoreCase("Italic")){
					//we save if italic
					newStyle.italic=Boolean.parseBoolean(line[i].trim());
				}else if (styleFormat[i].trim().equalsIgnoreCase("Underline")){
					//we save if underlined
					newStyle.underline=Boolean.parseBoolean(line[i].trim());
				}else if (styleFormat[i].trim().equalsIgnoreCase("Alignment")){
					//we save the alignment
					int placement =Integer.parseInt(line[i].trim());
					if (isASS){
						switch(placement){
						case 1:
							newStyle.textAlign="bottom-left";
							break;
						case 2:
							newStyle.textAlign="bottom-center";
							break;
						case 3:
							newStyle.textAlign="bottom-right";
							break;
						case 4:
							newStyle.textAlign="mid-left";
							break;
						case 5:
							newStyle.textAlign="mid-center";
							break;
						case 6:
							newStyle.textAlign="mid-right";
							break;
						case 7:
							newStyle.textAlign="top-left";
							break;
						case 8:
							newStyle.textAlign="top-center"; 
							break;
						case 9:
							newStyle.textAlign="top-right";
							break;
						default:
							this.warnings+="undefined alignment for style at line "+index+"\n\n";
						}
					} else {
						switch(placement){
						case 9:
							newStyle.textAlign="bottom-left";
							break;
						case 10:
							newStyle.textAlign="bottom-center";
							break;
						case 11:
							newStyle.textAlign="bottom-right";
							break;
						case 1:
							newStyle.textAlign="mid-left";
							break;
						case 2:
							newStyle.textAlign="mid-center";
							break;
						case 3:
							newStyle.textAlign="mid-right";
							break;
						case 5:
							newStyle.textAlign="top-left";
							break;
						case 6:
							newStyle.textAlign="top-center"; 
							break;
						case 7:
							newStyle.textAlign="top-right";
							break;
						default:
							this.warnings+="undefined alignment for style at line "+index+"\n\n";
						}
					}
				}
				
				
			}
		}

		
		return newStyle;
	}
	
	/**
	 * This methods transforms a dialogue line from ASS according to a format definition into an Caption object.
	 * 
	 * @param line the dialogue line without its declaration
	 * @param dialogueFormat the list of attributes in this dialogue line
	 * @param timer % to speed or slow the clock, above 100% span of the subtitles is reduced.
	 * @return a new Caption object
	 */
	private Caption parseDialogueForASS(String[] line, String[] dialogueFormat, float timer) {
		
		Caption newCaption = new Caption();
		
		//all information from fields 10 onwards are the caption text therefore needn't be split
		String captionText = line[9];
		//text is cleaned before being inserted into the caption
		newCaption.content = captionText.replaceAll("\\{.*?\\}", "").replace("\n", "<br />").replace("\\N", "<br />");		
		
		for (int i = 0; i < dialogueFormat.length; i++) {
			//we go through every format parameter and save the interesting values
			if (dialogueFormat[i].trim().equalsIgnoreCase("Style")){
				//we save the style
				Style s =  this.styling.get(line[i].trim());
				if (s!=null)
					newCaption.style= s;
				else
					this.warnings+="undefined style: "+line[i].trim()+"\n\n";
			} else if (dialogueFormat[i].trim().equalsIgnoreCase("Start")){
				//we save the starting time
				newCaption.start=new Time("h:mm:ss.cs",line[i].trim());
			} else if (dialogueFormat[i].trim().equalsIgnoreCase("End")){
				//we save the starting time
				newCaption.end=new Time("h:mm:ss.cs",line[i].trim());
			}
		}
		
		//timer is applied
    	if (timer != 100){
    		newCaption.start.mseconds /= (timer/100);
    		newCaption.end.mseconds /= (timer/100);
    	}
		return newCaption;
	}
	
	/**
	 * This method simply checks the style list and eliminate any style not referenced by any caption
	 * This might come useful when default styles get created and cover too much.
	 * 
	 */
	private void cleanUnusedStyles(){
		
	}

}
