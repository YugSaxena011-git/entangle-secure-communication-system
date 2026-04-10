package com.entangle.messaging.quantum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class QKDKeyExchangeService {

    private final Random random = new Random();

    public BB84SimulationResult simulateBB84(int numberOfBits, boolean eavesdropperPresent) {
        List<Integer> aliceBits = new ArrayList<>();
        List<String> aliceBases = new ArrayList<>();
        List<String> bobBases = new ArrayList<>();
        List<Integer> bobMeasurements = new ArrayList<>();
        List<Integer> sharedKey = new ArrayList<>();

        int matchedBasisCount = 0;
        int mismatchedKeyCount = 0;

        for (int i = 0; i < numberOfBits; i++) {
            int aliceBit = random.nextInt(2);
            String aliceBasis = randomBasis();
            String bobBasis = randomBasis();

            int transmittedBit = aliceBit;

            if (eavesdropperPresent) {
                String eveBasis = randomBasis();

                if (!eveBasis.equals(aliceBasis)) {
                    transmittedBit = random.nextInt(2);
                }
            }

            int bobBit;
            if (bobBasis.equals(aliceBasis)) {
                bobBit = transmittedBit;
                matchedBasisCount++;

                sharedKey.add(bobBit);

                if (bobBit != aliceBit) {
                    mismatchedKeyCount++;
                }
            } else {
                bobBit = random.nextInt(2);
            }

            aliceBits.add(aliceBit);
            aliceBases.add(aliceBasis);
            bobBases.add(bobBasis);
            bobMeasurements.add(bobBit);
        }

        double keyMatchRate;
        boolean eavesdroppingDetected;

        if (matchedBasisCount == 0) {
            keyMatchRate = 0.0;
            eavesdroppingDetected = false;
        } else {
            keyMatchRate = ((double) (matchedBasisCount - mismatchedKeyCount) / matchedBasisCount) * 100.0;
            eavesdroppingDetected = mismatchedKeyCount > 0;
        }

        BB84SimulationResult result = new BB84SimulationResult();
        result.setAliceBits(aliceBits);
        result.setAliceBases(aliceBases);
        result.setBobBases(bobBases);
        result.setBobMeasurements(bobMeasurements);
        result.setSharedKey(sharedKey);
        result.setMatchedBasisCount(matchedBasisCount);
        result.setKeyMatchRate(keyMatchRate);
        result.setEavesdroppingDetected(eavesdroppingDetected);
        result.setGeneratedRoomKey(convertSharedKeyToString(sharedKey));

        return result;
    }

    private String randomBasis() {
        return random.nextBoolean() ? "+" : "×";
    }

    private String convertSharedKeyToString(List<Integer> sharedKey) {
        StringBuilder keyBuilder = new StringBuilder();

        for (Integer bit : sharedKey) {
            keyBuilder.append(bit);
        }

        return keyBuilder.toString();
    }
}