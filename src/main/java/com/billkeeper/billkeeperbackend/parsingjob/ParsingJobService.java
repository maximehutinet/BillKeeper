package com.billkeeper.billkeeperbackend.parsingjob;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.ParsingJobRepository;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.model.ParsingJob;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Transactional
@Service
public class ParsingJobService {

    private final ParsingJobRepository parsingJobRepository;

    public ParsingJobService(ParsingJobRepository parsingJobRepository) {
        this.parsingJobRepository = parsingJobRepository;
    }

    public ParsingJob create(Bill bill) {
        ParsingJob parsingJob = new ParsingJob();
        parsingJob.setDateTime(OffsetDateTime.now());
        parsingJob.setStatus(ParsingJob.Status.IN_PROGRESS);
        parsingJob.setBill(bill);
        parsingJobRepository.save(parsingJob);
        return parsingJob;
    }
}