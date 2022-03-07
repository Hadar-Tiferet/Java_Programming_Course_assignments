import java.util.TreeMap;
import javax.xml.bind.annotation.XmlElement;
// class used in the process of saving to and loading from an xml file

public class TreeMapXML {
	@XmlElement(name="contact")
	private TreeMap<String, ContactInformation> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	
	public TreeMap<String, ContactInformation> getTreeMap() {return treeMap;}
}
