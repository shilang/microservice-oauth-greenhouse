package com.greenhouse.auth.dao.impl;

import com.greenhouse.auth.dao.RoleDAO;
import com.greenhouse.auth.dao.mapper.RoleMapper;
import com.greenhouse.auth.entity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author keets
 * @date 2017/11/22
 */
@Repository
public class MybatisRoleDAO implements RoleDAO {

    @Autowired
    private RoleMapper roleMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisRoleDAO.class);

    @Override
    public void insert(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public int deleteById(UUID id) {
        return roleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Role role) {
        roleMapper.updateByPrimaryKey(role);
    }

    @Override
    public Role selectById(UUID id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Role> selectAll() {
        return roleMapper.selectAll();
    }

}

