package com.main.domain.mm;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.mw.framework.bean.impl.UUIDEntity;
/**
 * 补件（客服补购&免费订单）
 * @author Administrator
 *
 */
@Entity
@Table(name = "MATERIAL_BUJIAN")
public class MaterialBujian  extends UUIDEntity implements Serializable {
    /**
     * 物料id
     */
    private String materialHeadId;
    /**
     * 可用状态  用于删除标识
     * 不可用：X
     */
    private String  status;
    private String  type;
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
    
    //客户投诉信息
    // ZZTSNR ZZTSNR 投诉内容描述 CHAR 80
    private String  zztsnr;
    
    //ZZEZX ZZEZX 出错中心 char
    private String  zzezx;
    
    // ZZEZX1 ZZEZX1 出错中心1 NUMC 2
    private String  zzezx1;
    
    // ZZEZX2 ZZEZX2 出错中心2 NUMC 2
    private String  zzezx2;
    
    // ZZEBM ZZEBM 出错部门  char 
    private String  zzebm;
    
    // ZZEBM1 ZZEBM1 出错部门1 NUMC 2
    private String  zzebm1;
    
    // ZZEBM2 ZZEBM2 出错部门2 NUMC 2
    private String  zzebm2;
    
    // ZZECJ ZZECJ 出错车间  char
    private String  zzecj;
    
    // ZZECJ1 ZZECJ1 出错车间1 NUMC 2
    private String  zzecj1;
    
    // ZZECJ2 ZZECJ2 出错车间2 NUMC 2
    private String  zzecj2;
    
    // ZZESCX ZZESCX 出错生产线 char
    private String  zzescx;
    
    // ZZESCX1 ZZESCX1 出错生产线1 NUMC 2
    private String  zzescx1;
    
    // ZZESCX2 ZZESCX2 出错生产线2 NUMC 2
    private String  zzescx2;
    
    // ZZRGX ZZRGX 责任工序  char
    private String  zzrgx;
    
    // ZZRGX1 ZZRGX1 责任工序1 NUMC 2
    private String  zzrgx1;
    
    // ZZRGX2 ZZRGX2 责任工序2 NUMC 2
    private String  zzrgx2;
    
    // ZZELB ZZELB 出错类别  char
    private String  zzelb;
    
    // ZZELB1 ZZELB1 出错类别1 NUMC 2
    private String  zzelb1;
    
    // ZZELB2 ZZELB2 出错类别2 NUMC 2
    private String  zzelb2;
    
    // ZZXBMM ZZXBMM 需补材料规格 char
    private String zzxbmm;
    
    //deadline deadline 交期天数 NUMC 2
    private String deadline;
    
    //duty duty 责任人 char 
    private String duty;
    
    private String ComplaintTime;
    
    //补件barcode
    private String barcode;
    
	//出错组
    private String  zzccz;
    //产品名称
    private String cpmc;
    //出错问题
    private String zzccwt;
    //产线
    private String bgdispo;
    
    @Column(name="COMPLAINT_TIME")
	public String getComplaintTime() {
		return ComplaintTime;
	}
	public void setComplaintTime(String complaintTime) {
		ComplaintTime = complaintTime;
	}
	/**投诉内容描述 CHAR 80 **/
    @Column(name = "ZZTSNR",length=4000)
    public String getZztsnr() {
        return zztsnr;
    }
    public void setZztsnr(String zztsnr) {
        this.zztsnr = zztsnr;
    }
    
    /**出错中心1 NUMC 2 **/
    @Column(name = "ZZEZX1",length=10)
    public String getZzezx1() {
        return zzezx1;
    }
    public void setZzezx1(String zzezx1) {
        this.zzezx1 = zzezx1;
    }
    
    /**出错中心2 **/
    @Column(name = "ZZEZX2",length=10)
    public String getZzezx2() {
        return zzezx2;
    }
    public void setZzezx2(String zzezx2) {
        this.zzezx2 = zzezx2;
    }
    
    /**出错部门1 **/
    @Column(name = "ZZEBM1",length=10)
    public String getZzebm1() {
        return zzebm1;
    }
    public void setZzebm1(String zzebm1) {
        this.zzebm1 = zzebm1;
    }
    
    /** 出错部门2**/
    @Column(name = "ZZEBM2",length=10)
    public String getZzebm2() {
        return zzebm2;
    }
    public void setZzebm2(String zzebm2) {
        this.zzebm2 = zzebm2;
    }
    
    /**出错车间1 **/
    @Column(name = "ZZECJ1",length=10)
    public String getZzecj1() {
        return zzecj1;
    }
    public void setZzecj1(String zzecj1) {
        this.zzecj1 = zzecj1;
    }
    
    /**出错车间2  **/
    @Column(name = "ZZECJ2",length=10)
    public String getZzecj2() {
        return zzecj2;
    }
    public void setZzecj2(String zzecj2) {
        this.zzecj2 = zzecj2;
    }
    
    /** 出错生产线1**/
    @Column(name = "ZZESCX1",length=10)
    public String getZzescx1() {
        return zzescx1;
    }
    public void setZzescx1(String zzescx1) {
        this.zzescx1 = zzescx1;
    }
    
    /**出错生产线2 **/
    @Column(name = "ZZESCX2",length=10)
    public String getZzescx2() {
        return zzescx2;
    }
    public void setZzescx2(String zzescx2) {
        this.zzescx2 = zzescx2;
    }
    
    /** 责任工序1**/
    @Column(name = "ZZRGX1",length=10)
    public String getZzrgx1() {
        return zzrgx1;
    }
    public void setZzrgx1(String zzrgx1) {
        this.zzrgx1 = zzrgx1;
    }
    
    /**责任工序2 **/
    @Column(name = "ZZRGX2",length=10)
    public String getZzrgx2() {
        return zzrgx2;
    }
    public void setZzrgx2(String zzrgx2) {
        this.zzrgx2 = zzrgx2;
    }
    
    /** 出错类别1 **/
    @Column(name = "ZZELB1",length=10)
    public String getZzelb1() {
        return zzelb1;
    }
    public void setZzelb1(String zzelb1) {
        this.zzelb1 = zzelb1;
    }
    
    /**出错类别2 **/
    @Column(name = "ZZELB2",length=10)
    public String getZzelb2() {
        return zzelb2;
    }
    public void setZzelb2(String zzelb2) {
        this.zzelb2 = zzelb2;
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
    @Transient
    public String getLoadStatus() {
        return loadStatus;
    }
    public void setLoadStatus(String loadStatus) {
        this.loadStatus = loadStatus;
    }
    @Transient
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    @Column(name = "MATERIAL_HEAD_ID", length = 32)
    public String getMaterialHeadId() {
        return materialHeadId;
    }

    public void setMaterialHeadId(String materialHeadId) {
        this.materialHeadId = materialHeadId;
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
	
	/** 出错车间 **/
	@Column(name = "ZZECJ",length=255)
	public String getZzecj() {
		return zzecj;
	}
	public void setZzecj(String zzecj) {
		this.zzecj = zzecj;
	}
	
	/** 出错生产线 **/
	@Column(name = "ZZESCX",length=255)
	public String getZzescx() {
		return zzescx;
	}
	public void setZzescx(String zzescx) {
		this.zzescx = zzescx;
	}
	
	/** 出错工序 **/
	@Column(name = "ZZRGX",length=255)
	public String getZzrgx() {
		return zzrgx;
	}
	public void setZzrgx(String zzrgx) {
		this.zzrgx = zzrgx;
	}
	
	/** 出错类别 **/
	@Column(name = "ZZELB",length=255)
	public String getZzelb() {
		return zzelb;
	}
	public void setZzelb(String zzelb) {
		this.zzelb = zzelb;
	}
	
	/** 物料需求**/
	@Column(name = "ZZXBMM",length=4000)
	public String getZzxbmm() {
		return zzxbmm;
	}
	public void setZzxbmm(String zzxbmm) {
		this.zzxbmm = zzxbmm;
	}
	
	/** 交期天数**/
	@Column(name = "DEADLINE",length=255)
	public String getDeadline() {
		return deadline;
	}
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	
	/** 责任人**/
	@Column(name = "DUTY",length=255)
	public String getDuty() {
		return duty;
	}
	public void setDuty(String duty) {
		this.duty = duty;
	}
	
	@Column(name = "BARCODE",length=40)
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
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
	@Column(name="BGDISPO",length=255)
	public String getBgdispo() {
		return bgdispo;
	}
	public void setBgdispo(String bgdispo) {
		this.bgdispo = bgdispo;
	}
	
	
	
    
    
}
