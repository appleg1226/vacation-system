package com.vacation.controller;

import com.vacation.domain.Vacation;
import com.vacation.domain.YearlyVacation;
import com.vacation.repository.VacationRepository;
import com.vacation.repository.YearlyVacationRepository;
import com.vacation.service.VacationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@Log
@RequiredArgsConstructor
public class VacationController {

    private final VacationRepository vacationRepository;
    private final YearlyVacationRepository yearlyRepository;
    private final VacationService vacationService;

    @GetMapping("/vacation/day/{userId}/{year}")
    public ResponseEntity<Map<String, String>> getLeftVacation(@PathVariable("userId") String userId, @PathVariable("year") int year){
        YearlyVacation result = yearlyRepository.findByUserIdAndYear(userId, year);

        Map<String, String> response = new HashMap<>();
        if(result == null){
            response.put("message", "요청에 맞는 데이터가 없습니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("left_vacation", String.valueOf(result.getLeftVacation()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/vacation/list/{userId}/{year}")
    public ResponseEntity<List<Vacation>> getVacationList(@PathVariable("userId") String userId, @PathVariable("year") int year){
        List<Vacation> result = vacationService.getVacationList(userId, year);
        if(result.size() == 0){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/vacation/{vacationId}")
    public ResponseEntity<Vacation> getVacationInfo(@PathVariable("vacationId") long vacationId){
        Vacation result = vacationRepository.findById(vacationId).orElseThrow(NoSuchElementException::new);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/vacation/register/{userId}")
    public ResponseEntity<Map<String, String>> registerVacation(@RequestBody Vacation vacation, @PathVariable("userId") String userId){
        float result = vacationService.saveVacation(vacation, userId);
        Map<String, String> response = new HashMap<>();

        if(result == -1f){
            response.put("message", "지난 날짜의 휴가를 신청할 수 없습니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        } else if (result == -2f) {
            response.put("message", "신청 일수가 0일 입니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        } else if (result == -3f) {
            response.put("message", "연차가 남지 않았습니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        } else if (result == -4f) {
            response.put("message", "신청 날짜가 중복됩니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        } else {
            response.put("left_vacation", String.valueOf(result));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @DeleteMapping("/vacation/cancel/{vacationId}")
    public ResponseEntity<Map<String, String>> cancelVacation(@PathVariable("vacationId") long vacationId){
        float result = vacationService.cancelVacation(vacationId);
        Map<String, String> response = new HashMap<>();
        if(result == -1){
            response.put("message", "지난 날의 휴가를 취소할 수 없습니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        } else {
            response.put("left_vacation", String.valueOf(result));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Not Found")
    @ExceptionHandler(NoSuchElementException.class)
    public void conflict() { }

}
