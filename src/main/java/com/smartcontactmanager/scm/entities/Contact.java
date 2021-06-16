package com.smartcontactmanager.scm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "CONTACT")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cid;
    private String name;
    private String sec_name;
    private String work;
    private String c_email;
    private String phone;
    private String image;
    @Column(length = 5000)
    private String description;

    @ManyToOne()
    @JsonIgnore
    private User user;


    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }


    public String getWork() {
        return work;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSec_name() {
        return sec_name;
    }

    public void setSec_name(String sec_name) {
        this.sec_name = sec_name;
    }

    public String getC_email() {
        return c_email;
    }

    public void setC_email(String c_email) {
        this.c_email = c_email;
    }

    public void setWork(String work) {
        this.work = work;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "cid=" + cid +
                ", name='" + name + '\'' +
                ", sec_name='" + sec_name + '\'' +
                ", work='" + work + '\'' +
                ", c_email='" + c_email + '\'' +
                ", phone='" + phone + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return this.cid==((Contact)obj).getCid();
    }
}
