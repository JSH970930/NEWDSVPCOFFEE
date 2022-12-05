package com.project.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Answer2Form {
    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;
}