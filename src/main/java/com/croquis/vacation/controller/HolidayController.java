package com.croquis.vacation.controller;

import com.croquis.vacation.domain.Holiday;
import com.croquis.vacation.repository.HolidayRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log
public class HolidayController {

    @Autowired
    private HolidayRepository holidayRepository;

    @PostMapping("/holiday/add")
    public ResponseEntity<String> addHoliday(@RequestBody Holiday holiday){
        holidayRepository.save(holiday);
        return new ResponseEntity<>(holiday.getLocalDate() + ": added to system", HttpStatus.OK);
    }

    @GetMapping("/holiday/{year}")
    public ResponseEntity<List<Holiday>> getHolidayList(@PathVariable("year") int year){
        List<Holiday> result = holidayRepository.findAllByYear(year);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
