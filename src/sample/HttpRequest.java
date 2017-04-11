package sample;

import com.google.gson.Gson;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Created by Flaviu on 19-Sep-16.
 */
public class HttpRequest {

    private static String jiraProtocol; //= "http";
    private static String jiraServerName;// = "www.fasttrackit.ninja:8080";
    private static String jiraUserName;// = "flaviu.mesesan";
    private static String jiraPassword;// = "parola11";
    private static String jiraQuerry;// = "type = \"technical task\" and status != closed and assignee = currentuser()";

    private static String getUserToken(){
        String toEncode = jiraUserName +":"+ jiraPassword;
        return Base64.encode(toEncode.getBytes());
    }

    public static boolean doJiraLoginRequest() throws IOException {
        URL connection = new URL(jiraProtocol+"://"+jiraServerName + "/rest/auth/1/session");
        HttpURLConnection request = (HttpURLConnection) connection.openConnection();
        request.setRequestMethod("POST");
        String body = "{\"username\":\"" + jiraUserName + "\",\"password\":\"" + jiraPassword + "\"}";
        request.setRequestProperty("body", body);
        request.setRequestProperty("Content-Type", "application/json");
        request.setDoOutput(true);
        OutputStream os = request.getOutputStream();
        os.write(body.getBytes());
        os.flush();
        return request.getResponseCode()==200;
    }

    public static JiraRetrievedIssues getMyIssues() throws Throwable {
        URI uri = new URI(
                jiraProtocol,
                jiraServerName,
                "/rest/api/2/search",
                "jql=" + jiraQuerry,
                null);
        URL connection = new URL(uri.toASCIIString());
        HttpURLConnection request = (HttpURLConnection) connection.openConnection();
        request.setRequestMethod("GET");
        request.setRequestProperty("Authorization", "Basic "+getUserToken());
        BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String json = response.toString();

        Gson gson = new Gson();
        return gson.fromJson(json, JiraRetrievedIssues.class);
    }

    public static boolean doLogWorkRequest (String taskKey, Integer timeMinutes) throws Throwable{
        URL connection = new URL(jiraProtocol+"://"+jiraServerName + "/rest/api/2/issue/"+taskKey.toLowerCase()+"/worklog");
        HttpURLConnection request = (HttpURLConnection) connection.openConnection();
        request.setRequestMethod("POST");
        request.setRequestProperty("Authorization", "Basic "+getUserToken());
        String body = "{\"timeSpent\": \""+timeMinutes+"m\"}";
        request.setRequestProperty("body", body);
        request.setRequestProperty("Content-Type", "application/json");
        request.setDoOutput(true);
        OutputStream os = request.getOutputStream();
        os.write(body.getBytes());
        os.flush();
        return request.getResponseCode()==201;
    }

    public static void setJiraProtocol(String jiraProtocol) {
        HttpRequest.jiraProtocol = jiraProtocol;
    }

    public static void setJiraServerName(String jiraServerName) {
        HttpRequest.jiraServerName = jiraServerName;
    }

    public static void setJiraUserName(String jiraUserName) {
        HttpRequest.jiraUserName = jiraUserName;
    }

    public static void setJiraPassword(String jiraPassword) {
        HttpRequest.jiraPassword = jiraPassword;
    }

    public static void setJiraQuerry(String jiraQuerry) {
        HttpRequest.jiraQuerry = jiraQuerry;
    }
}
