package com.restaurant.order_service.menu.infra.controller;

import com.restaurant.order_service.menu.core.dto.output.MenuItemOutputDTO;
import com.restaurant.order_service.menu.core.usecase.ListMenuItemsUseCase;
import com.restaurant.order_service.security.JwtTokenValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class MenuItemControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private ListMenuItemsUseCase listMenuItems;
    @MockBean private JwtTokenValidator jwtTokenValidator;

    @Test
    @DisplayName("GET /menu-items returns the active catalog")
    void listMenu() throws Exception {
        when(listMenuItems.execute()).thenReturn(List.of(
                new MenuItemOutputDTO(UUID.randomUUID(), "X-Burger", "tasty", new BigDecimal("25.00"), "BURGER", true),
                new MenuItemOutputDTO(UUID.randomUUID(), "Coca-Cola", "350ml", new BigDecimal("7.00"), "DRINK", true)
        ));

        mvc.perform(get("/menu-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("X-Burger"))
                .andExpect(jsonPath("$[0].price").value(25.00))
                .andExpect(jsonPath("$[1].name").value("Coca-Cola"));
    }

    @Test
    @DisplayName("GET /menu-items returns empty array when catalog is empty")
    void emptyCatalog() throws Exception {
        when(listMenuItems.execute()).thenReturn(List.of());

        mvc.perform(get("/menu-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
