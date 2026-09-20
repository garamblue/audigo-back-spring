package com.audigo.audigo_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.audigo.audigo_back.entity.FavoriteEntity;
import com.audigo.audigo_back.entity.pKey.FavoritePk;
import com.audigo.audigo_back.repository.resultSet.GetFavoriteListResultSet;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, FavoritePk> {
    // Custom query methods (if needed) can be defined here
    FavoriteEntity findBybIdxAndUserEmail(Integer bIdx, String userEmail);

    @Query(value = "SELECT " +
            "U.email AS email " +
            ", U.nickname AS nickname " +
            ", U.profile_image AS profileImage " +
            "FROM favorite AS F " +
            "INNER JOIN user AS U " +
            "ON F.user_email = U.email " +
            "WHERE F.b_idx = ?1 ", nativeQuery = true)
    List<GetFavoriteListResultSet> getFavoriteList(Integer bIdx);
}
