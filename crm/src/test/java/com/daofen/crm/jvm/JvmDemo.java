package com.daofen.crm.jvm;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class JvmDemo {

    private Long id;
    private String name= UUID.randomUUID().toString()+"__"+UUID.randomUUID().toString();

    public JvmDemo(Long id) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "JvmDemo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
