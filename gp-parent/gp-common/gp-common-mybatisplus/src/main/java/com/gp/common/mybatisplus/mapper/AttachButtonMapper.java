package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.AttachButton;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 附加按钮Mapper接口
 *
 * @author axing
 * @date 2025-06-09
 */
public interface AttachButtonMapper extends BaseMapper<AttachButton>
{
    /**
     * 查询附加按钮
     *
     * @param id 附加按钮ID
     * @return 附加按钮
     */
    public AttachButton selectAttachButtonById(Integer id);

    /**
     * 查询附加按钮列表
     *
     * @param attachButton 附加按钮
     * @return 附加按钮集合
     */
    public List<AttachButton> selectAttachButtonList(AttachButton attachButton);

    /**
     * 新增附加按钮
     *
     * @param attachButton 附加按钮
     * @return 结果
     */
    public int insertAttachButton(AttachButton attachButton);

    /**
     * 修改附加按钮
     *
     * @param attachButton 附加按钮
     * @return 结果
     */
    public int updateAttachButton(AttachButton attachButton);

    /**
     * 删除附加按钮
     *
     * @param id 附加按钮ID
     * @return 结果
     */
    public int deleteAttachButtonById(Integer id);

    /**
     * 批量删除附加按钮
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteAttachButtonByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
