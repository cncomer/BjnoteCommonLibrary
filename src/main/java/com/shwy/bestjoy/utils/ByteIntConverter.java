package com.shwy.bestjoy.utils;

import android.text.TextUtils;

/**
 * int和数组相互转换
 * Created by bestjoy on 2017/3/29.
 */

public class ByteIntConverter {

    /**
     * 将int数值转换为占2个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intTo2Bytes( int value ) {
        byte[] src = new byte[2];
        src[0] =  (byte) ((value>>8) & 0xFF);
        src[1] =  (byte) (value & 0xFF);
        return src;
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public static byte[] intTo4Bytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intTo4Bytes2( int value ) {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int twoBytesToInt(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset+0] & 0xFF)<<8) | (src[offset+1] & 0xFF));
        return value;
    }

    public static int byteToInt(byte[] src, int offset) {
        return (src[offset] & 0xFF);
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int fourBytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ( ((src[offset] & 0xFF)<<24)
                |((src[offset+1] & 0xFF)<<16)
                |((src[offset+2] & 0xFF)<<8)
                |(src[offset+3] & 0xFF));
        return value;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int fourBytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8));
        return value;
    }

    public static byte[] convertByteHexStringToByte(String byteHexString) {
        byteHexString = byteHexString.replaceAll(" ", "");
        int len = byteHexString.length();
        byte[] result = new byte[len/2];
        len = result.length;
        int j=0;
        for(int index=0;index<len;index++) {
            result[index] = (byte) Integer.parseInt(byteHexString.substring(j, j+=2), 16);
        }
        return result;
    }

    public static int convertByteHexStringToInt(String hexString) {
        return Integer.parseInt(hexString, 16);
    }

    public static int convertByteHexStringToInt(String highByteHexString, String lowByteHexString) {
        return Integer.parseInt(highByteHexString, 16)<<8 | Integer.parseInt(lowByteHexString, 16);
    }

    public static byte convertHexStringToByte(String hexString) {
        return Byte.parseByte(hexString, 16);
    }


    public static String bytesToHexString(byte[] src, String divider){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if (!TextUtils.isEmpty(divider)) {
                stringBuilder.append(divider);
            }
        }
        return stringBuilder.toString().trim();
    }
}