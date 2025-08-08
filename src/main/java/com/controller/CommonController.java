package com.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.IgnoreAuth;
import com.baidu.aip.face.AipFace;
import com.baidu.aip.face.MatchRequest;
import com.baidu.aip.util.Base64Util;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.ConfigEntity;
import com.service.CommonService;
import com.service.ConfigService;
import com.utils.BaiduUtil;
import com.utils.FileUtil;
import com.utils.R;
/**
 * 通用接口
 */
@RestController
public class CommonController{
	@Autowired
	private CommonService commonService;

    private static AipFace client = null;
    
    @Autowired
    private ConfigService configService;    
	/**
	 * 获取table表中的column列表(联动接口)
	 * @param table
	 * @param column
	 * @return
	 */
	@IgnoreAuth
	@RequestMapping("/option/{tableName}/{columnName}")
	public R getOption(@PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName,String level,String parent) {
		// 创建一个Map对象，用于存储参数
		Map<String, Object> params = new HashMap<String, Object>();
		// 将tableName和columnName存入params中
		params.put("table", tableName);
		params.put("column", columnName);
		// 如果level不为空，则将level存入params中
		if(StringUtils.isNotBlank(level)) {
			params.put("level", level);
		}
		// 如果parent不为空，则将parent存入params中
		if(StringUtils.isNotBlank(parent)) {
			params.put("parent", parent);
		}
		// 调用commonService的getOption方法，获取data
		List<String> data = commonService.getOption(params);
		// 返回R对象，将data存入data中
		return R.ok().put("data", data);
	}
	
	/**
	 * 根据table中的column获取单条记录
	 * @param table
	 * @param column
	 * @return
	 */
	@IgnoreAuth
	@RequestMapping("/follow/{tableName}/{columnName}")
	public R getFollowByOption(@PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName, @RequestParam String columnValue) {
		// 创建一个Map对象，用于存储参数
		Map<String, Object> params = new HashMap<String, Object>();
		// 将tableName、columnName和columnValue存入params中
		params.put("table", tableName);
		params.put("column", columnName);
		params.put("columnValue", columnValue);
		// 调用commonService的getFollowByOption方法，获取result
		Map<String, Object> result = commonService.getFollowByOption(params);
		// 返回R对象，将result存入data中
		return R.ok().put("data", result);
	}
	
	/**
	 * 修改table表的sfsh状态
	 * @param table
	 * @param map
	 * @return
	 */
	@RequestMapping("/sh/{tableName}")
	public R sh(@PathVariable("tableName") String tableName, @RequestBody Map<String, Object> map) {
		// 将tableName存入map中
		map.put("table", tableName);
		// 调用commonService的sh方法
		commonService.sh(map);
		// 返回R对象
		return R.ok();
	}
	
	/**
	 * 获取需要提醒的记录数
	 * @param tableName
	 * @param columnName
	 * @param type 1:数字 2:日期
	 * @param map
	 * @return
	 */
	@IgnoreAuth
	@RequestMapping("/remind/{tableName}/{columnName}/{type}")
	public R remindCount(@PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName, 
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		// 将tableName、columnName和type存入map中
		map.put("table", tableName);
		map.put("column", columnName);
		map.put("type", type);
		
		// 如果type为2，则进行日期转换
		if(type.equals("2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			Date remindStartDate = null;
			Date remindEndDate = null;
			// 如果remindstart不为空，则进行日期转换
			if(map.get("remindstart")!=null) {
				Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
				c.setTime(new Date()); 
				c.add(Calendar.DAY_OF_MONTH,remindStart);
				remindStartDate = c.getTime();
				map.put("remindstart", sdf.format(remindStartDate));
			}
			// 如果remindend不为空，则进行日期转换
			if(map.get("remindend")!=null) {
				Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindEnd);
				remindEndDate = c.getTime();
				map.put("remindend", sdf.format(remindEndDate));
			}
		}
		
		// 调用commonService的remindCount方法，获取count
		int count = commonService.remindCount(map);
		// 返回R对象，将count存入count中
		return R.ok().put("count", count);
	}
	
	/**
	 * 单列求和
	 */
	@IgnoreAuth
	@RequestMapping("/cal/{tableName}/{columnName}")
	public R cal(@PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName) {
		// 创建一个Map对象，用于存储参数
		Map<String, Object> params = new HashMap<String, Object>();
		// 将tableName和columnName存入params中
		params.put("table", tableName);
		params.put("column", columnName);
		// 调用commonService的selectCal方法，获取result
		Map<String, Object> result = commonService.selectCal(params);
		// 返回R对象，将result存入data中
		return R.ok().put("data", result);
	}
	
	/**
	 * 分组统计
	 */
	@IgnoreAuth
	@RequestMapping("/group/{tableName}/{columnName}")
	public R group(@PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName) {
		// 创建一个Map对象，用于存储参数
		Map<String, Object> params = new HashMap<String, Object>();
		// 将tableName和columnName存入params中
		params.put("table", tableName);
		params.put("column", columnName);
		// 调用commonService的selectGroup方法，获取result
		List<Map<String, Object>> result = commonService.selectGroup(params);
		// 创建一个SimpleDateFormat对象，用于日期格式化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 遍历result中的每一个Map
		for(Map<String, Object> m : result) {
			// 遍历Map中的每一个key
			for(String k : m.keySet()) {
				// 如果value是Date类型，则进行日期格式化
				if(m.get(k) instanceof Date) {
					m.put(k, sdf.format((Date)m.get(k)));
				}
			}
		}
		// 返回R对象，将result存入data中
		return R.ok().put("data", result);
	}
	
	/**
	 * （按值统计）
	 */
	@IgnoreAuth
	@RequestMapping("/value/{tableName}/{xColumnName}/{yColumnName}")
	public R value(@PathVariable("tableName") String tableName, @PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName) {
		// 创建一个Map对象，用于存储参数
		Map<String, Object> params = new HashMap<String, Object>();
		// 将tableName、xColumnName和yColumnName存入params中
		params.put("table", tableName);
		params.put("xColumn", xColumnName);
		params.put("yColumn", yColumnName);
		// 调用commonService的selectValue方法，获取result
		List<Map<String, Object>> result = commonService.selectValue(params);
		// 创建一个SimpleDateFormat对象，用于日期格式化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 遍历result中的每一个Map
		for(Map<String, Object> m : result) {
			// 遍历Map中的每一个key
			for(String k : m.keySet()) {
				// 如果value是Date类型，则进行日期格式化
				if(m.get(k) instanceof Date) {
					m.put(k, sdf.format((Date)m.get(k)));
				}
			}
		}
		// 返回R对象，将result存入data中
		return R.ok().put("data", result);
	}

	/**
 	 * （按值统计）时间统计类型
	 */
	@IgnoreAuth
	@RequestMapping("/value/{tableName}/{xColumnName}/{yColumnName}/{timeStatType}")
	public R valueDay(@PathVariable("tableName") String tableName, @PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName, @PathVariable("timeStatType") String timeStatType) {
		// 创建一个Map对象，用于存储参数
		Map<String, Object> params = new HashMap<String, Object>();
		// 将tableName、xColumnName、yColumnName和timeStatType存入params中
		params.put("table", tableName);
		params.put("xColumn", xColumnName);
		params.put("yColumn", yColumnName);
		params.put("timeStatType", timeStatType);
		// 调用commonService的selectTimeStatValue方法，获取result
		List<Map<String, Object>> result = commonService.selectTimeStatValue(params);
		// 创建一个SimpleDateFormat对象，用于日期格式化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 遍历result中的每一个Map
		for(Map<String, Object> m : result) {
			// 遍历Map中的每一个key
			for(String k : m.keySet()) {
				// 如果value是Date类型，则进行日期格式化
				if(m.get(k) instanceof Date) {
					m.put(k, sdf.format((Date)m.get(k)));
				}
			}
		}
		// 返回R对象，将result存入data中
		return R.ok().put("data", result);
	}
	
    /**
     * 人脸比对
     * 
     * @param face1 人脸1
     * @param face2 人脸2
     * @return
     */
    @RequestMapping("/matchFace")
    @IgnoreAuth
    public R matchFace(String face1, String face2,HttpServletRequest request) {
        // 如果client为空，则进行初始化
        if(client==null) {
            /*String AppID = configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", "AppID")).getValue();*/
            String APIKey = configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", "APIKey")).getValue();
            String SecretKey = configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", "SecretKey")).getValue();
            String token = BaiduUtil.getAuth(APIKey, SecretKey);
            // 如果token为空，则返回错误信息
            if(token==null) {
                return R.error("请在配置管理中正确配置APIKey和SecretKey");
            }
            // 初始化client
            client = new AipFace(null, APIKey, SecretKey);
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);
        }
        // 创建一个JSONObject对象，用于存储返回结果
        JSONObject res = null;
        try {
            // 获取face1和face2的文件
            File file1 = new File(request.getSession().getServletContext().getRealPath("/upload")+"/"+face1);
            File file2 = new File(request.getSession().getServletContext().getRealPath("/upload")+"/"+face2);
            // 将face1和face2转换为Base64编码
            String img1 = Base64Util.encode(FileUtil.FileToByte(file1));
            String img2 = Base64Util.encode(FileUtil.FileToByte(file2));
            // 创建一个MatchRequest对象，用于存储face1和face2
            MatchRequest req1 = new MatchRequest(img1, "BASE64");
            MatchRequest req2 = new MatchRequest(img2, "BASE64");
            // 创建一个ArrayList对象，用于存储req1和req2
            ArrayList<MatchRequest> requests = new ArrayList<MatchRequest>();
            requests.add(req1);
            requests.add(req2);
            // 调用client的match方法，获取res
            res = client.match(requests);
            System.out.println(res.get("result"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return R.error("文件不存在");
        } catch (IOException e) {
            e.printStackTrace();
        } 
        // 返回R对象，将res存入data中
        return R.ok().put("data", com.alibaba.fastjson.JSONObject.parse(res.get("result").toString()));
    }
}
