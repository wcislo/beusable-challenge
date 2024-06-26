package be.usable.beusablechallenge.controller;

import be.usable.beusablechallenge.dto.FreeRoomsDTO;
import be.usable.beusablechallenge.dto.RoomsUsageDTO;
import be.usable.beusablechallenge.service.RoomsUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("room-usage")
public class RoomsUsageController {

    private final RoomsUsageService roomsUsageService;

    @Autowired
    public RoomsUsageController(RoomsUsageService roomsUsageService) {
        this.roomsUsageService = roomsUsageService;
    }

    @GetMapping("calculate-usage")
    public ResponseEntity<RoomsUsageDTO> calculateUsage(@RequestBody FreeRoomsDTO freeRoomsDTO) {
        RoomsUsageDTO roomsUsageDTO = roomsUsageService.calculateRoomsUsage(freeRoomsDTO);
        return new ResponseEntity<>(roomsUsageDTO, HttpStatus.OK);
    }
}
