package anzihe.com.common_template.controller.system;

import anzihe.com.common_template.common.BaseResponse;
import anzihe.com.common_template.common.DeleteRequest;
import anzihe.com.common_template.common.PageRequest;
import anzihe.com.common_template.common.ResultUtils;
import anzihe.com.common_template.model.DTO.role.SysRoleDTO;
import anzihe.com.common_template.model.VO.role.SysRoleVO;
import anzihe.com.common_template.model.entity.SysRole;
import anzihe.com.common_template.service.SysRoleService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.db.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    private SysRoleService sysRoleService;

    @PostMapping("/save")
    public BaseResponse save( @RequestBody SysRoleDTO roleDTO){

        return ResultUtils.success(sysRoleService.save(BeanUtil.toBean(roleDTO, SysRole.class)));
    }

    @PostMapping("/delete")
    public BaseResponse save( @RequestBody DeleteRequest deleteRequest){
        return ResultUtils.success(sysRoleService.removeById(deleteRequest.getId()));
    }

    @PostMapping("/update")
    public BaseResponse update( @RequestBody SysRoleDTO roleDTO){
        return ResultUtils.success(sysRoleService.updateById(BeanUtil.toBean(roleDTO, SysRole.class)));
    }

    @GetMapping("/get/{id}")
    public BaseResponse<SysRoleVO> detail( @PathVariable Long id ){
        return ResultUtils.success(BeanUtil.toBean(sysRoleService.getById(id), SysRoleVO.class));
    }

//    public  BaseResponse<PageRequest<SysRoleVO>> getList(){
//
//    }
}
