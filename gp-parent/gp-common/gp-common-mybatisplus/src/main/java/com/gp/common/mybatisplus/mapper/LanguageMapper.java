package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.Language;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LanguageMapper extends BaseMapper<Language> {

    /**
     * 查询语言
     *
     * @param id 语言ID
     * @return 语言
     */
    public Language selectLanguageById(Integer id);

    /**
     * 查询语言列表
     *
     * @param tLanguage 语言
     * @return 语言集合
     */
    public List<Language> selectLanguageList(Language tLanguage);

    /**
     * 查询语言数量
     *
     * @param tLanguage 语言
     * @return 语言数量
     */
    public Long selectLanguageCount(Language tLanguage);

    /**
     * 新增语言
     *
     * @param tLanguage 语言
     * @return 结果
     */
    public int insertLanguage(Language tLanguage);

    /**
     * 修改语言
     *
     * @param tLanguage 语言
     * @return 结果
     */
    public int updateLanguage(Language tLanguage);

    /**
     * 删除语言
     *
     * @param id 语言ID
     * @return 结果
     */
    public int deleteLanguageById(Integer id);

    /**
     * 批量删除语言
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteLanguageByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();
}