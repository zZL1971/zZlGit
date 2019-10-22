package com.main.domain.mm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.mw.framework.bean.impl.UUIDEntity;

/**
 * 散件
 * @author zjc
 *
 */
@Entity
@Table(name = "MATERIAL_SANJIAN_ITEM")
public class MaterialSanjianItem extends UUIDEntity implements Serializable {
    private MaterialSanjianHead materialSanjianHead;
    /**
     * 基本单位
     */
    private String  meins;
    /**
     * 基本单位
     */
    @Transient
    public String getMeins() {
        return meins;
    }
    /**
     * 基本单位
     */
    public void setMeins(String meins) {
        this.meins = meins;
    }
    /**
     * 排序
     */
    private Integer orderby;
    /**
     * 排序
     */
    @Column(name = "ORDERBY")
    public Integer getOrderby() {
        return orderby;
    }
    /**
     * 排序
     */
    public void setOrderby(Integer orderby) {
        this.orderby = orderby;
    }
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
	 * 数量
	 */
	private Integer amount;
	
	/**
	 * 费用化单位
	 */
	private String fyhmeins;
	/**
	 * 单价
	 */
	private Double price;
	
	/**
	 * 折扣
	 */
	private Double zhekou;
	/**
	 * 总价
	 */
	private Double totalPrice;
	/**
	 * 尺寸
	 */
	private String wsize;

	
	/**
	 * 产线
	 */
	private String chanxian;
	
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
	@Column(name = "WSIZE",length=255)
	public String getWsize() {
		return wsize;
	}
	public void setWsize(String wsize) {
		this.wsize = wsize;
	}
	@Column(name = "CHANXIAN",length=255)
    public String getChanxian() {
		return chanxian;
	}
	public void setChanxian(String chanxian) {
		this.chanxian = chanxian;
	}
	@Column(name = "AMOUNT")
    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    @Column(name = "PRICE")
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    @Column(name = "TOTAL_PRICE")
    public Double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    @Column(name = "MATNR",length=32)
    public String getMatnr() {
        return matnr;
    }
    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PID",referencedColumnName="ID")
    public MaterialSanjianHead getMaterialSanjianHead() {
        return materialSanjianHead;
    }
    public void setMaterialSanjianHead(MaterialSanjianHead materialSanjianHead) {
        this.materialSanjianHead = materialSanjianHead;
    }
    @Column(name = "ZHEKOU")
	public Double getZhekou() {
		return zhekou;
	}
	public void setZhekou(Double zhekou) {
		this.zhekou = zhekou;
	}
    @Column(name = "MATERIAL_HEAD_ID", length = 32)
    public String getMaterialHeadId() {
        return materialHeadId;
    }

    public void setMaterialHeadId(String materialHeadId) {
        this.materialHeadId = materialHeadId;
    }
    
    @Column(name = "FYHMEINS",length=32)
	public String getFyhmeins() {
		return fyhmeins;
	}
	public void setFyhmeins(String fyhmeins) {
		this.fyhmeins = fyhmeins;
	}
    
}
