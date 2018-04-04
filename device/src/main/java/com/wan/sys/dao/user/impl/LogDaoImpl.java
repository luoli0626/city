package com.wan.sys.dao.user.impl;

import org.springframework.stereotype.Repository;

import com.wan.sys.dao.common.impl.BaseDaoImpl;
import com.wan.sys.dao.user.ILogDao;
import com.wan.sys.entity.Log;
/**
 * 
 * 文件名称： 系统日志管理dao类实现
 * 内容摘要： 
 * 创建人： 唐君左
 * 创建日期： 2017-6-26
 * 版本号： v1.0.0
 * 公  司：金科物业服务有限公司
 * 版权所有： (C)2016-2017     
 * 修改记录1 
 * 修改日期：
 * 版本号：
 * 修改人：
 * 修改内容：  
 *
 */
@Repository("logDao")
public class LogDaoImpl extends BaseDaoImpl<Log> implements ILogDao {

}
