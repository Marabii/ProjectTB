package com.lab1.SpringBootLab1.SpringData.Service;

import com.lab1.SpringBootLab1.SpringData.records.ApiGouvAdress;
import com.lab1.SpringBootLab1.SpringData.records.ApiGouvFeature;
import com.lab1.SpringBootLab1.SpringData.records.ApiGouvResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Service
public class AdressSearchService {
    private final RestTemplate restTemplate;

    public AdressSearchService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.rootUri("https://api-adresse.data.gouv.fr/").build();
    }

    public List<ApiGouvAdress> searchAdress(List<String> keys) {
        String params = String.join("+", keys);
        String uri = UriComponentsBuilder
                .fromHttpUrl("https://api-adresse.data.gouv.fr/search") // Use fromHttpUrl to build the full URL
                .queryParam("q", params)
                .queryParam("limit", 15)
                .build()
                .toUriString();

        return Objects.requireNonNull(restTemplate.getForObject(uri, ApiGouvResponse.class))
                .features()
                .stream()
                .map(ApiGouvFeature::address)
                .toList();
    }
}
