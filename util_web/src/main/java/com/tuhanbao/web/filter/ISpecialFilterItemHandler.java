package com.tuhanbao.web.filter;

import com.tuhanbao.util.db.table.Column;

public interface ISpecialFilterItemHandler {
    void handle(WebFilterItem item, Column col);
}
