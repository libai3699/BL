import zhHans from './locales/zh-Hans.json'
import zhHant from './locales/zh-Hant.json'
import en from './locales/en.json'
import es from './locales/es.json'
import fr from './locales/fr.json'
import de from './locales/de.json'
import ko from './locales/ko.json'
import ja from './locales/ja.json'
import ru from './locales/ru.json'

let settings = {
    lang: uni.getLocale(),
    locales: {
        en,
        es,
        fr,
        de,
        ko,
        ja,
        ru,
        'zh-Hant': zhHant,
        'zh-Hans': zhHans
    }
};

uni.onLocaleChange((e) => {
    settings.lang = e.locale;
})

/**
 * 多语言方法
 */
export function t(value, params = {}) {
    // console.log(settings.locales[settings.lang])
    if (value) {
        let lang = settings.lang
        // If the exact locale is not found, try to find a fallback (e.g. 'es-MX' -> 'es')
        if (!settings.locales[lang]) {
            const prefix = lang.split('-')[0];
            if (settings.locales[prefix]) {
                lang = prefix;
            } else {
                lang = 'zh-Hans';
            }
        }

        // If still not found (should be covered by 'zh-Hans' fallback above, but for safety)
        if (!settings.locales[lang]) {
            lang = 'zh-Hans';
        }

        let result = settings.locales[lang][value] || value;
        // 替换{xxx}格式的变量
        Object.keys(params).forEach(key => {
            const reg = new RegExp(`{${key}}`, 'g');
            result = result.replace(reg, params[key]);
        });
        return result;
    } else {
        return value;
    }
}

export default {
    settings: settings
}
