package be.usable.beusablechallenge.service;

import be.usable.beusablechallenge.dto.FreeRoomsDTO;
import be.usable.beusablechallenge.dto.RoomsUsageDTO;

public interface RoomsUsageService {

    RoomsUsageDTO calculateRoomsUsage(FreeRoomsDTO freeRoomsDTO);
}
