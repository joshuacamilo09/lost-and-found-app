package com.lostandfound.dto.chat.message;

import jakarta.validation.constraints.NotBlank;

public class MessageRequest {

    @NotBlank(message = "O conteúdo da mensagem não pode estar vazio")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
