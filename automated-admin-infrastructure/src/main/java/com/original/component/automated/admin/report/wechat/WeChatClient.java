package com.original.component.automated.admin.report.wechat;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Slf4j
public class WeChatClient {

    public static void main(String[] args) {
        WeChatClient client = new WeChatClient("ww45a7430c7463efaf", "qjq_himXQqngYoJRn0V-lxbP7ozbOZLQ2QsqKR9_wBs");
        client.sendMessage("ALv", "hello world");
    }

    private final String corpid;
    private final String corpsecret;

    public WeChatClient(String corpid, String corpsecret) {
        this.corpid = corpid;
        this.corpsecret = corpsecret;
    }

    public WeChatResponse sendMessage(String toUser, String content) {
        WeChatRequest requestMessage = new WeChatRequest();
        requestMessage.setTouser(toUser);
        requestMessage.setMsgtype("text");
        requestMessage.setText(new WeChatRequest.WeChatText(content));
        requestMessage.setAgentid(1000002);
        requestMessage.setSafe(0);
        return postMessage(requestMessage);
    }

    public WeChatResponse postMessage(WeChatRequest requestMessage) {
        return postMessage(AccessTokenManager.getAccessToken(corpid, corpsecret), requestMessage);
    }

    public WeChatResponse postMessage(String accessToken, WeChatRequest requestMessage) {
        // 创建 HttpClient 实例
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建 POST 请求
            HttpPost postRequest = new HttpPost("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + accessToken);
            // 设置请求体
            StringEntity entity = new StringEntity(JSON.toJSONString(requestMessage));
            postRequest.setEntity(entity);
            // 设置请求头
            postRequest.setHeader("Content-Type", "application/json");
            // 发送请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                // 获取响应实体
                HttpEntity responseEntity = response.getEntity();
                // 如果响应体不为空，打印结果
                if (responseEntity != null) {
                    String result = EntityUtils.toString(responseEntity);
                    return JSON.parseObject(result, WeChatResponse.class);
                }
            }
        } catch (IOException e) {
            log.error("post message error", e);
        }
        return null;
    }

}
