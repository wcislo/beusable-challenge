package be.usable.beusablechallenge.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RoomsUsageDTO {

    private Map<String, UsageDTO> roomUsageMap = new HashMap<>();

}
