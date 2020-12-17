package com.vacation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

class VacationApplicationTests {

	@Test
	void contextLoads() {
		LocalDate l1 = LocalDate.of(2020, 11, 26);
		LocalDate l2 = LocalDate.of(2020, 11, 27);

		System.out.println(l1.isBefore(l2));
	}

}
