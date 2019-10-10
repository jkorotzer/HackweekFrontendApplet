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
  private HackweekApplet applet;

  public CommentListener(HackweekApplet applet) {
    this.applet = applet;
  }

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

    if (addCommentReq.sourceOfComment.equalsIgnoreCase("intellij")) {
      // If sender of this request is intellij, forward to other applet

      // Get the port of this applet's server
      int localPort = request.getLocalPort();
      // If request coming from applet on 8080 then it should send it to applet on 8081 and vice versa
      int remotePort = localPort == 8080 ? 8081 : 8080;

      // set the new sourceOfComment as the applet before forwarding it to the other applet
      addCommentReq.sourceOfComment = "applet";
      new CommentClient("127.0.0.1", remotePort, "").addComment(addCommentReq);
    } else {
      // If sender is the applet (emulating remote) then forward to local intellij
      new CommentClient("127.0.0.1", 63343, "api.addComment").addComment(addCommentReq);
      applet.notificationReceived(addCommentReq.filepath);
    }

    // Declare response status code
    response.setStatus(HttpServletResponse.SC_OK);

    // Write back response
    response.getWriter().println("Thumbs up dude");

    // Inform jetty that this request has now been handled
    baseRequest.setHandled(true);
  }
}
