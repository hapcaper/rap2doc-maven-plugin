package io.github.hapcaper.dao;

import io.github.hapcaper.entity.ModuleDO;
import io.github.hapcaper.util.MySqlUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Description:
 *
 * @author 李自豪（zihao.li01@ucarinc.com）
 * @since 2020/4/22
 */
public class ModuleDAO {

    @Nullable
    public static Long findId(Long repositoryId, String name) throws Exception {
        PreparedStatement statement = MySqlUtil.getConnection().prepareStatement("select id from Modules where repositoryId = ? and name = ? and deletedAt is null");
        statement.setLong(1, repositoryId);
        statement.setString(2, name);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong(1);
        } else {
            return null;
        }
    }

    public static Long insert(@NotNull ModuleDO moduleDO) throws Exception {
//        PreparedStatement statement = MySqlUtil.getConnection().prepareStatement("insert into Modules (name, description, createdAt, updatedAt, repositoryId) values (?,?,now(),now(),?);");
//        statement.setString(1, moduleDO.getName());
//        statement.setString(2, moduleDO.getDescription());
//        statement.setLong(3, moduleDO.getRepositoryId());
//        statement.executeUpdate();
//        return findId(moduleDO.getRepositoryId(), moduleDO.getName());

        Statement statement = MySqlUtil.getConnection().createStatement();
        statement.executeUpdate(String.format("insert into Modules (name, description, createdAt, updatedAt, repositoryId) values ('%s','%s',now(),now(),%d);", moduleDO.getName(), moduleDO.getDescription(), moduleDO.getRepositoryId()), Statement.RETURN_GENERATED_KEYS);
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        } else {
            throw new RuntimeException("获取添加的新module的id失败");
        }

    }

    public static Long update(@NotNull ModuleDO moduleDO) throws Exception {
        PreparedStatement statement = MySqlUtil.getConnection().prepareStatement("update Modules set name = ? , description = ? , updatedAt = now() , repositoryId = ? where id = ?");
        statement.setString(1, moduleDO.getName());
        statement.setString(2, moduleDO.getDescription());
        statement.setLong(3, moduleDO.getRepositoryId());
        statement.setLong(4, moduleDO.getId());
        statement.executeUpdate();
        return moduleDO.getId();

    }

    public static Long insertOrUpdate(@NotNull ModuleDO moduleDO) throws Exception {
        if (moduleDO.getId() == null) {
            return insert(moduleDO);
        } else {
            return update(moduleDO);
        }


    }
}
