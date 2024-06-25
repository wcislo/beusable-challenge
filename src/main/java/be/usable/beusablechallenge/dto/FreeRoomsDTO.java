package be.usable.beusablechallenge.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FreeRoomsDTO {

    private Map<String, Integer> freeRoomsMap = new HashMap<>();

}
