package com.batuhan.bankingapi.integration;

import com.batuhan.bankingapi.entity.Role;
import com.batuhan.bankingapi.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoleAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void userShouldNotAccessUserManagementEndpoints() throws Exception {

        String token = jwtService.generateToken(
                "user@test.com",
                Role.USER
        );

        mockMvc.perform(
                        get("/api/users")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldAccessUserManagementEndpoints() throws Exception {

        String token = jwtService.generateToken(
                "admin@test.com",
                Role.ADMIN
        );

        mockMvc.perform(
                        get("/api/users")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }
}