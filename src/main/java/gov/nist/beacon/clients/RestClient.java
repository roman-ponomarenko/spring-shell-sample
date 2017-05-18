package gov.nist.beacon.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClient {

    @Value("${rest.client.timeout}")
    private int timeout;

    private RestTemplate template;

    private RestClient() {
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        factory.setWriteTimeout(timeout);
        template = new RestTemplate(factory);

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
