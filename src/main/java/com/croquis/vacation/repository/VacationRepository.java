package com.croquis.vacation.repository;

import com.croquis.vacation.domain.Vacation;
import org.springframework.data.repository.CrudRepository;

public interface VacationRepository extends CrudRepository<Vacation, Long> {
}
