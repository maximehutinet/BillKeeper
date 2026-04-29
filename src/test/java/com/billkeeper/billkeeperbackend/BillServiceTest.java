package com.billkeeper.billkeeperbackend;

import com.billkeeper.billkeeperbackend.bill.BillService;
import com.billkeeper.billkeeperbackend.bill.api.model.UpdateBillReimbursementRequest;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.DocumentService;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.parsingjob.ParsingJobService;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.model.ParsingJob;
import com.billkeeper.billkeeperbackend.submission.InsuranceSubmissionService;
import com.billkeeper.billkeeperbackend.submission.persistence.model.InsuranceSubmission;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.parsing.BillParsingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillServiceTest {

    @Mock
    private BillRepository billRepository;
    @Mock
    private DocumentService documentService;
    @Mock
    private InsuranceSubmissionService insuranceSubmissionService;
    @Mock
    private AppConfig appConfig;
    @Mock
    private ParsingJobService parsingJobService;
    @Mock
    private BillParsingService billParsingService;

    @InjectMocks
    private BillService billService;

    @Test
    void createBill_shouldCreateEntityAndTriggerParsing() {
        User user = new User();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        ParsingJob parsingJob = new ParsingJob();

        when(appConfig.getDocumentsDirectory()).thenReturn("/tmp");
        when(parsingJobService.create(any(Bill.class))).thenReturn(parsingJob);

        billService.createBill(file, user);

        verify(billRepository).save(any(Bill.class));
        verify(documentService).create(anyString(), any(Bill.class), eq(user));
        verify(parsingJobService).create(any(Bill.class));
        verify(billParsingService).parseAndUpdateBill(any(Bill.class), any(File.class), eq(parsingJob));
    }

    @Test
    void createBill_shouldThrowInternalServerErrorException_whenTransferFails() throws IOException {
        User user = new User();
        MultipartFile file = Mockito.mock(MultipartFile.class);

        when(appConfig.getDocumentsDirectory()).thenReturn("/tmp");
        doThrow(new IOException("")).when(file).transferTo(any(Path.class));

        assertThrows(InternalServerErrorException.class, () -> billService.createBill(file, user));
        verify(billRepository, never()).save(any(Bill.class));
        verify(billParsingService, never()).parseAndUpdateBill(any(), any(), any());
    }

    @Test
    void getBillForUser_shouldThrownNotFoundException_whenNoBillFoundForUser() {
        UUID billId = UUID.randomUUID();
        User user = new User();
        when(billRepository.findById(billId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> billService.getBillForUser(billId, user));
    }

    @Test
    void updatePayment_shouldSetStatusToToFile_whenStatusWasToPay() {
        Bill bill = new Bill();
        bill.setStatus(Bill.Status.TO_PAY);
        OffsetDateTime paymentDateTime = OffsetDateTime.now();

        billService.updatePayment(bill, paymentDateTime);

        assertEquals(Bill.Status.TO_FILE, bill.getStatus());
        assertEquals(paymentDateTime, bill.getPaidDateTime());
        verify(billRepository).save(bill);
    }

    @Test
    void updatePayment_shouldNotChangeStatus_whenStatusWasToFile() {
        Bill bill = new Bill();
        bill.setStatus(Bill.Status.TO_FILE);
        OffsetDateTime paymentDateTime = OffsetDateTime.now();

        billService.updatePayment(bill, paymentDateTime);

        assertEquals(Bill.Status.TO_FILE, bill.getStatus());
        assertEquals(paymentDateTime, bill.getPaidDateTime());
        verify(billRepository).save(bill);
    }

    @Test
    void updateReimbursement_shouldSetStatusToReimbursementInProgress_whenStatusWasNotReimbursed() {
        Bill bill = new Bill();
        bill.setStatus(Bill.Status.TO_FILE);
        OffsetDateTime reimbursementDateTime = OffsetDateTime.now();
        double reimbursedAmount = 100.0;
        UpdateBillReimbursementRequest request = new UpdateBillReimbursementRequest(reimbursementDateTime, reimbursedAmount);
        billService.updateReimbursement(bill, request);

        assertEquals(Bill.Status.REIMBURSEMENT_IN_PROGRESS, bill.getStatus());
        verify(billRepository).save(bill);
    }

    @Test
    void delete_shouldSetStatusToFalse() {
        Bill bill = new Bill();
        bill.setId(UUID.randomUUID());
        bill.setActive(true);

        billService.delete(bill);

        assertEquals(false, bill.getActive());
        verify(documentService).deactivateForBill(bill.getId());
        verify(billRepository).save(bill);
    }

    @Test
    void updateStatus_shouldTriggerSubmissionStatusUpdate_whenBillHasSubmission() {
        Bill bill = new Bill();
        bill.setStatus(Bill.Status.TO_FILE);
        InsuranceSubmission submission = new InsuranceSubmission();
        bill.setSubmission(submission);

        billService.updateStatus(bill, Bill.Status.FILING_IN_PROGRESS);

        assertEquals(Bill.Status.FILING_IN_PROGRESS, bill.getStatus());
        verify(insuranceSubmissionService).updateStatus(submission);
        verify(billRepository).save(bill);
    }
}
