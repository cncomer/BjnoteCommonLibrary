package com.shwy.bestjoy.utils;

import com.shwy.bestjoy.exception.StatusException;
import com.shwy.bestjoy.utils.SecurityUtils.SecurityKeyValuesObject;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class NetworkUtils {
	private static final String TAG = "NetworkUtils";
	
	public static InputStream openContectionLocked(String uri, SecurityKeyValuesObject securityKeyValues) throws IOException {
//		DebugUtils.logNetworkOp(TAG, "HttpGet uri=" + uri);
		if (uri == null) {
			return null;
		}
		int index = uri.indexOf("?");
		if (index > 0 && index < uri.length()) {
			DebugUtils.logD(TAG, "HttpGet param=" + uri.substring(index+1));
		}
		 
		HttpGet httpRequest = new HttpGet(uri);
		addSecurityKeyValuesObject(httpRequest, securityKeyValues);
		HttpClient httpClient = /*new DefaultHttpClient();*/AndroidHttpClient.newInstance("android");
		HttpResponse response = httpClient.execute(httpRequest);
		int stateCode = response.getStatusLine().getStatusCode();
		DebugUtils.logNetworkOp(TAG, "return HttpStatus is " + stateCode);
		if(!httpStatusOk(stateCode)) {
			throw new StatusException(String.valueOf(stateCode), String.valueOf(stateCode));
		}
		return response.getEntity().getContent();
	}
	
	public static boolean httpStatusOk(int statusCode) {
		return statusCode == HttpStatus.SC_OK || statusCode == 302;
	}
	/**
	 * 
	 * @param uri ��ַ����http://www.baidu.com/
	 * @param path ��Ҫʹ��URLEncoder�����·��
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static InputStream openContectionLocked(String uri, String path, SecurityKeyValuesObject securityKeyValues) throws ClientProtocolException, IOException {
		String encodedUri = uri + URLEncoder.encode(path);
		return openContectionLocked(encodedUri, securityKeyValues);
	}
	
	public static InputStream openContectionLocked(String[] uris, String[] paths, SecurityKeyValuesObject securityKeyValues) throws ClientProtocolException, IOException {
		StringBuffer encodedUri = new StringBuffer();
		int i = 0;
		for(String uri : uris) {
			encodedUri.append(uri);
			encodedUri.append(URLEncoder.encode(paths[i++]));
		}
		return openContectionLocked(encodedUri.toString(), securityKeyValues);
	}
	
	public static String getUrlEncodedString(String[] params, String[] values) {
		StringBuffer encodedUri = new StringBuffer();
		int i = 0;
		for(String param : params) {
			encodedUri.append(param);
			encodedUri.append(URLEncoder.encode(values[i++]));
		}
		return encodedUri.toString();
	}
	
	/**
	 *
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static InputStream openPostContectionLocked(String uri, String paramName, String paramValue, SecurityKeyValuesObject securityKeyValues) throws ClientProtocolException, IOException {
//		String encodedUri = uri + URLEncoder.encode(path);
//		DebugUtils.logNetworkOp(TAG, "HttpPost uri=" + uri);
		DebugUtils.logD(TAG, "HttpPost paramName=" + paramName + ", paramValue=" + paramValue);
		HttpPost httpRequest = new HttpPost(uri);
		/**Post ���д��ݱ��������� NameValuePair[] ����洢*/
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(paramName, paramValue));
        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));  
        addSecurityKeyValuesObject(httpRequest, securityKeyValues);
		HttpClient httpClient = /*new DefaultHttpClient();*/AndroidHttpClient.newInstance("android");
		HttpResponse response = httpClient.execute(httpRequest);
		int stateCode = response.getStatusLine().getStatusCode();
		DebugUtils.logNetworkOp(TAG, "return HttpStatus is " + stateCode);
		if(!httpStatusOk(stateCode)) {
			throw new StatusException(String.valueOf(stateCode), String.valueOf(stateCode));
		}
		return response.getEntity().getContent();
	}
	/**
	 * 
	 * @param uri
	 * @param param  参数键值对
	 * @param securityKeyValues
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static InputStream openPostContectionLocked(String uri, HashMap<String, String> param, SecurityKeyValuesObject securityKeyValues) throws ClientProtocolException, IOException {
//		String encodedUri = uri + URLEncoder.encode(path);
//		DebugUtils.logNetworkOp(TAG, "HttpPost uri=" + uri);
		HttpPost httpRequest = new HttpPost(uri);
		/**Post ���д��ݱ��������� NameValuePair[] ����洢*/
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Set<String>  keySet = param.keySet();
        for(String key: keySet) {
        	params.add(new BasicNameValuePair(key, param.get(key)));
        }
        DebugUtils.logD(TAG, "HttpPost param=" + params);
        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));  
        addSecurityKeyValuesObject(httpRequest, securityKeyValues);
		HttpClient httpClient = /*new DefaultHttpClient();*/AndroidHttpClient.newInstance("android");
		HttpResponse response = httpClient.execute(httpRequest);
		int stateCode = response.getStatusLine().getStatusCode();
		DebugUtils.logNetworkOp(TAG, "return HttpStatus is " + stateCode);
		if(!httpStatusOk(stateCode)) {
			throw new StatusException(String.valueOf(stateCode), String.valueOf(stateCode));
		}
		return response.getEntity().getContent();
	}
	
	public static HttpResponse openContectionLockedV2(String uri, SecurityKeyValuesObject securityKeyValues) throws ClientProtocolException, IOException{
//		DebugUtils.logNetworkOp(TAG, "HttpGet uri=" + uri);
		HttpGet httpRequest = new HttpGet(uri);
		addSecurityKeyValuesObject(httpRequest, securityKeyValues);
		HttpClient httpClient = /*new DefaultHttpClient();*/AndroidHttpClient.newInstance("android");
		return httpClient.execute(httpRequest);
	}
	
	public static String getContentFromInput(InputStream is) {
		if (is == null) {
			DebugUtils.logNetworkOp(TAG, "getContentFromInput passed null InputStream in");
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int size;
		try {
			size = is.read(buffer);
			while (size >= 0) {
				out.write(buffer, 0, size);
				size = is.read(buffer);
			}
			out.flush();
			buffer = out.toByteArray();
			out.close();
			String result = new String(buffer, "UTF-8");
			DebugUtils.logD(TAG, "getContentFromInput return " + result);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void closeInputStream(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void closeOutStream(OutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void addSecurityKeyValuesObject(HttpRequest request, SecurityKeyValuesObject keyValues) {
		if (keyValues == null) {
			return;
		}
		HashMap<String, String> map = keyValues.mKeyValuesMap;
		for(String key:map.keySet()) {
			request.addHeader(key, map.get(key));
		}
	}
}
