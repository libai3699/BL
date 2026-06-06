package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.enums.CodeEnum;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Language;
import com.gp.common.mybatisplus.mapper.LanguageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class LanguageService extends ServiceImpl<LanguageMapper, Language> {
    @Autowired
    private LanguageMapper tLanguageMapper;

    /**
     * 查询语言
     *
     * @param id 语言ID
     * @return 语言
     */

    public Language selectLanguageById(Integer id) {
        return tLanguageMapper.selectLanguageById(id);
    }

    /**
     * 查询语言列表
     *
     * @param tLanguage 语言
     * @return 语言
     */
    public List<Language> selectLanguageList(Language tLanguage) {
        return tLanguageMapper.selectLanguageList(tLanguage);
    }

    /**
     * 查询语言数量
     *
     * @param tLanguage 语言
     * @return 语言数量
     */
    public Long selectLanguageCount(Language tLanguage) {
        return tLanguageMapper.selectLanguageCount(tLanguage);
    }

    /**
     * 新增语言
     *
     * @param tLanguage 语言
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertLanguage(Language tLanguage) {
        tLanguage.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(tLanguage);
        if (null != tLanguage.getIsDefault() && tLanguage.getIsDefault() == 1) {
            updateDefaultLanguage(tLanguage.getId());
        }
        return result;
    }

    /**
     * 修改语言
     *
     * @param tLanguage 语言
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateLanguage(Language tLanguage) {
        boolean result = this.updateById(tLanguage);
        if (tLanguage.getIsDefault() == 1) {
            this.updateDefaultLanguage(tLanguage.getId());
        }
        return result;
    }

    /**
     * 批量删除语言
     *
     * @param ids 需要删除的语言ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteLanguageByIds(Integer[] ids) {
        LambdaQueryWrapper<Language> queryWrapper = Wrappers.lambdaQuery(Language.class);
        queryWrapper.in(Language::getId, Arrays.asList(ids)).eq(Language::getIsDefault, 1);
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(CodeEnum.Error.getCode(), "默认语言不能删除");
        }
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除语言信息
     *
     * @param id 语言ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteLanguageById(Integer id) {
        if (this.getById(id).getIsDefault() == 1) {
            throw new BusinessException(CodeEnum.Error.getCode(), "默认语言不能删除");
        }
        boolean result = this.removeById(id);
        return result;
    }

    public String getLanNameByLanKey(String lanKey) {
        String lanName = "";
        Language tLanguage = baseMapper.selectOne(new LambdaQueryWrapper<Language>().eq(Language::getLanKey, lanKey));
        if (tLanguage != null) {
            lanName = tLanguage.getLanName();
        }
        return lanName;
    }

    public String getDefaultLanguage() {
        //查询下默认语言没有的话走下英语

        Language defaultLanguage = baseMapper.selectOne(new LambdaQueryWrapper<Language>().eq(Language::getIsDefault, 1).last("LIMIT 1"));
        if (defaultLanguage == null) {
            return Locale.US.toString();
        }
        return defaultLanguage.getLanKey();
    }
    public String getDefaultLanguageWin() {
        //查询下默认语言没有的话走下英语

        Language defaultLanguage = baseMapper.selectOne(new LambdaQueryWrapper<Language>().eq(Language::getIsDefault, 1).last("LIMIT 1"));
        if (defaultLanguage == null) {
            return Locale.CHINA.toString();
        }
        return defaultLanguage.getLanKey();
    }
    /**
     * 更新默认语言
     *
     * @param id 默认语言ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDefaultLanguage(Integer id) {
        //设置除默认语言外的语言为非默认
        LambdaUpdateWrapper<Language> updateWrapper = Wrappers.lambdaUpdate(Language.class);
        updateWrapper.ne(Language::getId, id).set(Language::getIsDefault, 0);
        baseMapper.update(null, updateWrapper);
        //设置默认语言
        LambdaUpdateWrapper<Language> updateWrapper1 = Wrappers.lambdaUpdate(Language.class);
        updateWrapper1.eq(Language::getId, id).set(Language::getIsDefault, 1);
        baseMapper.update(null, updateWrapper1);
    }

    public List<Language> initLanguage() {
        return baseMapper.selectLanguageList(new Language());
    }
}
