package io.github.hapcaper.conf;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Description:
 *
 * @author 李自豪（zihao.li01@ucarinc.com）
 * @since 2020/4/21
 */
public class ModuleConf {
    /**
     * 模块名
     */
    @Parameter(required = true)
    private String name;

    /**
     * 模块描述
     */
    @Parameter
    private String description;
    /**
     * 仓库名称
     */
    @Parameter(required = true)
    private String repositoryName;

    /**
     * 仓库描述
     */
    @Parameter
    private String repositoryDesc;

    /**
     * 仓库拥有者
     */
    @Parameter
    private Long ownerId;


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

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryDesc() {
        return repositoryDesc;
    }

    public void setRepositoryDesc(String repositoryDesc) {
        this.repositoryDesc = repositoryDesc;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
