package com.main.domain.sale;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 *附加信息 
 */
@Entity
@Table(name = "SALE_ITEM_FJ")
public class SaleItemFj extends UUIDEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6127185350353309795L;
	/**
     * 销售行项目id(关联saleItem)
     */
    private String saleItemId;
    /**
     * 我的物品id(关联myGoods)
     */
    private String myGoodsId;
    /**
     * 安装地点
     */
    private String zzazdr;
    
    /**
     * 物料id
     */
    private String materialHeadId;
    /**
	 * 可用状态  用于删除标识
	 * 可用：1
	 * 不可用：X
	 */
	private String  status;
	
	private String errRea;
	
	private String errType;
	
//	生产预完工日期：ppcDate
//	预计出货日期：psDate
//	计划完工日期：pcDate
//	实际入库日期：pbDate
//	实际出库日期：poDate
//  交期天数: deliveryDay
	private Date ppcDate;
	private Date psDate;
	private Date pcDate;
	private Date pbDate;
	private Date poDate;
	private String deliveryDay;

	private String productSpace;//产品空间
	@Column(name = "PRODUCT_SPACE", length = 8)
    public String getProductSpace() {
		return productSpace;
	}

	public void setProductSpace(String productSpace) {
		this.productSpace = productSpace;
	}
	private String jiaoQiStyle;
	private String zzjqlb;
	
    @Column(name = "SALE_ITEM_ID", length = 32)
    public String getSaleItemId() {
        return saleItemId;
    }

    public void setSaleItemId(String saleItemId) {
        this.saleItemId = saleItemId;
    }
    
    @Column(name = "MY_GOODS_ID", length = 32)
    public String getMyGoodsId() {
        return myGoodsId;
    }

    public void setMyGoodsId(String myGoodsId) {
        this.myGoodsId = myGoodsId;
    }
	
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
     * 安装地点
     */
	@Column(name = "ZZAZDR",length=20)
    public String getZzazdr() {
        return zzazdr;
    }

    public void setZzazdr(String zzazdr) {
        this.zzazdr = zzazdr;
    }
    @Column(name = "MATERIAL_HEAD_ID", length = 32)
	public String getMaterialHeadId() {
		return materialHeadId;
	}

	public void setMaterialHeadId(String materialHeadId) {
		this.materialHeadId = materialHeadId;
	}
	@Column(name = "ERR_REA",length=50)
	public String getErrRea() {
		return errRea;
	}

	public void setErrRea(String errRea) {
		this.errRea = errRea;
	}
	@Column(name = "ERR_TYPE",length=50)
	public String getErrType() {
		return errType;
	}

	public void setErrType(String errType) {
		this.errType = errType;
	}

	@Column(name = "PPC_DATE")
	public Date getPpcDate() {
		return ppcDate;
	}

	public void setPpcDate(Date ppcDate) {
		this.ppcDate = ppcDate;
	}
	@Column(name = "PS_DATE")
	public Date getPsDate() {
		return psDate;
	}

	public void setPsDate(Date psDate) {
		this.psDate = psDate;
	}
	@Column(name = "PC_DATE")
	public Date getPcDate() {
		return pcDate;
	}

	public void setPcDate(Date pcDate) {
		this.pcDate = pcDate;
	}
	@Column(name = "PB_DATE")
	public Date getPbDate() {
		return pbDate;
	}

	public void setPbDate(Date pbDate) {
		this.pbDate = pbDate;
	}
	@Column(name = "PO_DATE")
	public Date getPoDate() {
		return poDate;
	}

	public void setPoDate(Date poDate) {
		this.poDate = poDate;
	}
	@Column(name = "DELIVERY_DAY",length=20)
	public String getDeliveryDay() {
		return deliveryDay;
	}

	public void setDeliveryDay(String deliveryDay) {
		this.deliveryDay = deliveryDay;
	}
	@Column(name = "JIAO_QI_STYLE",length=10)
	public String getJiaoQiStyle() {
		return jiaoQiStyle;
	}

	public void setJiaoQiStyle(String jiaoQiStyle) {
		this.jiaoQiStyle = jiaoQiStyle;
	}

	@Column(name = "ZZJQLB",length=10)
	public String getZzjqlb() {
		return zzjqlb;
	}

	public void setZzjqlb(String zzjqlb) {
		this.zzjqlb = zzjqlb;
	}
}
