package com.lets.go.right.now.domain.trip.controller;

import com.lets.go.right.now.domain.trip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/trip")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;
}
