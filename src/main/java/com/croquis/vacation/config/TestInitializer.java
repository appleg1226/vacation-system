package com.croquis.vacation.config;

import com.croquis.vacation.domain.*;
import com.croquis.vacation.repository.HolidayRepository;
import com.croquis.vacation.repository.UserRepository;
import com.croquis.vacation.repository.VacationRepository;
import com.croquis.vacation.repository.YearlyVacationRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;

@Configuration
@Profile("test")
@Log
public class TestInitializer implements ApplicationRunner {
    @Autowired HolidayRepository holidayRepository;
    @Autowired UserRepository userRepository;
    @Autowired YearlyVacationRepository yearlyVacationRepository;
    @Autowired VacationRepository vacationRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args){

        // 1. 연휴 추가
        Holiday holiday1 = Holiday.builder().year(2020).localDate(LocalDate.of(2020, 1, 24)).content("설연휴").build();
        Holiday holiday2 = Holiday.builder().year(2020).localDate(LocalDate.of(2020, 1, 27)).content("설연휴").build();
        Holiday holiday3 = Holiday.builder().year(2020).localDate(LocalDate.of(2020, 4, 15)).content("선거").build();
        Holiday holiday4 = Holiday.builder().year(2020).localDate(LocalDate.of(2020, 4, 30)).content("석가탄신일").build();
        Holiday holiday5 = Holiday.builder().year(2020).localDate(LocalDate.of(2020, 5, 5)).content("어린이날").build();
        Holiday holiday6 = Holiday.builder().year(2020).localDate(LocalDate.of(2020, 9, 30)).content("추석연휴").build();
        Holiday holiday7 = Holiday.builder().year(2020).localDate(LocalDate.of(2020, 10, 1)).content("추석연휴").build();
        Holiday holiday8 = Holiday.builder().year(2020).localDate(LocalDate.of(2020, 10, 2)).content("추석연휴").build();
        Holiday holiday9 = Holiday.builder().year(2020).localDate(LocalDate.of(2020, 10, 9)).content("한글날").build();
        Holiday holiday10 = Holiday.builder().year(2020).localDate(LocalDate.of(2020, 12, 25)).content("성탄절").build();

        holidayRepository.saveAll(Arrays.asList(holiday1, holiday2, holiday3, holiday4, holiday5, holiday6,
                holiday7, holiday8, holiday9, holiday10));

        log.info("Holiday Information Initialized!");

        // 2. 유저 데이터 추가
        String encoded = passwordEncoder.encode("1q2w3e4r");
        UserInfo userInfo1 = new UserInfo("chong", encoded);
        userRepository.save(userInfo1);
        UserInfo userInfo2 = new UserInfo("chong2", encoded);
        userRepository.save(userInfo2);

        // 3. 유저의 년별 휴가 테이블 추가
        YearlyVacation yearlyVacation1 = YearlyVacation.builder().year(2020).leftVacation(8).user(userInfo1).build();
        YearlyVacation yearlyVacation2 = YearlyVacation.builder().year(2019).leftVacation(15).user(userInfo1).build();
        YearlyVacation yearlyVacation3 = YearlyVacation.builder().year(2021).leftVacation(15).user(userInfo1).build();
        YearlyVacation yearlyVacation4 = YearlyVacation.builder().year(2020).leftVacation(1).user(userInfo2).build();
        yearlyVacationRepository.saveAll(Arrays.asList(yearlyVacation1, yearlyVacation2, yearlyVacation3, yearlyVacation4));

        // 4. 휴가 데이터 추가
        Vacation vacation1 = Vacation.builder().type(VacType.FULL)
                .startDate(LocalDate.of(2020, 1, 3)).endDate(LocalDate.of(2020, 1, 5))
                .comment("쉬고싶어요!").yearlyVacation(yearlyVacation1)
                .build();

        Vacation vacation2 = Vacation.builder().type(VacType.FULL)
                .startDate(LocalDate.of(2020, 7, 10)).endDate(LocalDate.of(2020, 7, 13))
                .comment("쉬고싶어요!").yearlyVacation(yearlyVacation1)
                .build();

        Vacation vacation3 = Vacation.builder().type(VacType.FULL)
                .startDate(LocalDate.of(2020, 12, 24)).endDate(LocalDate.of(2020, 12, 29))
                .comment("연말휴가").yearlyVacation(yearlyVacation1)
                .build();

        vacationRepository.saveAll(Arrays.asList(vacation1, vacation2, vacation3));

        log.info("User Information Initialized!");
    }
}