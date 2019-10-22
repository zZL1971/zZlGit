package com.main.bean;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import com.main.domain.mm.MaterialBujian;
import com.main.domain.mm.MaterialComplainid;
import com.main.domain.mm.MaterialHead;
import com.main.domain.mm.MaterialItem;
import com.main.domain.mm.MaterialPrice;
import com.main.domain.mm.MaterialProperty;
import com.main.domain.mm.MaterialPropertyItem;
import com.main.domain.mm.MaterialSanjianHead;
import com.main.domain.mm.MaterialSanjianItem;
import com.main.domain.mm.MyGoods;
import com.main.domain.mm.PriceCondition;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemFj;
import com.main.domain.sale.SaleItemPrice;
import com.mw.framework.domain.SysUser;

public class MaterialBean {
	private MaterialHead materialHead;
	private Set<MaterialItem>materialItems;
	private Set<MaterialProperty>materialPropertys;
	private Set<PriceCondition>priceConditions;
	private Set<MaterialPropertyItem>materialPropertyItems;
	private SaleItem saleItem;
	private List<SaleItemPrice> saleItemPrices;
	private String shouDaFang;//售达方
	
	private List<MaterialSanjianItem>materialSanjians;//散件
	private MaterialSanjianHead materialSanjianHead;
	private MaterialBujian materialBujian;//补件
	private MaterialComplainid materialComplainidFrom;//客诉报表
	private SysUser sysUser;//登陆用户	
	private SaleItemFj saleItemFj;
	private MyGoods myGoods;
	private Date bjTime;
	private List<MaterialComplainid> materialComplainid;
	private List<MaterialPrice> materialPrices;
	private String errRea;
	private String errType;
	private String errDesc;
	private String tackit;
	private String kunnr;
	private String bgOrderType;
	private String tousucishu;
	private String saleId;
	private String problem;
	public Set<MaterialItem> getMaterialItems() {
		return materialItems;
	}
	public void setMaterialItems(Set<MaterialItem> materialItems) {
		this.materialItems = materialItems;
	}
	public MaterialHead getMaterialHead() {
		return materialHead;
	}
	public void setMaterialHead(MaterialHead materialHead) {
		this.materialHead = materialHead;
	}
	public Set<PriceCondition> getPriceConditions() {
		return priceConditions;
	}
	public void setPriceConditions(Set<PriceCondition> priceConditions) {
		this.priceConditions = priceConditions;
	}
	public Set<MaterialProperty> getMaterialPropertys() {
		return materialPropertys;
	}
	public void setMaterialPropertys(Set<MaterialProperty> materialPropertys) {
		this.materialPropertys = materialPropertys;
	}
	public Set<MaterialPropertyItem> getMaterialPropertyItems() {
		return materialPropertyItems;
	}
	public void setMaterialPropertyItems(
			Set<MaterialPropertyItem> materialPropertyItems) {
		this.materialPropertyItems = materialPropertyItems;
	}
	public SaleItem getSaleItem() {
		return saleItem;
	}
	public void setSaleItem(SaleItem saleItem) {
		this.saleItem = saleItem;
	}
	public List<SaleItemPrice> getSaleItemPrices() {
		return saleItemPrices;
	}
	public void setSaleItemPrices(List<SaleItemPrice> saleItemPrices) {
		this.saleItemPrices = saleItemPrices;
	}
	public String getShouDaFang() {
		return shouDaFang;
	}
	public void setShouDaFang(String shouDaFang) {
		this.shouDaFang = shouDaFang;
	}
    public List<MaterialSanjianItem> getMaterialSanjians() {
        return materialSanjians;
    }
    public void setMaterialSanjians(List<MaterialSanjianItem> materialSanjians) {
        this.materialSanjians = materialSanjians;
    }
    public MaterialSanjianHead getMaterialSanjianHead() {
        return materialSanjianHead;
    }
    public void setMaterialSanjianHead(MaterialSanjianHead materialSanjianHead) {
        this.materialSanjianHead = materialSanjianHead;
    }


	public MaterialBujian getMaterialBujian() {
		return materialBujian;
	}
	public void setMaterialBujian(MaterialBujian materialBujian) {
		this.materialBujian = materialBujian;
	}
	public SysUser getSysUser() {
        return sysUser;
    }
    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }
    public SaleItemFj getSaleItemFj() {
        return saleItemFj;
    }
    public void setSaleItemFj(SaleItemFj saleItemFj) {
        this.saleItemFj = saleItemFj;
    }
    public MyGoods getMyGoods() {
        return myGoods;
    }
    public void setMyGoods(MyGoods myGoods) {
        this.myGoods = myGoods;
    }
    public List<MaterialPrice> getMaterialPrices() {
		return materialPrices;
	}
	public void setMaterialPrices(List<MaterialPrice> materialPrices) {
		this.materialPrices = materialPrices;
	}
	public Date getBjTime() {
		return bjTime;
	}
	public void setBjTime(Date bjTime) {
		this.bjTime = bjTime;
	}
	public List<MaterialComplainid> getMaterialComplainid() {
		return materialComplainid;
	}
	public void setMaterialComplainid(List<MaterialComplainid> materialComplainid) {
		this.materialComplainid = materialComplainid;
	}
	
	public String getTackit() {
		return tackit;
	}
	public void setTackit(String tackit) {
		this.tackit = tackit;
	}
	
	public String getErrDesc() {
		return errDesc;
	}
	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}
	public String getErrType() {
		return errType;
	}
	public void setErrType(String errType) {
		this.errType = errType;
	}
	public String getErrRea() {
		return errRea;
	}
	public void setErrRea(String errRea) {
		this.errRea = errRea;
	}
	public String getKunnr() {
		return kunnr;
	}
	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}
	public String getbgOrderType() {
		return bgOrderType;
	}
	public void setbgOrderType(String bgOrderType) {
		this.bgOrderType = bgOrderType;
	}
	public MaterialComplainid getMaterialComplainidFrom() {
		return materialComplainidFrom;
	}
	public void setMaterialComplainidFrom(MaterialComplainid materialComplainidFrom) {
		this.materialComplainidFrom = materialComplainidFrom;
	}
	public String getTousucishu() {
		return tousucishu;
	}
	public void setTousucishu(String tousucishu) {
		this.tousucishu = tousucishu;
	}
	public String getSaleId() {
		return saleId;
	}
	public void setSaleId(String saleId) {
		this.saleId = saleId;
	}
	public String getProblem() {
		return problem;
	}
	public void setProblem(String problem) {
		this.problem = problem;
	}
	
	
	
}
