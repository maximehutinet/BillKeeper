package com.billkeeper.billkeeperbackend.utils;

import com.billkeeper.billkeeperbackend.AppConfig;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PDFParser {

    private final AppConfig appConfig;
    private final Logger logger = LoggerFactory.getLogger(PDFParser.class);

    public PDFParser(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public String extractTextFromFile(File file) {
        try {
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath(appConfig.getTesseractDataDirectory());
            tesseract.setLanguage("fra");
            StringBuilder text = new StringBuilder();
            File tempImageFile = getPagesFromPDFAsTmpImages(file).getFirst();
            String result = tesseract.doOCR(tempImageFile);
            text.append(result);
            tempImageFile.delete();
            return text.toString();
        } catch (Throwable e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static List<File> getPagesFromPDFAsTmpImages(File file) throws IOException {
        List<File> tempImageFiles = new ArrayList<>();
        PDDocument document = Loader.loadPDF(file);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300, ImageType.GRAY);
            File tempImageFile = File.createTempFile(UUID.randomUUID().toString(), ".png");
            ImageIO.write(bufferedImage, "png", tempImageFile);
            tempImageFiles.add(tempImageFile);
        }
        return tempImageFiles;
    }
}
