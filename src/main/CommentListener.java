package main;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class CommentListener extends AbstractHandler {
  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
    // Declare response encoding and types
    response.setContentType("text/plain; charset=utf-8");
    BufferedReader bfReader = new BufferedReader(request.getReader());

    String temp = null;
    String raw = "";
    while((temp = bfReader.readLine()) != null) {
      raw = raw + temp;
    }

    System.out.println(raw);

    Gson gson = new Gson();
    AddCommentRequest addCommentReq = gson.fromJson(raw, AddCommentRequest.class);

    if (request.getHeader("Host").contains("127.0.0.1")) {
      // If local request, forward to remote Applet
      new CommentClient("other-ip", 8080, "").addComment(addCommentReq);
    } else {
      // If remote request, forward to local IntelliJ
      new CommentClient("127.0.0.1", 63343, "api.addComment").addComment(addCommentReq);
    }

    // Declare response status code
    response.setStatus(HttpServletResponse.SC_OK);

    // Write back response
    response.getWriter().println("Thumbs up dude");

    // Inform jetty that this request has now been handled
    baseRequest.setHandled(true);
  }
}
