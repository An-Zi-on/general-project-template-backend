package anzihe.com.common_template.service;

import anzihe.com.common_template.model.DTO.role.SysRoleDTO;
import anzihe.com.common_template.model.entity.SysRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;

/**
* @author qq529
* @description 针对表【sys_role(角色表)】的数据库操作Service
* @createDate 2026-09-22 11:06:39
*/
public interface SysRoleService extends IService<SysRole> {

    Page<SysRoleDTO> getlist(SysRoleDTO roleDTO);
}
