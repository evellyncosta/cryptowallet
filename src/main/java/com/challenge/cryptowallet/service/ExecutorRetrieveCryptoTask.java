package com.challenge.cryptowallet.service;


import com.challenge.cryptowallet.client.Client;
import com.challenge.cryptowallet.model.Asset;
import com.challenge.cryptowallet.vo.AssetReaderVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.challenge.cryptowallet.util.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class ExecutorRetrieveCryptoTask {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    private final Client client;
    private ExecutorService executor;

    @Autowired
    public ExecutorRetrieveCryptoTask(Client client, ExecutorService executor) {
        this.client = client;
        this.executor = executor;
    }


    public List<Asset> retrieveCrypto(List<AssetReaderVO> wallet) {
        int batchSize = 3;
        int totalBatches = (wallet.size() + batchSize - 1) / batchSize;

        List<Asset> assets = new ArrayList<>();

        for (int i = 0; i < totalBatches; i++) {
            logger.info(String.format("Now is: %s", DateUtils.getCurrentDateTime()));

            int startIndex = i * batchSize;
            int endIndex = Math.min(startIndex + batchSize, wallet.size());
            List<AssetReaderVO> batch = wallet.subList(startIndex, endIndex);

            CountDownLatch countDownLatch = new CountDownLatch(batch.size());

            List<Future<Optional<Asset>>> futures = new ArrayList<>();

            for (AssetReaderVO assetReaderVO : batch) {
                Future<Optional<Asset>> future = executor.submit(() -> {
                    Optional<Asset> asset = client.getAssetBySymbol(assetReaderVO.symbol());
                    countDownLatch.countDown();
                    return asset;
                });
                futures.add(future);
            }

            try {
                countDownLatch.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            try {
                for (Future<Optional<Asset>> future : futures) {
                    Optional<Asset> asset = future.get();
                    asset.ifPresent(assets::add);
                }
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return assets;
    }


}
