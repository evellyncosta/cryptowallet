package com.challenge.cryptowallet.vo;

import com.challenge.cryptowallet.model.Asset;

import java.util.List;

public record AllAssetsVO(List<Asset> data, String timestamp) {
}
