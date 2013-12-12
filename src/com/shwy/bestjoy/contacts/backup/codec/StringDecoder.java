package com.shwy.bestjoy.contacts.backup.codec;

/**
 * Decodes a String into a String. 
 *
 * @author Apache Software Foundation
 * @version $Id: StringDecoder.java,v 1.9 2004/02/29 04:08:31 tobrien Exp $
 */
public interface StringDecoder extends Decoder {
    
    /**
     * Decodes a String and returns a String.
     * 
     * @param pString a String to encode
     * 
     * @return the encoded String
     * 
     * @throws DecoderException thrown if there is
     *  an error conidition during the Encoding process.
     */
    String decode(String pString) throws DecoderException;
}  
