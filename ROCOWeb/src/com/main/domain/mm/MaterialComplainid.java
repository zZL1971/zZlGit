package com.main.domain.mm;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;
@Entity
@Table(name = "MATERIAL_COMPLAINID")
public class MaterialComplainid extends UUIDEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


    //原订单行号
    private String orderCodePosex;
    
    //saleheadid
    private String pid;
    //客户投诉信息
    // ZZTSNR ZZTSNR 投诉内容描述 CHAR 80
    private String  zztsnr;
    //ZZEZX ZZEZX 出错中心 char
    private String  zzezx;
    // ZZEBM ZZEBM 出错部门  char 
    private String  zzebm;
    // ZZELB ZZELB 出错类别  char
    private String  zzelb;
    //duty duty 责任人 char 
    private String duty;
    //投诉日期
    private Date complaintTime;
	//出错组
    private String  zzccz;
    //产品名称
    private String cpmc;
    //出错问题
    private String zzccwt;
    //备注
    private String remark;
    
    private String color;
    
    private String cabinetName;
    
    private String salefor;
    //出错类型
    private String zzcclx;
    
    @Column(name="COMPLAINT_TIME")
    public Date getComplaintTime() {
		return complaintTime;
	}
	public void setComplaintTime(Date complainidTime) {
		this.complaintTime = complainidTime;
	}

	/**投诉内容描述 CHAR 80 **/
    @Column(name = "ZZTSNR",length=4000)
    public String getZztsnr() {
        return zztsnr;
    }

	public void setZztsnr(String zztsnr) {
        this.zztsnr = zztsnr;
    }
    

    
    
    /** 出错中心**/
    @Column(name = "ZZEZX",length=255)
	public String getZzezx() {
		return zzezx;
	}
	public void setZzezx(String zzezx) {
		this.zzezx = zzezx;
	}
	
	/** 出错部门 **/
    @Column(name = "ZZEBM",length=255)
	public String getZzebm() {
		return zzebm;
	}
	public void setZzebm(String zzebm) {
		this.zzebm = zzebm;
	}
	
	/** 出错类别 **/
	@Column(name = "ZZELB",length=255)
	public String getZzelb() {
		return zzelb;
	}
	public void setZzelb(String zzelb) {
		this.zzelb = zzelb;
	}
	
	/** 责任人**/
	@Column(name = "DUTY",length=255)
	public String getDuty() {
		return duty;
	}
	public void setDuty(String duty) {
		this.duty = duty;
	}
	
	@Column(name = "ZZCCZ",length=255)
	public String getZzccz() {
		return zzccz;
	}
	 
     public void setZzccz(String zzccz) {
		this.zzccz = zzccz;
	}
     
    @Column(name = "ZZCCWT",length=500)
	public String getZzccwt() {
		return zzccwt;
	}
	public void setZzccwt(String zzccwt) {
		this.zzccwt = zzccwt;
	}
	@Column(name="CPMC",length=255)
	public String getCpmc() {
		return cpmc;
	}
	public void setCpmc(String cpmc) {
		this.cpmc = cpmc;
	}
	@Column(name="ORDER_CODE_POSEX",length=255)
	public String getOrderCodePosex() {
		return orderCodePosex;
	}
	public void setOrderCodePosex(String orderCodePosex) {
		this.orderCodePosex = orderCodePosex;
	}

	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
    @Column(name = "REMARK",length=500)
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Column(name="COLOR",length=255)
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	@Column(name="CABINET_NAME",length=255)
	public String getCabinetName() {
		return cabinetName;
	}
	public void setCabinetName(String cabinetName) {
		this.cabinetName = cabinetName;
	}
	
	@Column(name="SALEFOR",length=255)
	public String getSalefor() {
		return salefor;
	}
	public void setSalefor(String salefor) {
		this.salefor = salefor;
	}
	
	
	@Column(name = "ZZCCLX",length=500)
	public String getZzcclx() {
		return zzcclx;
	}
	public void setZzcclx(String zzcclx) {
		this.zzcclx = zzcclx;
	}
	
	
	
}
