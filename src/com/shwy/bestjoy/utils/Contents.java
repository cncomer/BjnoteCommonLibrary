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

import android.graphics.Bitmap.CompressFormat;
import android.provider.Contacts;
import android.text.TextUtils;
import android.util.Log;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	   * BM前缀和后缀使用来标识BM字段的
	   * BMS BM前缀
	   */
	  public static final String BMS="BM:";
	  /**
	   * BMS BM后缀
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
     * 我的名片类型
     */
    public static final String MYCONTACT = "MYCONTACT_TYPE";
    
    /**
     * 备用联系人类型
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
   * 用于card
   */
  public static final String BID="bid";
  public static final String MODE="mode";
  public static final String ORG="org";
  /**@deprecated use ID*/
  public static final String CARD_ID="id";
  /**@deprecated use ID*/
  public static final String MEMO_ID="id";
  public static final String ID ="id";
  
  /*这两个配合使用，用于生成.jpg图片格式的qrcode图片*/
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
	  /**直接可以查看名档网联系人信息,如http://c.mingdown.com/17位名号，等同于http://www.mingdown.com/a.aspx?m=17位*/
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
	  /**http://c.mingdown.com/[d/]17位mm号,这是条码的编码样式,[d\]是为了让其他条码识别软件能够跳到我们的apk下载连接，而名档网软件则直接解析17位mm*/
	  private static Pattern mCloudUrlPattern = Pattern.compile("(?i)((http://)*c\\.mingdown\\.com/(d/)*(\\d{17}))");
	  /**表示的是在{@link #mCloudUrlPattern}模式中mm号码所在的匹配组，由0开始*/
	  private static final int INDEX_MM = 4;
	  /**商家名号密码保留字，也是商家标识，该名号密码只能是商家使用，且是默认值*/
	  public static final String FLAG_MERCHANT="123456";
	  /**发送模块 www.mingdown.com/send.aspx?m=收信人手机号码|发件人mm号*/
	  public static final String WEB_URI_SEND_PREFIX="http://www.mingdown.com/send.aspx?m=";
	  /**收短信模块 wwww.mingdown.com/rece.aspx?m=本人手机号码*/
	  public static final String WEB_URI_RECE_PREFIX="http://www.mingdown.com/rece.aspx?m=";
	  
	  
	  /**根据MM号取出后六位，如果不是123456，则表示是个人名号*/
	  public static int PERSONAL_POSITION = 11;
	  public static boolean isPersonal(String mm) {
		  return !FLAG_MERCHANT.equals(mm.substring(PERSONAL_POSITION));
	  }
	  /**个人名片大头照片的网址是 http://www.mingdown.com/mmimage/名号.jpg*/
	  public static final String WEB_URL_PERSONAL_AVATOR = "http://www.mingdown.com/mmimage/";
	  /**单位名片大头照片的网址是 http://www.mingdown.com/com/mmimage/名号.jpg*/
	  public static final String WEB_URL_COMPANY_AVATOR = "http://www.mingdown.com/com/mmimage/";
	  
	  /**
	   * 来电、去电、短信监听电话号码规则
	   * 呼入呼出电话，从右往左取11位，不足11位加上设置中用户填写的区号后再从右往左取11位，如果还不足11位则右边补0
	   * @param orgPhoneNumber
	   * @return null 如果没有找到规则生成号码，否则返回规则后的号码
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
//			  //主要是针对的座机，情况大致如下
//			  //四位区号 + 7位号码 
//			  //三位区号 + 8位号码
//			  //区号+5位号码，如021 95599
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
	   * 电话号码转换规则,用于输名号和
	   * 1.如果号码长度<=8, 追加区号；
	   * 2.前面补5个0
	   * 3.从右往左取出11位
	   */
	  public static String buildValidPhoneNumber(String orgPhoneNumber, String areacode) {
		  if (orgPhoneNumber == null) return orgPhoneNumber;
		  DebugUtils.logD(TAG, "buildValidPhoneNumber orgPhoneNumber=" + orgPhoneNumber);
		  String target = null;
		  orgPhoneNumber = orgPhoneNumber.replaceAll("[\\(\\+\\-\\*\\[\\] \\.\\)]", "");
		  int len = orgPhoneNumber.length();
		  StringBuilder sb = new StringBuilder(orgPhoneNumber);
		  if (len <= 8) {
			  sb.insert(0, areacode);//追加区号
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
	   * 电话号码转换规则,用于输名号和
	   * 1.如果号码长度<=8, 追加区号；
	   * 2.前面补5个0
	   * 3.从右往左取出11位
	   * 4.追加123456构成17位名号
	   * @param orgPhoneNumber
	   * @return 转换后的17位名号, 如果不符合规则，返回null
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
	   * 构建下载地址,建议使用{@link #buildDownloadUri(String mm)}
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
	   * 构建vcf文件下载地址www.mingdown.com/vcf/mm.vcf
	   * @param mm
	   * @deprecated 这个方法不安全，已被放弃使用了,并且内部实现也已经成了后面的方式，不要在使用该方法，请使用安全的下载方式{@link #buildSecurityDownloadUri(String mm)}
	   * @return
	   */
	  public static String buildDownloadUri(String mm) {
		  //modify by chenkai, 20131123, 增加buildSecurityDownloadUri方式 begin
//		  if (mm == null || mm.length() != MM_LENGTH) return null;
//		  StringBuilder sb = new StringBuilder();
//		  sb.append(WEB_URI)
//		  .append("vcf/").append(mm).append(".vcf");
//		  return sb.toString();
		  return buildSecurityDownloadUri(mm);
		  //modify by chenkai, 20131123, 增加buildSecurityDownloadUri方式 end
	  }
	  
	  private static final String SECURITY_DOWNLOAD_VCF_URL_PREFIXX = "http://www.mingdown.com/mobile/downLoadVcfByMM.ashx?MM=";
	  /**
	   * 构建安全的名片下载URI, 该链接需要在请求头中增加Key和Cell,key=md5(用户手机(手机号码1替换成i，去掉最后一位),当前日期)</br>
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
	   * 构建来电未知号码查询URL
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
	   * 构建特征短信查询URL
	   * @param mm
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
	  
	  //add by chenkai, 2012-11-24, 自动注册功能 begin
      //http:www.mingdown.com/ljzc.asmx/quickCreate?para=姓名|单位|职务|手机|名号密码
	  public static final String WEB_NEW_ACCOUNT_URI = "http://www.mingdown.com/ljzc.asmx/quickCreate1?para=";
	  
	  //add by chenkai, 2012-11-24, 自动注册功能 end
	  
	  //add by chenkai, 2013-02-02, 增加创建名片api begin
	  /**如果设置里保存了档案号MD, 则自动填入本机号码,用户设定6位数字名片下载密码,则联网，
	   * www.mingdown.com/quickCreateMM?para=urlencode(姓名|单位|职务|手机|名号密码|MD|Email|Tag)*/
	  public static final String WEB_CREATE_CARD_URI = "http://www.mingdown.com/ljzc.asmx/quickCreateMM1?para=";
	  //add by chenkai, 2013-02-02, 增加创建名片api end
	  
	  /**http://www.mingdown.com/cell/updatevcf.aspx?
	   * MM=xx&&org=xx&&username=xx&&title=xx&&memo=xx&&email=xx&&tag=xx 其中xx是urlencode过后的, 成功ok,失败failed*/
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
	   * @return mm号以及http://c.mingdown.com/d/mm联系人查看网址
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
	   * 如果是http://c.mingdown.com/[d/]17的URL，返回非空的17位MM号,[d/]表示可有可无
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
	   * 构建形如http://c.mingdown.com/d/17位的名号URL
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
	   * 构建形如http://c.mingdown.com/17位的名号URL
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
	   * @param recipients 收信人号码，逗号分割
	   * @param senderMM 发信人名档号
	   * @return path编码后的url
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
	   * 构建个人名片大头贴下载网址
	   * @param mm
	   * @return
	   */
	  public static String buildPersonalAvatorUrl(String mm) {
		  StringBuilder sb = new StringBuilder(WEB_URL_PERSONAL_AVATOR);
		  sb.append(mm).append(".jpg");
		  return sb.toString();
	  }
	  /**
	   * 构建名档网名片大头贴下载网址，依据MM最后6位是否为123456来选择对应的个人和企业大头贴网址
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
	  /**根据用户pmd来获取头像，如http://www.mingdown.com/image/md.jpg*/
	  public static String buildPmdAvatorUrl(String pmd) {
		  StringBuilder sb = new StringBuilder(WEB_URI);
		  sb.append("image/");
		  sb.append(pmd).append(".jpg");
		  return sb.toString();
	  }
	  /**
	   * 构建企业名片大头贴下载网址
	   * @param mm
	   * @return
	   */
	  public static String buildCompanyAvatorUrl(String mm) {
		  StringBuilder sb = new StringBuilder(WEB_URL_COMPANY_AVATOR);
		  sb.append(mm).append(".jpg");
		  return sb.toString();
	  }
	  /**
	   * www.mingdown.com/cell/new1.aspx?vcf=mm|交换的密码|自己的手机|截止日期|活动主题|活动时间|活动地址 传递的参数请用URLENCODE编码 (如果记录相同会报错返回。)
	   * 如http://www.mingdown.com/cell/new1.aspx?vcf=13816284988201202|bestjoy2|13816284988|20130103|title001|201301020816|didian008
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
	   * 输入本次聚会的名片交换密码，加入交换。
	   * www.mingdown.com/cell/add.aspx?vcf=mm|交换的密码|自己的手机 传递的参数请用URLENCODE编码(注:如果是从拍一下按钮过来的,密码暗号自动填入)
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
       * 查看记录”按钮。调 用 http://www.mingdown.com/cell/seetitle1.aspx?cell=手机&pageindex=查看页的索引&pagesize=每页大小 
       * (如www.mingdown.com/cell/ seetitle.aspx?cell=13816284988 &pageindex=1&pagesize=10)
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
       * www.mingdown.com/cell/SeeAll.aspx?ID=返回的ID , 
       * 会获得名片列表,则直接进入可以交换的名片页面,显示可以交换的 名片列表,界面类似收名片界面。所有名片可以滚动拖 动显示
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
       * 用于登陆模块验证
       * http://www.mingdown.com/cell/denglu.aspx?m=手机号码&p=登录密码
       */
      public static String buildLogin(String tel, String password) {
    	  StringBuilder sb = new StringBuilder("http://www.mingdown.com/mobile/denglu.ashx?m=");
    	  sb.append(tel).append("&p=").append(password);
    	  return sb.toString();
      }
      /***
       * 找回密码 如http://www.mingdown.com/cell/zhaohui.aspx?m=13816284988， 这个功能是调用了短信平台，将密码发送到手机上。
       * @return
       */
      public static String buildFindPassword(String tel) {
    	  StringBuilder sb = new StringBuilder("http://www.mingdown.com/cell/zhaohui.aspx?m=");
    	  sb.append(tel);
    	  return sb.toString();
      }
      
  }
}
