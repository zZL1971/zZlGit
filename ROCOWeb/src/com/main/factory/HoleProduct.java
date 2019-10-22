package com.main.factory;

import java.util.List;
import java.util.Map;

public interface HoleProduct {


	 /**
	  * 锁孔两面打穿板件
	  * @param imosIdbwg
	 * @param length 
	 * @param width 
	  * @return
	  */
	Boolean holeWearPlate(List<Map<String, Object>> imosIdbwg, Double width, Double length,String grid);

	/**
	 * 槽孔相撞\孔孔相撞
	 * @param imosIdbwg
	 * @return
	 */
	Boolean holeCollide(List<Map<String, Object>> imosIdbwg,Double thickness);

	/**
	 * 板件之外
	 * @param imosIdbwg
	 * @param width
	 * @param length
	 * @param grid 
	 * @return
	 */
	Boolean holeIsOut(List<Map<String, Object>> imosIdbwg, Double width,
			Double length, String grid);

	/**
	 * 门铰引孔撞层板
	 * @param imosIdbwg
	 * @return
	 */
	Boolean holeHinge(List<Map<String, Object>> imosIdbwg);
	 
	
	
	/**
	 * 水平孔位置出错
	 * @param grid 
	 */
	Boolean holehorizontalPosition (List<Map<String, Object>> imosIdbwg,Double width,Double length, String grid);
	
	
	/**
	 * 胶粒孔位置必须>=8.8
	 * @param imosIdbwg
	 * @param width
	 * @param length
	 * @return
	 */
	Boolean colloidalParticlesHole(List<Map<String, Object>> imosIdbwg,Double width,Double length,String grid,Double thickness);
	
	
	/**
	 * 锁孔小于安全距离17MM
	 * @param imosIdbwg
	 * @param width
	 * @param length
	 * @param grid
	 * @return
	 */
	Boolean safeDistance(List<Map<String, Object>> imosIdbwg, Double width, Double length,String grid);
	/**
	 * 同一板件不能出现二合一和三合一
	 * @param imosIdbwg
	 * @param width
	 * @param length
	 * @param grid
	 * @return
	 */
	Boolean connectHole(List<Map<String, Object>> imosIdbwg, Double width, Double length,String grid);
	/**
	 * 同一边出现两种连接孔
	 * @param imosIdbwg
	 * @param width
	 * @param length
	 * @param grid
	 * @return
	 */
	Boolean twoConnectHole(List<Map<String, Object>> imosIdbwg, Double width, Double length,String grid);
	/**
	 * 中横撞门铰
	 * @param imosIdbwg
	 * @return
	 */
	Boolean zhDoorHinge(List<Map<String, Object>> imosIdbwg);
	/**
	 * 二合一层板有槽
	 * @param imosIdbwg
	 * @return
	 */
	Boolean twoinoneGroove(List<Map<String, Object>> imosIdbwg);
	/**
	 * 拉手孔撞木框螺丝
	 * @param imosIdbwg
	 * @return
	 */
	Boolean handleHoleknockScrew(List<Map<String, Object>> imosIdbwg);
}
