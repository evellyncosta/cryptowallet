package com.challenge.cryptowallet;

import com.challenge.cryptowallet.client.Client;
import com.challenge.cryptowallet.model.Asset;
import com.challenge.cryptowallet.service.CalculatorService;
import com.challenge.cryptowallet.service.ExecutorRetrieveCryptoTask;
import com.challenge.cryptowallet.service.FileService;
import com.challenge.cryptowallet.vo.AssetReaderVO;
import com.challenge.cryptowallet.vo.WalletPerformanceVO;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
public class Initializer {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    private FileService fileService;
    private Client client;
    private CalculatorService calculatorService;
    private ExecutorService executorService;

    @Autowired
    public Initializer(FileService fileService, Client client, CalculatorService calculatorService, ExecutorService executorService) {
        this.fileService = fileService;
        this.client = client;
        this.calculatorService = calculatorService;
        this.executorService = executorService;
    }

    @PostConstruct
    public void init() {
        List<AssetReaderVO> wallet = fileService.getAssetsFromCSV();

        ExecutorRetrieveCryptoTask executorRetrieveCryptoTask = new ExecutorRetrieveCryptoTask(client, executorService);
        List<Asset> assets = executorRetrieveCryptoTask.retrieveCrypto(wallet);

        WalletPerformanceVO walletPerformanceVO = calculatorService.calculateWalletPerformance(assets, wallet);

        logger.info(walletPerformanceVO.toString());
    }
}
