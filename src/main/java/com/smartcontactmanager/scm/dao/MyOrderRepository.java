package com.smartcontactmanager.scm.dao;

import com.smartcontactmanager.scm.entities.MyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyOrderRepository extends JpaRepository<MyOrder, Long> {

    public MyOrder findByOrderId(String orderId);
}
