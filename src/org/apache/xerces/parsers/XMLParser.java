/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999,2000 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.parsers;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.XMLComponentManager;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import java.util.Hashtable;

/**
 * @author Stubs generated by DesignDoc on Mon Sep 11 11:10:57 PDT 2000
 * @author Arnaud  Le Hors, IBM
 * @version $Id$
 */
public abstract class XMLParser
    implements XMLComponentManager {

    //
    // Constants
    //

    /** SAX2 features prefix. */
    protected static final String SAX2_FEATURES_PREFIX =
        "http://xml.org/sax/features/";

    /** SAX2 properties prefix. */
    protected static final String SAX2_PROPERTIES_PREFIX =
        "http://xml.org/sax/properties/";

    /** Xerces features prefix. */
    protected static final String XERCES_FEATURES_PREFIX =
        "http://apache.org/xml/features/";

    /** Xerces properties prefix. */
    protected static final String XERCES_PROPERTIES_PREFIX =
        "http://apache.org/xml/properties/";

    //
    // Data
    //

    /** fSymbolTable */
    protected SymbolTable fSymbolTable;

    /** fEntityManager */
    protected XMLEntityManager fEntityManager;

    /** fErrorReporter */
    protected XMLErrorReporter fErrorReporter;

    /** properties table */
    protected Hashtable fProperties;

    /** features table */
    protected Hashtable fFeatures;

    //
    // Constructors
    //

    /**
     * Default Constructor. Creates a parser with its own SymbolTable.
     */
    public XMLParser() {
        this(new SymbolTable());
    }

    /**
     * Constructor allowing the SymbolTable to be specified.
     * 
     * @param symbolTable 
     */
    protected XMLParser(SymbolTable symbolTable) {

        fProperties = new Hashtable();
        fFeatures = new Hashtable();

        // create and register components
        fSymbolTable = symbolTable;
        fProperties.put(XERCES_PROPERTIES_PREFIX + "internal/symbol-table",
                        fSymbolTable);
        fEntityManager = new XMLEntityManager();
        fProperties.put(XERCES_PROPERTIES_PREFIX + "internal/entity-manager",
                        fEntityManager);
        fErrorReporter = new XMLErrorReporter( fEntityManager.getEntityScanner() );
        XMLMessageFormatter xmft = new XMLMessageFormatter();
        fErrorReporter.putMessageFormatter(XMLMessageFormatter.XML_DOMAIN, xmft);
        fErrorReporter.putMessageFormatter(XMLMessageFormatter.XMLNS_DOMAIN, xmft);
        fProperties.put(XERCES_PROPERTIES_PREFIX + "internal/error-reporter",
                        fErrorReporter);

        // set features to their default values
    }

    //
    // Methods
    //

    /**
     * Set the state of a feature.
     *
     * Set the state of any feature in a SAX2 parser.  The parser
     * might not recognize the feature, and if it does recognize
     * it, it might not be able to fulfill the request.
     *
     * @param featureId The unique identifier (URI) of the feature.
     * @param state The requested state of the feature (true or false).
     *
     * @exception org.xml.sax.SAXNotRecognizedException If the
     *            requested feature is not known.
     * @exception org.xml.sax.SAXNotSupportedException If the
     *            requested feature is known, but the requested
     *            state is not supported.
     * @exception org.xml.sax.SAXException If there is any other
     *            problem fulfilling the request.
     */
    public void setFeature(String featureId, boolean state)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        checkFeature(featureId);

        // forward to every component
        fEntityManager.setFeature(featureId, state);
        fErrorReporter.setFeature(featureId, state);
        // then store the information
        fFeatures.put(featureId, state ? Boolean.TRUE : Boolean.FALSE);

    } // setFeature

    /**
     * getFeature
     * 
     * @param featureId 
     * 
     * @return 
     */
    public boolean getFeature(String featureId)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        checkFeature(featureId);

        Boolean state = (Boolean) fFeatures.get(featureId);
        return state.booleanValue();

    } // getFeature

    /**
     * Check a feature. If feature is know and supported, this method simply
     * returns. Otherwise, the appropriate exception is thrown.
     *
     * @param featureId The unique identifier (URI) of the feature.
     *
     * @exception org.xml.sax.SAXNotRecognizedException If the
     *            requested feature is not known.
     * @exception org.xml.sax.SAXNotSupportedException If the
     *            requested feature is known, but the requested
     *            state is not supported.
     * @exception org.xml.sax.SAXException If there is any other
     *            problem fulfilling the request.
     */
    protected void checkFeature(String featureId)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        //
        // SAX2 Features
        //

        if (featureId.startsWith(SAX2_FEATURES_PREFIX)) {
            String feature =
                featureId.substring(SAX2_FEATURES_PREFIX.length());
            //
            // http://xml.org/sax/features/validation
            //   Validate (true) or don't validate (false).
            //
            if (feature.equals("validation")) {
                return;
            }
            //
            // http://xml.org/sax/features/external-general-entities
            //   Expand external general entities (true) or not (false).
            //
            if (feature.equals("external-general-entities")) {
                return;
            }
            //
            // http://xml.org/sax/features/external-parameter-entities
            //   Expand external parameter entities (true) or not (false).
            //
            if (feature.equals("external-parameter-entities")) {
                return;
            }
            //
            // http://xml.org/sax/features/namespaces
            //   Preprocess namespaces (true) or not (false).  See also
            //   the http://xml.org/sax/properties/namespace-sep property.
            //
            if (feature.equals("namespaces")) {
                return;
            }
            //
            // Not recognized
            //
        }

        //
        // Xerces Features
        //

        else if (featureId.startsWith(XERCES_FEATURES_PREFIX)) {
            String feature =
                featureId.substring(XERCES_FEATURES_PREFIX.length());
            //
            // http://apache.org/xml/features/validation/schema
            //   Lets the user turn Schema validation support on/off.
            //
            if (feature.equals("validation/schema")) {
                return;
            }
            //
            // http://apache.org/xml/features/validation/dynamic
            //   Allows the parser to validate a document only when it
            //   contains a grammar. Validation is turned on/off based
            //   on each document instance, automatically.
            //
            if (feature.equals("validation/dynamic")) {
                return;
            }
            //
            // http://apache.org/xml/features/validation/default-attribute-values
            //
            if (feature.equals("validation/default-attribute-values")) {
                // REVISIT
                throw new SAXNotSupportedException(featureId);
            }
            //
            // http://apache.org/xml/features/validation/default-attribute-values
            //
            if (feature.equals("validation/validate-content-models")) {
                // REVISIT
                throw new SAXNotSupportedException(featureId);
            }
            //
            // http://apache.org/xml/features/validation/nonvalidating/load-dtd-grammar
            //
            if (feature.equals("nonvalidating/load-dtd-grammar")) {
                return;
            }

            //
            // http://apache.org/xml/features/validation/default-attribute-values
            //
            if (feature.equals("validation/validate-datatypes")) {
                throw new SAXNotSupportedException(featureId);
            }
            //
            // http://apache.org/xml/features/validation/warn-on-duplicate-attdef
            //   Emits an error when an attribute is redefined.
            //
            if (feature.equals("validation/warn-on-duplicate-attdef")) {
                return;
            }
            //
            // http://apache.org/xml/features/validation/warn-on-undeclared-elemdef
            //   Emits an error when an element's content model
            //   references an element, by name, that is not declared
            //   in the grammar.
            //
            if (feature.equals("validation/warn-on-undeclared-elemdef")) {
                return;
            }
            //
            // http://apache.org/xml/features/allow-java-encodings
            //   Allows the use of Java encoding names in the XML
            //   and TextDecl lines.
            //
            if (feature.equals("allow-java-encodings")) {
                return;
            }
            //
            // http://apache.org/xml/features/continue-after-fatal-error
            //   Allows the parser to continue after a fatal error.
            //   Normally, a fatal error would stop the parse.
            //
            if (feature.equals("continue-after-fatal-error")) {
                return;
            }
            //
            // Not recognized
            //
        }

        //
        // Not recognized
        //

        throw new SAXNotRecognizedException(featureId);

    } // checkFeature(String)


    /**
     * setProperty
     * 
     * @param propertyId 
     * @param value 
     */
    public void setProperty(String propertyId, Object value)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        checkProperty(propertyId);

        // forward to every component
        fEntityManager.setProperty(propertyId, value);
        fErrorReporter.setProperty(propertyId, value);
        // then store the information
        fProperties.put(propertyId, value);

    } // setProperty

    /**
     * getProperty
     * 
     * @param propertyId 
     * 
     * @return 
     */
    public Object getProperty(String propertyId)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        checkProperty(propertyId);

        return fProperties.get(propertyId);

    } // getProperty

    /**
     * Check a property. If the property is know and supported, this method
     * simply returns. Otherwise, the appropriate exception is thrown.
     *
     * @param propertyId The unique identifier (URI) of the property
     *                   being set.
     * @exception org.xml.sax.SAXNotRecognizedException If the
     *            requested property is not known.
     * @exception org.xml.sax.SAXNotSupportedException If the
     *            requested property is known, but the requested
     *            value is not supported.
     * @exception org.xml.sax.SAXException If there is any other
     *            problem fulfilling the request.
     */
    protected void checkProperty(String propertyId)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        //
        // SAX2 Properties
        //

        if (propertyId.startsWith(SAX2_PROPERTIES_PREFIX)) {
            String property =
                propertyId.substring(SAX2_PROPERTIES_PREFIX.length());
            //
            // http://xml.org/sax/properties/namespace-sep
            // Value type: String
            // Access: read/write, pre-parse only
            //   Set the separator to be used between the URI part of a name
            //   and the local part of a name when namespace processing is
            //   being performed (see the
            //   http://xml.org/sax/features/namespaces feature).  By default,
            //   the separator is a single space.  This property may not be set
            //   while a parse is in progress (throws a
            //   SAXNotSupportedException).
            //
            /***
            if (property.equals("namespace-sep")) {
                try {
                    setNamespaceSep((String)value);
                }
                catch (ClassCastException e) {
                    throw new SAXNotSupportedException(propertyId);
                }
                return;
            }
            /***/
            
            //
            // http://xml.org/sax/properties/xml-string
            // Value type: String
            // Access: read-only
            //   Get the literal string of characters associated with the
            //   current event.  If the parser recognises and supports this
            //   property but is not currently parsing text, it should return
            //   null (this is a good way to check for availability before the
            //   parse begins).
            //
            if (property.equals("xml-string")) {
                // REVISIT - we should probably ask xml-dev for a precise
                // definition of what this is actually supposed to return, and
                // in exactly which circumstances.
                throw new SAXNotSupportedException(propertyId);
            }
            //
            // Not recognized
            //
        }

        //
        // Xerces Properties
        //

        else if (propertyId.startsWith(XERCES_PROPERTIES_PREFIX)) {
            String property =
                propertyId.substring(XERCES_PROPERTIES_PREFIX.length());
            if (property.equals("internal/symbol-table")) {
                return;
            }
            if (property.equals("internal/error-reporter")) {
                return;
            }
            if (property.equals("internal/entity-manager")) {
                return;
            }
            if (property.equals("internal/grammar-pool")) {
                return;
            }
            if (property.equals("internal/datatype-validator-factory")) {
                return;
            }
            if (property.equals("internal/entity-resolver")) {
                return;
            }
            if (property.equals("internal/error-handler")) {
                return;
            }
        }

        //
        // Not recognized
        //

        throw new SAXNotRecognizedException(propertyId);

    } // checkProperty(String)

    /**
     * Sets the resolver used to resolve external entities. The EntityResolver
     * interface supports resolution of public and system identifiers.
     *
     * @param resolver The new entity resolver. Passing a null value will
     *                 uninstall the currently installed resolver.
     */
    public void setEntityResolver(EntityResolver resolver) {
        fProperties.put(XERCES_PROPERTIES_PREFIX + "internal/entity-resolver",
                        resolver);
    } // setEntityResolver

    /**
     * Return the current entity resolver.
     *
     * @return The current entity resolver, or null if none
     *         has been registered.
     * @see #setEntityResolver
     */
    public EntityResolver getEntityResolver() {
        return (EntityResolver)fProperties.get(XERCES_PROPERTIES_PREFIX +
                                               "internal/entity-resolver");
    } // getEntityResolver

    /**
     * Allow an application to register an error event handler.
     *
     * <p>If the application does not register an error handler, all
     * error events reported by the SAX parser will be silently
     * ignored; however, normal processing may not continue.  It is
     * highly recommended that all SAX applications implement an
     * error handler to avoid unexpected bugs.</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param errorHandler The error handler.
     * @exception java.lang.NullPointerException If the handler 
     *            argument is null.
     * @see #getErrorHandler
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        fProperties.put(XERCES_PROPERTIES_PREFIX + "internal/error-handler",
                        errorHandler);
    } // setErrorHandler

    /**
     * Return the current error handler.
     *
     * @return The current error handler, or null if none
     *         has been registered.
     * @see #setErrorHandler
     */
    public ErrorHandler getErrorHandler() {
        return (ErrorHandler)fProperties.get(XERCES_PROPERTIES_PREFIX +
                                             "internal/error-handler");
    } // getErrorHandler

    /**
     * Parses the input source specified by the given system identifier.
     * <p>
     * This method is equivalent to the following:
     * <pre>
     *     parse(new InputSource(systemId));
     * </pre>
     *
     * @param source The input source.
     *
     * @exception org.xml.sax.SAXException Throws exception on SAX error.
     * @exception java.io.IOException Throws exception on i/o error.
     */
    public void parse(String systemId)
        throws SAXException, IOException {

        InputSource source = new InputSource(systemId);
        parse(source);
        try {
            Reader reader = source.getCharacterStream();
            if (reader != null) {
                reader.close();
            }
            else {
                InputStream is = source.getByteStream();
                if (is != null) {
                    is.close();
                }
            }
        }
        catch (IOException e) {
            // ignore
        }

    } // parse(String)

    /**
     * parse
     *
     * @param inputSource
     *
     * @exception org.xml.sax.SAXException
     * @exception java.io.IOException
     */
    public void parse(InputSource inputSource)
        throws SAXException, IOException {
    } // parse

    /**
     * reset all components before parsing
     */
    public void reset()
        throws SAXException {

        // reset every component
        fEntityManager.reset(this);
        fErrorReporter.reset(this);

    } // reset

    //
    // Locale 
    //

    /**
     * Set the locale to use for messages.
     *
     * @param locale The locale object to use for localization of messages.
     *
     * @exception SAXException An exception thrown if the parser does not
     *                         support the specified locale.
     *
     * @see org.xml.sax.Parser
     */
    public void setLocale(Locale locale) throws SAXException {

        fErrorReporter.setLocale(locale);

    } // setLocale(Locale)


} // class XMLParser
