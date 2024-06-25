package be.usable.beusablechallenge.service;

import be.usable.beusablechallenge.dto.FreeRoomsDTO;
import be.usable.beusablechallenge.dto.RoomsUsageDTO;
import be.usable.beusablechallenge.service.impl.RoomsUsageServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class RoomsUsageServiceImplUnitTest {

    private RoomsUsageServiceImpl roomsUsageServiceImpl = new RoomsUsageServiceImpl();

    static class RoomUsageCalculationTestCase {
        public FreeRoomsDTO input;
        public RoomsUsageDTO output;
    }

    static Stream<Arguments> provideRoomUsageData() throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get("src/test/resources/testdata/room_usage_test.json")));
        List<RoomUsageCalculationTestCase> roomUsageCalculationTestCases = new ObjectMapper().readValue(jsonContent, new TypeReference<>()
        {});

        return roomUsageCalculationTestCases.stream().map(roomUsageCalculationTestCase -> Arguments.of(roomUsageCalculationTestCase.input, roomUsageCalculationTestCase.output));
    }


    @ParameterizedTest
    @MethodSource("provideRoomUsageData")
    public void givenFreeRoomsCountAndType_shouldReturnValidRoomUsageStatistics(FreeRoomsDTO inputDto, RoomsUsageDTO expectedDto) {
        RoomsUsageDTO actualDto = roomsUsageServiceImpl.calculateRoomsUsage(inputDto);
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }

}
