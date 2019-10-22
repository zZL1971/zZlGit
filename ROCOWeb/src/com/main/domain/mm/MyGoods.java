package com.main.domain.mm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 * 我的物品
 * @author zjc
 *
 */
@Entity
@Table(name = "MY_GOODS")
public class MyGoods extends UUIDEntity implements Serializable {
	/**
	 * 可用状态  用于删除标识
	 * 可用：1
	 * 不可用：X
	 */
	private String  status;
	
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
	
	/**
	 * 物料表id
	 */
	private String materialHeadId;
	/**
	 * 描述信息
	 */
	private String materialDesc;
	/**
	 * 标准产品 (带属性)价格id
	 * 标准产品 (没属性)和非标产品为null
	 */
	private String materialPropertyItemId;
	/**
	 * 标准产品(带属性)价格 描述
	 * 标准产品 (没属性)和非标产品为null
	 * info0_id1,info1_id2
	 */
	private String materialPropertyItemInfo;
	/**
	 * 标准产品(带属性)价格
	 * 标准产品(没属性)和非标为0
	 */
	private Double materialPrice;
	/**
     * 散件id
     */
    private String sanjianHeadId;
    /**
     * 补件id
     */
    private String bujianId;
	/**
	 * 
	 */
	private String matnr;
	/**
	 * 类型(用于添加散件)
	 */
	private String type;
	private String ortype;
	
	@Column(name = "MATERIAL_HEAD_ID", length = 32)
	public String getMaterialHeadId() {
		return materialHeadId;
	}
	public void setMaterialHeadId(String materialHeadId) {
		this.materialHeadId = materialHeadId;
	}
	@Column(name = "MATERIAL_DESC", length = 250)
	public String getMaterialDesc() {
		return materialDesc;
	}
	public void setMaterialDesc(String materialDesc) {
		this.materialDesc = materialDesc;
	}
	@Column(name = "MATERIAL_PROPERTY_ITEM_ID", length = 32)
	public String getMaterialPropertyItemId() {
		return materialPropertyItemId;
	}
	public void setMaterialPropertyItemId(String materialPropertyItemId) {
		this.materialPropertyItemId = materialPropertyItemId;
	}
	@Column(name = "MATERIAL_PRICE")
	public Double getMaterialPrice() {
		return materialPrice;
	}
	public void setMaterialPrice(Double materialPrice) {
		this.materialPrice = materialPrice;
	}
	@Column(name = "MATERIAL_PROPERTY_ITEM_INFO", length = 500)
	public String getMaterialPropertyItemInfo() {
		return materialPropertyItemInfo;
	}
	public void setMaterialPropertyItemInfo(String materialPropertyItemInfo) {
		this.materialPropertyItemInfo = materialPropertyItemInfo;
	}
	@Column(name = "MATNR", length = 30)
    public String getMatnr() {
        return matnr;
    }
    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }
    @Column(name = "TYPE", length = 10)
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    @Column(name = "ORTYPE", length = 10)
    public String getOrtype() {
        return ortype;
    }
    public void setOrtype(String ortype) {
        this.ortype = ortype;
    }
    @Column(name = "SANJIAN_HEAD_ID", length = 32)
    public String getSanjianHeadId() {
        return sanjianHeadId;
    }
    public void setSanjianHeadId(String sanjianHeadId) {
        this.sanjianHeadId = sanjianHeadId;
    }
    @Column(name = "BUJIAN_ID", length = 32)
    public String getBujianId() {
        return bujianId;
    }
    public void setBujianId(String bujianId) {
        this.bujianId = bujianId;
    }
}
