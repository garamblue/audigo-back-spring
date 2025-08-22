package com.audigo.audigo_back.entity;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import com.audigo.audigo_back.dto.request.admin.board.PostCommentRequestDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "comment")
@Table(name = "comment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentNumber;
    private String content;
    private String writeDatetime;
    private String userEmail;
    private int bIdx;

    /**
     * 생성자 추가
     * 
     * @param dto
     * @param boardNum
     * @param email
     */
    public CommentEntity(PostCommentRequestDto dto, Integer boardNum, String email) {
        Date now = Date.from(Instant.now());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String wdt = simpleDateFormat.format(now);

        this.content = dto.getContent();
        this.bIdx = boardNum;
        this.userEmail = email;
        this.writeDatetime = wdt;
    }

}