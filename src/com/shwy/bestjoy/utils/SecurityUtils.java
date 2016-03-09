package com.shwy.bestjoy.utils;

import android.text.TextUtils;

import org.apache.http.HttpRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 加密模块
 * @author chenkai
 *
 */
public class SecurityUtils {
	private static final String TAG = "SecurityUtils";
	//add by chenkai, 20131123, add Security token header
	public static final String TOKEN_KEY = "key";
	public static final String TOKEN_CELL = "cell";
	private static final byte[] DESIV = {0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};// 设置向量，略去

	public static class SecurityKeyValuesObject {
		//年月日
		private long mDate = -1;
		private static final long UPDATE_DURATION = 1000 * 60 * 60 * 24; //24小时
		public HashMap<String, String> mKeyValuesMap = new HashMap<String, String>();

		public HashMap<String, String> put(String key, String value) {
			mKeyValuesMap.put(key, value);
			return mKeyValuesMap;
		}

		public SecurityKeyValuesObject(long date) {
			mDate = date;
		}

		public static SecurityKeyValuesObject getSecurityKeyValuesObject() {
			return new SecurityKeyValuesObject(new Date().getTime());
		}

		public void updateCellKey(String cell) {
			if (TextUtils.isEmpty(cell)) {
				//由于目前的实现是每次进入应用主界面快就会调用一次这个方法来确保我们的时间是最新的，有可能用户还没有登录，这里的cell就是空的
				return;
			}
			long currentDate = new Date().getTime();
			if (currentDate - mDate >= UPDATE_DURATION || mDate < currentDate) {
				DebugUtils.logD(TAG, "SecurityKeyValuesObject.updateKey " + cell);
				mKeyValuesMap.put(TOKEN_KEY, getMd5Key(cell));
			}
		}
	}


	/**
	 * 对称加密算法
	 */
	public static class DES {
		/**
		 * 注意：DES加密和解密过程中，密钥长度都必须是8的倍数
		 *
		 * @param datasource
		 * @param password
		 * @return
		 */
		public static String enCrypto(byte[] datasource, String password) {
			try {
				SecureRandom random = new SecureRandom();
				DESKeySpec desKey = new DESKeySpec(password.getBytes());
				//创建一个密匙工厂，然后用它把DESKeySpec转换成
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				SecretKey securekey = keyFactory.generateSecret(desKey);
				//Cipher对象实际完成加密操作
				Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
				IvParameterSpec iv = new IvParameterSpec(DESIV);// 设置向量
				//用密匙初始化Cipher对象
				cipher.init(Cipher.ENCRYPT_MODE, securekey, iv);
				//现在，获取数据并加密
				//正式执行加密操作
				byte[] encodedByte = cipher.doFinal(datasource);
				return Base64.encodeToString(encodedByte, Base64.DEFAULT);

			} catch (Throwable e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * DES解密
		 *
		 * @param src
		 * @param password
		 * @return
		 * @throws Exception
		 */
		public static String deCrypto(String src, String password) throws Exception {
			// DES算法要求有一个可信任的随机数源
//            SecureRandom random = new SecureRandom();
			byte[] data = Base64.decode(src, Base64.DEFAULT);
			// 创建一个DESKeySpec对象
			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 创建一个密匙工厂
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// 将DESKeySpec对象转换成SecretKey对象
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成解密操作
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(DESIV);// 设置向量
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, securekey, iv);
			// 真正开始解密操作
			byte[] decodedByte = cipher.doFinal(data);
			return new String(decodedByte);
		}

	}

	public static class MD5 {
		public final static String md5(String message) {
			try {
				byte[] strTemp = message.getBytes();
				//使用MD5创建MessageDigest对象
				MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				messageDigest.update(strTemp);
				byte[] md5DecodedStr = messageDigest.digest();
				//之后以十六进制格式格式化
				StringBuffer hexValue = new StringBuffer();
				for (int i = 0; i < md5DecodedStr.length; i++) {
					int val = ((int) md5DecodedStr[i]) & 0xff;
					if (val < 16) hexValue.append("0");
					hexValue.append(Integer.toHexString(val));
				}
//			   String result = Base64.encodeToString(md5DecodedStr, Base64.DEFAULT);
				String result = hexValue.toString().toLowerCase();
				DebugUtils.logD(TAG, "md5 encode " + message + " to " + result);
				return result;
			} catch (Exception e) {
				return null;
			}
		}

		public static boolean verifyFileMd5(File file, String needMd5) {
			if (TextUtils.isEmpty(needMd5)) {
				return true;
			}

			try {
				MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
				byte[] buffer = new byte[1024];
				int read = 0;
				read = is.read(buffer);
				while (read != -1) {
					messageDigest.update(buffer, 0, read);
					read = is.read(buffer);
				}
				byte[] md5DecodedStr = messageDigest.digest();
				//之后以十六进制格式格式化
				StringBuffer hexValue = new StringBuffer();
				for (int i = 0; i < md5DecodedStr.length; i++) {
					int val = ((int) md5DecodedStr[i]) & 0xff;
					if (val < 16) hexValue.append("0");
					hexValue.append(Integer.toHexString(val));
				}
				String result = hexValue.toString().toLowerCase();
				DebugUtils.logD(TAG, "verifyFileMd5 file MD5 is " + result + ", needMd5 is " + needMd5 + ", result " + needMd5.equals(result));
				return needMd5.equals(result);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	public static void genSecurityRequestToken(HttpRequest request, String key, String value) {
		request.addHeader(key, value);
	}

	/**
	 * key=md5(用户手机(手机号码1替换成i，去掉最后一位),当前日期如20021212)
	 *
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


	public static class SHA {
		public final static String sha1(String message) {
			try {
				byte[] strTemp = message.getBytes();
				//使用MD5创建MessageDigest对象
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
				messageDigest.update(strTemp);
				byte[] md5DecodedStr = messageDigest.digest();
				//之后以十六进制格式格式化
				StringBuffer hexValue = new StringBuffer();
				for (int i = 0; i < md5DecodedStr.length; i++) {
					int val = ((int) md5DecodedStr[i]) & 0xff;
					if (val < 16) hexValue.append("0");
					hexValue.append(Integer.toHexString(val));
				}
//			   String result = Base64.encodeToString(md5DecodedStr, Base64.DEFAULT);
				String result = hexValue.toString().toLowerCase();
				DebugUtils.logD(TAG, "sha1 encode " + message + " to " + result);
				return result;
			} catch (Exception e) {
				return null;
			}
		}

	}
}