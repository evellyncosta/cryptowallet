package com.challenge.cryptowallet.vo;

import java.math.BigDecimal;

public record WalletPerformanceVO(BigDecimal total, String bestAsset, BigDecimal bestPerformancePercent, String worstAsset, BigDecimal worstAssetPerformancePercent) {
    @Override
    public String toString() {
        return String.format("total={USD %s},best_asset={%s},best_performance={%s%%},worst_asset={%s},worst_performance={%s%%}",
                total, bestAsset, bestPerformancePercent, worstAsset, worstAssetPerformancePercent);
    }
}
