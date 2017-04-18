package pl.edu.pk.obdtracker.api.model;

/**
 * Created by Wojciech on 18.04.2017.
 */

public class ObdData {

    private long epoch;
    private String label;
    private String value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
