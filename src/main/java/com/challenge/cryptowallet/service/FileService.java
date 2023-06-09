package com.challenge.cryptowallet.service;

import com.challenge.cryptowallet.client.Client;
import com.challenge.cryptowallet.vo.AssetReaderVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    private static final String FILE_PATH = "/file.csv";
    public List<AssetReaderVO> getAssetsFromCSV() {
        List<AssetReaderVO> assets = new ArrayList<>();
        String line = "";
        String csvSplitBy = ",";

        Resource resource = new ClassPathResource(FILE_PATH);
        if (!resource.exists()) {
            logger.error(String.format("Arquivo não encontrado em %s", FILE_PATH));
            return assets;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] assetData = line.split(csvSplitBy);
                AssetReaderVO asset = new AssetReaderVO(assetData[0], assetData[1], assetData[2]);
                assets.add(asset);
            }
            return assets;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return assets;
    }


}
