package com.challenge.cryptowallet.client;

import com.challenge.cryptowallet.model.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;



@ExtendWith(MockitoExtension.class)
public class ClientUnitTest {
    private RestTemplate restTemplate;

    private Client client;

    @BeforeEach
    public void init() {
        restTemplate = Mockito.mock(RestTemplate.class);
        client = new Client(restTemplate);
    }

    @Test
    /**
     * Given crypto symbol, should return one Asset non empty
     */
    void getAssetBySymbol() {
        Asset expectedAsset = new Asset();
        expectedAsset.setPriceUsd(new BigDecimal("0.3095447774720339").setScale(16, RoundingMode.HALF_EVEN));
        expectedAsset.setSymbol("ADA");
        expectedAsset.setId("cardano");
        expectedAsset.setRank(7);
        expectedAsset.setSupply("34904060251");
        expectedAsset.setMaxSupply(new BigDecimal(45000000000.1).setScale(1, RoundingMode.HALF_EVEN));
        expectedAsset.setName("Cardano");
        expectedAsset.setMarketCapUsd(new BigDecimal(10804369563.4).setScale(1, RoundingMode.HALF_EVEN));
        expectedAsset.setVolumeUsd24Hr(new BigDecimal(127412424.7).setScale(1, RoundingMode.HALF_EVEN));
        expectedAsset.setVwap24Hr(new BigDecimal(0.3).setScale(1, RoundingMode.HALF_EVEN));
        expectedAsset.setChangePercent24Hr(new BigDecimal(-4.9).setScale(1, RoundingMode.HALF_EVEN));
        expectedAsset.setExplorer("https://cardanoexplorer.com/");
        String jsonStr = """
                            {
                           "data": [
                             {
                               "id": "cardano",
                               "rank": "7",
                               "symbol": "ADA",
                               "name": "Cardano",
                               "supply": "34904060251",
                               "maxSupply": "45000000000.1",
                               "marketCapUsd": "10804369563.4",
                               "volumeUsd24Hr": "127412424.7",
                               "priceUsd": "0.3095447774720339",
                               "changePercent24Hr": "-4.9",
                               "vwap24Hr": "0.3",
                               "explorer": "https://cardanoexplorer.com/"
                             },
                             {
                               "id": "adappter-token",
                               "rank": "747",
                               "symbol": "ADP",
                               "name": "Adappter Token",
                               "supply": "0.0000000000000000",
                               "maxSupply": "10000000000.0000000000000000",
                               "marketCapUsd": "0.0000000000000000",
                               "volumeUsd24Hr": "346931.4690486381343240",
                               "priceUsd": "0.0031562427135754",
                               "changePercent24Hr": "-1.6851484574455029",
                               "vwap24Hr": "0.0031727988170948",
                               "explorer": "https://etherscan.io/token/0xc314b0e758d5ff74f63e307a86ebfe183c95767b"
                             }
                           ],
                           "timestamp": 1686329230648
                         }
                """;

        ResponseEntity<String> response = ResponseEntity.ok(jsonStr);
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(String.class)))
                .thenReturn(response);

        Optional<Asset> result = client.getAssetBySymbol("ADA");

        assertTrue(result.isPresent());
        assertThat(result.get()).isEqualToComparingFieldByField(expectedAsset);
    }

    @Test
    /**
     * Given some crypto symbol non-existing in api, should return an empty asset
     */
    void getAssetBySymbolWith() {
        ResponseEntity<String> response = ResponseEntity.notFound().build();
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(String.class)))
                .thenReturn(response);

        Optional<Asset> result = client.getAssetBySymbol("SOME");

        assertTrue(!result.isPresent());
    }

    @Test
    /**
     * Given some crypto symbol and the api, reach timeout, an exception should be thrown
     */
    void getAssetBySymbolWithTimeout() {
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(String.class)))
                        .thenThrow(ResourceAccessException.class);

        Optional<Asset> result = client.getAssetBySymbol("SOME");

        assertTrue(!result.isPresent());
    }
}