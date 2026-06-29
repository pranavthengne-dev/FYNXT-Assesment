package com.fynxt.orderbook.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fynxt.orderbook.domain.model.Trader;
import com.fynxt.orderbook.domain.model.enums.Sector;
import com.fynxt.orderbook.dto.AddHoldingRequest;
import com.fynxt.orderbook.repository.TraderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PortfolioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TraderRepository traderRepository;

    @Test
    void addsHoldingAndCalculatesOverlapThroughHttpEndpoints() throws Exception {
        String traderId = traderRepository.save(new Trader("T-PORT", "Portfolio Trader")).getId();
        AddHoldingRequest request = new AddHoldingRequest("AAPL", Sector.TECH, 10);

        mockMvc.perform(post("/portfolio/{traderId}/add", traderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.positions.AAPL").value(10))
                .andExpect(jsonPath("$.sectorBreakdown.TECH").value(10));

        mockMvc.perform(get("/portfolio/{traderId}/overlap", traderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dominantBasket").value("TECH_HEAVY"))
                .andExpect(jsonPath("$.riskFlag").value("LOW"))
                .andExpect(jsonPath("$.overlaps[0].overlap").value("33.33%"));
    }

    @Test
    void rejectsInvalidHoldingEnumValues() throws Exception {
        String request = """
                {"stock":"AAPL","sector":"UNKNOWN","quantity":10}
                """;

        mockMvc.perform(post("/portfolio/{traderId}/add", "T-BAD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request value. Check enum values and parameter types."));
    }

    @Test
    void rejectsStockSectorMismatchForHolding() throws Exception {
        String request = """
                {"stock":"JPM","sector":"TECH","quantity":100}
                """;

        mockMvc.perform(post("/portfolio/{traderId}/add", "T-BAD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("stock must match sector"));
    }

    @Test
    void rejectsLowercaseHoldingStockSymbol() throws Exception {
        String request = """
                {"stock":"jpm","sector":"FINANCE","quantity":100}
                """;

        mockMvc.perform(post("/portfolio/{traderId}/add", "T-BAD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("stock must be an uppercase stock symbol"));
    }

    @Test
    void rejectsInvalidHoldingQuantity() throws Exception {
        String request = """
                {"stock":"AAPL","sector":"TECH","quantity":0}
                """;

        mockMvc.perform(post("/portfolio/{traderId}/add", "T-BAD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("quantity must be greater than or equal to 1"));
    }
}
