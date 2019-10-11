package main;

class AddCommentRequest {
    public String filepath;
    public Integer line;
    public String comment;
    public String sourceOfComment; // This is a hack to figure out if Intellij or the Applet sent this request
    public int sourceIntellijServerPort; // This captures the intellij server port of the Intellij instance that sent this comment

    public AddCommentRequest() {
    }
}
