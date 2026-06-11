package com.mickey.dinggading.domain;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/time")
public class TimeController {
    private final TimeWrapper timeWrapper;

    // 현재 시간 설정하는 get API
    @PostMapping("/set")
    public ResponseEntity<?> setDemoTime(@RequestBody LocalDateTime setDay) {
        timeWrapper.setCurrentTime(setDay);
        return ResponseEntity.ok(timeWrapper.now());
    }

    // 현재 시간에 일수를 추가하는 API
    @PostMapping("/plus")
    public ResponseEntity<?> plusDays(@RequestBody Integer plusDays) {
        timeWrapper.addDays(plusDays);
        return ResponseEntity.ok(timeWrapper.now());
    }

    // 현재 시간에 일수를 추가하는 API
    @PostMapping("/reset")
    public ResponseEntity<?> reset() {
        timeWrapper.reset();
        return ResponseEntity.ok(timeWrapper.now());
    }

    @GetMapping("/now")
    public ResponseEntity<?> getPlans() {
        return ResponseEntity.ok(timeWrapper.now());
    }

}