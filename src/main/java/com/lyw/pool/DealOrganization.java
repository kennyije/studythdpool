package com.lyw.pool;

import lombok.Data;

import java.util.List;

/**
 * Created by Lenovo on 2018/10/29.
 */
@Data
public class DealOrganization {
    private Long id;

    private String name;

    private Long pid;

    private Integer dept;

    private String emp;

    private List<DealOrganization> children;
}
