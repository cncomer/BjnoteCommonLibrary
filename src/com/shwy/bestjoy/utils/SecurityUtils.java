package com.shwy.bestjoy.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.http.HttpRequest;

import android.text.TextUtils;

/**
 * ����ģ��
 * @author chenkai
 *
 */
public class SecurityUtils {
	private static final String TAG = "SecurityUtils";
	//add by chenkai, 20131123, add Security token header
	public static final String TOKEN_KEY = "key";
	public static final String TOKEN_CELL = "cell";
	private static final byte[] DESIV = {0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};// ������������ȥ
	
	public static class SecurityKeyValuesObject {
		//������
		private long mDate = -1;
		private static final long UPDATE_DURATION = 1000 * 60 * 60 * 24; //24Сʱ
		public HashMap<String, String> mKeyValuesMap = new HashMap<String, String>();
		public HashMap<String, String> put(String key, String value) {
			mKeyValuesMap.put(key, value);
			return mKeyValuesMap;
		}
		public SecurityKeyValuesObject(long date) {
			mDate = date;
		}
		public static SecurityKeyValuesObject getSecurityKeyValuesObject() {
		    return  new SecurityKeyValuesObject(new Date().getTime());
		}
		
		public void updateCellKey(String cell) {
			if (TextUtils.isEmpty(cell)) {
				//����Ŀǰ��ʵ����ÿ�ν���Ӧ���������ͻ����һ�����������ȷ�����ǵ�ʱ�������µģ��п����û���û�е�¼�������cell���ǿյ�
				return;
			}
			long currentDate = new Date().getTime();
			if (currentDate - mDate >= UPDATE_DURATION || mDate < currentDate) {
				DebugUtils.logD(TAG, "SecurityKeyValuesObject.updateKey " + cell);
				mKeyValuesMap.put(TOKEN_KEY, getMd5Key(cell));
			}
		}
	}
	

	/**�ԳƼ����㷨*/
	public static class DES {
		/**
		 * ע�⣺DES���ܺͽ��ܹ����У���Կ���ȶ�������8�ı���
		 * @param datasource
		 * @param password
		 * @return
		 */
        public static String enCrypto(byte[] datasource, String password) {            
            try{
	            SecureRandom random = new SecureRandom();
	            DESKeySpec desKey = new DESKeySpec(password.getBytes());
	            //����һ���ܳ׹�����Ȼ��������DESKeySpecת����
	            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	            SecretKey securekey = keyFactory.generateSecret(desKey);
	            //Cipher����ʵ����ɼ��ܲ���
	            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	            IvParameterSpec iv = new IvParameterSpec(DESIV);// ��������
	            //���ܳ׳�ʼ��Cipher����
	            cipher.init(Cipher.ENCRYPT_MODE, securekey, iv);
	            //���ڣ���ȡ���ݲ�����
	            //��ʽִ�м��ܲ���
	            byte[] encodedByte = cipher.doFinal(datasource);
	            return Base64.encodeToString(encodedByte, Base64.DEFAULT);

            } catch(Throwable e){
                e.printStackTrace();
            }
            return null;
        }
        
        /**
         * DES����
         * @param src
         * @param password
         * @return
         * @throws Exception
         */
        public static String deCrypto(String src, String password) throws Exception {
            // DES�㷨Ҫ����һ�������ε������Դ
//            SecureRandom random = new SecureRandom();
            byte[] data = Base64.decode(src, Base64.DEFAULT);
            // ����һ��DESKeySpec����
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            // ����һ���ܳ׹���
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // ��DESKeySpec����ת����SecretKey����
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher����ʵ����ɽ��ܲ���
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(DESIV);// ��������
            // ���ܳ׳�ʼ��Cipher����
            cipher.init(Cipher.DECRYPT_MODE, securekey, iv);
            // ������ʼ���ܲ���
            byte[] decodedByte = cipher.doFinal(data);
            return new String(decodedByte);
        }
	}
	
	public static class MD5 { 
		public final static String md5(String message) { 
			try { 
			   byte[] strTemp = message.getBytes(); 
			   //ʹ��MD5����MessageDigest���� 
			   MessageDigest messageDigest = MessageDigest.getInstance("MD5"); 
			   messageDigest.update(strTemp); 
			   byte[] md5DecodedStr = messageDigest.digest(); 
			   //֮����ʮ�����Ƹ�ʽ��ʽ��
			   StringBuffer hexValue = new StringBuffer();  
		        for (int i = 0; i < md5DecodedStr.length; i++){  
		            int val = ((int) md5DecodedStr[i]) & 0xff;  
		            if (val < 16) hexValue.append("0");  
		            hexValue.append(Integer.toHexString(val));  
		        }  
//			   String result = Base64.encodeToString(md5DecodedStr, Base64.DEFAULT);
			   String result = hexValue.toString().toLowerCase();
			   DebugUtils.logD(TAG, "md5 encode " + message + " to "+ result);
			   return result;
			} catch (Exception e) {
				return null;
			} 
		}
	} 
	
	public static void genSecurityRequestToken(HttpRequest request, String key, String value) {
		request.addHeader(key, value);
	}
	
	/**
	 * key=md5(�û��ֻ�(�ֻ�����1�滻��i��ȥ�����һλ),��ǰ������20021212)
	 * @param cell
	 * @return
	 */
	public static final String getMd5Key(String cell) {
		DebugUtils.logD(TAG, "getMd5Key() " + cell);
		String message = cell;
		message = message.replaceAll("1", "i");
		DebugUtils.logD(TAG, "replace 1 as i " + message);
		int len = message.length();
		message = message.substring(0, len - 1);
		DebugUtils.logD(TAG, "remove the last char " + message);
		String dataStr = DateUtils.TOPIC_CREATE_DATE_FORMAT.format(new Date());
		message += dataStr;
		DebugUtils.logD(TAG, "return final encoded " + message);
		return MD5.md5(message);
	}
}
