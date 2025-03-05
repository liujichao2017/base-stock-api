package com.hm.stock.modules.utils;

import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.InternalException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpClient {
    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .cookieJar(CookieJar.NO_COOKIES)
            .callTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .sslSocketFactory(buildSslSocketFactory(), getX509TrustManager())
            .hostnameVerifier((hostname, session) -> true)
            .build();

    public static void main(String[] args) {
        System.out.println(sendGet("http://127.0.0.1:8080/admin/login"));
    }

    static{
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");
    }

    public static String sendGet(String url) {
        log.debug("sendGet - {}", url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("accept", "*/*")
                .addHeader("connection", "Keep-Alive")
                .addHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
                .get()
                .build();
        return sendHttp(request);
    }

    public static String sendGet(String url,String headerKey,String headerVal) {
        log.debug("sendGet - {}", url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("accept", "*/*")
                .addHeader("connection", "Keep-Alive")
                .addHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
                .addHeader(headerKey, headerVal)
                .get()
                .build();
        return sendHttp(request);
    }

    public static String sendPost(String url, Object param) {
        log.debug("sendPost - {}", url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .post(RequestBody.create(JsonUtil.toStr(param),MediaType.get("application/json;charset=UTF-8")))
                .build();
        return sendHttp(request);
    }


    public static String sendHttp(Request request) {
        Call call = client.newCall(request);
        try (Response execute = call.execute()) {
            if (execute.code() == HttpStatus.OK.value()) {
                return execute.body().string();
            }
            log.error("请求接口失败: \nreq:{},\n,res:{}", request, execute);
            return "";
        } catch (Exception e) {
            log.error("请求接口失败: ", e);
            return "";
        }
    }

    public static byte[] sendGetByByte(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        try (Response execute = call.execute()) {
            if (execute.code() == HttpStatus.OK.value()) {
                return IOUtils.toByteArray(execute.body().byteStream());
            }
            log.error("请求接口失败: \nreq:{},\n,res:{}", request, execute);
        } catch (Exception e) {
            throw new InternalException(CommonResultCode.INTERNAL_ERROR);
        }
        throw new InternalException(CommonResultCode.INTERNAL_ERROR);
    }

    private static SSLSocketFactory buildSslSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static TrustManager[] getTrustManager() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
    }

    public static X509TrustManager getX509TrustManager() {
        X509TrustManager trustManager = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            trustManager = (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return trustManager;
    }
}
