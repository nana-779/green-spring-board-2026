package com.green.spring_board;

public class BoardCreateRequest {
    private String title;
    private String content;

    // 생성자
    public BoardCreateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 일반 생성자
    public BoardCreateRequest() {}

    // getter, setter
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
