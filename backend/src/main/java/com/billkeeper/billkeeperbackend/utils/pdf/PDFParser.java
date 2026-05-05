package com.billkeeper.billkeeperbackend.utils.pdf;

import com.billkeeper.billkeeperbackend.AppConfig;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class PDFParser {

    private final AppConfig appConfig;
    private final ITesseract tesseract;
    private final int TESSERACT_PAGE_SEG_MODE = 6;
    private final int TESSERACT_OCR_ENGINE_MODE = 1;
    private final String TESSERACT_LANGUAGE = "fra";
    private final Logger logger = LoggerFactory.getLogger(PDFParser.class);

    public PDFParser(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath(this.appConfig.getTesseractDataDirectory());
        this.tesseract.setLanguage(TESSERACT_LANGUAGE);
        this.tesseract.setPageSegMode(TESSERACT_PAGE_SEG_MODE);
        this.tesseract.setOcrEngineMode(TESSERACT_OCR_ENGINE_MODE);
    }

    public String extractTextFromImage(BufferedImage image) {
        try {
            return tesseract.doOCR(image);
        } catch (Throwable e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
