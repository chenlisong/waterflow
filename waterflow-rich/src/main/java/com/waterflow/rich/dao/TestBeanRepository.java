package com.waterflow.rich.dao;

import com.waterflow.rich.bean.TestBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestBeanRepository extends JpaRepository<TestBean, Long> {

}
