package org.beep.sbpp.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Getter
@ToString
public class PageRequestDTO {
    private int page = 1;
    private int size = 10;

    @Setter
    private String type;
    @Setter
    private String keyword;

    public String getPageLink() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("?page=").append(page).append("&size=").append(size);
        buffer.append(getLink());

        return buffer.toString();
    }

    public String getLink() {
        if (keyword == null || type == null) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();

        buffer.append("&type=").append(type);
        buffer.append("&keyword=").append(URLEncoder.encode(keyword, StandardCharsets.UTF_8));
        return buffer.toString();
    }

    public String[] getArr() {
        if (type == null || keyword == null) {
            return null;
        }
        return type.split("");
    }

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }

    public void setPage(int page) {
        if (page < 1) {
            this.page = 1;
            return;
        }
        if (page > 100) {
            this.page = 100;
            return;
        }
        this.page = page;
    }

    public void setSize(int size) {
        if (size < 1) {
            this.size = 10;
            return;
        }
        if (size > 100) {
            this.size = 100;
            return;
        }
        this.size = size;
    }
}