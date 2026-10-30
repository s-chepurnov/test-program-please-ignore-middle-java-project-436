package com.shop.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.shop.dto.CreateOrderRequest;
import com.shop.dto.ItemRequest;
import com.shop.dto.ShippingDetailsDto;
import com.shop.seed.Seeder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrderApiTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private Seeder seeder;

  @Autowired private JdbcTemplate jdbcTemplate;

  private MockHttpSession sessionUser1;
  private MockHttpSession sessionUser2;

  private int availableProductId;
  private int unavailableProductId;

  @BeforeEach
  void setUp() throws Exception {
    seeder.seed();
    availableProductId =
        jdbcTemplate.queryForObject(
            "select id from products where available = true limit 1", Integer.class);
    unavailableProductId =
        jdbcTemplate.queryForObject(
            "select id from products where available = false limit 1", Integer.class);

    sessionUser1 = registerAndGetSession("user1@example.com");
    sessionUser2 = registerAndGetSession("user2@example.com");
  }

  private MockHttpSession registerAndGetSession(String email) throws Exception {
    var request = new CredentialsRequest(email, "secret1");
    var json = objectMapper.writeValueAsString(request);
    var result =
        mockMvc
            .perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated())
            .andReturn();
    return (MockHttpSession) result.getRequest().getSession(false);
  }

  private MvcResult createOrderRequest(
      MockHttpSession session, CreateOrderRequest request, int expectedStatus) throws Exception {
    var json = objectMapper.writeValueAsString(request);
    return mockMvc
        .perform(
            post("/api/orders")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().is(expectedStatus))
        .andReturn();
  }

  private CreateOrderRequest deliveryOrder(int productId, int quantity, String address) {
    return new CreateOrderRequest(
        List.of(new ItemRequest(productId, quantity)),
        new ShippingDetailsDto("delivery", "Иван", "+79991234567", address));
  }

  private CreateOrderRequest pickupOrder(int productId, int quantity) {
    return new CreateOrderRequest(
        List.of(new ItemRequest(productId, quantity)),
        new ShippingDetailsDto("pickup", "Петр", "+79997654321", null));
  }

  private record CredentialsRequest(String email, String password) {}

  @Test
  void createOrder_asAnonymous_returns401() throws Exception {
    var request = pickupOrder(availableProductId, 1);
    var json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.type").value("/problems/unauthorized"));
  }

  @Test
  void createOrder_delivery_success() throws Exception {
    var request = deliveryOrder(availableProductId, 2, "ул. Ленина, 15");
    var result = createOrderRequest(sessionUser1, request, 201);

    var responseBody = result.getResponse().getContentAsString();
    var orderId = com.jayway.jsonpath.JsonPath.read(responseBody, "$.id");

    mockMvc
        .perform(get("/api/orders/" + orderId).session(sessionUser1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("paid"))
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[0].productId").value(String.valueOf(availableProductId)))
        .andExpect(jsonPath("$.items[0].quantity").value(2))
        .andExpect(jsonPath("$.shipping.method").value("delivery"))
        .andExpect(jsonPath("$.shipping.address").value("ул. Ленина, 15"))
        .andExpect(jsonPath("$.total.amount").isNumber());
  }

  @Test
  void createOrder_pickup_successWithoutAddress() throws Exception {
    var request = pickupOrder(availableProductId, 1);
    createOrderRequest(sessionUser1, request, 201);
  }

  @Test
  void createOrder_deliveryWithoutAddress_returns400() throws Exception {
    var request = deliveryOrder(availableProductId, 1, null);
    var json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/api/orders")
                .session(sessionUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.type").value("/problems/address-required"));
  }

  @Test
  void createOrder_emptyItems_returns400() throws Exception {
    var request =
        new CreateOrderRequest(
            List.of(), new ShippingDetailsDto("pickup", "Иван", "+79991234567", null));
    var json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/api/orders")
                .session(sessionUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.type").value("/problems/validation-error"));
  }

  @Test
  void createOrder_withUnavailableItems_returns400AndList() throws Exception {
    var request =
        new CreateOrderRequest(
            List.of(
                new ItemRequest(availableProductId, 1), new ItemRequest(unavailableProductId, 1)),
            new ShippingDetailsDto("pickup", "Иван", "+79991234567", null));
    var json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/api/orders")
                .session(sessionUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.type").value("/problems/unavailable-items"))
        .andExpect(jsonPath("$.unavailableProductIds", hasSize(1)))
        .andExpect(
            jsonPath("$.unavailableProductIds[0]").value(String.valueOf(unavailableProductId)));
  }

  @Test
  void orders_areIsolatedBetweenUsers() throws Exception {
    var request = pickupOrder(availableProductId, 1);
    createOrderRequest(sessionUser1, request, 201);
    createOrderRequest(sessionUser2, request, 201);

    mockMvc
        .perform(get("/api/orders").session(sessionUser1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));

    mockMvc
        .perform(get("/api/orders").session(sessionUser2))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void getOrder_byAnotherUser_returns404() throws Exception {
    var request = pickupOrder(availableProductId, 1);
    var result = createOrderRequest(sessionUser1, request, 201);

    var responseBody = result.getResponse().getContentAsString();
    var orderId = com.jayway.jsonpath.JsonPath.read(responseBody, "$.id");

    mockMvc
        .perform(get("/api/orders/" + orderId).session(sessionUser2))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.type").value("/problems/not-found"));
  }
}
