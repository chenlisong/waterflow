package com.waterflow.ccopen.dao;

import com.waterflow.ccopen.bean.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NovelDao extends JpaRepository<Novel, Long> {

    @Modifying
    @Transactional
    @Query(value="delete from t_novel where type_name = :typeName and name = :name ", nativeQuery = true)
    void deleteByName(@Param("typeName")String typeName, @Param("name")String name);

//    @Query(value="select * from t_ad_log where userid = :userId and platform=1 order by id desc limit :limit", nativeQuery = true)
//    AdLog findTop(@Param("userId")String userId, @Param("limit")int limit);

}