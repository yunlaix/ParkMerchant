package com.xs.parkmerchant.Net;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Man on 2016/7/5.
 */
public class NetCore {
    public static String postResulttoNet (String url , List<NameValuePair> params) throws Exception{
        HttpPost httpRequest = new HttpPost(url);
        HttpEntity httpEntity = new UrlEncodedFormEntity(params, "utf-8");
        httpRequest.setEntity(httpEntity);
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
        org.apache.http.HttpResponse httpResponse = httpClient.execute(httpRequest);

        String result = "";
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            BufferedReader bin = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            result = bin.readLine();
        }
        return result;
    }
}
