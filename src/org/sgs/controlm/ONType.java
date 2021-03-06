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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ONType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ONType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="DOMAIL" type="{}DOMAILType"/>
 *         &lt;element name="DO" type="{}DOType"/>
 *         &lt;element name="DOCOND" type="{}DOCONDType"/>
 *         &lt;element name="DOREMEDY" type="{}DOREMEDYType"/>
 *       &lt;/choice>
 *       &lt;attribute name="CODE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="STMT" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ONType", propOrder = {
    "domailOrDOOrDOCOND"
})
public class ONType {

    @XmlElements({
        @XmlElement(name = "DOMAIL", type = DOMAILType.class),
        @XmlElement(name = "DO", type = DOType.class),
        @XmlElement(name = "DOCOND", type = DOCONDType.class),
        @XmlElement(name = "DOREMEDY", type = DOREMEDYType.class)
    })
    protected List<Object> domailOrDOOrDOCOND;
    @XmlAttribute(name = "CODE")
    protected String code;
    @XmlAttribute(name = "STMT")
    protected String stmt;

    /**
     * Gets the value of the domailOrDOOrDOCOND property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the domailOrDOOrDOCOND property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDOMAILOrDOOrDOCOND().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DOMAILType }
     * {@link DOType }
     * {@link DOCONDType }
     * {@link DOREMEDYType }
     * 
     * 
     */
    public List<Object> getDOMAILOrDOOrDOCOND() {
        if (domailOrDOOrDOCOND == null) {
            domailOrDOOrDOCOND = new ArrayList<Object>();
        }
        return this.domailOrDOOrDOCOND;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCODE() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCODE(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the stmt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTMT() {
        return stmt;
    }

    /**
     * Sets the value of the stmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTMT(String value) {
        this.stmt = value;
    }

}
