package io.github.hapcaper.dao;

import io.github.hapcaper.entity.RepositoryDO;
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
public class RepositoryDAO {
    @Nullable
    public static Long findIdByName(String name) throws Exception {
        PreparedStatement statement = MySqlUtil.getConnection().prepareStatement("select id from Repositories where name = ?");
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong(1);
        } else {
            return null;
        }

    }

    @NotNull
    public static Long insert(@NotNull RepositoryDO repositoryDO) throws Exception {
        Statement statement = MySqlUtil.getConnection().createStatement();
        statement.executeUpdate(String.format("insert into Repositories (name,description,ownerId,creatorId,createdAt,updatedAt) values ('%s','%s',%d,%d,now(),now())"
                , repositoryDO.getName(), repositoryDO.getDescription(), repositoryDO.getOwnerId(),repositoryDO.getCreatorId()), Statement.RETURN_GENERATED_KEYS);
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        } else {
            throw new RuntimeException();
        }
    }

    public static void updateById(@NotNull RepositoryDO repositoryDO) throws Exception {
        PreparedStatement statement = MySqlUtil.getConnection().prepareStatement("update Repositories set name = ?, description = ? , updatedAt = now() where id = ?");
        statement.setString(1, repositoryDO.getName());
        statement.setString(2, repositoryDO.getName());
        statement.setLong(3, repositoryDO.getId());
        statement.executeUpdate();
    }

    /**
     * @param repositoryDO 将要添加的数据对象
     * @return repositoryId 如果是新增则返回新的repositoryId 否则返回已有的repositoryId
     * @throws Exception 所有异常都向上抛出
     */
    public static Long insertOrUpdate(@NotNull RepositoryDO repositoryDO) throws Exception {
        if (repositoryDO.getId() != null) {
            updateById(repositoryDO);
            return repositoryDO.getId();
        } else {
            return insert(repositoryDO);
        }
    }
}
