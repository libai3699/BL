/**
 * 首页公告 sourceName → 设计稿标签配色（固定顺序）
 * 顺序：交易 → 活动 → 研报 → IPO → 系统
 */
const NOTICE_STYLE_PRESETS = [
  {
    key: 'trade',
    background: '#E6F8F1',
    color: '#10B981',
    closeBtn: 'primary'
  },
  {
    key: 'act',
    background: 'linear-gradient(135deg, #FCE7F3, #FBCFE8)',
    color: '#BE185D',
    closeBtn: 'success'
  },
  {
    key: 'report',
    background: '#EEF2FF',
    color: '#4F46E5',
    closeBtn: 'primary'
  },
  {
    key: 'ipo',
    background: 'linear-gradient(135deg, #F59E0B, #EF4444)',
    color: '#FFFFFF',
    closeBtn: 'success'
  },
  {
    key: 'sys',
    background: '#F1F5F9',
    color: '#475569',
    closeBtn: 'primary'
  }
];

/** sourceName 与设计稿类目顺序对应（可按后端返回值扩展） */
const SOURCE_NAME_INDEX = {
  交易: 0,
  trade: 0,
  TRADE: 0,
  活动: 1,
  活动通知: 1,
  activity: 1,
  act: 1,
  ACT: 1,
  研报: 2,
  研究报告: 2,
  report: 2,
  REPORT: 2,
  IPO: 3,
  ipo: 3,
  新股: 3,
  系统: 4,
  系统通知: 4,
  system: 4,
  sys: 4,
  SYSTEM: 4
};

const hashSourceName = (name) => {
  let h = 0;
  for (let i = 0; i < name.length; i++) {
    h = (h + name.charCodeAt(i)) % NOTICE_STYLE_PRESETS.length;
  }
  return h;
};

export const getNoticeSourceStyle = (sourceName) => {
  const key = (sourceName || '').trim();
  const idx =
    key && SOURCE_NAME_INDEX[key] != null
      ? SOURCE_NAME_INDEX[key]
      : hashSourceName(key || 'default');
  const preset = NOTICE_STYLE_PRESETS[idx];
  return {
    ...preset,
    label: key || ''
  };
};

export const noticeCatInlineStyle = (sourceName) => {
  const s = getNoticeSourceStyle(sourceName);
  return {
    background: s.background,
    color: s.color
  };
};
