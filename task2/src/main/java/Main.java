import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {

        String url = "https://api.nasa.gov/planetary/apod?api_key=";
        String myKey = "b9fcwCfJKI6F9cO0vI9ApH3Nb1mwVYyk0KcOT7bV";

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(url + myKey);
        String json = null;
        try (CloseableHttpResponse response = httpClient.execute(request);) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                json = EntityUtils.toString(entity);
            }
            ObjectMapper mapper = new ObjectMapper();
            NasaResponse nasaResponse = mapper.readValue(json, new TypeReference<>() {});
            HttpGet requestImage = new HttpGet(nasaResponse.getUrl());
            try (CloseableHttpResponse responseImage = httpClient.execute(requestImage);) {
                HttpEntity entityImage = responseImage.getEntity();
                if (entityImage != null) {
                    String fileName = Paths.get(nasaResponse.getUrl()).getFileName().toString();
                    File image = new File(fileName);
                    try (FileOutputStream fileOutputStream = new FileOutputStream(image)) {
                        entityImage.writeTo(fileOutputStream);
                        System.out.println("File saved as " + fileName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
