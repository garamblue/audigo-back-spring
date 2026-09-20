package com.audigo.audigo_back.entity;

import com.audigo.audigo_back.entity.pKey.FavoritePk;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "favorite")
@Table(name = "favorite")
@IdClass(FavoritePk.class) // Specify
public class FavoriteEntity {

    // private int favoriteId;
    @Id
    private String userEmail;
    @Id
    private int bIdx;

}
