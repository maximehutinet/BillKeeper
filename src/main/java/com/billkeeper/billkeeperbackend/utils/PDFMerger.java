package com.billkeeper.billkeeperbackend.utils;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class PDFMerger {

    public static File mergeFiles(List<File> files) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        File mergedFile = File.createTempFile(UUID.randomUUID().toString(), ".pdf");
        merger.setDestinationFileName(mergedFile.getPath());
        for (File file : files) {
            merger.addSource(file);
        }
        merger.mergeDocuments(null);
        return mergedFile;
    }
}
