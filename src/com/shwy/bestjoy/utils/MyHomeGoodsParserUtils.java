package com.shwy.bestjoy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class MyHomeGoodsParserUtils {
	private static final String TAG = "MyHomeGoodsParser";
	/*
	 * http://haier.dzbxk.com/9264574251AA8M8L0070OACC7M0443��ϱ���ĺ�������
	 * ������ + ��֯��������(һ��9λ) + �ͺ���(P��) + ������ˮ��(S��)
	 * ��������ַ��ȡ��SN,KY��
	 * SN=�ַ����ĵ�11λ��ʼ֮���ȫ���ַ���
	 * KY=�ַ����ĵ�2λ��ʼ֮���N���ַ���N=�ַ����ĵ�һλ����ֵ+9
	 * ��һλ�ַ���������ѭ1-9��A=10��B=11, Z=....������O��I��������Ϊ�����ʱ�����׺�0��1����
	 */
	private static final Pattern MyHomeGoodsQrcode_Pattern[] = new Pattern []{
		Pattern.compile("http://c.dzbxk.com/([0-9A-Z])(.+)"),
		Pattern.compile("http://haier.dzbxk.com/([0-9A-Z])(.+)"),
	};
	public static class MyHomeGoodsObject {
		public String mKY;
		public String mSN;
	}

	public static MyHomeGoodsObject extractFromDecodedString(String decodedString) {
		DebugUtils.logD(TAG, "extractFromDecodedString " + decodedString);
		return isMyHomeGoodsQrcode(decodedString);
	}
	
	private static MyHomeGoodsObject isMyHomeGoodsQrcode(String decodedString) {
		DebugUtils.logD(TAG, "enter isMyHomeGoodsQrcode()");
		if (TextUtils.isEmpty(decodedString)) {
			DebugUtils.logD(TAG, "decodedString is null, so just return null");
			return null;
		}
		DebugUtils.logD(TAG, "decodedString length " + decodedString.length());
		decodedString = decodedString.trim();
		decodedString = decodedString.replace("\r\n", "\n");
		DebugUtils.logD(TAG, "decodedString length " + decodedString.length());
		DebugUtils.logD(TAG, "start to extract " + decodedString);
		Matcher matcher = null;
		for(Pattern homeGoodsRqcodePattern:MyHomeGoodsQrcode_Pattern) {
			matcher = homeGoodsRqcodePattern.matcher(decodedString);
			if (matcher.find()) {
				String count = matcher.group(1);
				DebugUtils.logD(TAG, "find count char " + matcher.group(1));
				int len = convertValidateLength(count);
				DebugUtils.logD(TAG, "convert int len " + len);
				if (len == -1) {
					DebugUtils.logD(TAG, "can't find int value according to char value, return null");
					return null;
				}
				String result = matcher.group(2);
				DebugUtils.logD(TAG, "find " + result);
				
				MyHomeGoodsObject myHomeGoodsObject = new MyHomeGoodsObject();
				myHomeGoodsObject.mKY = result.substring(0, len + 9);
				myHomeGoodsObject.mSN = result.substring(9);
				DebugUtils.logD(TAG, "find KY " + myHomeGoodsObject.mKY);
				DebugUtils.logD(TAG, "find SN " + myHomeGoodsObject.mSN);
				return myHomeGoodsObject;
			}
		}
		return null;
	}
	
	private static final int[] VALIDATE_INT= new int[]{
		1, 2 ,3 ,4 ,5 ,6 ,7 ,8 ,9 ,10 ,
		11 ,12 ,13 ,14 ,15 ,16 ,17 ,18 ,19 ,20 ,
		21 ,22 ,23 ,24 ,25 ,26 ,27 ,28 ,29 ,30 ,
		31 ,32 ,33 ,
	};
	private static final String[] VALIDATE_CHARS = new String[]{
		"1","2","3","4","5","6","7","8","9","A",
		"B","C","D","E","F","G","H","J","K","L",
		"M","N","P","Q","R","S","T","U","V","W",
		"X","Y","Z",
	};
	private static int convertValidateLength(String str) {
		for(int index = 0; index < VALIDATE_CHARS.length; index++) {
			if (VALIDATE_CHARS[index].equals(str)) {
				return VALIDATE_INT[index];
			}
			
		}
		return -1;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		isMyHomeGoodsQrcode("http://haier.dzbxk.com/9264574251AA8M8L0070OACC7M0443");
	}
	
}
