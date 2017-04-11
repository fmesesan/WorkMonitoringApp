package sample;

import com.google.gson.internal.LinkedTreeMap;


/**
 * Created by Flaviu on 01-Oct-16.
 */

public class JiraIssue {
    private String key;
    private LinkedTreeMap fields;

    public String getSummary() {
        return this.fields.get("summary").toString();
    }

    public String getKey() {
        return key;
    }
}