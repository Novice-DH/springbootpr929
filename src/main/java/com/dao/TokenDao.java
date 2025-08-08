
package com.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.entity.TokenEntity;

/**
 * token
 */
public interface TokenDao extends BaseMapper<TokenEntity> {
	
	// 根据条件查询token列表
	List<TokenEntity> selectListView(@Param("ew") Wrapper<TokenEntity> wrapper);

	// 根据条件查询token列表，并分页
	List<TokenEntity> selectListView(Pagination page,@Param("ew") Wrapper<TokenEntity> wrapper);
	
}
