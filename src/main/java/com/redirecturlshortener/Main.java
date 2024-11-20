package com.redirecturlshortener;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final S3Client s3Client = S3Client.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static String BUCKET_NAME = "shorturl-aws-bucket";

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        // Exemplo de input: { "rawPath": "/381934sjdn" }
        String pathParameters = input.get("rawPath").toString();
        // Remove a barra inicial ("/") do código encurtado da URL.
        // Exemplo: "/381934sjdn" -> "381934sjdn"
        String shortUrlCode = pathParameters.replace("/", "");
        String keyBucket = shortUrlCode + ".json";

        if (shortUrlCode.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: 'shortUrlCode' is required");
        }

        //Cria uma requisição para buscar o arquivo JSON associado ao código no s3
        GetObjectRequest request = GetObjectRequest
                .builder()
                .bucket(BUCKET_NAME)
                .key(keyBucket)
                .build();

        InputStream inputStream;
        try {
            //Chamada do s3 para obter o arquivo json correspondente ao código encurtado
            inputStream = s3Client.getObject(request);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching URL data from S3: " + e.getMessage(), e);
        }

        UrlData urlData;
        try {
            //Deserializa o json para u objeto urldata
            urlData = objectMapper.readValue(inputStream, UrlData.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing URL data: " + e.getMessage(), e);
        }

        //obtem o tempo atual
        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        Map<String, Object> response = new HashMap<>();
        //requisição 11152522 -> 25 de outubro
        // currenttime 52626262 -> data atual
        if (currentTimeInSeconds > urlData.getExpirationTime()) {
            //url expirou
            // Retorna um erro HTTP 410 se a URL estiver expirada.
            response.put("statusCode", 410);
            response.put("body", "This URL has expired");
        } else {
            //url válida
            // Retorna um redirecionamento HTTP 302 para a URL original.
            response.put("statusCode", 302);
            Map<String, String> headers = new HashMap<>();
            headers.put("Location", urlData.getOriginalURL());
            response.put("headers", headers);
        }
        return response;
    }
}