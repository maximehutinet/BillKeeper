package com.billkeeper.billkeeperbackend.utils;

import com.billkeeper.billkeeperbackend.AppConfig;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class PDFParser {

    private final AppConfig appConfig;

    public PDFParser(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public String extractTextFromFile(File file) throws IOException {
        try {
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath(appConfig.getTesseractDataDirectory());
            tesseract.setLanguage("fra");
            StringBuilder text = new StringBuilder();
            File tempImageFile = createTempImageFileFromFile(file);
            String result = tesseract.doOCR(tempImageFile);
            text.append(result);
            tempImageFile.delete();
            return text.toString();
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }

    public static File createTempImageFileFromFile(File file) throws IOException {
        PDDocument document = Loader.loadPDF(file);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300, ImageType.GRAY);
        File tempImageFile = File.createTempFile(UUID.randomUUID().toString(), ".png");
        ImageIO.write(bufferedImage, "png", tempImageFile);
        return tempImageFile;
    }
}
