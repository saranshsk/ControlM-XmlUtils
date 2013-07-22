package org.sgs.controlm;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	
	// Magic strings
	private static final String DELIMITER = ";"; // email seperator in xml
	private static final String TOKEN_CHARS = "%%"; // Control-M tokens start with this
	
	// Created by decrypting "data/controlm_xml.tgz.gpg"
	private static final String INPUT_XML_FILE = "data/controlm_prd_2013-07-22.xml";
	
	// Some of the Control-M vars apparently have snuck in some mutt arguments --
	// this set will scrub any of these artifacts away
	private static final Set<String> EMAIL_ARTIFACTS;
	static{
		EMAIL_ARTIFACTS = new LinkedHashSet<String>();
		EMAIL_ARTIFACTS.add("-c ");
	}
	
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
	
	
	public List<JOBType> getAllJobs(){
		List<JOBType> jobList = new ArrayList<JOBType>();
        for(TABLEType table : tables.getTABLE()){
            for(JOBType job : table.getJOB()){
            	jobList.add(job);
            }//for job
        }//for table
        return jobList;
	}
	
	
	private Set<String> scrubEmailString(String emailString){
		
		Set<String> emailSet = new TreeSet<String>();
		
		String[] emails = emailString.split(DELIMITER);
		for (String email : emails) {
			email = email.trim();
			email = email.toLowerCase();
			for (String artifact : EMAIL_ARTIFACTS) {
				email = email.replaceAll(artifact, "");
			}
			emailSet.add(email);
		}
		return emailSet;
	}
	
	
	private boolean isToken(String testString){
		return testString.startsWith(TOKEN_CHARS);
	}
	
	private boolean containsToken(String testString){
		return testString.contains(TOKEN_CHARS);
	}
	
	private Set<String> getEmailsForToken(String destString, JOBType job) {
		for (AUTOEDIT2Type editType : job.getAUTOEDIT2()) {
			String name = editType.getNAME();
			if (name.equals(destString)) {
				String emailString = editType.getValueAttribute();
				return scrubEmailString(emailString);
			}
		}
		throw new RuntimeException(String.format("Could not find email for token '%s' in the job '%s'.", destString, job.getJOBNAME()));
	}
	
	
	
	private void addEntriesForDoMail(JOBType job, DOMAILType doMail, Map<String, Set<String>> nameToEmailsMap){
		
		// This could be a token, one email, a delimited list of emails
		String destString = doMail.getDEST();
		
		// These will be converted/added as entries to the passed in nameToEmailsMap
		String name = null;
		Set<String> emailSet = null;
		
		if(containsToken(destString)){
			// This is a Control-M token, change name to show what the purpose of
			// this token is, and convert the tokens into their corresponding 
			// right-side values from the xml "AUTOEDIT2" element's @value attr
			name = job.getJOBNAME() + "_" + destString.replaceAll(TOKEN_CHARS, "");
			emailSet = getEmailsForToken(destString, job);
		}else{
			// This is not a token, so we can use the job name for this
			// entry, and scrub the address(es) for artifacts
			name = job.getJOBNAME();
			emailSet = scrubEmailString(destString);
		}
		
		// Add to the map
		Set<String> testSet = nameToEmailsMap.get(name);
		if(testSet == null){
			testSet = new TreeSet<String>();
			nameToEmailsMap.put(name, testSet);
		}
		testSet.addAll(emailSet);

	}
	
	
	public Map<String, Set<String>> getEmailMap() {

		Map<String, Set<String>> nameToEmailsMap = new TreeMap<String, Set<String>>();

		List<JOBType> jobList = getAllJobs();
		for (JOBType job : jobList) {

			for (ONType onCondition : job.getON()) {

				for (Object doObject : onCondition.getDOMAILOrDOOrDOCOND()) {

					if (doObject instanceof DOMAILType) {
						DOMAILType doMail = (DOMAILType) doObject;
						addEntriesForDoMail(job, doMail, nameToEmailsMap);
					}// if doMail

				}// for doAction

			}// for onCondition

		}// for job

		return nameToEmailsMap;

	}
	
	/* <DEFTABLE>
	 *   <TABLE>
	 *     <JOB>
	 *       <ON CODE="...">  <!-- this "@CODE" is what we're after -->
	 *         <DOMAIL>
	 *           ...
	 *         </DOMAIL>
	 *       </ON>
	 *     </JOB>
	 *   </TABLE>
	 * <DEFTABLE>
	 */
	public static void printAllUniqueOnCodes(){
		XmlDriver driver = new XmlDriver();
		List<JOBType> allJobs = driver.getAllJobs();
    	Set<String> allMailCodes = new TreeSet<String>(); 
    	for(JOBType job : allJobs){
    		for(ONType on : job.getON()){
    			for(Object doObject : on.getDOMAILOrDOOrDOCOND()){
    				if(doObject instanceof DOMAILType){
    					// We have found a <DOMAIL> child of a <DO> element, we
    					// want to grab the condition code attribute of the
    					// <DO> element and add it to the set of known codes
    					allMailCodes.add(on.getCODE());
    				}
    			}
    		}
    	}
    	for(String code : allMailCodes){
    		System.out.printf("%s\n", code);
    	}
	}
	
	
	public static void printAllJobEmails(){
		XmlDriver driver = new XmlDriver();
		Map<String, Set<String>> emailMap = driver.getEmailMap();
    	for(Entry<String, Set<String>> entry : emailMap.entrySet()){
    		String name = entry.getKey();
    		Set<String> emails = entry.getValue();
    		System.out.printf("-------------------------------------------------\n");
    		System.out.printf("%s\n", name);
    		for(String email : emails){
    			System.out.printf("\t%s\n", email);
    		}
    	}
	}
	
	
    public static void main(final String[] args){
    	XmlDriver.printAllJobEmails();
    	//XmlDriver.printAllUniqueOnCodes();
    }

}
