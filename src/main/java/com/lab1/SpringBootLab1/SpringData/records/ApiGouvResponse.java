package com.lab1.SpringBootLab1.SpringData.records;

import java.util.List;

public record ApiGouvResponse(
        String version,
        String query,
        Integer limit,
        List<ApiGouvFeature> features
) {

}