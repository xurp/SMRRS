package com.shanghai.modules.sys.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.shanghai.common.utils.PageInfo;
import com.shanghai.common.utils.UserUtil;
import com.shanghai.modules.sys.dao.UserDao;
import com.shanghai.modules.sys.entity.User;
import com.shanghai.modules.sys.service.UserService;

/**
 * @author: YeJR
 * @version: 2018年4月28日 上午10:19:22
 * 
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	@Override
	public User getByLoginName(String loginName) {
		return userDao.getUserByName(loginName);
	}
	
	@Override
	public User getUserById(Integer id) {
		User user = new User(id);
		return userDao.get(user);
	}
	
	@Override
	public List<User> findUsers(User user) {
		return userDao.findList(user);
	}
	
	@Override
	public PageInfo<User> findUserByPage(int pageNo, int pageSize, User user) {
		PageHelper.startPage(pageNo, pageSize);
		return new PageInfo<>(userDao.findList(user));
	}

	// [注]:如果try catch了,那事务不回滚,除非在catch里throw了. 事务默认遇到IOException这种必须try catch的不会回滚
	// [注]:加了rollbackFor = Exception.class后,IOException也能回滚
	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Integer saveUser(User user) throws IOException {
		// [注]:用户输入的密码需要加密保存
		String newPassword = UserUtil.encryptPassword(user.getUsername(), user.getPassword());
		user.setPassword(newPassword);
		// 保存到db中
		Integer result = userDao.insert(user);
		if (user.getRoleIds() != null && user.getRoleIds().size() > 0) {
			//插入新的用户角色关联关系
			userDao.insertUserRole(user);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Integer updateUser(User user) throws IOException {
		if (StringUtils.isNotBlank(user.getPassword())) {
			String newPassword = UserUtil.encryptPassword(user.getUsername(), user.getPassword());
			user.setPassword(newPassword);
		}
		Integer result = userDao.update(user);
		// [注]:更新用户后需要修改用户的权限关系表
		//删除原有的用户角色关联关系
		userDao.deleteUserRole(user);
		if (user.getRoleIds() != null && user.getRoleIds().size() > 0) {
			//插入新的用户角色关联关系
			userDao.insertUserRole(user);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteUserById(Integer id) throws IOException {
		User user = new User(id);
		userDao.delete(user);
		//删除用户角色--角色关系
		userDao.deleteUserRole(user);
	}
	
	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteUserByIds(Integer[] ids) throws IOException {
		if (ids != null) {
			for (Integer id : ids) {
				deleteUserById(id);
			}
		}
		
	}
	
	@Override
	public boolean verifyUsername(String username, String oldUsername) {
		if (StringUtils.isNotBlank(username)) {
			if (username.equals(oldUsername)) {
				return true;
			}
			User user = userDao.getUserByName(username);
			if (user == null) {
				return true;
			}
		}
		return false;
	}


}
