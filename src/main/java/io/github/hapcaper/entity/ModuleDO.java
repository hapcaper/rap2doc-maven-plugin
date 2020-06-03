package io.github.hapcaper.entity;

import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author 李自豪（zihao.li01@ucarinc.com）
 * @since 2020/4/21
 */
public class ModuleDO {
    private Long id;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Long repositoryId;
    private Long creatorId;
    private Long priority;
    private List<InterfaceDO> interfaceDOList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public List<InterfaceDO> getInterfaceDOList() {
        return interfaceDOList;
    }

    public void setInterfaceDOList(List<InterfaceDO> interfaceDOList) {
        this.interfaceDOList = interfaceDOList;
    }
}
