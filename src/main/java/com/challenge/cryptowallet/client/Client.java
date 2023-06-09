package com.challenge.cryptowallet.client;

import com.challenge.cryptowallet.model.Asset;
import com.challenge.cryptowallet.vo.AllAssetsVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.challenge.cryptowallet.util.DateUtils;

import java.util.Optional;

@Component
public class Client {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    private final String BASE_URL = "https://api.coincap.io/v2/assets/";

    private RestTemplate restTemplate;

    @Autowired
    public Client(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<Asset> getAssetBySymbol(String symbol){
        logger.info(String.format("Submitted request %s at %s",symbol,DateUtils.getCurrentDateTime()));

        String url = BASE_URL+"?search="+symbol;

        try{
            ResponseEntity<?> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().equals(HttpStatus.OK)){
                String assetJson = (String) response.getBody();

                ObjectMapper mapper = new ObjectMapper();
                try {
                    AllAssetsVO assets = mapper.readValue(assetJson, AllAssetsVO.class);
                    for (Asset asset : assets.data()){
                        if(asset.getSymbol().equals(symbol)){
                            logger.info(String.format("Returning asset: %s", asset));
                            return Optional.of(asset);
                        }
                    }
                    return Optional.empty();
                }catch (Exception e) {
                    logger.error("Error on asset serialization: %s Error: %s",symbol, e.getMessage());
                }
            }

        }catch (ResourceAccessException e) {
            logger.error("Timeout at asset: %s Error: %s",symbol, e.getMessage());
        }catch  (Exception e) {
            logger.error("Error accessing the api: %s Error: %s",symbol, e.getMessage());
        }
        return Optional.empty();
    }
}
