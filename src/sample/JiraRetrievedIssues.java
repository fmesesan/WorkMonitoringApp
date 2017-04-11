package sample;

import java.util.List;

/**
 * Created by Flaviu on 01-Oct-16.
 */
public class JiraRetrievedIssues {

    private List<JiraIssue> issues;

    public List<JiraIssue> getIssues() {
        return issues;
    }

    public String getKey(String summary) {
        String key = null;
        for (int i = 0; i < issues.size(); i++) {
            if (summary != null && summary.equals(issues.get(i).getSummary())) {
                key = issues.get(i).getKey();
                break;
            }
        }
        return key;
    }
}