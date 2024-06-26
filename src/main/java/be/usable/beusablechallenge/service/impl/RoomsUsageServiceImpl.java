package be.usable.beusablechallenge.service.impl;

import be.usable.beusablechallenge.dto.FreeRoomsDTO;
import be.usable.beusablechallenge.dto.UsageDTO;
import be.usable.beusablechallenge.enums.RoomType;
import be.usable.beusablechallenge.service.RoomsUsageService;
import be.usable.beusablechallenge.dto.RoomsUsageDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RoomsUsageServiceImpl implements RoomsUsageService {

    private static final List<BigDecimal> TEST_DATA = Stream.of(23, 45, 155, 374, 22, 99.99, 100, 101, 115, 209).map(Number::doubleValue).map(BigDecimal::valueOf).toList();

    private static final String CURRENCY = "EUR";

    private static final BigDecimal MIN_PREMIUM_GUEST_AMOUNT = new BigDecimal(100);

    @Override
    public RoomsUsageDTO calculateRoomsUsage(FreeRoomsDTO freeRoomsDTO) {

        //TODO validation

        // Split guests into economy and premium based on booking amount. Sort descending.
        Map<Boolean, Deque<BigDecimal>> partitionedMapSortedDescending = TEST_DATA.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.partitioningBy(num -> num.compareTo(MIN_PREMIUM_GUEST_AMOUNT) < 0,
                        Collectors.toCollection(ArrayDeque::new)));

        Deque<BigDecimal> economyGuests = partitionedMapSortedDescending.get(true);
        Deque<BigDecimal> premiumGuests = partitionedMapSortedDescending.get(false);

        // Get available rooms count
        Integer premiumRoomsCount = freeRoomsDTO.getFreeRoomsMap().get(RoomType.PREMIUM.getName());
        Integer economyRoomsCount = freeRoomsDTO.getFreeRoomsMap().get(RoomType.ECONOMY.getName());

        // Calculate premium rooms booked by premium guests and their total cost
        int premiumGuestsCount = premiumGuests.size();
        int premiumRoomsBookedByPremiumGuests = Math.min(premiumRoomsCount, premiumGuestsCount);
        BigDecimal totalCostPremium = calculateBookedRoomsCost(premiumRoomsBookedByPremiumGuests, premiumGuests);


        // Allocate remaining premium rooms to highest paying economy guests if applicable
        int premiumRoomsRemaining = premiumRoomsCount - premiumRoomsBookedByPremiumGuests;

        int premiumRoomsBookedByEconomyGuests = 0;
        int economyGuestsCount = economyGuests.size();
        if (premiumRoomsRemaining > 0 && economyGuestsCount > economyRoomsCount) {

            //Calculate premium rooms booked by economy guests and their total cost
            int excessiveEconomyGuests = economyGuestsCount - economyRoomsCount;
            premiumRoomsBookedByEconomyGuests = Math.min(excessiveEconomyGuests, premiumRoomsRemaining);
            totalCostPremium = totalCostPremium.add(calculateBookedRoomsCost(premiumRoomsBookedByEconomyGuests, economyGuests));
        }

        // Calculate economy rooms booked by economy guests and their total cost
        economyGuestsCount = economyGuests.size();
        int economyRoomsBookedByEconomyGuests = Math.min(economyRoomsCount, economyGuestsCount);
        BigDecimal totalCostEconomy = calculateBookedRoomsCost(economyRoomsBookedByEconomyGuests, economyGuests);

        // Map to return DTO
        return mapToRoomUsage(economyRoomsBookedByEconomyGuests, totalCostEconomy, premiumRoomsBookedByEconomyGuests + premiumRoomsBookedByPremiumGuests, totalCostPremium);
    }

    /*
        Iterate over Deque sorted descending to get `roomsBooked` highest paying guests. Use `pop()` to remove already processed guests
     */
    private BigDecimal calculateBookedRoomsCost(int roomsBooked, Deque<BigDecimal> guestsBooking) {
        BigDecimal totalCost = BigDecimal.ZERO;
        for (int i = 0; i < roomsBooked; i++) {
            totalCost = totalCost.add(guestsBooking.pop());
        }
        return totalCost;
    }

    private RoomsUsageDTO mapToRoomUsage(int bookedEconomyRoomsCount, BigDecimal totalEconomyRoomsCost, int bookedPremiumRoomsCount, BigDecimal totalPremiumRoomsCost) {
        Map<String, UsageDTO> usageDTOMap = new HashMap<>();
        usageDTOMap.put(RoomType.ECONOMY.getName(), new UsageDTO(bookedEconomyRoomsCount, String.format("%s %s", CURRENCY, totalEconomyRoomsCost.stripTrailingZeros().toPlainString())));
        usageDTOMap.put(RoomType.PREMIUM.getName(), new UsageDTO(bookedPremiumRoomsCount, String.format("%s %s", CURRENCY, totalPremiumRoomsCost.stripTrailingZeros().toPlainString())));

        return new RoomsUsageDTO(usageDTOMap);
    }

}
