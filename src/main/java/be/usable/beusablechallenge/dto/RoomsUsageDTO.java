package be.usable.beusablechallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomsUsageDTO {

    private Map<String, UsageDTO> roomUsageMap = new HashMap<>();

}
