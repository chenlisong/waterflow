package com.waterflow.ccopen.dao;

import com.waterflow.ccopen.bean.Catalog;
import com.waterflow.ccopen.bean.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogDao extends JpaRepository<Catalog, Long> {

    @Query(value="delete from t_catalog where novel_id = :novelId", nativeQuery = true)
    void deleteByName(@Param("novelId")Long novelId);

//    @Query(value="select * from t_ad_log where userid = :userId and platform=1 order by id desc limit :limit", nativeQuery = true)
//    AdLog findTop(@Param("userId")String userId, @Param("limit")int limit);

}