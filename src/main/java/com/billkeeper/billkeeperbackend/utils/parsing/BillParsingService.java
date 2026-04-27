package com.billkeeper.billkeeperbackend.utils.parsing;

import com.billkeeper.billkeeperbackend.beneficiary.persistence.BeneficiaryRepository;
import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.ParsingJobRepository;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.model.ParsingJob;
import com.billkeeper.billkeeperbackend.utils.StringUtils;
import com.billkeeper.billkeeperbackend.utils.pdf.PDFParser;
import net.codecrete.qrbill.generator.QRCodeText;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class BillParsingService {

    private final PDFParser pdfParser;
    private final BeneficiaryRepository beneficiaryRepository;
    private final BillRepository billRepository;
    private final ParsingJobRepository parsingJobRepository;

    public BillParsingService(PDFParser pdfParser, BeneficiaryRepository beneficiaryRepository, BillRepository billRepository, ParsingJobRepository parsingJobRepository) {
        this.pdfParser = pdfParser;
        this.beneficiaryRepository = beneficiaryRepository;
        this.billRepository = billRepository;
        this.parsingJobRepository = parsingJobRepository;
    }

    @Async
    public void parseAndUpdateBill(Bill bill, File file, ParsingJob parsingJob) {
        try {
            String qrCodeText = "";
            List<File> filePages = PDFParser.getPagesFromPDFAsTmpImages(file);

            for (File filePage : filePages) {
                qrCodeText = QRCodeDecoder.decode(filePage);
                if (!qrCodeText.isEmpty()) break;
            }

            if (!qrCodeText.isEmpty()) {
                net.codecrete.qrbill.generator.Bill QRBill = QRCodeText.decode(qrCodeText);
                updateBillFromQRBill(bill, QRBill);
            } else {
                String text = pdfParser.extractTextFromFile(file);
                updateBillValues(bill, text);
            }
            parsingJob.setStatus(ParsingJob.Status.SUCCESS);
            parsingJobRepository.save(parsingJob);
        } catch (Exception ignored) {
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