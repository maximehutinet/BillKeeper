package com.billkeeper.billkeeperbackend.utils.parsing;

import com.billkeeper.billkeeperbackend.beneficiary.persistence.BeneficiaryRepository;
import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.ParsingJobRepository;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.model.ParsingJob;
import com.billkeeper.billkeeperbackend.utils.StringUtils;
import com.billkeeper.billkeeperbackend.utils.pdf.PDFParser;
import net.codecrete.qrbill.generator.QRCodeText;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BillParsingService {
    private final PDFParser pdfParser;
    private final BeneficiaryRepository beneficiaryRepository;
    private final BillRepository billRepository;
    private final ParsingJobRepository parsingJobRepository;
    private final Logger logger = LoggerFactory.getLogger(BillParsingService.class);


    public BillParsingService(PDFParser pdfParser, BeneficiaryRepository beneficiaryRepository, BillRepository billRepository, ParsingJobRepository parsingJobRepository) {
        this.pdfParser = pdfParser;
        this.beneficiaryRepository = beneficiaryRepository;
        this.billRepository = billRepository;
        this.parsingJobRepository = parsingJobRepository;
    }

    @Async
    public void parseAndUpdateBill(UUID billId, File file, UUID parsingJobId) {
        ParsingJob parsingJob = parsingJobRepository.findById(parsingJobId)
                .orElseThrow(() -> new NotFoundException("ParsingJob not found"));
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NotFoundException("Bill not found"));

        try (PDDocument document = Loader.loadPDF(file)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage firstPageImage = renderer.renderImageWithDPI(0, 200, ImageType.GRAY);
            String qrCodeContent = QRCodeDecoder.decode(firstPageImage);

            if (qrCodeContent.isEmpty()) {
                String text = pdfParser.extractTextFromImage(firstPageImage);
                updateBillValues(bill, text);
            } else {
                net.codecrete.qrbill.generator.Bill QRBill = QRCodeText.decode(qrCodeContent);
                updateBillFromQRBill(bill, QRBill);
            }

            parsingJob.setStatus(ParsingJob.Status.SUCCESS);
            parsingJobRepository.save(parsingJob);
        } catch (Exception e) {
            logger.error(e.getMessage());
            parsingJob.setStatus(ParsingJob.Status.FAILED);
            parsingJobRepository.save(parsingJob);
        }
    }

    private void updateBillFromQRBill(Bill bill, net.codecrete.qrbill.generator.Bill QRBill) {
        bill.setCurrency(Bill.Currency.valueOf(QRBill.getCurrency()));
        bill.setName(QRBill.getCreditor().getName());
        bill.setProvider(QRBill.getCreditor().getName());
        bill.setAmount(QRBill.getAmountAsDouble());
        getBeneficiaryFromQRBill(QRBill).ifPresent(bill::setBeneficiary);
        billRepository.save(bill);
    }

    private Optional<Beneficiary> getBeneficiaryFromQRBill(net.codecrete.qrbill.generator.Bill QRBill) {
        List<Beneficiary> registeredBeneficiaries = beneficiaryRepository.findAll();
        for (Beneficiary beneficiary : registeredBeneficiaries) {
            String debtorName = QRBill.getDebtor().getName() != null ? StringUtils.cleanStringForComparison(QRBill.getDebtor().getName()) : "";
            String beneficiaryName = StringUtils.cleanStringForComparison(beneficiary.getFirstname());

            if (debtorName.contains(beneficiaryName)) {
                return Optional.of(beneficiary);
            }
        }
        return Optional.empty();
    }

    private void updateBillValues(Bill bill, String text) {
        List<Beneficiary> registeredBeneficiaries = beneficiaryRepository.findAll();
        Bill.Currency currency = getBillCurrency(text);
        BillParser billParser = currency == Bill.Currency.CHF ? new CHFBillParser(text, registeredBeneficiaries) : new EuroBillParser(text, registeredBeneficiaries);
        bill.setCurrency(currency);
        billParser.getBillName().ifPresent(bill::setName);
        billParser.getBillAmount().ifPresent(bill::setAmount);
        billParser.getBillBeneficiary().ifPresent(bill::setBeneficiary);
        billRepository.save(bill);
    }

    private Bill.Currency getBillCurrency(String text) {
        if (text.contains("EURO") || text.contains("EUR") || text.contains("€")) {
            return Bill.Currency.EUR;
        }
        return Bill.Currency.CHF;
    }
}