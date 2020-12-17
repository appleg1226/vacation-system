package com.croquis.vacation.controller;

import com.croquis.vacation.domain.Holiday;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Log
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HolidayControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private WebApplicationContext ctx;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("휴일 추가 요청")
    void addHoliday() throws Exception {
        LocalDate date1 = LocalDate.of(2020, 1, 1);
        Holiday holiday = Holiday.builder().year(2020).localDate(date1).build();
        String data = mapper.writeValueAsString(holiday);

        mockMvc.perform(post("/holiday/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(data))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("휴일을 연도로 조회")
    void getHolidayList() throws Exception {
        mockMvc.perform(get("/holiday/2020"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}