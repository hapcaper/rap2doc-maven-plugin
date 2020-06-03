package io.github.hapcaper.conf;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Description:
 *
 * @author 李自豪（zihao.li01@ucarinc.com）
 * @since 2020/4/29
 */
public class DataBaseConf {
    @Parameter(required = true)
    private String url;
    @Parameter(required = true)
    private String user;
    @Parameter(required = true)
    private String passWord;


    public DataBaseConf(String url, String user, String passWord) {
        this.url = url;
        this.user = user;
        this.passWord = passWord;
    }

    public DataBaseConf() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DataBaseConf{");
        sb.append("url='").append(url).append('\'');
        sb.append(", user='").append(user).append('\'');
        sb.append(", passWord='").append(passWord).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
