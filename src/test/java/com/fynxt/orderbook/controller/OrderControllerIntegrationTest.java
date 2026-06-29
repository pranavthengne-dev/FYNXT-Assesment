package com.fynxt.orderbook.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fynxt.orderbook.domain.model.Trader;
import com.fynxt.orderbook.domain.model.enums.OrderSide;
import com.fynxt.orderbook.domain.model.enums.Sector;
import com.fynxt.orderbook.dto.OrderRequest;
import com.fynxt.orderbook.repository.TraderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TraderRepository traderRepository;

    @Test
    void placesOrderThroughHttpEndpoint() throws Exception {
        String traderId = traderRepository.save(new Trader("T-HTTP", "HTTP Trader")).getId();
        OrderRequest request = new OrderRequest(traderId, "AAPL", Sector.TECH, 5, OrderSide.BUY);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.traderId").value(traderId))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void rejectsInvalidOrderEnumValues() throws Exception {
        String request = """
                {"traderId":"T-BAD","stock":"AAPL","sector":"UNKNOWN","quantity":5,"side":"BUY"}
                """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request value. Check enum values and parameter types."));
    }

    @Test
    void rejectsStockSectorMismatchForOrder() throws Exception {
        String request = """
                {"traderId":"T-BAD","stock":"JPM","sector":"TECH","quantity":5,"side":"BUY"}
                """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("stock must match sector"));
    }

    @Test
    void rejectsInvalidOrderQuantity() throws Exception {
        String request = """
                {"traderId":"T-BAD","stock":"AAPL","sector":"TECH","quantity":0,"side":"BUY"}
                """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("quantity must be greater than or equal to 1"));
    }
}
