package com.ucar.entity;

import org.codehaus.plexus.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author 李自豪（zihao.li01@ucarinc.com）
 * @since 2020/4/21
 */
public class PropertyDO {
    private Long id;
    private String scope;
    private String name;
    private String type;
    private String rule;
    private String value;
    private String description;
    private Long parentId;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Long interfaceId;
    private Long creatorId;
    private Long moduleId;
    private Long repositoryId;
    private Long priority;
    private Integer pos;
    private Integer required;
    private List<PropertyDO> propertyDOList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRule() {
        return StringUtils.isBlank(rule) ? "" : rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getValue() {
        return StringUtils.isBlank(value) ? "" : value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return StringUtils.isBlank(description) ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public Long getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(Long interfaceId) {
        this.interfaceId = interfaceId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
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

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public List<PropertyDO> getPropertyDOList() {
        return propertyDOList;
    }

    public void setPropertyDOList(List<PropertyDO> propertyDOList) {
        this.propertyDOList = propertyDOList;
    }
}
