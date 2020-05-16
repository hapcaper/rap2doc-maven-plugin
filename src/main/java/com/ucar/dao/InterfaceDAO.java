package com.ucar.dao;

import com.ucar.entity.InterfaceDO;
import com.ucar.util.MySqlUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

/**
 * Description:
 *
 * @author 李自豪（zihao.li01@ucarinc.com）
 * @since 2020/4/22
 */
public class InterfaceDAO {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @NotNull
    public static Long insert(@NotNull InterfaceDO interfaceDO) throws Exception {
        Statement statement = MySqlUtil.getConnection().createStatement();
        statement.executeUpdate(String.format("insert into Interfaces (name, url, method, description, moduleId, repositoryId,createdAt, updatedAt) values ('%s','%s','%s','%s',%d,%d,NOW(),NOW());"
                , interfaceDO.getName(), interfaceDO.getUrl(), interfaceDO.getMethod(), interfaceDO.getDescription(), interfaceDO.getModuleId(), interfaceDO.getRepositoryId()), Statement.RETURN_GENERATED_KEYS);
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        } else {
            throw new RuntimeException();
        }
    }

    public static Long update(@NotNull InterfaceDO interfaceDO) throws Exception {
        PreparedStatement statement = MySqlUtil.getConnection().prepareStatement("update Interfaces set name = ? ,url = ?,method=?,description = ?,updatedAt=now(),moduleId=?,repositoryId=? where id = ?");
        statement.setString(1, interfaceDO.getName());
        statement.setString(2, interfaceDO.getUrl());
        statement.setString(3, interfaceDO.getMethod());
        statement.setString(4, interfaceDO.getDescription());
        statement.setLong(5,interfaceDO.getModuleId());
        statement.setLong(6,interfaceDO.getRepositoryId());
        statement.setLong(7,interfaceDO.getId());
        statement.executeUpdate();
        return interfaceDO.getId();
    }

    public static Long insertOrUpdate(@NotNull InterfaceDO interfaceDO) throws Exception {
        if (interfaceDO.getId() == null) {
            return insert(interfaceDO);
        } else {
            return update(interfaceDO);
        }
    }

    @Nullable
    public static Long findId(Long repositoryId, Long moduleId, String url) throws Exception {
        PreparedStatement statement = MySqlUtil.getConnection().prepareStatement("select id from Interfaces where repositoryId = ? and moduleId = ? and url = ?");
        statement.setLong(1, repositoryId);
        statement.setLong(2, moduleId);
        statement.setString(3, url);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong(1);
        } else {
            return null;
        }
    }
}
