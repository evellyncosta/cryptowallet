package com.challenge.cryptowallet.service;

import com.challenge.cryptowallet.model.Asset;
import com.challenge.cryptowallet.vo.AssetReaderVO;
import com.challenge.cryptowallet.vo.WalletPerformanceVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorServiceUnitTest {
    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorService();
    }


    private Asset createAsset(String symbol, String priceUsd) {
        Asset asset = new Asset();
        asset.setPriceUsd(new BigDecimal(priceUsd).setScale(30,RoundingMode.HALF_UP));
        asset.setSymbol(symbol);
        return asset;
    }


    @Test
    /**
     * Given three assets, with one asset with good perfomarnce, one asset with negative performance and one asset with neither negative nor positive performance, should return the correct values
     */
    void calculateWalletPerformance() {
        String bitcoinPrice= "60000.00087878";
        String ethereumPrice = "3000";
        String adaPrice = "0.017";

        List<Asset> assetsFromAPI = Stream.of(
                createAsset("BTC",bitcoinPrice),
                createAsset("ETH",ethereumPrice),
                createAsset("ADA",adaPrice)
        ).toList();

        String bitcointQuantity = "0.12345";
        String ethereumQuantity = "3";
        String adaQuantity = "50";

        List<AssetReaderVO> assetsFromWalelt = Stream.of(
                new AssetReaderVO("BTC", bitcointQuantity, "37870.5058"),
                new AssetReaderVO("ETH", ethereumQuantity, "3500.9774"),
                new AssetReaderVO("ADA",adaQuantity, "0.017")
        ).toList();

        BigDecimal totalBitcoin = new BigDecimal(bitcoinPrice).multiply(new BigDecimal(bitcointQuantity));
        BigDecimal totalEthereum = new BigDecimal(ethereumPrice).multiply(new BigDecimal(ethereumQuantity));
        BigDecimal totalAda = new BigDecimal(adaPrice).multiply(new BigDecimal(adaQuantity));
        BigDecimal totalWallet = totalBitcoin.add(totalEthereum).add(totalAda).setScale(2, RoundingMode.HALF_UP);

        WalletPerformanceVO result = calculatorService.calculateWalletPerformance(assetsFromAPI, assetsFromWalelt);

        assertEquals("BTC", result.bestAsset());
        assertEquals(new BigDecimal(58.43).setScale(2,RoundingMode.HALF_UP), result.bestPerformancePercent());

        assertEquals("ETH", result.worstAsset());
        assertEquals((new BigDecimal(-14.31).setScale(2,RoundingMode.HALF_UP)), result.worstAssetPerformancePercent());
        assertEquals(totalWallet, result.total());
    }

    @Test
    /**
     * Given three assets,both negative performance should return the correct values
     */
    void calculateWalletPerformanceWithBadPerformance() {
        String bitcoinPrice= "17870.5058";
        String ethereumPrice = "3000";
        String adaPrice = "0.016";

        List<Asset> assetsFromAPI = Stream.of(
                createAsset("BTC",bitcoinPrice),
                createAsset("ETH",ethereumPrice),
                createAsset("ADA",adaPrice)
        ).toList();

        String bitcointQuantity = "0.12345";
        String ethereumQuantity = "3";
        String adaQuantity = "50";

        List<AssetReaderVO> assetsFromWalelt = Stream.of(
                new AssetReaderVO("BTC", bitcointQuantity, "37870.5058"),
                new AssetReaderVO("ETH", ethereumQuantity, "3500.9774"),
                new AssetReaderVO("ADA",adaQuantity, "0.017")
        ).toList();

        BigDecimal totalBitcoin = new BigDecimal(bitcoinPrice).multiply(new BigDecimal(bitcointQuantity));
        BigDecimal totalEthereum = new BigDecimal(ethereumPrice).multiply(new BigDecimal(ethereumQuantity));
        BigDecimal totalAda = new BigDecimal(adaPrice).multiply(new BigDecimal(adaQuantity));
        BigDecimal totalWallet = totalBitcoin.add(totalEthereum).add(totalAda).setScale(2, RoundingMode.HALF_UP);

        WalletPerformanceVO result = calculatorService.calculateWalletPerformance(assetsFromAPI, assetsFromWalelt);

        assertEquals("ADA",result.bestAsset());
        assertEquals(new BigDecimal(-5.88).setScale(2,RoundingMode.HALF_UP), result.bestPerformancePercent());

        assertEquals("BTC", result.worstAsset());
        assertEquals((new BigDecimal(-52.81).setScale(2,RoundingMode.HALF_UP)), result.worstAssetPerformancePercent());

        assertEquals(totalWallet, result.total());
    }


}