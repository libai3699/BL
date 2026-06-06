// 判断规则中定义的必填项，表单是否都填写完毕
// 一般用于判断必填项填写完毕后开启表单验证
export function isRequireParamFilled(params, rules) {
  if (params == null) return false;
  for (let key in rules) {
    const rule = rules[key];
    const param = getFinalParam(params, key);
    // 如果规则里有必填校验，则参数判断为空则不通过
    if (rule.find((item) => item.required) && !param) {
      return false;
    }
  }
  // 对于键值中带.的属性名，比如'a.b'，迭代获取最终属性值
  function getFinalParam(param, key) {
    let result = param;
    // 如果带. 代表有层级
    if (key.includes('.')) {
      const keys = key.split('.');
      keys.forEach((k) => {
        result = result[k];
      });
    } else {
      result = param[key];
    }
    return result;
  }
  return true;
}

// 普通文本
export const ruleNormalText = ({
  isRequired = true,
  langKey = '',
  hasLimit = false,
  limit = 40,
  limitMin = 0,
  placeholder = ''
}) => {
  placeholder = placeholder || uni.$t(`form.normalText.${langKey}.placeholder`);
  const rules = [
    {
      type: 'string',
      required: isRequired.constructor === Function ? isRequired() : isRequired,
      message: placeholder,
      trigger: ['blur', 'change']
    },
    {
      validator: (rule, value, callback) => {
        // 返回true表示校验通过，返回false表示不通过
        // 非必填，所以设置value === ''
        if (value === '') return true;
        // 如果没有限制字数，也通过
        if (hasLimit === false) return true;
        if (limitMin > 0 && value.length < limitMin) {
          return callback(uni.$t(`form.normalText.limitMin`, { limit: limitMin }));
        }
        if (value.length > limit) {
          return callback(uni.$t('form.normalText.limit', { limit }));
        }
        return true;
      },
      trigger: ['blur', 'change']
    }
  ];
  return rules;
};

// 密码
export const rulePassword = ({
  isRequired = true,
  langKey = '',
  rule = 'normal', // normal-没有限制 | payPass-支付密码(6位数字)
  limitMin = 0,
  isConfirm = false, // 是否是确认密码
  comparePackage
} = {}) => {
  const placeholder = uni.$t(`form.password.${langKey}.placeholder`);
  const rules = [
    {
      type: 'string',
      required: isRequired.constructor === Function ? isRequired() : isRequired,
      message: placeholder,
      trigger: ['blur', 'change']
    },
    {
      validator: (rule, value, callback) => {
        if (limitMin > 0 && value.length < limitMin) {
          return callback(uni.$t(`form.password.error.limitMin`, { limit: limitMin }));
        }
        return true;
      },
      trigger: ['blur', 'change']
    }
  ];
  // 支付密码规则
  if (rule === 'payPass') {
    // 支付密码，必须是6位数字
    rules.push({
      // 正则检验前先将值转为字符串
      transform(value) {
        return String(value);
      },
      validator: (rule, value, callback) => {
        // 返回true表示校验通过，返回false表示不通过
        // 非必填，所以设置value === ''
        return value === '' || uni.$u.test.code(value, 6);
      },
      message: () => t('form.password.error.payPass'),
      trigger: ['blur', 'change']
    });
  }
  // 确认密码
  if (isConfirm) {
    rules.push({
      // 正则检验前先将值转为字符串
      transform(value) {
        return String(value);
      },
      validator: (rule, value, callback) => {
        // 返回true表示校验通过，返回false表示不通过
        const isNotEqual =
          value.toString() !== comparePackage.source[comparePackage.field].toString();
        const message = uni.$t(`form.password.error.confirm`);
        if (isNotEqual) {
          return callback(message);
        }
        return true;
      },
      trigger: ['blur', 'change']
    });
  }
  return rules;
};
