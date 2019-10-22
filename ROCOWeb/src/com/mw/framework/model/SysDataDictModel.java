/**
 *
 */
package com.mw.framework.model;

import java.util.List;

import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysTrieTree;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.model.SysDataDictModel.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-4-21
 * 
 */
public class SysDataDictModel {

	private SysTrieTree trie;
	
	private List<SysDataDict> dicts;
	public List<SysDataDict> getDicts() {
		return dicts;
	}

	public void setDicts(List<SysDataDict> dicts) {
		this.dicts = dicts;
	}

	public SysTrieTree getTrie() {
		return trie;
	}

	public void setTrie(SysTrieTree trie) {
		this.trie = trie;
	}
}
