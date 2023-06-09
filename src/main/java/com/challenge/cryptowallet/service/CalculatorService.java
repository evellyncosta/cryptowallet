package com.challenge.cryptowallet.service;

import com.challenge.cryptowallet.model.Asset;
import com.challenge.cryptowallet.vo.AssetReaderVO;
import com.challenge.cryptowallet.vo.WalletPerformanceVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class CalculatorService {
    final int SCALE_API = 30;
    final int SCALE_CALCULATOR = 2;

    private BigDecimal getPerformance(Asset asset, Optional<AssetReaderVO> assetAtWallet) {
        return asset.getPriceUsd().subtract(new BigDecimal(assetAtWallet.get().price())).setScale(SCALE_API, RoundingMode.HALF_EVEN);
    }

    private BigDecimal getPerformancePercentage(BigDecimal performance, BigDecimal assetPriceInWallet) {
        return performance.divide(assetPriceInWallet, SCALE_API, RoundingMode.HALF_EVEN)
                .multiply(new BigDecimal("100.00")).setScale(SCALE_API, RoundingMode.HALF_EVEN);
    }

    public WalletPerformanceVO calculateWalletPerformance(List<Asset> assetsFromAPI, List<AssetReaderVO> wallet){
        BigDecimal total = BigDecimal.ZERO;

        Asset bestAsset = assetsFromAPI.get(0);

        Asset worstAsset = assetsFromAPI.get(0);
        final String firstAssetSymbol = worstAsset.getSymbol();
        Optional<AssetReaderVO> worstAssetAtReader = wallet.stream().filter(assetReaderVO -> assetReaderVO.symbol().equals(firstAssetSymbol)).findFirst();

        BigDecimal worstPerformance = getPerformance(worstAsset, worstAssetAtReader);
        BigDecimal worstAssetPrice = new BigDecimal(worstAssetAtReader.get().price()).setScale(SCALE_API, RoundingMode.HALF_EVEN);
        BigDecimal worstAssetPercentage = getPerformancePercentage(worstPerformance, worstAssetPrice);

        BigDecimal bestPerformance = worstPerformance;
        BigDecimal bestPerformancePercentage = worstAssetPercentage;

        for (Asset asset : assetsFromAPI) {
            Optional<AssetReaderVO> assetAtWallet = wallet.stream().filter(assetReaderVO -> assetReaderVO.symbol().equals(asset.getSymbol())).findFirst();

            BigDecimal totalUsdAssetInWallet = asset.getPriceUsd().multiply(new BigDecimal(assetAtWallet.get().quantity())).setScale(SCALE_CALCULATOR, RoundingMode.HALF_EVEN);

            total = total.add(totalUsdAssetInWallet).setScale(SCALE_CALCULATOR, RoundingMode.HALF_EVEN);

            BigDecimal performance;
            BigDecimal performancePercentage = BigDecimal.ZERO;
            if(assetAtWallet.isPresent()){
                BigDecimal assetPriceInWallet = new BigDecimal(assetAtWallet.get().price()).setScale(SCALE_API, RoundingMode.HALF_EVEN);
                performance = getPerformance(asset, assetAtWallet);
                performancePercentage = getPerformancePercentage(performance, assetPriceInWallet);

                if(performance.compareTo(bestPerformance) > 0){
                    bestPerformance = performance;
                    bestAsset = asset;
                    bestPerformancePercentage = performancePercentage;
                }

                if(performance.compareTo(worstPerformance) < 0){
                    worstPerformance=performance;
                    worstAsset = asset;
                    worstAssetPercentage = performancePercentage;
                }
            }

        }

        return new WalletPerformanceVO(total.setScale(SCALE_CALCULATOR, RoundingMode.HALF_UP), bestAsset.getSymbol(), bestPerformancePercentage.setScale(SCALE_CALCULATOR, RoundingMode.HALF_UP), worstAsset.getSymbol(), worstAssetPercentage.setScale(SCALE_CALCULATOR, RoundingMode.HALF_UP));
    }


}
