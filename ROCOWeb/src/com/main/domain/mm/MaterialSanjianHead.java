package com.main.domain.mm;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 * 散件
 * @author zjc
 *
 */
@Entity
@Table(name = "MATERIAL_SANJIAN_HEAD")
public class MaterialSanjianHead extends UUIDEntity implements Serializable {
    private Set<MaterialSanjianItem> materialSanjianItemSet = new HashSet<MaterialSanjianItem>();
    /**
     * 物料id
     */
    private String materialHeadId;
	/**
	 * 可用状态  用于删除标识
	 * 不可用：X
	 */
	private String  status;
	/**
	 * 物料编号
	 */
	private String  matnr;
	/**
	 * 描述
	 */
	private String  miaoshu;
	/**
     * loadStatus从哪个页面进入
     * 2：我的物品 显示非标产品 ,3:订单
     */
	private String  loadStatus;
	
	/**
	 * 可用状态
	 */
	@Column(name = "STATUS",length=2)
	public String getStatus() {
		return status;
	}
	/**
	 * 可用状态
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	@Column(name = "MIAOSHU",length=255)
    public String getMiaoshu() {
        return miaoshu;
    }
    public void setMiaoshu(String miaoshu) {
        this.miaoshu = miaoshu;
    }
    @Column(name = "MATNR",length=32)
    public String getMatnr() {
        return matnr;
    }
    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "materialSanjianHead")
    public Set<MaterialSanjianItem> getMaterialSanjianItemSet() {
        return materialSanjianItemSet;
    }
    public void setMaterialSanjianItemSet(Set<MaterialSanjianItem> materialSanjianItemSet) {
        this.materialSanjianItemSet = materialSanjianItemSet;
    }
    @Transient
    public String getLoadStatus() {
        return loadStatus;
    }
    public void setLoadStatus(String loadStatus) {
        this.loadStatus = loadStatus;
    }
    @Column(name = "MATERIAL_HEAD_ID", length = 32)
    public String getMaterialHeadId() {
        return materialHeadId;
    }

    public void setMaterialHeadId(String materialHeadId) {
        this.materialHeadId = materialHeadId;
    }
}
