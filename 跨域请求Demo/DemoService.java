package com.test.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.entity.Demo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;

@Service
@Slf4j
public class DemoService {

    private static String url = "http://172.16.19.154:30100/api/sys/user";
    private static String token = "http://172.16.19.188:8099/api/v1/auth/oauth/token";
    private static boolean debug = true;

    public String response() throws IOException {

        String result = "";
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            HttpPost httpPost = new HttpPost(token);
            String encoding = DatatypeConverter.printBase64Binary("trusted-bbw-tally:bossnine".getBytes("UTF-8"));
            httpPost.setHeader("Authorization", "Basic " + encoding);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            ContentType strContent = ContentType.create("text/plain", Charset.forName("UTF-8"));

            builder.addTextBody("grant_type", "password");
            builder.addTextBody("password", "q1234567");
            builder.addTextBody("username", "admin");

            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);

            CloseableHttpResponse response = client.execute(httpPost);

            try {
                HttpEntity resEntity = response.getEntity();
                result = EntityUtils.toString(resEntity, "UTF-8");
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
        HashMap hashMap = JSON.parseObject(result, HashMap.class);
        //System.out.println(hashMap.get("access_token"));
        String token = (String)hashMap.get("access_token");
        return token;
    }

    public String request(Demo demo) {

        String tokenResponse = null;
        try {
            tokenResponse = response();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = "";
        HttpPost httpPost = new HttpPost(url);

        String responseContent = null;

        try {
            CloseableHttpClient client = HttpClients.createDefault();

            httpPost.addHeader("Content-Type", "application/json");

            httpPost.addHeader("Authorization", "Bearer " + tokenResponse);

//            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//
//            builder.addTextBody("password", demo.getPassword());
//            builder.addTextBody("phonenumber", demo.getPhonenumber());
//            //builder.addTextBody("realname", demo.getRealname());
//            builder.addTextBody("username", demo.getUsername());
            JSONObject param = new JSONObject();
            param.put("password", demo.getPassword());
            param.put("phonenumber", demo.getPhonenumber());
            param.put("username", demo.getUsername());
            System.out.println(param.toString());
            StringEntity se = new StringEntity(param.toString());

            httpPost.setEntity(se);

            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entityResp = response.getEntity();
            responseContent = EntityUtils.toString(entityResp, Consts.UTF_8);
            response.close();
            client.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseContent;

    }

}
