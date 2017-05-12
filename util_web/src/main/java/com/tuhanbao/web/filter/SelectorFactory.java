package com.tuhanbao.web.filter;

import java.util.HashMap;
import java.util.Map;

import com.tuhanbao.util.db.table.Table;

/**
 * 此类主要为了避免业务人员私自创建selector
 * @author tuhanbao
 *
 */
public class SelectorFactory {
	private static Map<Table, Selector> selectors = new HashMap<Table, Selector>();
	protected static Selector createSelector(Table table) {
		if (!selectors.containsKey(table)) {
			Selector selector = new Selector(table);
			selectors.put(table, selector);
			return selector;
		}
		return selectors.get(table);
	}

	public static Selector getTablesSelector(Table table) {
		if (!selectors.containsKey(table)) {
			return createSelector(table);
		}
		return selectors.get(table);
	}
}
