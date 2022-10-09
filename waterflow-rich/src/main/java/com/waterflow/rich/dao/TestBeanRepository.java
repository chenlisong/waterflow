package com.waterflow.rich.dao;

import com.waterflow.rich.bean.TestBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface TestBeanRepository extends JpaRepository<TestBean, Long> {

}
