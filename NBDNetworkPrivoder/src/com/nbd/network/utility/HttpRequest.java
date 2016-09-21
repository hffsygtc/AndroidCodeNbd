package com.nbd.network.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

/**
 * 公共方法 访问接口并打印输出
 * 
 * @param url
 * @param jsonIn
 * @throws Exception
 */
public class HttpRequest {
	private static String TAG = "HttpRequest";

	public static String doPost(String json, String url) {
		if (null == json)
			return "json is null";

		String message = null;
		try {
			StringEntity entity = new StringEntity(json, HTTP.UTF_8);
			entity.setContentType("application/json");
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(entity);
			// httpPost.setConnectionRequest()
			// 请求超时
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			// 读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					5000);

			HttpResponse response = client.execute(httpPost);
			InputStream inputStream = response.getEntity().getContent();
			StringBuffer buffer = new StringBuffer();
			InputStreamReader inputReader = new InputStreamReader(inputStream);
			BufferedReader bufferReader = new BufferedReader(inputReader);
			String str = new String("");

			while ((str = bufferReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferReader.close();
			message = buffer.toString();
			System.out.println(message);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return message;
	}

	public static String doGet(String url, String json) {
		if (null == json)
			return "json is null";
		String message = null;
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url + json);
			System.out.println(url + json);

			HttpResponse response = client.execute(httpGet);
			InputStream inputStream = response.getEntity().getContent();
			StringBuffer buffer = new StringBuffer();
			InputStreamReader inputReader = new InputStreamReader(inputStream);
			BufferedReader bufferReader = new BufferedReader(inputReader);
			String str = new String("");

			while ((str = bufferReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferReader.close();
			message = buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * 公共方法 访问接口并打印输出
	 * 
	 * @param url
	 * @param jsonIn
	 * @throws Exception
	 */
	public String httpIO(String url, String jsonIn) throws Exception {
		StringEntity entity = new StringEntity(jsonIn, HTTP.UTF_8);
		entity.setContentType("application/json");

		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(entity);

		HttpResponse response = client.execute(httpPost);
		InputStream inputStream = response.getEntity().getContent();
		StringBuffer buffer = new StringBuffer();
		InputStreamReader inputReader = new InputStreamReader(inputStream);
		BufferedReader bufferReader = new BufferedReader(inputReader);
		String str = new String("");
		while ((str = bufferReader.readLine()) != null) {
			buffer.append(str);
		}
		bufferReader.close();
		String jsonOut = buffer.toString();
		return jsonOut;
	}
}
