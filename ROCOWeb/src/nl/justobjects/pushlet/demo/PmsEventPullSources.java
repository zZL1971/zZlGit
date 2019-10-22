package nl.justobjects.pushlet.demo;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mw.framework.bean.Constants;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.utils.ZStringUtils;

import net.sf.json.JSONArray;
import nl.justobjects.pushlet.core.Event;
import nl.justobjects.pushlet.core.EventPullSource;
import nl.justobjects.pushlet.core.Session;
import nl.justobjects.pushlet.core.SessionManager;

/**
 * 
 * @author samguo
 * 
 */
@Controller
public class PmsEventPullSources implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4455393746521069738L;

	public static class BaoxiuEvent extends EventPullSource {

		@Override
		protected long getSleepTime() {
			return 5000; // 刷新时间
		}

		@Override
		protected Event pullEvent() {

			// CommonManager commonManager =
			// SpringContextHolder.getBean("commonManager");
			JdbcTemplate JdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");

			List<Map<String, Object>> queryForList = JdbcTemplate
					.queryForList("select t.id,t.receive_user,t.msg_body from sys_mes_send t where t.has_readed='0'");

			// 事件标识 //注意：此处”/pms/bgService”将对应页面js代码中的PjoinListen中的参数
			Event event = Event.createDataEvent("/pms/bgService");

			// 获取当前登陆用户Id(加入事件订阅的用户)
			Session[] sessions = SessionManager.getInstance().getSessions();

			// 查询当前用户的任务
			for (int i = 0; i < sessions.length; i++) {
				int count = 0;
				//StringBuffer sb = new StringBuffer();
				List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
				for (Map<String, Object> map : queryForList) {
					if (ZStringUtils.isNotEmpty(map.get("RECEIVE_USER"))
							&& map.get("RECEIVE_USER").toString().equals(
									sessions[i].getId())) {
						count++;
						
						//String data = (String) map.get("MSG_BODY");
						
						//sb.append(data+"^_^");
						
						dataList.add(map);
					}
					 
				}
					
                try {
                	String data = "";
                	//String data = sb.substring(0, sb.lastIndexOf("^_^"));
                	
                	data = new String(JSONArray.fromObject(dataList).toString().getBytes("UTF-8"),"ISO-8859-1");
	                	/*if(dataList.size()>0 && dataList.size()<4){
	            		data = JSONArray.fromObject(dataList).toString();
	            	}else{
	            		data = "你有"+dataList.size()+"条新的消息，请注意查收！";
	            	}*/
                    event.setField(sessions[i].getId(),count);// 封装参数
     				event.setField("content",data);
                }  catch  (UnsupportedEncodingException e) {
                     e.printStackTrace();
                }
				
				
			}

			return event;

		}
	}
}
