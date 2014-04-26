package com.shwy.bestjoy.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import com.shwy.bestjoy.utils.SecurityUtils.SecurityKeyValuesObject;

public class NetworkUtils {
	private static final String TAG = "NetworkUtils";
	
	public static InputStream openContectionLocked(String uri, SecurityKeyValuesObject securityKeyValues) throws ClientProtocolException, IOException {
		DebugUtils.logD(TAG, "HttpGet uri=" + uri);
		HttpGet httpRequest = new HttpGet(uri);
		addSecurityKeyValuesObject(httpRequest, securityKeyValues);
		HttpClient httpClient = /*new DefaultHttpClient();*/AndroidHttpClient.newInstance("android");
		HttpResponse response = httpClient.execute(httpRequest);
		int stateCode = response.getStatusLine().getStatusCode();
		DebugUtils.logD(TAG, "return HttpStatus is " + stateCode);
		if(!httpStatusOk(stateCode)) {
			return null;
		}
		return response.getEntity().getContent();
	}
	
	public static boolean httpStatusOk(int statusCode) {
		return statusCode == HttpStatus.SC_OK || statusCode == 302;
	}
	/**
	 * 
	 * @param uri 网址，如http://www.baidu.com/
	 * @param path 需要使用URLEncoder编码的路径
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
	
	/**
	 * 
	 * @param uri 网址，如http://www.baidu.com/
	 * @param path 需要使用URLEncoder编码的路径
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static InputStream openPostContectionLocked(String uri, String paramName, String paramValue, SecurityKeyValuesObject securityKeyValues) throws ClientProtocolException, IOException {
//		String encodedUri = uri + URLEncoder.encode(path);
		DebugUtils.logD(TAG, "HttpPost uri=" + uri);
		HttpPost httpRequest = new HttpPost(uri);
		/**Post 运行传递变量必须用 NameValuePair[] 数组存储*/  
        List<NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair(paramName, paramValue));
        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));  
        addSecurityKeyValuesObject(httpRequest, securityKeyValues);
		HttpClient httpClient = /*new DefaultHttpClient();*/AndroidHttpClient.newInstance("android");
		HttpResponse response = httpClient.execute(httpRequest);
		int stateCode = response.getStatusLine().getStatusCode();
		DebugUtils.logD(TAG, "return HttpStatus is " + stateCode);
		if(!httpStatusOk(stateCode)) {
			return null;
		}
		return response.getEntity().getContent();
	}
	
	public static HttpResponse openContectionLockedV2(String uri, SecurityKeyValuesObject securityKeyValues) throws ClientProtocolException, IOException{
		DebugUtils.logD(TAG, "HttpGet uri=" + uri);
		HttpGet httpRequest = new HttpGet(uri);
		addSecurityKeyValuesObject(httpRequest, securityKeyValues);
		HttpClient httpClient = /*new DefaultHttpClient();*/AndroidHttpClient.newInstance("android");
		return httpClient.execute(httpRequest);
	}
	
	public static String getContentFromInput(InputStream is) {
		if (is == null) {
			DebugUtils.logD(TAG, "getContentFromInput passed null InputStream in");
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
