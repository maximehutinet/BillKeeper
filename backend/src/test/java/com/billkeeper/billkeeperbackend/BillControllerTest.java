package com.billkeeper.billkeeperbackend;

import com.billkeeper.billkeeperbackend.bill.BillService;
import com.billkeeper.billkeeperbackend.bill.api.BillController;
import com.billkeeper.billkeeperbackend.bill.api.model.BillResponse;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.DocumentService;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.security.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillController.class)
class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BillService billService;

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private Authentication authentication;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setFirstname("Max");
        mockUser.setEmail("max@max.com");
        when(authentication.getCurrentUserFromToken(any())).thenReturn(mockUser);
    }

    @Test
    void findAllBills_shouldReturnBillList() throws Exception {
        Bill bill = new Bill();
        bill.setId(UUID.randomUUID());
        bill.setUser(mockUser);
        when(billService.findAllBills(mockUser)).thenReturn(List.of(new BillResponse(bill)));

        mockMvc.perform(get("/bills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findBillById_shouldReturnBill() throws Exception {
        UUID billId = UUID.randomUUID();
        Bill bill = new Bill();
        bill.setId(billId);
        bill.setUser(mockUser);
        when(billService.findBillById(eq(billId), eq(mockUser)))
                .thenReturn(new BillResponse(bill));

        mockMvc.perform(get("/bills/{id}", billId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(billId.toString()));
    }
}
