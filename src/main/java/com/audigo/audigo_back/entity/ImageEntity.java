package com.audigo.audigo_back.entity;

import jakarta.persistence.Column;
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
@Entity(name = "image")
@Table(name = "image")
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i_idx")
    private int iIdx;
    
    @Column(name = "b_idx")
    private int bIdx;
    
    @Column(name = "image_url")
    private String imageUrl;

    public ImageEntity(int bidx, String imgUrl) {
        this.bIdx = bidx;
        this.imageUrl = imgUrl;
    }
}
