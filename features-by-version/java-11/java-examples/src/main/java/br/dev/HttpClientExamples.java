package br.dev;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class HttpClientExamples {
    // Synchronous GET request
    public static void simpleGet() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/get"))
                .timeout(Duration.ofSeconds(10))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Body: " + response.body());
    }

    // Asynchronous GET request
    public static void asyncGet() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/get"))
                .timeout(Duration.ofSeconds(10))
                .build();
        CompletableFuture<HttpResponse<String>> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        future.thenAccept(response -> {
            System.out.println("[Async] Status code: " + response.statusCode());
            System.out.println("[Async] Body: " + response.body());
        }).join();
    }

    // POST request with JSON body
    public static void postJson() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\"name\":\"Java 11\",\"feature\":\"HttpClient\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(json))
                .timeout(Duration.ofSeconds(10))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST Status code: " + response.statusCode());
        System.out.println("POST Body: " + response.body());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("\n--- Asynchronous GET ---");
        asyncGet();
        System.out.println("--- Synchronous GET ---");
        simpleGet();
        System.out.println("\n--- POST with JSON ---");
        postJson();
    }
}

