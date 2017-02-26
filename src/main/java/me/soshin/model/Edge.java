package me.soshin.model;

public class Edge {

    private final String source;
    private final String target;
    private String content;

    public Edge(final String source, final String target) {
        this.source = source;
        this.target = target;
    }

    public Edge(final String source, final String target, final String content) {
        this(source, target);
        this.content = content;
    }

    public String getSource() {
        return this.source;
    }

    public String getTarget() {
        return this.target;
    }

    public String getContent() {
        return this.content;
    }
}
