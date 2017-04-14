package com.wojciechkocik.obd.data;

/**
 * @author Wojciech Kocik
 * @since 14.04.2017
 */
public class ObdData {
    private long epoch;
    private String label;
    private float value;
    private String accountId;

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "ObdData{" +
                "epoch=" + epoch +
                ", label='" + label + '\'' +
                ", value=" + value +
                ", accountId='" + accountId + '\'' +
                '}';
    }
}
