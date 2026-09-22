package anzihe.com.common_template.service.impl;

import anzihe.com.common_template.common.DeleteRequest;
import anzihe.com.common_template.exception.BusinessException;
import anzihe.com.common_template.exception.ErrorCode;
import anzihe.com.common_template.model.DTO.menu.SysMenuDTO;
import anzihe.com.common_template.model.VO.menu.SysMenuTreeVO;
import anzihe.com.common_template.model.VO.menu.SysMenuVO;
import anzihe.com.common_template.model.entity.SysMenu;
import anzihe.com.common_template.service.SysMenuService;
import anzihe.com.common_template.mapper.SysMenuMapper;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
* @author qq529
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Service实现
* @createDate 2026-09-22 11:06:39
*/
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
    implements SysMenuService{

    @Override
    public void save(SysMenuDTO dto) {
        SysMenu bean = BeanUtil.toBean(dto, SysMenu.class);
        bean.setCreateTime(new Date());
        bean.setUpdateTime(new Date());
        boolean save = this.save(bean);
        if (!save){
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    public void update(SysMenuDTO dto) {
        SysMenu exist = this.getById(dto.getId());
        if (BeanUtil.isEmpty(exist)){
            throw  new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        SysMenu bean = BeanUtil.toBean(dto, SysMenu.class);
        bean.setUpdateTime(new Date());
        boolean b = this.updateById(bean);
        if (!b){
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    public void delete(DeleteRequest id) {
        SysMenu exist = this.getById(id.getId());
        if (BeanUtil.isEmpty(exist)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean b = this.removeById(exist);
        if (!b){
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    public SysMenuVO getDetailById(Long id) {
        if (id == null){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysMenu exist = this.getById(id);
        if (BeanUtil.isEmpty(exist)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return BeanUtil.toBean(exist, SysMenuVO.class);
    }

    @Override
    public List<SysMenuTreeVO> tree() {
        // 1. 查询所有菜单
        List<SysMenu> list = this.list();

        // 2. 转成 VO
        List<SysMenuTreeVO> voList = list.stream().map(menu -> {
            SysMenuTreeVO vo = new SysMenuTreeVO();
            BeanUtils.copyProperties(menu, vo);
            return vo;
        }).collect(Collectors.toList());

        // 3. 按 parentId 分组
        Map<Long, List<SysMenuTreeVO>> parentMap = voList.stream()
                .collect(Collectors.groupingBy(SysMenuTreeVO::getParentId));

        // 4. 给每个节点设置 children,按照rank进行排序
        voList.forEach(vo -> {
            List<SysMenuTreeVO> children = parentMap.get(vo.getId());
            if (children != null) {
                children.sort(Comparator.comparing(SysMenuTreeVO::getRank,
                        Comparator.nullsLast(Integer::compareTo)));
            }
            vo.setChildren(children);
        });
        // 5. 返回根节点（parentId = 0）
        return parentMap.getOrDefault(0L, new ArrayList<>());
    }
}




