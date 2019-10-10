package main;

class AddCommentRequest {
    public String filepath;
    public Integer line;
    public String comment;
    public String sourceOfComment; // This is a hack to figure out if Intellij or the Applet sent this request

    public AddCommentRequest() {
    }
}
