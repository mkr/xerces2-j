/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.xerces.stax.events;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Attribute;

/**
 * @author Lucian Holland
 * 
 * @version $Id$
 */
public class AttributeImpl extends XMLEventImpl implements Attribute {

    private final boolean fIsSpecified;
    private final QName fName;
    private final String fValue;
    private final String fDtdType;

    /**
     * Constructor.
     */
    public AttributeImpl(final QName name, final String value, final String dtdType, final boolean isSpecified, final Location location, final QName schemaType) {
        this(ATTRIBUTE, name, value, dtdType, isSpecified, location, schemaType);
    }

    protected AttributeImpl(final int type, final QName name, final String value, final String dtdType, final boolean isSpecified, final Location location, final QName schemaType) {
        super(type, location, schemaType);
        fName = name;
        fValue = value;
        fDtdType = dtdType;
        fIsSpecified = isSpecified;
    }
    
    /**
     * @see javax.xml.stream.events.Attribute#getName()
     */
    public QName getName() {
        return fName;
    }

    /**
     * @see javax.xml.stream.events.Attribute#getValue()
     */
    public String getValue() {
        return fValue;
    }

    /**
     * @see javax.xml.stream.events.Attribute#getDTDType()
     */
    public String getDTDType() {
        return fDtdType;
    }

    /**
     * @see javax.xml.stream.events.Attribute#isSpecified()
     */
    public boolean isSpecified() {
        return fIsSpecified;
    }

}
