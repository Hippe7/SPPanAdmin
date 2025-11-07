package net.sppan.base.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.sppan.base.service.ISessionService;

import net.sppan.base.common.Constats;
import net.sppan.base.dao.IRoleDao;
import net.sppan.base.dao.support.IBaseDao;
import net.sppan.base.entity.Resource;
import net.sppan.base.entity.Role;
import net.sppan.base.service.IResourceService;
import net.sppan.base.service.IRoleService;
import net.sppan.base.service.support.impl.BaseServiceImpl;

/**
 * <p>
 * 角色表  服务实现类
 * </p>
 *
 * @author SPPan
 * @since 2016-12-28
 */
@Service
public class RoleServiceImpl extends BaseServiceImpl<Role, Integer> implements IRoleService {

	@Autowired
	private IRoleDao roleDao;
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private ISessionService sessionService;
	
	@Override
	public IBaseDao<Role, Integer> getBaseDao() {
		return this.roleDao;
	}

	@Override
	public void saveOrUpdate(Role role) {
		if(role.getId() != null){
			Role dbRole = find(role.getId());
			dbRole.setUpdateTime(new Date());
			dbRole.setName(role.getName());
			dbRole.setDescription(role.getDescription());
			dbRole.setUpdateTime(new Date());
			dbRole.setStatus(role.getStatus());
			update(dbRole);
		}else{
			role.setCreateTime(new Date());
			role.setUpdateTime(new Date());
			// 新增角色时自动分配资源管理菜单及其所有子资源的权限
			Resource resource = resourceService.findByName("资源管理");
			if (resource != null) {
				Set<Resource> resources = new HashSet<>();
				// 递归获取所有子资源
				findAllChildResources(resource, resources);
				role.setResources(resources);
			}
			save(role);
		}
	}
	
	/**
	 * 递归获取所有子资源
	 * @param parentResource
	 * @param resources
	 */
	private void findAllChildResources(Resource parentResource, Set<Resource> resources) {
		resources.add(parentResource);
		// 查找所有父资源为当前资源的子资源
		List<Resource> childResources = resourceService.findAllByParent(parentResource);
		for (Resource childResource : childResources) {
			findAllChildResources(childResource, resources);
		}
	}

	
	
	@Override
	public void delete(Integer id) {
		Role role = find(id);
		Assert.state(!"administrator".equals(role.getRoleKey()),"超级管理员角色不能删除");
		super.delete(id);
	}

	@Override
	@CacheEvict(value = Constats.RESOURCECACHENAME, key = "'tree_' + #id")
	public void grant(Integer id, String[] resourceIds) {
		Role role = find(id);
		Assert.notNull(role, "角色不存在");
		
		Assert.state(!"administrator".equals(role.getRoleKey()),"超级管理员角色不能进行资源分配");
		Resource resource;
		Set<Resource> resources = new HashSet<Resource>();
		if(resourceIds != null){
			for (int i = 0; i < resourceIds.length; i++) {
				if(StringUtils.isBlank(resourceIds[i]) || "0".equals(resourceIds[i])){
					continue;
				}
				Integer rid = Integer.parseInt(resourceIds[i]);
				resource = resourceService.find(rid);
				resources.add(resource);
			}
		}
		role.setResources(resources);
		update(role);
		
		// 失效拥有该角色的所有用户会话，确保权限实时更新
		sessionService.invalidateSessionsByRoleId(id);
	}

	@Override
	public Page<Role> findAllByLike(String searchText, PageRequest pageRequest) {
		if(StringUtils.isBlank(searchText)){
			searchText = "";
		}
		return roleDao.findAllByNameContainingOrDescriptionContaining(searchText,searchText, pageRequest);
	}
	
}
