package com.croquis.vacation.controller;

import com.croquis.vacation.domain.VacType;
import com.croquis.vacation.domain.Vacation;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VacationControllerTest {

    @Autowired
    private MockMvc mockMvc;
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
    @DisplayName("남은 휴가 일 수 가져오기 테스트")
    void getLeftVacation1() throws Exception {
        mockMvc.perform(get("/vacation/day/{userId}/{year}", "chong", "2020"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("입력을 잘못 전달했을 때 테스트")
    void getLeftVacation2() throws Exception {
        mockMvc.perform(get("/vacation/day/{userId}/{year}", "hello", "2020"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("신청한 휴가 내용 가져오기 테스트")
    void getVacationList() throws Exception {
        mockMvc.perform(get("/vacation/list/{userId}/{year}", "chong", "2020"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("신청한 휴가 내용이 없는 경우 테스트")
    void getVacationList2() throws Exception {
        mockMvc.perform(get("/vacation/list/{userId}/{year}", "chong2", "2020"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상세 휴가 정보 확인 테스트")
    void getVacationInfo() throws Exception {
        mockMvc.perform(get("/vacation/{vacationId}", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("상세 휴가 정보 확인 실패 테스트")
    void getVacationInfo2() throws Exception {
        mockMvc.perform(get("/vacation/{vacationId}", "100"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("휴가 등록 요청 성공 테스트")
    void registerVacation() throws Exception {
        Vacation vacation1 = Vacation.builder()
                .startDate(LocalDate.of(2020, 12, 24))
                .endDate(LocalDate.of(2020, 12, 29))
                .comment("연말휴가")
                .type(VacType.FULL)
                .build();

        String data = mapper.writeValueAsString(vacation1);

        mockMvc.perform(post("/vacation/register/{userId}", "chong")
                .contentType(MediaType.APPLICATION_JSON)
                .content(data))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("휴가 등록시 연차 초과로 실패 테스트")
    void registerVacation2() throws Exception {
        Vacation vacation1 = Vacation.builder()
                .startDate(LocalDate.of(2020, 12, 24))
                .endDate(LocalDate.of(2020, 12, 29))
                .comment("연말휴가")
                .type(VacType.FULL)
                .build();

        String data = mapper.writeValueAsString(vacation1);

        mockMvc.perform(post("/vacation/register/{userId}", "chong2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(data))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("휴가 등록시 지난 날짜 요청으로 실패 테스트")
    void registerVacation3() throws Exception {
        Vacation vacation1 = Vacation.builder()
                .startDate(LocalDate.of(2020, 10, 5))
                .endDate(LocalDate.of(2020, 10, 6))
                .comment("가을휴가")
                .type(VacType.FULL)
                .build();

        String data = mapper.writeValueAsString(vacation1);

        mockMvc.perform(post("/vacation/register/{userId}", "chong")
                .contentType(MediaType.APPLICATION_JSON)
                .content(data))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("휴가 취소 성공 테스트")
    void cancelVacation1() throws Exception {
        mockMvc.perform(delete("/vacation/cancel/{vacationId}", "3"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("휴가 취소시 지난 날짜 요청으로 실패 테스트")
    void cancelVacation2() throws Exception {
        mockMvc.perform(delete("/vacation/cancel/{vacationId}", "1"))
                .andExpect(status().isNotAcceptable());
    }
}