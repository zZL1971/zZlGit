/**
 *
 */
package com.main.model.spm;

import java.util.Set;

import com.main.domain.spm.SalePrModHeader;
import com.main.domain.spm.SalePrModItem;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @Version 1.0.0
 * @author LKL 
 * @time 2015-4-22
 * 
 */
public class SPMModel {

	private SalePrModHeaderModel spmHeaderModel;
	private Set<SalePrModItemModel> spmItemModelSet;

	public SPMModel() {
		super();
	}

	public SPMModel(SalePrModHeaderModel spmHeaderModel,
			Set<SalePrModItemModel> spmItemModelSet) {
		super();
		this.spmHeaderModel = spmHeaderModel;
		this.spmItemModelSet = spmItemModelSet;
	}

	public SalePrModHeaderModel getSpmHeaderModel() {
		return spmHeaderModel;
	}

	public void setSpmHeaderModel(SalePrModHeaderModel spmHeaderModel) {
		this.spmHeaderModel = spmHeaderModel;
	}

	public Set<SalePrModItemModel> getSpmItemModelSet() {
		return spmItemModelSet;
	}

	public void setSpmItemModelSet(Set<SalePrModItemModel> spmItemModelSet) {
		this.spmItemModelSet = spmItemModelSet;
	}

}
