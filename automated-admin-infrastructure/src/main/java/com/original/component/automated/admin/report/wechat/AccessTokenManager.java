package com.original.component.automated.admin.report.wechat;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.time.Instant;

@Slf4j
public class AccessTokenManager {

    // 内存中保存 access_token 和过期时间
    private static String accessToken;
    private static Instant expirationTime;

    /**
     * 获取 access_token。如果已过期，则重新获取并更新。
     *
     * @return 最新的 access_token
     */
    public static synchronized String getAccessToken(String corpid, String corpsecret) {
        if (isTokenExpired()) {
            refreshAccessToken(corpid, corpsecret);  // 过期，重新获取 access_token
        }
        return accessToken;
    }

    /**
     * 判断 access_token 是否过期。
     *
     * @return true 如果过期，false 否则
     */
    private static boolean isTokenExpired() {
        return accessToken == null || Instant.now().isAfter(expirationTime);
    }

    /**
     * 重新获取 access_token 并更新内存中的值和过期时间。
     * 注意：这里你需要根据你的实际 API 请求逻辑来实现获取 token 的功能。
     */
    private static void refreshAccessToken(String corpid, String corpsecret) {
        // 模拟请求 API 获取 access_token（可以使用 HttpClient 发送 POST/GET 请求）
        // 假设这里是重新获取的 access_token 和 expires_in 秒数
        String uri = String.format("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s", corpid, corpsecret);
        AccessToken token = requestAccessTokenFromApi(uri);  // 模拟请求 API
        if (token == null) {
            return;
        }
        int expiresIn = token.getExpires_in();  // 模拟 expires_in 为 7200 秒
        // 更新 access_token 和过期时间
        accessToken = token.getAccess_token();
        expirationTime = Instant.now().plusSeconds(expiresIn);
        log.info("Access token refreshed: {},expirationTime: {}", accessToken, expirationTime);
    }

    /**
     * 模拟请求 API 获取 access_token 的方法
     *
     * @param uri 请求地址
     * @return 返回新的 access_token
     */
    private static AccessToken requestAccessTokenFromApi(String uri) {
        // 这里需要根据你的实际需求通过 HttpClient 发送请求获取 access_token
        // 创建 HttpClient 实例
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建 GET 请求
            HttpGet request = new HttpGet(uri);
            // 发送请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                // 如果响应体不为空，打印结果
                if (entity != null) {
                    return JSON.parseObject(EntityUtils.toString(entity), AccessToken.class);
                }
            }
        } catch (IOException e) {
            log.error("request access_token error", e);
        }
        return null;
    }

}

