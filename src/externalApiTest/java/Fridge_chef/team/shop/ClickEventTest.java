package Fridge_chef.team.shop;

import Fridge_chef.team.FridgeChefApplicationApiTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ClickEventTest extends FridgeChefApplicationApiTest {
    @Autowired
    private RestClient config;
    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    @DisplayName("쿠팡 링크 없을시 네이버 쇼핑으로 ")
    void clickNaverOrCoupang() {

    }

    @Test
    void clickNaver() {
        String search = "소금";
        String link = getNaverSearchLink("/v1/search/shop?query=" + search + "&display=1&start=1&sort=sim");
        System.out.println(link);
    }

    @Test
    void clickCoupang() throws IOException {
        getCouPangLinkSimple();
    }

    public String getCouPangLinkSimple() throws IOException {
        Path coupangSearchPath = Paths.get("src/externalApiTest/resources/coupangSearchSample.json");
        JsonNode coupangSearchJson = objectMapper.readTree(Files.newInputStream(coupangSearchPath));

        JsonNode productData = coupangSearchJson.path("data").path("productData");

        if (productData.isArray() && productData.size() > 0) {
            String productUrl = productData.get(0).path("productUrl").asText(null);
            System.out.println("Product URL: " + productUrl);
        } else {
            System.out.println("No product data found.");
            return null;
        }

        Path coupangDeepLinkPath = Paths.get("src/externalApiTest/resources/coupangDeepLinkSampleRequest.json");
        JsonNode coupangDeepLinkJson = objectMapper.readTree(Files.newInputStream(coupangDeepLinkPath));

        JsonNode deepLinkData = coupangDeepLinkJson.path("data");
        if (deepLinkData.isArray() && deepLinkData.size() > 0) {
            String originalUrl = deepLinkData.get(0).path("originalUrl").asText(null);
            System.out.println("Original URL: " + originalUrl);
        } else {
            System.out.println("No deep link data found.");
            return null;
        }

        return "Product URL: " + productData.get(0).path("productUrl").asText(null) +
                "\nOriginal URL: " + deepLinkData.get(0).path("originalUrl").asText(null);
    }

    public String getNaverSearchLink(String path) {
        return config.get()
                .uri(path)
                .retrieve()
                .body(NaverSearchResponse.class)
                .firstItemLink();
    }
}
