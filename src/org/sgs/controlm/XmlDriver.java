package org.sgs.controlm;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
	
	
	private String scrubEmailString(String email) {
		email = email.trim();
		email = email.toLowerCase();
		for (String artifact : EMAIL_ARTIFACTS) {
			email = email.replaceAll(artifact, "");
		}
		return email;
	}
	
	
	private boolean isToken(String testString){
		return testString.startsWith(TOKEN_CHARS);
	}

	
	private String getEmailForToken(String token, List<AUTOEDIT2Type> autoEditTypeList) {
		for (AUTOEDIT2Type editType : autoEditTypeList) {
			String name = editType.getNAME();
			if (name.equals(token)) {
				String emailString = editType.getValueAttribute();
				return scrubEmailString(emailString);
			}
		}
		throw new RuntimeException(String.format("Could not find email for token '%s'.", token));
	}
	
	
	private Set<String> convertDoMailsToEmails(List<DOMAILType> doMailList, List<AUTOEDIT2Type> autoEditTypeList) {
		Set<String> emailSet = new TreeSet<String>();
		for(DOMAILType doMail : doMailList) {
			String destString = doMail.getDEST();
			for(String dest : destString.split(DELIMITER)) {
				if(isToken(dest)) {
					dest = getEmailForToken(dest, autoEditTypeList);
				}else {
					dest = scrubEmailString(dest);
				}
				emailSet.add(dest);
			}
		}
		return emailSet;
	}
	
	
	public Set<CmJobEmailDetails> getEmailDetails() {

		Set<CmJobEmailDetails> emailDetailSet = new TreeSet<CmJobEmailDetails>();

		List<JOBType> jobList = getAllJobs();
		
		for (JOBType job : jobList) {
			
			CmJobEmailDetails  jobEmailDetails = new CmJobEmailDetails(job.getJOBNAME()); 
			
			for (ONType onType : job.getON()) {
				
				List<DOMAILType> doMailList = new ArrayList<DOMAILType>();
				
				for (Object doObject : onType.getDOMAILOrDOOrDOCOND()) {

					if (doObject instanceof DOMAILType) {
						DOMAILType doMail = (DOMAILType) doObject;
						doMailList.add(doMail);
					}// if doMail
					
				}// for doAction
				
				if(doMailList.size() > 0) {
					Set<String> emailSet = convertDoMailsToEmails(doMailList, job.getAUTOEDIT2());
					Status status = Status.getStatusByCode(onType.getCODE());
					if(status == Status.OK) {
						jobEmailDetails.addAllToSuccessEmails(emailSet);
					}else {
						jobEmailDetails.addAllToFailureEmails(emailSet);
					}
				}
				
			}// for onType
			
			emailDetailSet.add(jobEmailDetails);
			
		}// for job

		return emailDetailSet;

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
		Set<CmJobEmailDetails> emailDetails = driver.getEmailDetails();
		
		// Control stdout reporting, only print if there is non-zero info available.
		// Also, keep counts to report on stats.
		int successCount = 0;
		int failureCount = 0;
		int totalJobCount = 0;
		int bothEmailTypeCount = 0;
		
    	for(CmJobEmailDetails detail : emailDetails){
    		
    		// For stat purposes, we only care that a job sends email,
    		// we don't care about how many recipients there are, so
    		// just increment counters by 1.
    		int tmpSuccess = (detail.getJobSuccessEmails().size() > 0 ? 1 : 0);
    		int tmpFailure = (detail.getJobFailureEmails().size() > 0 ? 1 : 0);
    		bothEmailTypeCount += (((tmpSuccess + tmpFailure) == 2) ? 1 : 0);
    		successCount += tmpSuccess;
    		failureCount += tmpFailure;
    		
    		if(tmpSuccess + tmpFailure < 1) {
    			continue;
    		}
    		totalJobCount++;
    		
    		System.out.printf("-------------------------------------------------\n");
    		System.out.printf("%s\n", detail.getCmName());
    		
    		
    		if (tmpSuccess > 0) {
				System.out.printf("  %s --------------------------------------\n", Status.OK);
				for (String email : detail.getJobSuccessEmails()) {
					System.out.printf("\t%s\n", email);
				}
			}
    		
			if (tmpFailure > 0) {
				System.out.printf("  %s -----------------------------------\n", Status.NOTOK);
				for (String email : detail.getJobFailureEmails()) {
					System.out.printf("\t%s\n", email);
				}
			}
			
    	}//for
    	
    	// Report email counts
		System.out.printf("-------------------------------------------------\n");
		System.out.printf("Summary\n");
		System.out.printf("Total number jobs w/ any email:      %3d\n", totalJobCount);
		System.out.printf("Total number jobs w/ success emails: %3d\n", successCount);
		System.out.printf("Total number jobs w/ failure emails: %3d\n", failureCount);
		System.out.printf("Total number jobs w/ both emails:    %3d\n", bothEmailTypeCount);
	}
	
	
    public static void main(final String[] args){
    	XmlDriver.printAllJobEmails();
    	//XmlDriver.printAllUniqueOnCodes();
    }

}
