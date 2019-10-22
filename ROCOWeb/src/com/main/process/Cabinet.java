package com.main.process;

public class Cabinet {

	private String tec_side;//面
	private String tec_groove;//槽
	private String tec_xlk;//铣孔  满足 不是H0 并且不为空 就是能做的 tec_xlk!=null&&!"H0".equals()
	private String tec_trough;//通槽
	private String tec_pore;//孔
	private double cwidth;//宽
	private double clength;//长
	/**
	 * ABL220(二部)
	 * @return
	 */
	public String cabinetABL2202(){
		String flg=null;
		if((this.cwidth>=200&&this.cwidth<=800)&&this.clength>=250){
			if(this.tec_side!=null){
				if("V5.5".indexOf(this.tec_groove)!=-1
						&&"V11.2".indexOf(this.tec_groove)!=-1
						){
					if("H0".equals(this.tec_xlk)||this.tec_xlk==null){
						if(this.tec_trough!=null){
							if(!("D".indexOf(this.tec_pore)!=-1)){
								flg="ABL220(2)";
							}
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * HBX500(二部)
	 * @return
	 */
	public String cabinetHBX5002(){
		String flg=null;
		if((this.cwidth>=70&&this.cwidth<=650)&&this.clength>=200){
			if(this.tec_side!=null){
				if("V5.5".equals(this.tec_groove)){
					if(!("H0".equals(this.tec_xlk))||this.tec_xlk!=null){
						if(this.tec_trough!=null){
							if("D".indexOf(this.tec_pore)!=-1){
								flg="HBX500(2)";
							}
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * PTP160(二部)
	 * @return
	 */
	public String cabinetPTP1602(){
		String flg=null;
		if((this.cwidth>=180&&this.cwidth<=1200)&&this.clength>=300){
			if(this.tec_side!=null){
				if("V5.5".indexOf(this.tec_groove)!=-1
						&&"V11.2".indexOf(this.tec_groove)!=-1
						){
					if(!("H0".equals(this.tec_xlk))||this.tec_xlk!=null){
						if(this.tec_trough!=null){
							if("D".indexOf(this.tec_pore)!=-1){
								flg="PTP160(2)";
							}
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * 手排钻(二部)
	 * @return
	 */
	public String cabinetShouPeiZhuan2(){
		String flg=null;
		if(this.cwidth<70&&this.clength<250){
			if(this.tec_side!=null){
				if("V0".equals(this.tec_groove)||this.tec_groove==null){
					if("".equals(this.tec_xlk)||("H0".equals(this.tec_xlk))){
						if(!(this.tec_trough!=null)){
							if("D".indexOf(this.tec_pore)!=-1){
								flg="手排钻(2)";
							}
						}
					}	
				}
			}
		}
		return flg;
	}
	/**
	 * CNC拉槽(二部)
	 * @return
	 */
	public String cabinetCNCLaCao2(){
		String flg=null;
		if(!(this.tec_side!=null)){
			if("V0".equals(this.tec_groove)||this.tec_groove==null){
				if(this.tec_xlk==null||("H0".equals(this.tec_xlk))){
					if("SA".indexOf(this.tec_trough)==-1||"SB".indexOf(this.tec_trough)==-1){//SOL
						if(!("D".indexOf(this.tec_pore)!=-1)){
							flg="CNC拉槽(2)";
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * 吊锣拉槽(二部)
	 * @return
	 */
	public String cabinetDiaoLuoLaCao2(){
		String flg=null;
		if(!(this.tec_side!=null)){
			if("H2".equals(this.tec_groove)
					){
				if(this.tec_xlk==null||("H0".equals(this.tec_xlk))){
					if("TA".indexOf(this.tec_trough)!=-1||"TB".indexOf(this.tec_trough)!=-1){
						if(!("D".indexOf(this.tec_pore)!=-1)){
							flg="吊锣拉槽(2)";
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * FT2(一部)
	 * @return
	 */
	public String cabinetFT21(){
		String flg=null;
		if((this.cwidth>=297&&this.cwidth<=630)&&this.clength>=300){
			if(this.tec_side!=null){
				if("V0".equals(this.tec_groove)||"V10".equals(this.tec_groove)||!(this.tec_groove!=null)){
					if(this.tec_xlk==null||"H0".equals(this.tec_xlk)){
						if(this.tec_trough!=null){
							if(!("D".indexOf(this.tec_pore)!=-1)){
								flg="FT2(1)";
							}
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * Skipper130(一部)
	 * @return
	 */
	public String cabinetSkipper1301(){
		String flg=null;
		if((this.cwidth>=100&&this.cwidth<=800)&&this.clength>=340){
			if(this.tec_side!=null){
				if("V5.5".equals(this.tec_groove)){
					if(this.tec_xlk==null||"H0".equals(this.tec_xlk)){
						if(this.tec_trough!=null){
							if("D".indexOf(this.tec_pore)!=-1){
								flg="Skipper130(1)";
							}
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * Skipper100(一部)
	 * @return
	 */
	public String cabinetSkipper1001(){
		String flg=null;
		if((this.cwidth>=200&&this.cwidth<=700)&&this.clength>=340){
			if(this.tec_side!=null){
				if("V5.5".equals(this.tec_groove)){
					if(this.tec_xlk==null||"H0".equals(this.tec_xlk)){
						if(this.tec_trough!=null){
							if("D".indexOf(this.tec_pore)!=-1){
								flg="Skipper100(1)";
							}
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * PTP160(一部)
	 * @return
	 */
	public String cabinetPTP1601_1(){
		String flg=null;
		if((this.cwidth>=180&&this.cwidth<=1200)&&this.clength>=300){
			if(this.tec_side!=null){
				if("V5.5".indexOf(this.tec_groove)!=-1
						&&"V11.2".indexOf(this.tec_groove)!=-1
						){
					if(!("H0".equals(this.tec_xlk))){
						if(this.tec_trough!=null){
							if("D".indexOf(this.tec_pore)!=-1){
								flg="PTP160(1)";
							}
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * PTP160(一部)
	 * @return
	 */
	public String cabinetPTP1601_2(){
		String flg=null;
		if((this.cwidth>=180&&this.cwidth<=1200)&&this.clength>=300){
			if(this.tec_side!=null){
				if("V5.5".indexOf(this.tec_groove)!=-1
						&&"V11.2".indexOf(this.tec_groove)!=-1
						){
					if(this.tec_xlk==null||("H0".equals(this.tec_xlk))){
						if(this.tec_trough!=null){
							if("D".indexOf(this.tec_pore)!=-1){
								flg="PTP160(1)";
							}
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * HBX050(一部)
	 * @return
	 */
	public String cabinetHBX0501(){
		String flg=null;
		if((this.cwidth>=70&&this.cwidth<=340)&&this.clength>=200){
			if(this.tec_side!=null){
				if("V5.5".equals(this.tec_groove)){
					if(this.tec_xlk==null||("H0".equals(this.tec_xlk))){
						if(this.tec_trough!=null){
							if(!("D".indexOf(this.tec_pore)!=-1)){
								flg="HBX050(1)";
							}
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * HBX200(一部)
	 * @return
	 */
	public String cabinetHBX2001(){
		String flg=null;
		if((this.cwidth>=40&&this.cwidth<=340)&&this.clength>=200){
			if(this.tec_side!=null){
				if("V5.5".equals(this.tec_groove)){
					if(this.tec_xlk==null||("H0".equals(this.tec_xlk))){
						if(this.tec_trough!=null){
							if(!("D".indexOf(this.tec_pore)!=-1)){
								flg="HBX200(1)";
							}
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * 手排钻(一部)
	 * @return
	 */
	public String cabinetShouPaiZhuan1(){
		String flg=null;
		if(this.cwidth<70&&this.clength<250){
			if(this.tec_side!=null){
				if("V0".equals(this.tec_groove)||this.tec_groove==null){
					if(this.tec_xlk==null||("H0".equals(this.tec_xlk))){
						if(!(this.tec_trough!=null)){
							if("D".indexOf(this.tec_pore)!=-1){
								flg="手排钻(1)";
							}
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * CNC拉槽(一部)
	 * @return
	 */
	public String cabinetCNCLaCao1(){
		String flg=null;
		if(!(this.tec_side!=null)){
			if("V0".equals(this.tec_groove)||this.tec_groove==null){
				if(this.tec_xlk==null||("H0".equals(this.tec_xlk))){
					if("SA".indexOf(this.tec_trough)==-1||"SB".indexOf(this.tec_trough)==-1){//SOL
						if(!("D".indexOf(this.tec_pore)!=-1)){
							flg="CNC拉槽(1)";
						}
					}
				}
			}
		}
		return flg;
	}
	/**
	 * 吊锣拉槽(一部)
	 * @return
	 */
	public String cabinetDiaoLuoLaCao1(){
		String flg=null;
		if(!(this.tec_side!=null)){
			if("H2".equals(this.tec_groove)
					){
				if(this.tec_xlk==null||("H0".equals(this.tec_xlk))){
					if("SA".indexOf(this.tec_trough)!=-1||"SB".indexOf(this.tec_trough)!=-1){
						if(!("D".indexOf(this.tec_pore)!=-1)){
							flg="吊锣拉槽(1)";
						}
					}
				}
			}
		}
		return flg;
	}
	public Cabinet(String tec_side, String tec_groove, String tec_xlk,
			String tec_trough, String tec_pore, double cwidth, double clength) {
		super();
		this.tec_side = tec_side;
		this.tec_groove = tec_groove;
		this.tec_xlk = tec_xlk;
		this.tec_trough = tec_trough;
		this.tec_pore = tec_pore;
		this.cwidth = cwidth;
		this.clength = clength;
	}
	public String getTec_side() {
		return tec_side;
	}
	public void setTec_side(String tec_side) {
		this.tec_side = tec_side;
	}
	public String getTec_groove() {
		return tec_groove;
	}
	public void setTec_groove(String tec_groove) {
		this.tec_groove = tec_groove;
	}
	public String getTec_xlk() {
		return tec_xlk;
	}
	public void setTec_xlk(String tec_xlk) {
		this.tec_xlk = tec_xlk;
	}
	public String getTec_trough() {
		return tec_trough;
	}
	public void setTec_trough(String tec_trough) {
		this.tec_trough = tec_trough;
	}
	public String getTec_pore() {
		return tec_pore;
	}
	public void setTec_pore(String tec_pore) {
		this.tec_pore = tec_pore;
	}
	public double getCwidth() {
		return cwidth;
	}
	public void setCwidth(double cwidth) {
		this.cwidth = cwidth;
	}
	public double getClength() {
		return clength;
	}
	public void setClength(double clength) {
		this.clength = clength;
	}

	
}
