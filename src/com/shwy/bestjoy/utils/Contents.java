/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shwy.bestjoy.utils;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap.CompressFormat;
import android.provider.Contacts;
import android.text.TextUtils;
import android.util.Log;

/**
 * The set of constants to use when sending Barcode Scanner an Intent which requests a barcode
 * to be encoded.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class Contents {
	private static final boolean DEBUG = DebugUtils.DEBUG_BJFILE;
	private static final String TAG = "Contents";
  private Contents() {
  }

  /**
   * All the formats we know about.
   */
  public static final class Format {
    public static final String UPC_A = "UPC_A";
    public static final String UPC_E = "UPC_E";
    public static final String EAN_8 = "EAN_8";
    public static final String EAN_13 = "EAN_13";
    public static final String CODE_39 = "CODE_39";
    public static final String CODE_128 = "CODE_128";
    public static final String QR_CODE = "QR_CODE";
    private Format() {
    }
  }
  
  public static final class Value{
	  /**
	   * BMǰ׺�ͺ�׺ʹ������ʶBM�ֶε�
	   * BMS BMǰ׺
	   */
	  public static final String BMS="BM:";
	  /**
	   * BMS BM��׺
	   */
	  public static final String BME="BM";
	  
	  public static final String BM="<BESTJOY>\n";
  }

  public static final class Type {
    /**
     * Plain text. Use Intent.putExtra(DATA, string). This can be used for URLs too, but string
     * must include "http://" or "https://".
     */
    public static final String TEXT = "TEXT_TYPE";

    /**
     * An email type. Use Intent.putExtra(DATA, string) where string is the email address.
     */
    public static final String EMAIL = "EMAIL_TYPE";

    /**
     * Use Intent.putExtra(DATA, string) where string is the phone number to call.
     */
    public static final String PHONE = "PHONE_TYPE";

    /**
     * An SMS type. Use Intent.putExtra(DATA, string) where string is the number to SMS.
     */
    public static final String SMS = "SMS_TYPE";

    /**
     * A contact. Send a request to encode it as follows:
     * <p/>
     * import android.provider.Contacts;
     * <p/>
     * Intent intent = new Intent(Intents.Encode.ACTION);
     * intent.putExtra(Intents.Encode.TYPE, CONTACT);
     * Bundle bundle = new Bundle();
     * bundle.putString(Contacts.Intents.Insert.NAME, "Jenny");
     * bundle.putString(Contacts.Intents.Insert.PHONE, "8675309");
     * bundle.putString(Contacts.Intents.Insert.EMAIL, "jenny@the80s.com");
     * bundle.putString(Contacts.Intents.Insert.POSTAL, "123 Fake St. San Francisco, CA 94102");
     * intent.putExtra(Intents.Encode.DATA, bundle);
     */
    public static final String CONTACT = "CONTACT_TYPE";
    
    /**
     * �ҵ���Ƭ����
     */
    public static final String MYCONTACT = "MYCONTACT_TYPE";
    
    /**
     * ������ϵ������
     */
    public static final String MEMOCONTACT = "MEMOCONTACT_TYPE";

    /**
     * A geographic location. Use as follows:
     * Bundle bundle = new Bundle();
     * bundle.putFloat("LAT", latitude);
     * bundle.putFloat("LONG", longitude);
     * intent.putExtra(Intents.Encode.DATA, bundle);
     */
    public static final String LOCATION = "LOCATION_TYPE";

    private Type() {
    }
  }

  /**
   * When using Type.CONTACT, these arrays provide the keys for adding or retrieving multiple
   * phone numbers and addresses.
   */
  public static final String[] PHONE_KEYS = {
      Contacts.Intents.Insert.PHONE,
      Contacts.Intents.Insert.SECONDARY_PHONE,
      Contacts.Intents.Insert.TERTIARY_PHONE
  };

  public static final String[] EMAIL_KEYS = {
      Contacts.Intents.Insert.EMAIL,
      Contacts.Intents.Insert.SECONDARY_EMAIL,
      Contacts.Intents.Insert.TERTIARY_EMAIL
  };
  
  public static final int[] TYPES = {
	  Contacts.ContactMethodsColumns.TYPE_WORK,
	  Contacts.ContactMethodsColumns.TYPE_HOME,
	  Contacts.ContactMethodsColumns.TYPE_OTHER
  };
  /**
   * ����card
   */
  public static final String BID="bid";
  public static final String MODE="mode";
  public static final String ORG="org";
  /**@deprecated use ID*/
  public static final String CARD_ID="id";
  /**@deprecated use ID*/
  public static final String MEMO_ID="id";
  public static final String ID ="id";
  
  /*���������ʹ�ã���������.jpgͼƬ��ʽ��qrcodeͼƬ*/
  public static final CompressFormat BITMAP_FORMAT=CompressFormat.JPEG;
  public static final String BITMAP_FORMAT_SUFFIX=".jpg";
  
  /**
   * added on 2012-01-09
   * @author chenkai
   *
   */
  public static class MingDang {
	  public static String CACHE_FILE_NAME_PREFIX = "cache_";
	  public static String CACHE_FILE_DIR = "contactinfo";
	  public static final String TAG = "MM:";
	  public static final String TAG_URL = "URL:";
	  public static int MM_LENGTH = 17;
	  /**ֱ�ӿ��Բ鿴��������ϵ����Ϣ,��http://c.mingdown.com/17λ���ţ���ͬ��http://www.mingdown.com/a.aspx?m=17λ*/
	  public static final String CLOUD_URI = "http://c.mingdown.com/";
	  public static final String WEB_URI = "http://www.mingdown.com/";
//	  public static final String WEB_URI_A_PREFIX = "a.aspx?m=";
	  public static final String WEB_URI_B_PREFIX = "b.aspx?m=";
//	  public static final String WEB_URI_D_PREFIX = "d.aspx?m=";
	  public static final String WEB_JIANKONG_URI = "http://www.mingdown.com/jiankong.aspx";
	  public static final int MIN_JIANKONG_LENGTH = 11;
	  public static final String WEB_URI_UNKOWN_INCOMING_CALL_PREFIX = "?m=";
	  public static final String WEB_URI_UNKOWN_OUTGOING_CALL_PREFIX = "?q=";
	  public static final String WEB_URI_UNKOWN_SMS_PREFIX = "?h=";
	  private static MingDang mInstance;
	  private static Pattern mMingDangPattern = Pattern.compile("(?i)^MM:\\s*(\\d{17})");
	  /**http://c.mingdown.com/[d/]17λmm��,��������ı�����ʽ,[d\]��Ϊ������������ʶ������ܹ��������ǵ�apk�������ӣ��������������ֱ�ӽ���17λmm*/
	  private static Pattern mCloudUrlPattern = Pattern.compile("(?i)((http://)*c\\.mingdown\\.com/(d/)*(\\d{17}))");
	  /**��ʾ������{@link #mCloudUrlPattern}ģʽ��mm�������ڵ�ƥ���飬��0��ʼ*/
	  private static final int INDEX_MM = 4;
	  /**�̼��������뱣���֣�Ҳ���̼ұ�ʶ������������ֻ�����̼�ʹ�ã�����Ĭ��ֵ*/
	  public static final String FLAG_MERCHANT="123456";
	  /**����ģ�� www.mingdown.com/send.aspx?m=�������ֻ�����|������mm��*/
	  public static final String WEB_URI_SEND_PREFIX="http://www.mingdown.com/send.aspx?m=";
	  /**�ն���ģ�� wwww.mingdown.com/rece.aspx?m=�����ֻ�����*/
	  public static final String WEB_URI_RECE_PREFIX="http://www.mingdown.com/rece.aspx?m=";
	  
	  
	  /**����MM��ȡ������λ���������123456�����ʾ�Ǹ�������*/
	  public static int PERSONAL_POSITION = 11;
	  public static boolean isPersonal(String mm) {
		  return !FLAG_MERCHANT.equals(mm.substring(PERSONAL_POSITION));
	  }
	  /**������Ƭ��ͷ��Ƭ����ַ�� http://www.mingdown.com/mmimage/����.jpg*/
	  public static final String WEB_URL_PERSONAL_AVATOR = "http://www.mingdown.com/mmimage/";
	  /**��λ��Ƭ��ͷ��Ƭ����ַ�� http://www.mingdown.com/com/mmimage/����.jpg*/
	  public static final String WEB_URL_COMPANY_AVATOR = "http://www.mingdown.com/com/mmimage/";
	  
	  /**
	   * ���硢ȥ�硢���ż����绰�������
	   * ��������绰����������ȡ11λ������11λ�����������û���д�����ź��ٴ�������ȡ11λ�����������11λ���ұ߲�0
	   * @param orgPhoneNumber
	   * @return null ���û���ҵ��������ɺ��룬���򷵻ع����ĺ���
	   */
//	  public static String buildValidPrivacyPhoneNumber(String orgPhoneNumber) {
//		  if (orgPhoneNumber == null) return orgPhoneNumber;
//		  String target = null;
//		  DebugLogger.logD(TAG, "buildValidPrivacyPhoneNumber src=" + orgPhoneNumber);
//		  orgPhoneNumber = orgPhoneNumber.replaceAll("[\\(\\+\\-\\*\\[\\] \\.\\)]", "");
//		  int len = orgPhoneNumber.length();
//		  if (len > MIN_JIANKONG_LENGTH) {
//			  target = orgPhoneNumber.substring(len -MIN_JIANKONG_LENGTH);
//		  } else if (len == MIN_JIANKONG_LENGTH) {
//			  target = orgPhoneNumber;
//		  } else if (len < MIN_JIANKONG_LENGTH) {
//			  //��Ҫ����Ե������������������
//			  //��λ���� + 7λ���� 
//			  //��λ���� + 8λ����
//			  //����+5λ���룬��021 95599
//			  String area = BJfileApp.getInstance().getPreferAreaCode();
//			  StringBuilder sb = new StringBuilder(area);
//			  sb.append(orgPhoneNumber);
//			  int addSpaceLen = MIN_JIANKONG_LENGTH - sb.length();
//			  if (addSpaceLen > 0) {
//				  for(int index=0; index < addSpaceLen; index++) {
//					  sb.insert(0, "0");
//				  }
//				  DebugLogger.logD(TAG, "add space at right of target=" + sb.toString());
//				  return sb.toString();
//			  }
//			  target = sb.substring(sb.length() -MIN_JIANKONG_LENGTH);
//		  } else if (len < 8) {
//			  
//		  }
//		  DebugLogger.logD(TAG, "buildValidPrivacyPhoneNumber target=" + target);
//		  return target;
//	  }
	  
	  /**
	   * �绰����ת������,���������ź�
	   * 1.������볤��<=8, ׷�����ţ�
	   * 2.ǰ�油5��0
	   * 3.��������ȡ��11λ
	   */
	  public static String buildValidPhoneNumber(String orgPhoneNumber, String areacode) {
		  if (orgPhoneNumber == null) return orgPhoneNumber;
		  DebugUtils.logD(TAG, "buildValidPhoneNumber orgPhoneNumber=" + orgPhoneNumber);
		  String target = null;
		  orgPhoneNumber = orgPhoneNumber.replaceAll("[\\(\\+\\-\\*\\[\\] \\.\\)]", "");
		  int len = orgPhoneNumber.length();
		  StringBuilder sb = new StringBuilder(orgPhoneNumber);
		  if (len <= 8) {
			  sb.insert(0, areacode);//׷������
			  DebugUtils.logD(TAG, "add areacode head " + sb.toString());
		  }
		  sb.insert(0, "00000");
		  DebugUtils.logD(TAG, "add 00000 head " + sb.toString());
		  len = sb.length();
		  target = sb.substring(len - PERSONAL_POSITION);
		  DebugUtils.logD(TAG, "buildValidPhoneNumber return final phoneNumber " + target);
		  return target;
	  }
	  
	  /**
	   * �绰����ת������,���������ź�
	   * 1.������볤��<=8, ׷�����ţ�
	   * 2.ǰ�油5��0
	   * 3.��������ȡ��11λ
	   * 4.׷��123456����17λ����
	   * @param orgPhoneNumber
	   * @return ת�����17λ����, ��������Ϲ��򣬷���null
	   */
	  public static String buildMerchantMM(String orgPhoneNumber, String areaCode) {
		  if (orgPhoneNumber == null) return orgPhoneNumber;
		  StringBuilder sb = new StringBuilder(buildValidPhoneNumber(orgPhoneNumber, areaCode));
		  sb.append(FLAG_MERCHANT);
		  DebugUtils.logD(TAG, "appended 123456 " + sb.toString());
		  
		  String target = sb.toString();
		 
		  DebugUtils.logD(TAG, "buildMerchantPhoneNumber return final mm " + target);
		  return target;
	  }
	  
	  /**
	   * 
	   * @param mm it must be a string with 17 digital characters
	   * @return true if mm is valid MingDang numbers
	   */
	  public static boolean isMingDangNo(String mm) {
		  return mm != null && mm.length()==MM_LENGTH;
	  }
	  
	  public static String parseMMFromContactNote(String note) {
		  if (TextUtils.isEmpty(note)) return null;
		  Matcher matcher = mMingDangPattern.matcher(note);
		  if (matcher.find()) {
			 return matcher.group(1);
		  }
		  return null;
	  }
	  
	  public static synchronized MingDang newInstance(){
		  if (mInstance == null) {
			  mInstance = new MingDang();
		  }
		  return mInstance;
	  }
	  
//	  public static String buildAWebUri(String...mm) {
//		  StringBuilder sb = new StringBuilder();
//		  sb.append(WEB_URI)
//		  .append(WEB_URI_A_PREFIX);
//		  if (mm != null && mm.length==MM_LENGTH) {
//			  sb.append(mm[0]);
//		  }
//		  return sb.toString();
//	  }
	  /**
	   * �������ص�ַ,����ʹ��{@link #buildDownloadUri(String mm)}
	   * @param mm
	   * @return
	   * @deprecated
	   */
	  public static String buildBWebUri(String mm) {
		  if (mm == null || mm.length() != MM_LENGTH) return null;
		  StringBuilder sb = new StringBuilder();
		  sb.append(WEB_URI)
		  .append(WEB_URI_B_PREFIX)
		  .append(mm);
		  return sb.toString();
	  }
	  /**
	   * ����vcf�ļ����ص�ַwww.mingdown.com/vcf/mm.vcf
	   * @param mm
	   * @deprecated �����������ȫ���ѱ�����ʹ����,�����ڲ�ʵ��Ҳ�Ѿ����˺���ķ�ʽ����Ҫ��ʹ�ø÷�������ʹ�ð�ȫ�����ط�ʽ{@link #buildSecurityDownloadUri()}
	   * @return
	   */
	  public static String buildDownloadUri(String mm) {
		  //modify by chenkai, 20131123, ����buildSecurityDownloadUri��ʽ begin
//		  if (mm == null || mm.length() != MM_LENGTH) return null;
//		  StringBuilder sb = new StringBuilder();
//		  sb.append(WEB_URI)
//		  .append("vcf/").append(mm).append(".vcf");
//		  return sb.toString();
		  return buildSecurityDownloadUri(mm);
		  //modify by chenkai, 20131123, ����buildSecurityDownloadUri��ʽ end
	  }
	  
	  private static final String SECURITY_DOWNLOAD_VCF_URL_PREFIXX = "http://www.mingdown.com/mobile/downLoadVcfByMM.ashx?MM=";
	  /**
	   * ������ȫ����Ƭ����URI, ��������Ҫ������ͷ������Key��Cell,key=md5(�û��ֻ�(�ֻ�����1�滻��i��ȥ�����һλ),��ǰ����)</br>
	   * http://www.mingdown.com/mobile/downLoadVcfByMM.ashx?MM=18696632323138465
	   * @return
	   */
	  public static String buildSecurityDownloadUri(String mm) {
		  if (mm == null || mm.length() != MM_LENGTH) return null;
		  StringBuilder sb = new StringBuilder(SECURITY_DOWNLOAD_VCF_URL_PREFIXX);
		  sb.append(mm);
		return sb.toString();
		  
	  }
	  
	  /**
	   * ��������δ֪�����ѯURL
	   * @param phoneNumber
	   * @return
	   */
	  public static String buildMingDownUnkownUriForCall(boolean incoming, String phoneNumber, String areaCode) {
		  phoneNumber = buildValidPhoneNumber(phoneNumber, areaCode);
		  if (!TextUtils.isEmpty(phoneNumber)) {
			  StringBuilder sb = new StringBuilder();
			  sb.append(WEB_JIANKONG_URI);
			  if (incoming) {
				  sb.append(WEB_URI_UNKOWN_INCOMING_CALL_PREFIX);
			  } else {
				  sb.append(WEB_URI_UNKOWN_OUTGOING_CALL_PREFIX);
			  }
			  sb.append(phoneNumber);
			  return sb.toString();
		  } else {
			  return null;
		  }
	  }
	  /**
	   * �����������Ų�ѯURL
	   * @param phoneNumber
	   * @return
	   */
	  public static String buildMingDownUnkownUriForSms(String mm) {
		  if (isMingDangNo(mm)) {
			  StringBuilder sb = new StringBuilder();
			  sb.append(WEB_JIANKONG_URI)
			  .append(WEB_URI_UNKOWN_SMS_PREFIX)
			  .append(mm);
			  return sb.toString();
		  }
		  return null;
	  }
	  
	  //add by chenkai, 2012-11-24, �Զ�ע�Ṧ�� begin
      //http:www.mingdown.com/ljzc.asmx/quickCreate?para=����|��λ|ְ��|�ֻ�|��������
	  public static final String WEB_NEW_ACCOUNT_URI = "http://www.mingdown.com/ljzc.asmx/quickCreate1?para=";
	  
	  //add by chenkai, 2012-11-24, �Զ�ע�Ṧ�� end
	  
	  //add by chenkai, 2013-02-02, ���Ӵ�����Ƭapi begin
	  /**��������ﱣ���˵�����MD, ���Զ����뱾������,�û��趨6λ������Ƭ��������,��������
	   * www.mingdown.com/quickCreateMM?para=urlencode(����|��λ|ְ��|�ֻ�|��������|MD|Email|Tag)*/
	  public static final String WEB_CREATE_CARD_URI = "http://www.mingdown.com/ljzc.asmx/quickCreateMM1?para=";
	  //add by chenkai, 2013-02-02, ���Ӵ�����Ƭapi end
	  
	  /**http://www.mingdown.com/cell/updatevcf.aspx?
	   * MM=xx&&org=xx&&username=xx&&title=xx&&memo=xx&&email=xx&&tag=xx ����xx��urlencode�����, �ɹ�ok,ʧ��failed*/
	  public static final String WEB_UPDATE_CARD_URI = "http://www.mingdown.com/mobile/updatevcf.ashx?"/*"http://www.mingdown.com/cell/updatevcf1.aspx?"*/;
	  
	  /**
	   * return a string array with 2 elements, [0] is mm, [1] is b type WEB URL.
	   * if the url is not a type url, we return string array with 2 null elements directly.
	   * @param url
	   * @return return a String[2], may be [null,null]
	   */
//	  public static String[] convertAUrlToBUrl(String url) {
//		  String[] result = new String[2];
//		  String aUrl = buildAWebUri();
//		  int start = url.indexOf(aUrl);
//		  if (start != -1) {//find a url, we need to convert it as b url.
//			  String mm = url.substring(start + aUrl.length()).trim();
//			  if (DEBUG) Log.v(TAG, "convertAUrlToBUrl find mm #" + mm);
//			  if (mm.length() == MM_LENGTH) {
//				  result[0] = mm;
//				  result[1] = buildBWebUri(mm);
//			  }
//		  }
//		  return result;
//	  }
	  
	  /**
	   * 
	   * @return mm���Լ�http://c.mingdown.com/d/mm��ϵ�˲鿴��ַ
	   */
	  public static String[] getCloudUri(String url) {
		  String[] result = null;
		  String mm = isCloudUri(url);
		  if (mm != null) {
			  if (DEBUG) Log.v(TAG, "getCloudUri find mm #" + mm);
			  result = new String[2];
			  result[0] = mm;
			  result[1] = buildCloudUri(mm);;
		  }
		  return result;
	  }
	  /**
	   * �����http://c.mingdown.com/[d/]17��URL�����طǿյ�17λMM��,[d/]��ʾ���п���
	   * @param url
	   * @return mm or null
	   */
	  public static String isCloudUri(String url) {
		  if (DEBUG) Log.v(TAG, "enter isCloudUri() # " + url);
		  String result = null;
		  Matcher matcher = mCloudUrlPattern.matcher(url);
		  if (matcher.find()) {
			  result = matcher.group(INDEX_MM);
		  } else {
			  result = null;
		  }
		  if (DEBUG) Log.v(TAG, "find # " + result);
		  return result;
	  }
	  /***
	   * ��������http://c.mingdown.com/d/17λ������URL
	   * @param mm
	   * @return
	   */
	  public static String buildCloudUri(String...mm) {
		  StringBuilder sb = new StringBuilder();
		  sb.append(CLOUD_URI);
		  sb.append("d/");
		  if (mm != null && mm.length==1) {
			  sb.append(mm[0]);
		  }
		  return sb.toString();
	  }
	  
	  /***
	   * ��������http://c.mingdown.com/17λ������URL
	   * @param mm
	   * @return
	   */
	  public static String buildDirectCloudUri(String...mm) {
		  StringBuilder sb = new StringBuilder();
		  sb.append(CLOUD_URI);
		  if (mm != null && mm.length==1) {
			  sb.append(mm[0]);
		  }
		  return sb.toString();
	  }
	  
	  /**
	   * 
	   * @param recipients �����˺��룬���ŷָ�
	   * @param senderMM ������������
	   * @return path������url
	   */
	  public static String buildSendUrl(String recipients, String senderMM) {
		  if (TextUtils.isEmpty(recipients) || TextUtils.isEmpty(senderMM)) {
			  throw new IllegalArgumentException("You must supply no-null recipients and no-null sender.");
		  }
		  StringBuilder sb = new StringBuilder(100);
		  sb.append(recipients);
		  sb.append("|");
		  sb.append(senderMM);
		  String encoded = URLEncoder.encode(sb.toString());
		  return WEB_URI_SEND_PREFIX + encoded;
	  }
	  
	  public static String builReceiveUrl(String myNumber) {
		  StringBuilder sb = new StringBuilder();
		  sb.append(WEB_URI_RECE_PREFIX).append(myNumber);
		  return sb.toString();
	  }
	  /**
	   * ����������Ƭ��ͷ��������ַ
	   * @param mm
	   * @return
	   */
	  public static String buildPersonalAvatorUrl(String mm) {
		  StringBuilder sb = new StringBuilder(WEB_URL_PERSONAL_AVATOR);
		  sb.append(mm).append(".jpg");
		  return sb.toString();
	  }
	  /**
	   * ������������Ƭ��ͷ��������ַ������MM���6λ�Ƿ�Ϊ123456��ѡ���Ӧ�ĸ��˺���ҵ��ͷ����ַ
	   * @param mm
	   * @return
	   */
	  public static String buildAvatorUrl(String mm) {
		  StringBuilder sb = new StringBuilder(WEB_URI);
		  if (!isPersonal(mm)) {
			  sb.append("com/");
		  }
		  sb.append("mmimage/");
		  sb.append(mm).append(".jpg");
		  return sb.toString();
	  }
	  /**�����û�pmd����ȡͷ����http://www.mingdown.com/image/md.jpg*/
	  public static String buildPmdAvatorUrl(String pmd) {
		  StringBuilder sb = new StringBuilder(WEB_URI);
		  sb.append("image/");
		  sb.append(pmd).append(".jpg");
		  return sb.toString();
	  }
	  /**
	   * ������ҵ��Ƭ��ͷ��������ַ
	   * @param mm
	   * @return
	   */
	  public static String buildCompanyAvatorUrl(String mm) {
		  StringBuilder sb = new StringBuilder(WEB_URL_COMPANY_AVATOR);
		  sb.append(mm).append(".jpg");
		  return sb.toString();
	  }
	  /**
	   * www.mingdown.com/cell/new1.aspx?vcf=mm|����������|�Լ����ֻ�|��ֹ����|�����|�ʱ��|���ַ ���ݵĲ�������URLENCODE���� (�����¼��ͬ�ᱨ���ء�)
	   * ��http://www.mingdown.com/cell/new1.aspx?vcf=13816284988201202|bestjoy2|13816284988|20130103|title001|201301020816|didian008
	   */
	  public static String buildCreateExchangeBC(String mm, String password, String tel, String deadline, String subject, String time, String where) {
		  StringBuilder sb = new StringBuilder();
		  sb.append(mm).append('|');
		  sb.append(password).append('|');
		  sb.append(tel).append('|');
		  sb.append(deadline).append('|');
		  sb.append(subject).append('|');
		  sb.append(time).append('|');
		  sb.append(where);
		  String path = URLEncoder.encode(sb.toString());
		  return "http://www.mingdown.com/cell/new1.aspx?vcf=" + path;
	  }
	  /**
	   * ���뱾�ξۻ����Ƭ�������룬���뽻����
	   * www.mingdown.com/cell/add.aspx?vcf=mm|����������|�Լ����ֻ� ���ݵĲ�������URLENCODE����(ע:����Ǵ���һ�°�ť������,���밵���Զ�����)
	   * @return
	   */
      public static String buildJoinExchangeBC(String mm, String password, String tel) {
    	  StringBuilder sb = new StringBuilder();
		  sb.append(mm).append('|');
		  sb.append(password).append('|');
		  sb.append(tel);
		  String path = URLEncoder.encode(sb.toString());
		  return "http://www.mingdown.com/cell/add.aspx?vcf=" + path;
	  }
      /**
       * �鿴��¼����ť���� �� http://www.mingdown.com/cell/seetitle1.aspx?cell=�ֻ�&pageindex=�鿴ҳ������&pagesize=ÿҳ��С 
       * (��www.mingdown.com/cell/ seetitle.aspx?cell=13816284988 &pageindex=1&pagesize=10)
       * @return
       */
      public static String buildJoinExchangeBCHistory(String tel, int pageIndex, int pageSize) {
    	  StringBuilder sb = new StringBuilder("http://www.mingdown.com/cell/seetitle.aspx?cell=");
		  sb.append(tel).append('&');
		  sb.append("pageindex=").append(pageIndex).append('&');
		  sb.append("pagesize=").append(pageSize);
    	  return sb.toString();
	  }
      
      public static String buildJoinExchangeBCHistory(String mm) {
    	  StringBuilder sb = new StringBuilder("http://www.mingdown.com/cell/seetitle2.aspx?mm=");
    	  sb.append(mm);
    	  return sb.toString();
	  }
      
      /**
       * www.mingdown.com/cell/SeeAll.aspx?ID=���ص�ID , 
       * ������Ƭ�б�,��ֱ�ӽ�����Խ�������Ƭҳ��,��ʾ���Խ����� ��Ƭ�б�,������������Ƭ���档������Ƭ���Թ����� ����ʾ
       */
      public static String buildExchangeBCList(String id) {
		  StringBuilder sb = new StringBuilder("http://www.mingdown.com/cell/SeeAll.aspx?ID=");
		  sb.append(id);
		  return sb.toString();
	  }
      
      public static String buildPageQuery(String url, int pageIndex, int pageSize) {
    	  StringBuilder sb = new StringBuilder(url).append('&');
		  sb.append("pageindex=").append(pageIndex).append('&');
		  sb.append("pagesize=").append(pageSize);
    	  return sb.toString();
	  }
      /**
       * ���ڵ�½ģ����֤
       * http://www.mingdown.com/cell/denglu.aspx?m=�ֻ�����&p=��¼����
       */
      public static String buildLogin(String tel, String password) {
    	  StringBuilder sb = new StringBuilder("http://www.mingdown.com/mobile/denglu.ashx?m=");
    	  sb.append(tel).append("&p=").append(password);
    	  return sb.toString();
      }
      /***
       * �һ����� ��http://www.mingdown.com/cell/zhaohui.aspx?m=13816284988�� ��������ǵ����˶���ƽ̨�������뷢�͵��ֻ��ϡ�
       * @return
       */
      public static String buildFindPassword(String tel) {
    	  StringBuilder sb = new StringBuilder("http://www.mingdown.com/cell/zhaohui.aspx?m=");
    	  sb.append(tel);
    	  return sb.toString();
      }
      
  }
}
