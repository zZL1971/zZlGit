package com.main.domain.mm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;
/**
 *孔位表 
 */
@Entity
@Table(name = "IMOS_IDBWG")
public class ImosIdbwg extends UUIDEntity implements Serializable {
    private String orderid;
    private String name1;
    private String wgname;
    
    private Integer machining; 
    private Integer wgtype; 
    private Integer machClass; 
    
    private String INFO1; 
    private String INFO2; 
    
    private Double ipX;
    private Double ipY;
    private Double ipZ;
    
    private Double orX;
    private Double orY;
    private Double orZ;
    
    private Double epX;
    private Double epY;
    private Double epZ;
    
    private Integer cnt;
    private Double length;
    private Double dia;
    private Double de;
    
    private String ortype; 
    private Integer difftype;
    
    @Column(name = "ORDERID",length=30)
    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }
    @Column(name = "NAME1",length=32)
    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }
    @Column(name = "WGNAME",length=32)
    public String getWgname() {
        return wgname;
    }

    public void setWgname(String wgname) {
        this.wgname = wgname;
    }
    @Column(name = "MACHINING")
    public Integer getMachining() {
        return machining;
    }

    public void setMachining(Integer machining) {
        this.machining = machining;
    }
    @Column(name = "WGTYPE")
    public Integer getWgtype() {
        return wgtype;
    }

    public void setWgtype(Integer wgtype) {
        this.wgtype = wgtype;
    }
    @Column(name = "MACH_CLASS")
    public Integer getMachClass() {
        return machClass;
    }

    public void setMachClass(Integer machClass) {
        this.machClass = machClass;
    }
    @Column(name = "INFO1",length=255)
    public String getINFO1() {
        return INFO1;
    }

    public void setINFO1(String iNFO1) {
        INFO1 = iNFO1;
    }
    @Column(name = "INFO2",length=255)
    public String getINFO2() {
        return INFO2;
    }

    public void setINFO2(String iNFO2) {
        INFO2 = iNFO2;
    }
    @Column(name = "ORTYPE",length=10)
    public String getOrtype() {
        return ortype;
    }

    public void setOrtype(String ortype) {
        this.ortype = ortype;
    }
    @Column(name = "DIFFTYPE")
    public Integer getDifftype() {
        return difftype;
    }

    public void setDifftype(Integer difftype) {
        this.difftype = difftype;
    }
    @Column(name = "IP_X")
    public Double getIpX() {
        return ipX;
    }

    public void setIpX(Double ipX) {
        this.ipX = ipX;
    }
    @Column(name = "IP_Y")
    public Double getIpY() {
        return ipY;
    }

    public void setIpY(Double ipY) {
        this.ipY = ipY;
    }
    @Column(name = "IP_Z")
    public Double getIpZ() {
        return ipZ;
    }

    public void setIpZ(Double ipZ) {
        this.ipZ = ipZ;
    }
    @Column(name = "OR_X")
    public Double getOrX() {
        return orX;
    }

    public void setOrX(Double orX) {
        this.orX = orX;
    }
    @Column(name = "OR_Y")
    public Double getOrY() {
        return orY;
    }

    public void setOrY(Double orY) {
        this.orY = orY;
    }
    @Column(name = "OR_Z")
    public Double getOrZ() {
        return orZ;
    }

    public void setOrZ(Double orZ) {
        this.orZ = orZ;
    }
    @Column(name = "EP_X")
    public Double getEpX() {
        return epX;
    }

    public void setEpX(Double epX) {
        this.epX = epX;
    }
    @Column(name = "EP_Y")
    public Double getEpY() {
        return epY;
    }

    public void setEpY(Double epY) {
        this.epY = epY;
    }
    @Column(name = "EP_Z")
    public Double getEpZ() {
        return epZ;
    }

    public void setEpZ(Double epZ) {
        this.epZ = epZ;
    }
    @Column(name = "LENGTH")
    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }
    @Column(name = "DIA")
    public Double getDia() {
        return dia;
    }

    public void setDia(Double dia) {
        this.dia = dia;
    }
    @Column(name = "DE")
    public Double getDe() {
        return de;
    }

    public void setDe(Double de) {
        this.de = de;
    }
    @Column(name = "CNT")
    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    } 
}
