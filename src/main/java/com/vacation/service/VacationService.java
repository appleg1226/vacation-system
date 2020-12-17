package com.vacation.service;

import com.vacation.domain.Holiday;
import com.vacation.domain.VacType;
import com.vacation.domain.Vacation;
import com.vacation.domain.YearlyVacation;
import com.vacation.repository.HolidayRepository;
import com.vacation.repository.VacationRepository;
import com.vacation.repository.YearlyVacationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Log
@RequiredArgsConstructor
public class VacationService {

    private final YearlyVacationRepository yearlyVacationRepository;
    private final VacationRepository vacationRepository;
    private final HolidayRepository holidayRepository;

    @Transactional
    public List<Vacation> getVacationList(String userId, int year){
        log.info("getting vacation list...");
        YearlyVacation result = yearlyVacationRepository.findByUserIdAndYear(userId, year);
        if(result == null){
            return new ArrayList<>();
        }
        return result.getVacationList();
    }

    @Transactional
    public float saveVacation(Vacation vacationRequest, String userId) {
        log.info("registering vacation...");

        if(!validateRequestDate(vacationRequest.getStartDate())){
            log.info("신청 날짜가 현재 시각 이전 입니다.");
            return -1;
        }

        if(vacationRequest.getType().equals(VacType.HALF) || vacationRequest.getType().equals(VacType.HALFHALF)){
            vacationRequest.setEndDate(vacationRequest.getStartDate());
        }

        YearlyVacation yearly = yearlyVacationRepository.findByUserIdAndYear(userId, vacationRequest.getStartDate().getYear());

        float left = yearly.getLeftVacation();
        float nowUse = getVacationDate(vacationRequest);

        if(nowUse == 0){
            return -2;
        }

        log.info("사용할 휴가의 기간은 " + nowUse + " 일 입니다.");

        if(left - nowUse < 0){
            log.info("신청 가능한 날짜를 초과하였습니다.");
            return -3;
        } else {
            if(!validateOverlapped(vacationRequest, userId)){
                return -4;
            }

            yearly.setLeftVacation(left - nowUse);
            vacationRequest.setYearlyVacation(yearly);
            yearly.getVacationList().add(vacationRequest);
            yearlyVacationRepository.save(yearly);

            return left - nowUse;
        }
    }

    private boolean validateOverlapped(Vacation vacationRequest, String userId) {
        List<Vacation> result = getVacationList(userId, vacationRequest.getStartDate().getYear());
        if(result.size() == 0){
            return true;
        }

        for (Vacation v: result) {
            if((vacationRequest.getStartDate().isBefore(v.getEndDate()) || vacationRequest.getStartDate().isEqual(v.getEndDate())) &&
                    (v.getStartDate().isBefore(vacationRequest.getEndDate()) || v.getStartDate().isEqual(vacationRequest.getEndDate()))){
                return false;
            }
        }

        return true;
    }

    @Transactional
    public float cancelVacation(long vacationId){
        Vacation vacationToDelete = vacationRepository.findById(vacationId).orElseThrow(NoSuchElementException::new);
        YearlyVacation yearly = vacationToDelete.getYearlyVacation();

        boolean isValid = validateRequestDate(vacationToDelete.getStartDate());

        if(!isValid){
            log.info("취소 날짜가 현재 시각 이전 입니다.");
            return -1;
        }

        float toAdd = getVacationDate(vacationToDelete);

        log.info("취소할 휴가의 기간은 " + toAdd + " 일 입니다.");

        yearly.setLeftVacation(yearly.getLeftVacation() + toAdd);
        yearlyVacationRepository.save(yearly);

        vacationRepository.deleteById(vacationToDelete.getId());

        return yearly.getLeftVacation();
    }

    private float getVacationDate(Vacation vacationRequest) {
        LocalDate start = vacationRequest.getStartDate();
        LocalDate end = vacationRequest.getEndDate();
        long businessDay = countBusinessDaysBetween(start, end);

        if(businessDay == 0){
            return 0;
        }
        if(vacationRequest.getType().equals(VacType.FULL)){
            return businessDay;
        } else if(vacationRequest.getType().equals(VacType.HALF)) {
            return 0.5f;
        } else {
            return 0.25f;
        }
    }

    private long countBusinessDaysBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> holidays = getHolidaysOf(startDate.getYear());

        Predicate<LocalDate> isHoliday = holidays::contains;
        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        long daysBetween = DAYS.between(startDate, endDate);

        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(daysBetween + 1)
                .filter(isHoliday.or(isWeekend).negate())
                .count();
    }

    private List<LocalDate> getHolidaysOf(int year){
        List<Holiday> holidays = holidayRepository.findAllByYear(year);
        return holidays.stream()
                .map(Holiday::getLocalDate)
                .collect(Collectors.toList());
    }

    private boolean validateRequestDate(LocalDate requestDate){
        return !requestDate.isBefore(LocalDate.now());
    }
}
