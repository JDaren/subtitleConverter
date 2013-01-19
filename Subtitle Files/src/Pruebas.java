import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import subtitleFile.TimedTextObject;


public class Pruebas {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TimedTextObject tto;
		
		try {

			//To test the correct implementation of the SRT parser and writter.
			String [] fileSRT = IOClass.leerFicheroTxt("standards\\SRT\\Avengers.2012.Eng.Subs.srt");
			tto = new TimedTextObject();
			tto.parseSRT(fileSRT,"probandoNombre");
			fileSRT = tto.toSRT();
			IOClass.escribirFicheroTxt("prueba.txt", fileSRT);
			
			//To test the correct implementation of the ASS/SSA parser and writter.
			String [] fileASS = IOClass.leerFicheroTxt("standards\\ASS\\test.ass");
			tto = new TimedTextObject();
			tto.parseASS(fileASS,"probandoNombre");
			fileASS = tto.toSRT();
			IOClass.escribirFicheroTxt("prueba.txt", fileASS);
			fileASS = tto.toASS();
			IOClass.escribirFicheroTxt("prueba.txt", fileASS);

			//To test the use of the XML parser
			File fXmlFile = new File(System.getProperty("user.dir")+"\\"+"standards\\XML\\Aqui_no_hay_quien_viva_1.dfxp.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("style");
			System.out.println("-----------------------");
			System.out.println(nList.getLength());


			/*
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					System.out.println("First Name : " + getTagValue("firstname", eElement));
					System.out.println("Last Name : " + getTagValue("lastname", eElement));
					System.out.println("Nick Name : " + getTagValue("nickname", eElement));
					System.out.println("Salary : " + getTagValue("salary", eElement));

				}
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/*
	 private static String getTagValue(String sTag, Element eElement) {
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		 
		        Node nValue = (Node) nlList.item(0);
		 
			return nValue.getNodeValue();
		  }
	*/
}
