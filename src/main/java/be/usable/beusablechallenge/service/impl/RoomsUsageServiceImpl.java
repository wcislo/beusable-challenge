package be.usable.beusablechallenge.service.impl;

import be.usable.beusablechallenge.dto.FreeRoomsDTO;
import be.usable.beusablechallenge.dto.UsageDTO;
import be.usable.beusablechallenge.service.RoomsUsageService;
import be.usable.beusablechallenge.dto.RoomsUsageDTO;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RoomsUsageServiceImpl implements RoomsUsageService {

    private static final List<BigDecimal> TEST_DATA = Stream.of(23, 45, 155, 374, 22, 99.99, 100, 101, 115, 209).map(Number::doubleValue).map(BigDecimal::valueOf).collect(Collectors.toList());

    private static final String CURRENCY = "EUR";

    private static final BigDecimal MIN_PREMIUM_GUEST_AMOUNT = new BigDecimal(100);

    @Override
    public RoomsUsageDTO calculateRoomsUsage(FreeRoomsDTO freeRoomsDTO) {
        Map<Boolean, Deque<BigDecimal>> partitionedMapSortedDescending = TEST_DATA.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.partitioningBy(num -> num.compareTo(MIN_PREMIUM_GUEST_AMOUNT) < 0,
                        Collectors.toCollection(ArrayDeque::new)));

        Deque<BigDecimal> lt100 = partitionedMapSortedDescending.get(true);
        Deque<BigDecimal> gte100 = partitionedMapSortedDescending.get(false);

        Integer premiumRoomsCount = freeRoomsDTO.getFreeRoomsMap().get("Premium");
        Integer economyRoomsCount = freeRoomsDTO.getFreeRoomsMap().get("Economy");

        int premiumRoomsBookedByPremiumGuests = premiumRoomsCount;
        if (gte100.size() < premiumRoomsCount) {
            premiumRoomsBookedByPremiumGuests = gte100.size();
        }

        BigDecimal totalCostPremium = BigDecimal.ZERO;
        for (int i = 0; i < premiumRoomsBookedByPremiumGuests; i++) {
            totalCostPremium = totalCostPremium.add(gte100.pop());
        }

        int premiumRoomsRemaining = premiumRoomsCount - premiumRoomsBookedByPremiumGuests;

        int premiumRoomsBookedByEconomyGuests = 0;
        if (premiumRoomsRemaining > 0 && lt100.size() > economyRoomsCount) {
            int excessiveEconomyGuests = lt100.size() - economyRoomsCount;
            premiumRoomsBookedByEconomyGuests = Math.min(excessiveEconomyGuests, premiumRoomsRemaining);
            for (int i = 0; i < premiumRoomsBookedByEconomyGuests; i++) {
                totalCostPremium = totalCostPremium.add(lt100.pop());
            }
        }

        int economyRoomsBookedByEconomyGuests = economyRoomsCount;
        if (lt100.size() < economyRoomsCount) {
            economyRoomsBookedByEconomyGuests = lt100.size();
        }

        BigDecimal totalCostEconomy = BigDecimal.ZERO;
        for (int i = 0; i < economyRoomsBookedByEconomyGuests; i++) {
            totalCostEconomy = totalCostEconomy.add(lt100.pop());
        }


        Map<String, UsageDTO> usageDTOMap = new HashMap<>();
        usageDTOMap.put("Economy", new UsageDTO(economyRoomsBookedByEconomyGuests, CURRENCY.concat(" ").concat(totalCostEconomy.stripTrailingZeros().toPlainString())));
        usageDTOMap.put("Premium", new UsageDTO(premiumRoomsBookedByEconomyGuests + premiumRoomsBookedByPremiumGuests, CURRENCY.concat(" ").concat(totalCostPremium.stripTrailingZeros().toPlainString())));

        return new RoomsUsageDTO(usageDTOMap);
    }
}
