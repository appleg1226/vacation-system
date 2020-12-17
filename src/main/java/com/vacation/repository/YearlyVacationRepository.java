package com.vacation.repository;

import com.vacation.domain.YearlyVacation;
import org.springframework.data.repository.CrudRepository;

public interface YearlyVacationRepository extends CrudRepository<YearlyVacation, Long> {
    YearlyVacation findByUserIdAndYear(String userId, int year);
}
