package io.github.hapcaper.entity;

import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author 李自豪（zihao.li01@ucarinc.com）
 * @since 2020/4/21
 */
public class InterfaceDO {

    private Long id;
    private String name;
    private String url;
    private String method;
    private String description;
    private Date createAt;
    private Date updateAt;
    private Date deleteAt;
    private Long moduleId;
    private Long creatorId;
    private Long lockerId;
    private Long repositoryId;
    private Long priority;
    private Integer status;

    private List<PropertyDO> requestPropertyDOList;
    private List<PropertyDO> responsePropertyDOList;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Date getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(Date deleteAt) {
        this.deleteAt = deleteAt;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getLockerId() {
        return lockerId;
    }

    public void setLockerId(Long lockerId) {
        this.lockerId = lockerId;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<PropertyDO> getRequestPropertyDOList() {
        return requestPropertyDOList;
    }

    public void setRequestPropertyDOList(List<PropertyDO> requestPropertyDOList) {
        this.requestPropertyDOList = requestPropertyDOList;
    }

    public List<PropertyDO> getResponsePropertyDOList() {
        return responsePropertyDOList;
    }

    public void setResponsePropertyDOList(List<PropertyDO> responsePropertyDOList) {
        this.responsePropertyDOList = responsePropertyDOList;
    }
}
