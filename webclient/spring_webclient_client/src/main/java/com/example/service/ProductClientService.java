package com.example.service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.exception.MyCustomExceptionServerIssue;
import com.example.exception.MyServiceException;
import com.example.exception.ProductNotFoundException;
import com.example.model.Product;
import com.example.model.ProductClientCreateRessponse;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class ProductClientService {

    private WebClient productWebClient;
    private Logger log = LoggerFactory.getLogger(ProductClientService.class);

    public ProductClientService(WebClient.Builder webclientBuilder) {
        productWebClient = webclientBuilder.baseUrl("http://localhost:8081")
                .filter((request, next) -> {
                    log.info("request method {}, url - {}", request.method(), request.url());
                    Mono<ClientResponse> response = next.exchange(request);
                    log.info("response - {}", response.block().statusCode());
                    return response;
                })
                .filter(((request, next) -> {
                    log.info("request1 method {}, url - {}", request.method(), request.url());
                    Mono<ClientResponse> response = next.exchange(request);
                    log.info("response1 - {}", response.block().statusCode());
                    return response;
                }))
                .build();
    }

    public ProductClientCreateRessponse createProduct(Product product) {
        if (product.getId() <= 0 || product.getName() == null || product.getName().isEmpty() || product.getPrice() <= 0) {
            throw new IllegalArgumentException("Invalid product data. Please provide valid product information.");
        }

        return productWebClient.post()
                .uri("/product")
                .body(BodyInserters.fromValue(product))
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()){
                        log.info("got success resonse..");
                        return clientResponse.bodyToMono(ProductClientCreateRessponse.class);
                    } else if (clientResponse.statusCode().is4xxClientError()) {
                        log.error("Client error while creating product {}", clientResponse.bodyToMono(String.class).block());
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MyServiceException("Client error: " + errorBody)));
                    } else {
                        log.error("Error occurred while creating product: {}", clientResponse.bodyToMono(String.class).block());
                        return Mono.error(new MyServiceException("Error occurred while creating product"));
                    }
                })
                .block();
    }

    public String updateProduct(Product product) {
        return productWebClient.put()
                .uri("/product")
                .body(BodyInserters.fromValue(product))
                .retrieve()
                .onRawStatus(status -> status ==403, resonse ->{
                    return Mono.error(new MyServiceException("custom message"));
                })
                .bodyToMono(String.class)
                .block();
    }

    public Product getProductById(int id) {
        return productWebClient.get()
                .uri("/product/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Internal server error occured", response);
                    return Mono.error(new MyCustomExceptionServerIssue("Internal server error.."));
                })
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("Product not found..", clientResponse);
                    return Mono.error(new ProductNotFoundException("Product not found.."));
                })
                .bodyToMono(Product.class)
                .log()
                .doOnSubscribe(subscription -> log.info("subscribed .."))
                .doOnSuccess(succ -> log.info("suceess.. {} ", succ))
                .doOnError(erro -> log.error("errored out ", erro))
//                .retry(3)
                .retryWhen(Retry.fixedDelay(10,Duration.ofMillis(1000))
                        .doBeforeRetry(x -> log.info("retrying.. -{}", x.totalRetries()))
                        .doAfterRetry(x -> log.info("after retry .. -{}", x.totalRetriesInARow()))
                )
                .block();
    }

    public List<Product> getProductsByName(String name) {
        String uri = UriComponentsBuilder.fromPath("/product/")
                .queryParam("name", name)
                .buildAndExpand()
                .toUriString();

        return productWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(Product.class)
                .log()
                .collectList()
                .block();
    }

    public String deleteProduct(int id) {
        return productWebClient.delete()
                .uri("/product/{id}", id)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(700))
                .onErrorResume(TimeoutException.class, error ->{
                    log.error("timed out..");
                    return Mono.error(new TimeoutException("timeout exception..."));
                })
                .block();
    }
}
