package com.waterflow.ccopen.dao;

import com.waterflow.ccopen.bean.AdLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdLogDao extends JpaRepository<AdLog, Long> {

    @Query(value="select * from t_ad_log where userid = :userId and platform=1 order by id desc limit 10", nativeQuery = true)
    List<AdLog> findTop10(@Param("userId")String userId);

    @Query(value="select * from t_ad_log where userid = :userId and platform=1 order by id desc limit :limit", nativeQuery = true)
    AdLog findTop(@Param("userId")String userId, @Param("limit")int limit);

}