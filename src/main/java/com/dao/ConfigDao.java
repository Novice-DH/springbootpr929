
package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.entity.ConfigEntity;

/**
 * 配置
 */
public interface ConfigDao extends BaseMapper<ConfigEntity> {
	
	// 继承BaseMapper接口，用于操作ConfigEntity实体类
}
