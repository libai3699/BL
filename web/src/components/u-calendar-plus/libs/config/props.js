/**
 * 此文件的作用为统一配置所有组件的props参数
 * 借此用户可以全局覆盖组件的props默认值
 * 无需在每个引入组件的页面中都配置一次
 */
import config from './config'
import zIndex from './zIndex.js'
import color from './color.js'
import http from '../function/http.js'
import { shallowMerge } from '../function/index.js'
import Calendar from '../../calendar.js'

const props = {
    ...Calendar
}

function setConfig(configs) {
    shallowMerge(config, configs.config || {})
    shallowMerge(props, configs.props || {})
    shallowMerge(color, configs.color || {})
    shallowMerge(zIndex, configs.zIndex || {})
}

// 初始化自定义配置
if (uni && uni.upuiParams) {
    console.log('setting uview-plus')
    let temp = uni.upuiParams()
    if (temp.httpIns) {
        temp.httpIns(http)
    }
    if (temp.options) {
        setConfig(temp.options)
    }
}

export default props
