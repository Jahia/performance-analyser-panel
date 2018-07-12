package org.jahia.modules.pageperformanceanalyser;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Provides a static OkHttpClient object</p>
 *
 * @author Alex Karmanov, Stefan Savu, Cédric Mailleux
 * @since 1.0.0
 * @version 1.0.0
 */
public class HttpClient {

    private static OkHttpClient client;

    private HttpClient() {}

    /**
     * <p>Returns static client</p>
     *
     * @return OkHttpClient
     */
    public static OkHttpClient getClient() {
        if (client == null) {
            synchronized (HttpClient.class) {
                if (client == null) {
                    client = getUnsafeOkHttpClient().build();
                }
            }
        }
        return client;
    }

    /**
     * <p>Returns unsafe ssl client builder. Resulting client will trust all certificates.</p>
     *
     * @return OkHttpClient.Builder
     */
    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Retruns url builder with set parameters</p>
     *
     * @param url Request url
     * @param params Parameter map
     * @return HttpUrl.Builder
     */
    public static HttpUrl.Builder buildUrl(String url, Map<String, String> params) {
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for(Map.Entry<String, String> param : params.entrySet()) {
                httpBuider.addQueryParameter(param.getKey(),param.getValue());
            }
        }
        return httpBuider;
    }
}