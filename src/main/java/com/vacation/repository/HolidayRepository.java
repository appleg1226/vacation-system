package com.vacation.repository;

import com.vacation.domain.Holiday;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HolidayRepository extends CrudRepository<Holiday, Long> {
    List<Holiday> findAllByYear(int year);
}
