package com.wan.sys.service.cityManager.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.wan.sys.dao.common.IBaseDao;
import com.wan.sys.entity.User;
import com.wan.sys.entity.cityManager.Code;
import com.wan.sys.pojo.ErrorCodeEnum;
import com.wan.sys.pojo.ResponseHead;
import com.wan.sys.pojo.UserBean;
import com.wan.sys.service.cityManager.IcityManagerService;
import com.wan.sys.service.common.impl.CommonServiceImpl;
import com.wan.sys.util.Encrypt;
import com.wan.sys.util.StringUtil;

@Service
public class cityManagerServiceImpl extends CommonServiceImpl implements IcityManagerService{

	@Autowired
	IBaseDao baseDao;
	
	
	@Override
	public ResponseHead toLogin(UserBean bean) {
		JSONObject js=new JSONObject();
		// 参数验证，用户名密码
		if(StringUtil.isBlank(bean.getLoginAcct())||StringUtil.isBlank(bean.getPassword())){
			return response(ErrorCodeEnum.FAIL_PARAMSISNULL.getCode(), 
					ErrorCodeEnum.FAIL_PARAMSISNULL.getValue(), js, 0,null);
		}
		String token= Encrypt.md5(bean.getLoginAcct()+bean.getPassword())+(int)(Math.random()*100);
		//查询用户在数据库是否存在
		User u=(User)baseDao.get(" from User where loginAcct=? and recordStatus='Y'", bean.getLoginAcct());
		if(u==null){//不存在
			return response(ErrorCodeEnum.FAIL_NOUSER.getCode(), 
					ErrorCodeEnum.FAIL_NOUSER.getValue(), js, 0,null);
		}else{//存在，判断密码是否一致
			if(Encrypt.md5(bean.getPassword()).equals(u.getPassword())){
				u.setToken(token);
				u.setExpireTime((1000*24*60*60+System.currentTimeMillis()));
				baseDao.update(u);
			}else{//密码错误
				return response(ErrorCodeEnum.FAIL_PASS.getCode(), 
						ErrorCodeEnum.FAIL_PASS.getValue(), js, 0,null);
			}
		}
		//返回token值
		js.put("token",token);
		return response(ErrorCodeEnum.SUCCESS.getCode(), 
				ErrorCodeEnum.SUCCESS.getValue(), js, 0,null);
	}


	@Override
	public ResponseHead toRegister(UserBean bean) {
		JSONObject js=new JSONObject();
		// 参数验证，根据flag的不同判断
		
		if(StringUtil.isNotBlank(bean.getFlag())){
			if(bean.getFlag().equals("1")){//普通手机号注册
				//参数验证，手机号和密码和验证码
				if(StringUtil.isBlank(bean.getMobilePhone())||StringUtil.isBlank(bean.getPassword())
						||StringUtil.isBlank(bean.getCode())){
					return response(ErrorCodeEnum.FAIL_PARAMSISNULL.getCode(), 
							ErrorCodeEnum.FAIL_PARAMSISNULL.getValue(), js, 0,null);
				}
				Code c=(Code)baseDao.get(" from Code where phone=?", bean.getMobilePhone());
				if(c!=null&&c.getCode().equals(bean.getCode())){//验证成功
					User u=new User();
					u.setLoginAcct(bean.getMobilePhone());
					u.setPassword(Encrypt.md5(bean.getPassword()));
					u.setMobilePhone(bean.getMobilePhone());
					baseDao.save(u);
					return response(ErrorCodeEnum.SUCCESS.getCode(), 
							ErrorCodeEnum.SUCCESS.getValue(), js, 0,null);
				}else{//验证码不正确
					return response(ErrorCodeEnum.FAIL_PARAMSISNULL.getCode(), 
							ErrorCodeEnum.FAIL_PARAMSISNULL.getValue(), js, 0,null);
				}
			}
		}else{
			return response(ErrorCodeEnum.FAIL_PARAMSISNULL.getCode(), 
					ErrorCodeEnum.FAIL_PARAMSISNULL.getValue(), js, 0,null);
		}
		return null;
	}


	@Override
	public ResponseHead getCode(UserBean bean) {
		JSONObject js=new JSONObject();
		//参数验证
		if(StringUtil.isBlank(bean.getMobilePhone())){
			return response(ErrorCodeEnum.FAIL_PARAMSISNULL.getCode(), 
					ErrorCodeEnum.FAIL_PARAMSISNULL.getValue(), js, 0,null);
		}
		//生成验证码，6位随机数
		int code=((int)(Math.random()*1000000))-1;
		//查询该手机号有没有存有验证码，有的话只是更新
		Code c1=(Code)baseDao.get(" from Code where phone=?", bean.getMobilePhone());
		if(c1==null){
			Code c=new Code();
			c.setPhone(bean.getMobilePhone());
			c.setCode(code+"");
			c.setCreateTime(new Date());
			baseDao.save(c);
		}else{
			c1.setCode(code+"");
			c1.setCreateTime(new Date());
			baseDao.update(c1);
		}
		
		js.put("code", code);
		return response(ErrorCodeEnum.SUCCESS.getCode(), 
				ErrorCodeEnum.SUCCESS.getValue(), js, 0,null);	
	}
	
}
