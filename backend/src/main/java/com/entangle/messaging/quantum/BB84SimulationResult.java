package com.entangle.messaging.quantum;

import java.util.List;

public class BB84SimulationResult {

    private List<Integer> aliceBits;
    private List<String> aliceBases;
    private List<String> bobBases;
    private List<Integer> bobMeasurements;
    private List<Integer> sharedKey;
    private int matchedBasisCount;
    private double keyMatchRate;
    private boolean eavesdroppingDetected;
    private String generatedRoomKey;

    public BB84SimulationResult() {
    }

    public List<Integer> getAliceBits() {
        return aliceBits;
    }

    public void setAliceBits(List<Integer> aliceBits) {
        this.aliceBits = aliceBits;
    }

    public List<String> getAliceBases() {
        return aliceBases;
    }

    public void setAliceBases(List<String> aliceBases) {
        this.aliceBases = aliceBases;
    }

    public List<String> getBobBases() {
        return bobBases;
    }

    public void setBobBases(List<String> bobBases) {
        this.bobBases = bobBases;
    }

    public List<Integer> getBobMeasurements() {
        return bobMeasurements;
    }

    public void setBobMeasurements(List<Integer> bobMeasurements) {
        this.bobMeasurements = bobMeasurements;
    }

    public List<Integer> getSharedKey() {
        return sharedKey;
    }

    public void setSharedKey(List<Integer> sharedKey) {
        this.sharedKey = sharedKey;
    }

    public int getMatchedBasisCount() {
        return matchedBasisCount;
    }

    public void setMatchedBasisCount(int matchedBasisCount) {
        this.matchedBasisCount = matchedBasisCount;
    }

    public double getKeyMatchRate() {
        return keyMatchRate;
    }

    public void setKeyMatchRate(double keyMatchRate) {
        this.keyMatchRate = keyMatchRate;
    }

    public boolean isEavesdroppingDetected() {
        return eavesdroppingDetected;
    }

    public void setEavesdroppingDetected(boolean eavesdroppingDetected) {
        this.eavesdroppingDetected = eavesdroppingDetected;
    }

    public String getGeneratedRoomKey() {
        return generatedRoomKey;
    }

    public void setGeneratedRoomKey(String generatedRoomKey) {
        this.generatedRoomKey = generatedRoomKey;
    }
}