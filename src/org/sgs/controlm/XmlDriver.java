package org.sgs.controlm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;


/*
 * Proof of concept on how to hydrate java objects from Control-M generated xml.
 */
public class XmlDriver{
	
	// Default xml
	private static final String INPUT_XML_FILE = "data/controlm.xml";
	
	// Hydrated POJO from XML file
	private DEFTABLEType tables;
	
	
	
	public XmlDriver(){
		this(INPUT_XML_FILE);
	}
	
	public XmlDriver(String filePath){
		unmarshallTables(filePath);
	}
	
	
	
	private void unmarshallTables(String filePath){
        
		File file = FileUtils.getFile(INPUT_XML_FILE);

        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(DEFTABLEType.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            this.tables = (DEFTABLEType)jaxbUnmarshaller.unmarshal(file);
        }catch(JAXBException e){
            throw new RuntimeException(e);
        }
        
	}
	
	
	private List<JOBType> getAllJobs(){
		List<JOBType> jobList = new ArrayList<JOBType>();
        for(TABLEType table : tables.getTABLE()){
            for(JOBType job : table.getJOB()){
            	jobList.add(job);
            }//for job
        }//for table
        return jobList;
	}
	
	
	private Pair<String, String> detokenizeEmail(String token, JOBType job){
		for(AUTOEDIT2Type editType : job.getAUTOEDIT2()){
			String name = editType.getNAME(); 
			if(name.equals(token)){
				name = name.replace("%%", "");
				name = job.getJOBNAME() + "_" + name;
				String email = editType.getValue().toLowerCase();
				return new Pair<String, String>(name, email);
			}
		}
		throw new RuntimeException(String.format("Could not find email for token '%s' in the job '%s'.", token, job.getJOBNAME()));
	}
	
	private Pair<String, String> scrubEmail(String email, JOBType job){
		email = email.trim();
		email = email.toLowerCase();
		return new Pair<String, String>(job.getJOBNAME(), email);
	}
	
	private Pair<String, String> getTokenEmailPair(String email, JOBType job){
		if(email.startsWith("%%")){
			return detokenizeEmail(email, job);
		}else{
			return scrubEmail(email, job);
		}
	}
	
	
	public Map<String, Set<String>> getEmailMap(){

		Map<String, Set<String>> tokenToEmailsMap = new TreeMap<String, Set<String>>();
		
			List<JOBType> jobList = getAllJobs();
            for(JOBType job : jobList){
                for(ONType onCondition : job.getON()){
                    for(Object doObject : onCondition.getDOMAILOrDOOrDOCOND()){
                        if(doObject instanceof DOMAILType){
                            DOMAILType doMail = (DOMAILType)doObject;
                            String emailString = doMail.getDEST();
                            emailString = emailString.replaceAll(" ", "");
                            String[] emails = emailString.split(";");
                            for(String email : emails){
                            	Pair<String, String> pair = getTokenEmailPair(email, job);
                            	String token = pair.getFirst();
                            	String processedEmail = pair.getSecond();
                            	Set<String> testSet = tokenToEmailsMap.get(pair.getFirst());
                            	if(testSet == null){
                            		testSet = new TreeSet<String>();
                            		testSet.add(processedEmail);
                            		tokenToEmailsMap.put(token, testSet);
                            	}
                            }
                        }//if doMail
                    }//for doAction
                }// for onCondition
            }//for job
            
            return getEmailMap();

	}
	
    public static void main(final String[] args){
    	
    	XmlDriver driver = new XmlDriver();



    }
}
