package com.audigo.audigo_back.dto.request.admin.board;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostBoardRequestDto {
    
    //@NotBlank 반드시 값을 담고 있고 String 에 적용가능
    @NotBlank 
    private String boardType;

    @NotBlank
    private String publishDt;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String titleEn;
    private String contentEn;

    //@NotNull 빈값 혹은 빈배열이 올 수 있지만 해당 필드는 반드시 있을 것 Integer 등에 사용가능
    @NotNull
    private List<String> boardImageList;
}
