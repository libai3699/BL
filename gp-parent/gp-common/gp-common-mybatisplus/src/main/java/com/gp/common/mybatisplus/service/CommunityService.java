package com.gp.common.mybatisplus.service;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.FiletypeUtil;
import com.gp.common.mybatisplus.configuration.BotConfiguration;
import com.gp.common.mybatisplus.configuration.ReportTelegramUtil;
import com.gp.common.mybatisplus.entity.AttachButton;
import com.gp.common.mybatisplus.entity.Community;
import com.gp.common.mybatisplus.mapper.CommunityMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 客服Service业务层处理
 *
 * @author axing
 * @date 2024-03-13
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
public class CommunityService extends ServiceImpl<CommunityMapper, Community> {
    @Resource
    private CommunityMapper communityMapper;

    @Resource
    private AttachButtonService attachButtonService;

    /**
     * 查询客服
     *
     * @param id 客服ID
     * @return 客服
     */

    public Community selectCommunityById(Integer id) {
        return communityMapper.selectCommunityById(id);
    }

    /**
     * 查询客服列表
     *
     * @param community 客服
     * @return 客服
     */

    public List<Community> selectCommunityList(Community community) {
        return communityMapper.selectCommunityList(community);
    }

    /**
     * 新增客服
     *
     * @param community 客服
     * @return 结果
     */

    public Boolean insertCommunity(Community community) {
        community.setCreateTime(DateUtils.getNowDate());
        return this.save(community);
    }

    /**
     * 修改客服
     *
     * @param community 客服
     * @return 结果
     */

    public Boolean updateCommunity(Community community) {
        return this.updateById(community);
    }

    /**
     * 批量删除客服
     *
     * @param ids 需要删除的客服ID
     * @return 结果
     */

    public Boolean deleteCommunityByIds(Integer[] ids) {
        return this.removeByIds(Arrays.asList(ids));
    }

    /**
     * 删除客服信息
     *
     * @param id 客服ID
     * @return 结果
     */

    public Boolean deleteCommunityById(Integer id) {
        return this.removeById(id);
    }

    public Boolean sendNotify(Community community) {
        String replace = community.getUrl().replace(TelegramUtil.tgAdmin, "").trim();
        String chatUsername = "@" + replace;
        List<File> files = FiletypeUtil.URLToFile(community.getFileUrls());
        //获取播报机器人
        ReportTelegramUtil reportTelegramUtil = BotConfiguration.getReportBot(CecuUtil.getDbCode());
        //查询消息附加按钮
        List<AttachButton> buttonList = attachButtonService.list(Wrappers.lambdaQuery(AttachButton.class).eq(AttachButton::getLanKey,
                community.getLanKey()));
        List<List<InlineKeyboardButton>> keyboardList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(buttonList)) {
            // 添加按钮，每行两个
            for (int i = 0; i < buttonList.size(); i += 2) {
                List<InlineKeyboardButton> keyboard = new ArrayList<>();
                // 第一个按钮
                InlineKeyboardButton keyboardButton1 = InlineKeyboardButton.builder()
                        .text(buttonList.get(i).getName())
                        .url(buttonList.get(i).getUrl())
                        .build();
                keyboard.add(keyboardButton1);

                // 如果存在第二个按钮，则添加到同一行
                if (i + 1 < buttonList.size()) {
                    InlineKeyboardButton keyboardButton2 = InlineKeyboardButton.builder()
                            .text(buttonList.get(i + 1).getName())
                            .url(buttonList.get(i + 1).getUrl())
                            .build();
                    keyboard.add(keyboardButton2);
                }

                keyboardList.add(keyboard);
            }
        }
        //发送消息
        Message message = reportTelegramUtil.sendMedia(files, community.getMessage(), chatUsername, keyboardList);
        log.info("sendNotify:{}", message);
        //删除保存的文件
        for (File file : files) {
            FileUtil.del(file);
        }
        return message != null;
    }

}
