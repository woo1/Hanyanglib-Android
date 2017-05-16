package com.wooil.hanyanglib;

/**
 * Created by wijang on 2017. 5. 3..
 */
public class ListViewItem {
    private String name ;
    private String left ;
    private String tot ;
    private String linkURL;

    public String getName() {
        return name;
    }

    public String getLeft() {
        return left;
    }

    public String getTot() {
        return tot;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTot(String tot) {
        this.tot = tot;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }
}
