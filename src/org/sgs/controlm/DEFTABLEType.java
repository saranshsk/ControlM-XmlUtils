//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.07.18 at 05:12:25 PM MST 
//

package org.sgs.controlm;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Java class for DEFTABLEType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DEFTABLEType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TABLE" type="{}TABLEType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "DEFTABLE", namespace = "")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DEFTABLEType", propOrder = {
        "table"
})
public class DEFTABLEType{

    @XmlElement(name = "TABLE")
    protected List<TABLEType> table;


    /**
     * Gets the value of the table property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the table property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTABLE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TABLEType }
     * 
     * 
     */
    public List<TABLEType> getTABLE(){
        if(table == null){
            table = new ArrayList<TABLEType>();
        }
        return this.table;
    }

}
