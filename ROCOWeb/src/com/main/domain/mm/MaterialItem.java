package com.main.domain.mm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;
/**
 * 物料信息-- 移门
 * @author Administrator
 *
 */
@Entity
@Table(name = "MATERIAL_ITEM")
public class MaterialItem  extends UUIDEntity implements Serializable {
    //订单编号
	public String orderid;
	
	public String artDescript1;
	
	//类型
	public int typ;
	
	//板件类型
	public int parttype;
	
	//
	public String comment1;
	public String comment2;
	
	//名称
	public String name;
	
	//名称2
	public String name2;
	
	
	public String kp;
	public String bta;
	
	//长度
	public Float length;
	
	//宽度
	public Float width;
	
	//厚度
	public Float thickness;
	
	//
	public Float clength;
	public Float cwidth;
	public Float cthickness;
	
	//父ID
	public String parentid;
	
	//单元的号码
	public String cnt;
	
	//产品ID
	public String articleId;
	
	//标号
	public String barcode;
	
	//异行分类
	public String ispec;
	
	//栅格
	public String grid;
	
	//材料纹理
	public Float gror;
	
	//边的数量
	public int edgeId;
	
	//边修休整
	public String edgeTrans;
	
	//表面休整
	public String surfTrans;
	
	//价格
	public Float price;
	
	//所在订单位置（行号）
	public String orderpos;
	
	//重量
	public Float weight;
	
	//批量
	public String idSerie;
	
	//文本
	public String idText;
	
	//程序编号
	public String idNcno;
	
	//NC_标记
	public int ncFlag;
	
	//物料清单标记
	public int bomFlag;
	
	//开料标记
	public int cutFlag;
	
	//信息1
	public String info1;
	
	//信息2
	public String info2;
	
	//信息3
	public String info3;
	
	//信息4
	public String info4;
	
	//信息5
	public String info5;
	
	//汇总核对
	public String checksum2;
	
	//颜色1
	public String color1;
	
	//颜色2
	public String color2;
	
	private MaterialHead materialHead;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HEADID",referencedColumnName="ID")
	public MaterialHead getMaterialHead() {
		return materialHead;
	}
	public void setMaterialHead(MaterialHead materialHead) {
		this.materialHead = materialHead;
	}
	
	@Column(name = "ARTDESCRIPT1",length=30)
	public String getArtDescript1() {
		return artDescript1;
	}
	public void setArtDescript1(String artDescript1) {
		this.artDescript1 = artDescript1;
	}
	@Column(name = "TYP")
	public int getTyp() {
		return typ;
	}
	public void setTyp(int typ) {
		this.typ = typ;
	}
	@Column(name = "PARTTYPE",length=30)
	public int getParttype() {
		return parttype;
	}
	public void setParttype(int parttype) {
		this.parttype = parttype;
	}
	@Column(name = "COMMENT1",length=30)
	public String getComment1() {
		return comment1;
	}
	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}
	@Column(name = "COMMENT2",length=30)
	public String getComment2() {
		return comment2;
	}
	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}
	@Column(name = "NAME",length=30)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "NAME2",length=30)
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
	@Column(name = "KP",length=30)
	public String getKp() {
		return kp;
	}
	public void setKp(String kp) {
		this.kp = kp;
	}
	@Column(name = "BTA",length=30)
	public String getBta() {
		return bta;
	}
	public void setBta(String bta) {
		this.bta = bta;
	}
	@Column(name = "LENGTH")
	public Float getLength() {
		return length;
	}
	public void setLength(Float length) {
		this.length = length;
	}
	@Column(name = "WIDTH")
	public Float getWidth() {
		return width;
	}
	public void setWidth(Float width) {
		this.width = width;
	}
	@Column(name = "THICKNESS")
	public Float getThickness() {
		return thickness;
	}
	public void setThickness(Float thickness) {
		this.thickness = thickness;
	}
	@Column(name = "CLENGTH")
	public Float getClength() {
		return clength;
	}
	public void setClength(Float clength) {
		this.clength = clength;
	}
	@Column(name = "cwidth")
	public Float getCwidth() {
		return cwidth;
	}
	public void setCwidth(Float cwidth) {
		this.cwidth = cwidth;
	}
	@Column(name = "CTHICKNESS")
	public Float getCthickness() {
		return cthickness;
	}
	public void setCthickness(Float cthickness) {
		this.cthickness = cthickness;
	}
	@Column(name = "PARENTID",length=2)
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	@Column(name = "CNT")
	public String getCnt() {
		return cnt;
	}
	public void setCnt(String cnt) {
		this.cnt = cnt;
	}
	@Column(name = "ARTICLE_ID",length=36)
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	@Column(name = "BARCODE",length=12)
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	@Column(name = "ISPEC",length=3)
	public String getIspec() {
		return ispec;
	}
	public void setIspec(String ispec) {
		this.ispec = ispec;
	}
	@Column(name = "GRID",length=3)
	public String getGrid() {
		return grid;
	}
	public void setGrid(String grid) {
		this.grid = grid;
	}
	@Column(name = "GROR")
	public Float getGror() {
		return gror;
	}
	public void setGror(Float gror) {
		this.gror = gror;
	}

	@Column(name = "ORDERID")
	public String getOrderid() {
        return orderid;
    }
    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }
    @Column(name = "EDGE_ID")
    public int getEdgeId() {
        return edgeId;
    }
    public void setEdgeId(int edgeId) {
        this.edgeId = edgeId;
    }
    @Column(name = "EDGE_TRANS")
    public String getEdgeTrans() {
        return edgeTrans;
    }
    public void setEdgeTrans(String edgeTrans) {
        this.edgeTrans = edgeTrans;
    }
    @Column(name = "SURF_TRANS")
    public String getSurfTrans() {
        return surfTrans;
    }
    public void setSurfTrans(String surfTrans) {
        this.surfTrans = surfTrans;
    }
    @Column(name = "PRICE")
    public Float getPrice() {
        return price;
    }
    public void setPrice(Float price) {
        this.price = price;
    }
    @Column(name = "ORDERPOS")
    public String getOrderpos() {
        return orderpos;
    }
    public void setOrderpos(String orderpos) {
        this.orderpos = orderpos;
    }
    @Column(name = "WEIGHT")
    public Float getWeight() {
        return weight;
    }
    public void setWeight(Float weight) {
        this.weight = weight;
    }
    @Column(name = "ID_SERIE")
    public String getIdSerie() {
        return idSerie;
    }
    public void setIdSerie(String idSerie) {
        this.idSerie = idSerie;
    }
    @Column(name = "ID_TEXT")
    public String getIdText() {
        return idText;
    }
    public void setIdText(String idText) {
        this.idText = idText;
    }
    @Column(name = "ID_NCNO")
    public String getIdNcno() {
        return idNcno;
    }
    public void setIdNcno(String idNcno) {
        this.idNcno = idNcno;
    }
    @Column(name = "NC_FLAG")
    public int getNcFlag() {
        return ncFlag;
    }
    public void setNcFlag(int ncFlag) {
        this.ncFlag = ncFlag;
    }
    @Column(name = "BOM_FLAG")
    public int getBomFlag() {
        return bomFlag;
    }
    public void setBomFlag(int bomFlag) {
        this.bomFlag = bomFlag;
    }
    @Column(name = "CUT_FLAG")
    public int getCutFlag() {
        return cutFlag;
    }
    public void setCutFlag(int cutFlag) {
        this.cutFlag = cutFlag;
    }
    @Column(name = "INFO1",length=80)
	public String getInfo1() {
		return info1;
	}
	public void setInfo1(String info1) {
		this.info1 = info1;
	}
	
	@Column(name = "INFO2",length=80)
	public String getInfo2() {
		return info2;
	}
	public void setInfo2(String info2) {
		this.info2 = info2;
	}
	
	@Column(name = "INFO3",length=80)
	public String getInfo3() {
		return info3;
	}
	public void setInfo3(String info3) {
		this.info3 = info3;
	}
	
	@Column(name = "INFO4",length=80)
	public String getInfo4() {
		return info4;
	}
	public void setInfo4(String info4) {
		this.info4 = info4;
	}
	
	@Column(name = "INFO5",length=80)
	public String getInfo5() {
		return info5;
	}
	public void setInfo5(String info5) {
		this.info5 = info5;
	}
	
	@Column(name = "CHECKSUM2",length=80)
	public String getChecksum2() {
		return checksum2;
	}
	public void setChecksum2(String checksum2) {
		this.checksum2 = checksum2;
	}
	
	@Column(name = "COLOR1",length=64)
	public String getColor1() {
		return color1;
	}
	public void setColor1(String color1) {
		this.color1 = color1;
	}
	
	@Column(name = "COLOR2",length=64)
	public String getColor2() {
		return color2;
	}
	public void setColor2(String color2) {
		this.color2 = color2;
	}
	
}
