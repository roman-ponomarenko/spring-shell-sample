package gov.nist.beacon.clients;

import gov.nist.beacon.utils.WaitUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLException;
import java.io.InterruptedIOException;
import java.net.SocketException;

@Component
public class RestClient {

    @Value("${rest.client.timeout}")
    private int timeout;

    private RestTemplate template;

    private int retryTimeOut = 2_000;

    private int retryCount = 3;

    private RestClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();

        HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> {
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            if (executionCount >= retryCount || exception instanceof SSLException) {
                return false; // Do not retry if over max retry count
            } else if (exception instanceof NoHttpResponseException ||
                    exception instanceof InterruptedIOException ||
                    exception instanceof SocketException) {
                WaitUtils.delay(retryTimeOut);
                return true;
            }
            //if request is idempotent
            return !(request instanceof HttpEntityEnclosingRequest);
        };

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(retryHandler)
                .build();

        template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

        template.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            protected boolean hasError(HttpStatus statusCode) {
                return false;
            }
        });
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.add("X-Requested-With", "XMLHttpRequest");
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        return headers;
    }

    public <T> ResponseEntity<T> getForObject(String url, Class<T> responseType, Object... urlVariables) {
        HttpEntity requestEntity = new HttpEntity<>(getHeaders());
        return template.exchange(url, HttpMethod.GET, requestEntity, responseType, urlVariables);
    }
}
