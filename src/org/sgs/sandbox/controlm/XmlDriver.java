package org.sgs.sandbox.controlm;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.io.FileUtils;


/*
 * Proof of concept on how to hydrate java objects from Control-M generated xml.
 */
public class XmlDriver{
	
	public static final String INPUT_XML_FILE = "data/controlm.xml";
	
	
    public static void main(final String[] args){

        File file = FileUtils.getFile(INPUT_XML_FILE);

        DEFTABLEType tables;
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(DEFTABLEType.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            tables = (DEFTABLEType)jaxbUnmarshaller.unmarshal(file);
        }catch(JAXBException e){
            throw new RuntimeException(e);
        }

        // Dig all the way down to where email values are stored
        for(TABLEType table : tables.getTABLE()){
            System.out.printf("----------------------------------------------------------------------\n");
            System.out.printf("Table name '%s'\n", table.getTABLENAME().split(":")[1]);
            for(JOBType job : table.getJOB()){
                System.out.printf("--------------\n");
                System.out.printf("Job name: %s\n", job.getMEMNAME().split("-")[2]);
                for(ONType onCondition : job.getON()){
                    for(Object doObject : onCondition.getDOMAILOrDOOrDOCOND()){
                        if(doObject instanceof DOMAILType){
                            DOMAILType doMail = (DOMAILType)doObject;
                            System.out.printf("TO: %s\n", doMail.getDEST());
                            System.out.printf("SUBJECT: %s\n", doMail.getSUBJECT());
                            System.out.printf("ATTACH: %s\n", doMail.getATTACHSYSOUT());
                        }//if doMail
                    }//for doAction
                }// for onCondition
            }//for job

        }//for table

    }
}
