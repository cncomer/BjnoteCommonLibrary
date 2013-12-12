package com.shwy.bestjoy.contacts.backup.codec;

/**
 * Encodes a String into a String. 
 *
 * @author Apache Software Foundation
 * @version $Id: StringEncoder.java,v 1.9 2004/02/29 04:08:31 tobrien Exp $
 */
public interface StringEncoder extends Encoder {
    
    /**
     * Encodes a String and returns a String.
     * 
     * @param pString a String to encode
     * 
     * @return the encoded String
     * 
     * @throws EncoderException thrown if there is
     *  an error conidition during the Encoding process.
     */
    String encode(String pString) throws EncoderException;
}