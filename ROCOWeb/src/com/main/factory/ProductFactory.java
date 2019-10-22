package com.main.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.main.process.Carcase;
/**
 * 转接工艺路线数据
 * @author Chaly
 *
 */
public class ProductFactory {

	private Carcase carcase;
	
	private Map<String,Object> imosIdbext;
	
	private List<Map<String,Object>> imosIdbwg;
	public ProductFactory(Map<String, Object> imosIdbext2,
			List<Map<String, Object>> imosIdbwg2) {
		this.imosIdbext=imosIdbext2;
		this.imosIdbwg=imosIdbwg2;
		this.resolve();
	}

	/**
	 * 
	 * @param carcase
	 */
	public void cabinet(){
		this.setCarcase(new Carcase());
	}

	public Carcase getCarcase() {
		return carcase;
	}

	public void setCarcase(Carcase carcase) {
		this.carcase = carcase;
	}
	
	public Map<String,Object> getImosIdbext() {
		return imosIdbext;
	}
	public void setImosIdbext(Map<String,Object> imosIdbext) {
		this.imosIdbext = imosIdbext;
	}

	public List<Map<String,Object>> getImosIdbwg() {
		return imosIdbwg;
	}

	public void setImosIdbwg(List<Map<String,Object>> imosIdbwg) {
		this.imosIdbwg = imosIdbwg;
	}
	public void resolve(){
		List<Map<String, Object>> imosWg = this.getImosIdbwg();
		for (Map<String, Object> map : imosWg) {
			String ortype = String.valueOf(map.get("ORTYPE"));
			
			if("H".equals(ortype)){
				double ip_x = Double.parseDouble(String.valueOf(map.get("IP_X")));
				double ip_y = Double.parseDouble(String.valueOf(map.get("IP_Y")));
				this.setIpXList(ip_x);
				this.setIpYList(ip_y);
			}
			if("V".equals(ortype)){
				int machining = Integer.parseInt(String.valueOf(map.get("MACHINING")));
				double dia = Double.parseDouble(String.valueOf(map.get("DIA")));
				double de = Double.parseDouble(String.valueOf(map.get("DE")));
				double epy = Double.parseDouble(String.valueOf(map.get("EP_Y")));
				double epx = Double.parseDouble(String.valueOf(map.get("EP_X")));
				double orz = Double.parseDouble(String.valueOf(map.get("OR_Z")));
				if(machining==2){
					this.setMachiningList(machining);
					this.setDiaList(dia);
					this.setDeList(de);
					this.setEpYList(epy);
					this.setEpXList(epx);
					this.setOrZList(orz);
				}
				
			}
		}
	}
	private List<Double> orXList = new ArrayList<Double>();;
	private List<Double> orZList = new ArrayList<Double>();;
	private List<String> orTypeList = new ArrayList<String>();;
	private List<Double> ipXList = new ArrayList<Double>();;
	private List<Double> ipYList = new ArrayList<Double>();;
	private List<Double> epYList = new ArrayList<Double>();;
	private List<Double> epXList = new ArrayList<Double>();;
	private List<Double> deList = new ArrayList<Double>();;
	private List<Double> diaList = new ArrayList<Double>();;
	private List<Double> erXList = new ArrayList<Double>();;
	private List<Double> erYList = new ArrayList<Double>();;
	private List<Integer> machiningList = new ArrayList<Integer>();;
	public List<Double> getOrXList() {
		return orXList;
	}

	public void setOrXList(Double orXList) {
		this.orXList.add(orXList);
		HashSet<Double> h = new HashSet<Double>(this.orXList);
		this.orXList.clear();
		this.orXList.addAll(h);	
		
		/*if(this.orXList.size()==0){
			this.orXList.add(orXList);
		}else{
			for (int i = 0; i < this.orXList.size(); i++) {
				Double result = this.orXList.get(i);
				if(!(result.equals(orXList))){
					this.orXList.add(orXList);
				}
			}
		}*/
		
	}

	public List<Double> getOrZList() {
		return orZList;
	}

	public void setOrZList(Double orZList) {
		this.orZList.add(orZList);
		HashSet<Double> h = new HashSet<Double>(this.orZList);
		this.orZList.clear();
		this.orZList.addAll(h);		
		
		/*if(this.orZList.size()==0){
			this.orZList.add(orZList);
		}else{
			for (int i = 0; i < this.orZList.size(); i++) {
				Double result = this.orZList.get(i);
				if(!(result.equals(orZList))){
					this.orZList.add(orZList);
				}
			}
		}*/
	}

	public List<String> getOrTypeList() {
		return orTypeList;
	}

	public void setOrTypeList(String orTypeList) {
		this.orTypeList.add(orTypeList);
		HashSet<String> h = new HashSet<String>(this.orTypeList);
		this.orTypeList.clear();
		this.orTypeList.addAll(h);
		
		/*if(this.orTypeList.size()==0){
			this.orTypeList.add(orTypeList);
		}else{
			for (int i = 0; i < this.orTypeList.size(); i++) {
				String result = this.orTypeList.get(i);
				if(!(orTypeList.equals(result))){
					this.orTypeList.add(orTypeList);
				}
			}
		}*/
	}

	public List<Double> getIpXList() {
		return ipXList;
	}

	public void setIpXList(Double ipXList) {
		this.ipXList.add(ipXList);
		HashSet<Double> h = new HashSet<Double>(this.ipXList);
		this.ipXList.clear();
		this.ipXList.addAll(h);
		
		/*if(this.ipXList.size()==0){
			this.ipXList.add(ipXList);
		}else{
			for (int i = 0; i < this.ipXList.size(); i++) {
				Double result = this.ipXList.get(i);
				if(!(ipXList.equals(result))){
					this.ipXList.add(ipXList);
				}
			}
		}*/
		
	}

	public List<Double> getIpYList() {
		return ipYList;
	}

	public void setIpYList(Double ipYList) {
		this.ipYList.add(ipYList);
		HashSet<Double> h = new HashSet<Double>(this.ipYList);
		this.ipYList.clear();
		this.ipYList.addAll(h);
		
		/*if(this.ipYList.size()==0){
			this.ipYList.add(ipYList);
		}else{
			for (int i = 0; i < this.ipYList.size(); i++) {
				Double result = this.ipYList.get(i);
				if(!(ipYList.equals(result))){
					this.ipYList.add(ipYList);
				}
			}
		}*/
	}

	public List<Double> getEpYList() {
		return epYList;
	}

	public void setEpYList(Double epYList) {
		this.epYList.add(epYList);
		HashSet<Double> h = new HashSet<Double>(this.epYList);
		this.epYList.clear();
		this.epYList.addAll(h);
		
		/*if(this.epYList.size()==0){
			this.epYList.add(epYList);
		}else{
			for (int i = 0; i < this.epYList.size(); i++) {
				Double result = this.epYList.get(i);
				if(!(epYList.equals(result))){
					this.epYList.add(epYList);
				}
			}
		}*/
	}

	public List<Double> getEpXList() {
		return epXList;
	}

	public void setEpXList(Double epXList) {
		this.epXList.add(epXList);
		HashSet<Double> h = new HashSet<Double>(this.epXList);
		this.epXList.clear();
		this.epXList.addAll(h);
		
	/*	if(this.epXList.size()==0){
			this.epXList.add(epXList);
		}else{
			for (int i = 0; i < this.epXList.size(); i++) {
				Double result = this.epXList.get(i);
				if(!(epXList.equals(result))){
					this.epXList.add(epXList);
				}
			}
		}*/
	}

	public List<Double> getDeList() {
		return deList;
	}

	public void setDeList(Double deList) {
		this.deList.add(deList);
		HashSet<Double> h = new HashSet<Double>(this.deList);
		this.deList.clear();
		this.deList.addAll(h);
/*		if(this.deList.size()==0){
			this.deList.add(deList);
		}else{
			for (int i = 0; i < this.deList.size(); i++) {
				Double result = this.deList.get(i);
				if(!(deList.equals(result))){
					this.deList.add(deList);
				}
			}
		}*/
	}

	public List<Double> getDiaList() {
		return diaList;
	}

	public void setDiaList(Double diaList) {
		this.diaList.add(diaList);
		HashSet<Double> h = new HashSet<Double>(this.diaList);
		this.diaList.clear();
		this.diaList.addAll(h);
	}

	public List<Double> getErXList() {
		return erXList;
	}

	public void setErXList(Double erXList) {
		this.erXList.add(erXList);
		HashSet<Double> h = new HashSet<Double>(this.erXList);
		this.erXList.clear();
		this.erXList.addAll(h);
		
		/*if(this.erXList.size()==0){
			this.erXList.add(erXList);
		}else{
			for (int i = 0; i < this.erXList.size(); i++) {
				Double result = this.erXList.get(i);
				if(!(erXList.equals(result))){
					this.erXList.add(erXList);
				}
			}
		}*/
	}

	public List<Double> getErYList() {
		return erYList;
	}

	public void setErYList(Double erYList) {
		this.erYList.add(erYList);
		HashSet<Double> h = new HashSet<Double>(this.erYList);
		this.erYList.clear();
		this.erYList.addAll(h);
		
		/*if(this.erYList.size()==0){
			this.erYList.add(erYList);
		}else{
			for (int i = 0; i < this.erYList.size(); i++) {
				Double result = this.erYList.get(i);
				if(!(erYList.equals(result))){
					this.erYList.add(erYList);
				}
			}
		}*/
	}

	public List<Integer> getMachiningList() {
		return machiningList;
	}

	public void setMachiningList(Integer machiningList) {
		this.machiningList.add(machiningList);
		HashSet<Integer> h = new HashSet<Integer>(this.machiningList);
		this.machiningList.clear();
		this.machiningList.addAll(h);
		
		/*this.machiningList.add(machiningList);
		if(this.machiningList.size()==0){
			this.machiningList.add(machiningList);
		}else{
			for (int i = 0; i < this.machiningList.size(); i++) {
				Integer result = this.machiningList.get(i);
				if(!(machiningList.equals(result))){
					this.machiningList.add(machiningList);
				}
			}
		}*/
	}
}
