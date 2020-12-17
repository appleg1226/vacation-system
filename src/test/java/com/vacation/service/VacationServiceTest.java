package com.vacation.service;

import com.vacation.domain.UserInfo;
import com.vacation.domain.VacType;
import com.vacation.domain.Vacation;
import com.vacation.domain.YearlyVacation;
import com.vacation.repository.UserRepository;
import com.vacation.repository.YearlyVacationRepository;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log
@ActiveProfiles("test")
class VacationServiceTest {

    @Autowired VacationService vacationService;
    @Autowired
    UserRepository userRepository;
    @Autowired YearlyVacationRepository yearlyVacationRepository;

    private UserInfo testUser;

    @BeforeEach // 데이터 초기화
    void readyData(){
        testUser = userRepository.save(new UserInfo("testUser", "1111"));
    }

    @Test
    @DisplayName("휴가 목록 가져오기")
    @Transactional
    void getVacations(){
        YearlyVacation testYearlyVacation = YearlyVacation.builder()
                .user(testUser)
                .year(2020)
                .leftVacation(15)
                .vacationList(new ArrayList<>())
                .build();
        Vacation testVacation = Vacation.builder()
                .startDate(LocalDate.of(2020, 10, 5))
                .endDate(LocalDate.of(2020, 10, 6))
                .comment("가을휴가")
                .type(VacType.FULL)
                .yearlyVacation(testYearlyVacation)
                .build();
        testYearlyVacation.getVacationList().add(testVacation);
        yearlyVacationRepository.save(testYearlyVacation);

        List<Vacation> result = vacationService.getVacationList(testUser.getId(), 2020);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("휴가 등록 메서드 테스트")
    @Transactional
    @Rollback(value = false)
    void saveVacation() {
        YearlyVacation testYearlyVacation = YearlyVacation.builder().user(testUser).year(2020).leftVacation(15).vacationList(new ArrayList<>()).build();
        Vacation testVacation = Vacation.builder()
                .startDate(LocalDate.of(2020, 12, 24)).endDate(LocalDate.of(2020, 12, 29))
                .comment("연말휴가").type(VacType.FULL).yearlyVacation(testYearlyVacation).build();

        testYearlyVacation.getVacationList().add(testVacation);
        yearlyVacationRepository.save(testYearlyVacation);

        Vacation vacationRequest = Vacation.builder()
                .startDate(LocalDate.of(2020, 12, 24)).endDate(LocalDate.of(2020, 12, 29))
                .comment("연말휴가").type(VacType.FULL).yearlyVacation(testYearlyVacation).build();

        float saveResult = vacationService.saveVacation(vacationRequest, testUser.getId());
        YearlyVacation result = yearlyVacationRepository.findByUserIdAndYear(testUser.getId(), 2020);

        assertEquals(12, saveResult);
        assertEquals(2, result.getVacationList().size());
        assertEquals("연말휴가", result.getVacationList().get(1).getComment());
    }

    @Test
    @DisplayName("날짜가 이전인 경우")
    @Transactional
    void saveFailTest1(){
        YearlyVacation testYearlyVacation = YearlyVacation.builder().user(testUser).year(2020).leftVacation(15).vacationList(new ArrayList<>()).build();
        yearlyVacationRepository.save(testYearlyVacation);

        Vacation testVacation = Vacation.builder()
                .startDate(LocalDate.of(2020, 10, 5)).endDate(LocalDate.of(2020, 10, 6))
                .comment("").type(VacType.FULL).yearlyVacation(testYearlyVacation).build();

        float result = vacationService.saveVacation(testVacation, testUser.getId());

        assertEquals(-2, result);
    }

    @Test
    @DisplayName("휴가 사용일이 초과한 경우")
    @Transactional
    void saveFailTest2(){
        YearlyVacation testYearlyVacation = YearlyVacation.builder()
                .user(testUser)
                .year(2020)
                .leftVacation(2)
                .vacationList(new ArrayList<>())
                .build();
        yearlyVacationRepository.save(testYearlyVacation);

        Vacation testVacation = Vacation.builder()
                .startDate(LocalDate.of(2020, 12, 24))
                .endDate(LocalDate.of(2020, 12, 29))
                .comment("")
                .type(VacType.FULL)
                .yearlyVacation(testYearlyVacation)
                .build();

        float result = vacationService.saveVacation(testVacation, testUser.getId());

        assertEquals(-1, result);
    }

    @Test
    @DisplayName("휴가 사용일이 중복된 경우")
    @Transactional
    void saveFailTest3(){
        YearlyVacation testYearlyVacation = YearlyVacation.builder()
                .user(testUser)
                .year(2020)
                .leftVacation(15)
                .vacationList(new ArrayList<>())
                .build();
        yearlyVacationRepository.save(testYearlyVacation);

        Vacation testVacation = Vacation.builder()
                .startDate(LocalDate.of(2020, 12, 24))
                .endDate(LocalDate.of(2020, 12, 29))
                .comment("")
                .type(VacType.FULL)
                .yearlyVacation(testYearlyVacation)
                .build();

        float result = vacationService.saveVacation(testVacation, testUser.getId());
        assertEquals(12, result);

        float result2 = vacationService.saveVacation(testVacation, testUser.getId());
        assertEquals(-3, result2);
    }

    @Test
    @DisplayName("휴가 취소 테스트")
    @Transactional
    void cancelTest1(){
        YearlyVacation testYearlyVacation = YearlyVacation.builder()
                .user(testUser)
                .year(2020)
                .leftVacation(15)
                .vacationList(new ArrayList<>())
                .build();
        Vacation testVacation = Vacation.builder()
                .startDate(LocalDate.of(2020, 12, 24))
                .endDate(LocalDate.of(2020, 12, 29))
                .comment("연말휴가")
                .type(VacType.FULL)
                .yearlyVacation(testYearlyVacation)
                .build();
        testYearlyVacation.getVacationList().add(testVacation);
        yearlyVacationRepository.save(testYearlyVacation);

        YearlyVacation foundYearly = yearlyVacationRepository.findByUserIdAndYear(testUser.getId(), 2020);
        float cancelResult = vacationService.cancelVacation(foundYearly.getVacationList().get(0).getId());

        assertEquals(foundYearly.getLeftVacation(), cancelResult);
    }

    @Test
    @DisplayName("휴가취소 신청 날짜가 이전인 경우")
    @Transactional
    void cancelTest2(){
        YearlyVacation testYearlyVacation = YearlyVacation.builder()
                .user(testUser)
                .year(2020)
                .leftVacation(15)
                .vacationList(new ArrayList<>())
                .build();
        Vacation testVacation = Vacation.builder()
                .startDate(LocalDate.of(2020, 10, 5))
                .endDate(LocalDate.of(2020, 10, 6))
                .comment("가을휴가")
                .type(VacType.FULL)
                .yearlyVacation(testYearlyVacation)
                .build();
        testYearlyVacation.getVacationList().add(testVacation);
        yearlyVacationRepository.save(testYearlyVacation);

        YearlyVacation foundYearly = yearlyVacationRepository.findByUserIdAndYear(testUser.getId(), 2020);
        float cancelResult = vacationService.cancelVacation(foundYearly.getVacationList().get(0).getId());

        assertEquals(-1, cancelResult);
    }
}