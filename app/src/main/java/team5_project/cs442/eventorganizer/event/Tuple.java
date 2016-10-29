package team5_project.cs442.eventorganizer.event;

import java.io.Serializable;

/**
 * Created by Tae Kyong Han on 11/5/2015.
 */
public class Tuple implements Serializable {
    private String mKey;
    private String mValue;

    public Tuple(String key, String value) {
        mKey = key;
        mValue = value;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        this.mValue = value;
    }
}
