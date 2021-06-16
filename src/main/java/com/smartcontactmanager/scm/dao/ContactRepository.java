package com.smartcontactmanager.scm.dao;

import com.smartcontactmanager.scm.entities.Contact;
import com.smartcontactmanager.scm.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
//    pagination...

    @Query("from Contact as c where c.user.id =:userId")
//    currentPage-page
//    contact Per page - 5
    public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable p);

    public List<Contact> findByNameContainingAndUser(String name, User user);
}
