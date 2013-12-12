package com.google.zxing;
/**
 * Quoted-PrintableҲ��MIME�ʼ��г��õı��뷽ʽ֮һ��ͬBase64һ������Ҳ��������ַ��������ݱ����ȫ��ASCII��Ŀɴ�ӡ�ַ�����
 * Quoted-Printable����Ļ��������ǣ�����������33-60��62-126��Χ�ڵģ�ֱ������������������Ϊ��=���������ֽڵ�HEX��(��д)��
 * Ϊ��֤����в������涨����(76���ַ�)������β�ӡ�=\r\n��������Ϊ��س��� 
 */


import java.io.UnsupportedEncodingException;

/**
 * 
 * @author yeluosuifeng2005@gmail.com (�¿�)
 *
 */
public class QuotedPrintableDecoder {

	public QuotedPrintableDecoder() {

	}

	static public String EncodeQuoted(String pSrc) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < pSrc.length(); i++) {
			if(i%76==0) {//Ϊ��֤����в������涨����(76���ַ�)������β�ӡ�=\r\n��������Ϊ��س�
				result.append("=\r\n");
			}
			char ch = pSrc.charAt(i);
			if ((ch >= '!') && (ch <= '~') && (ch != '=')) {
				// ASCII 33-60, 62-126ԭ�����������������
				result.append(ch);
			} else {
				String string =Integer.toHexString(ch).toUpperCase();
				if(string.length()==1)result.append("=0" + Integer.toHexString(ch).toUpperCase());
				else result.append('=' + Integer.toHexString(ch).toUpperCase());
			}
		}
		result.append("\r\n");
		return result.toString();
	}

	/**
	 * Quoted-Printable����ܼ򵥣���������̷����������ˡ�
	 * 
	 * @param pSrc
	 */
	static public String DecodeQuoted(String pSrc) {
		pSrc = pSrc.replace("=\r\n", "");
		int i = 0;
		StringBuffer sb = new StringBuffer();
		while (i < pSrc.length()) {
			char ch = pSrc.charAt(i);
			if (ch == '=') {// �Ǳ����ֽ�
				int toChar = Integer.parseInt(pSrc.substring(i+1 , i + 3), 16);
				sb.append((char) toChar);
				i += 3;
			} else { // �Ǳ����ֽ�
				sb.append(ch);
				i++;
			}
		}
		try {
			return new String(sb.toString().replace("\r", "").getBytes("ISO8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;// ����ʧ�ܷ���null
		}
	}

//	public static void main(String[] args) {
//		String encoded = "=\r\n"
//				+ "200072=20=E4=B8=8A=E6=B5=B7=E5=B8=82=E9=97=B8=E5=8C=97=E5=8C=BA=E5=B9=BF=E=\r\n"+
//				"4=B8=AD=E8=A5=BF=E8=B7=AF757=E5=8F=B7=E5=A4=9A=E5=AA=92=E4=BD=93=E5=A4=A7=E5=8E" +
//				"=A69=E6=A5=BC@31=C2=B016'45.76\",121=C2=B026'07.65\"";
//		String decoded="0��31��127(��33��)�ǿ����ַ���ͨ��ר���ַ�������Ϊ����ʾ�ַ���������Ʒ���LF�����У���CR���س�����FF����ҳ����DEL��ɾ������BS���˸�)��BEL�����壩�ȣ�ͨ��ר���ַ���SOH����ͷ����EOT����β����ACK��ȷ�ϣ��ȣ�ASCIIֵΪ 8��9��10 �� 13 �ֱ�ת��Ϊ�˸��Ʊ����кͻس��ַ������ǲ�û���ض���ͼ����ʾ����������ͬ��Ӧ�ó��򣬶����ı���ʾ�в�ͬ��Ӱ�졣32��126(��95��)���ַ�(32sp�ǿո񣩣�����48��57Ϊ0��9ʮ�����������֣� ����65��90Ϊ26����дӢ����ĸ��97��122��Ϊ26��СдӢ����ĸ������ΪһЩ�����š�������ŵȡ� ����ͬʱ��Ҫע�⣬�ڱ�׼ASCII�У������λ(b7)������żУ��λ����ν��żУ�飬��ָ�ڴ��봫�͹��������������Ƿ���ִ����һ�ַ�����һ�����У���żУ�����֡���У��涨����ȷ�Ĵ���һ���ֽ���1�ĸ��������������������������������λb7��1��żУ��涨����ȷ�Ĵ���һ���ֽ���1�ĸ���������ż��������ż�����������λb7��1�� ������128����Ϊ��չASCII�룬Ŀǰ������x86��ϵͳ��֧��ʹ����չ���򡰸ߡ���ASCII����չ ASCII ������ÿ���ַ��ĵ� 8 λ����ȷ�����ӵ� 128 ����������ַ�����������ĸ��ͼ�η��š�";
//
//		String test;
//		try {
//			test = QuotedPrintableDecoder.EncodeQuoted(new String(decoded.getBytes("UTF-8"),"ISO8859-1"));
//			System.out.println(test);
//			String sb = QuotedPrintableDecoder.DecodeQuoted(test);
//			System.out.println(sb);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//
//	}

}
