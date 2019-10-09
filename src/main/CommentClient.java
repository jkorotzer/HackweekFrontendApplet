package main;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommentClient {
    private final String url;
    private final Integer port;
    private final String path;

    public CommentClient(String url, Integer port, String path) {
        this.url = url;
        this.port = port;
        this.path = path;
    }

    public void addComment(AddCommentRequest addCommentRequest) {
        Gson gson = new Gson();
        String requestBody = gson.toJson(addCommentRequest, AddCommentRequest.class);

        try {
            URL url = new URL(String.format("http://%s:%d/%s", this.url, this.port, this.path));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");


            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
                os.close();
            }
            InputStream is = con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response.toString());


        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
