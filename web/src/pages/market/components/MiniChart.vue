<template>
  <view class="mini-chart" :style="{ width: width + 'rpx', height: height + 'rpx' }">
    <svg :width="width" :height="height" :viewBox="`0 0 ${width} ${height}`" preserveAspectRatio="none">
      <defs>
        <linearGradient :id="gradientId" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" :style="`stop-color:${color};stop-opacity:0.3`" />
          <stop offset="100%" :style="`stop-color:${color};stop-opacity:0.05`" />
        </linearGradient>
      </defs>
      <!-- 先绘制背景渐变 -->
      <polygon :points="areaPoints" :fill="`url(#${gradientId})`" />
      <!-- 再绘制线条，确保线条在最上层 -->
      <polyline :points="points" :stroke="color" stroke-width="2" fill="none" stroke-linecap="round"
        stroke-linejoin="round" />
    </svg>
  </view>
</template>

<script>
let chartId = 0;

export default {
  name: 'MiniChart',
  props: {
    data: {
      type: Array,
      default: () => []
    },
    width: {
      type: Number,
      default: 100
    },
    height: {
      type: Number,
      default: 40
    },
    color: {
      type: String,
      default: '#1890ff'
    }
  },
  data() {
    return {
      gradientId: `gradient-${++chartId}-${Date.now()}`
    }
  },
  computed: {
    points() {
      if (!this.data || this.data.length === 0) return ''

      const values = this.data.map(item => item.close || item.value || 0)
      const max = Math.max(...values)
      const min = Math.min(...values)
      const range = max - min || 1

      // 添加padding避免线条超出
      const padding = 3
      const effectiveWidth = this.width - padding * 2
      const effectiveHeight = this.height - padding * 2

      return this.data.map((item, index) => {
        const value = item.close || item.value || 0
        // 确保 x 坐标不会超出边界
        const ratio = this.data.length > 1 ? index / (this.data.length - 1) : 0
        const x = padding + ratio * effectiveWidth
        const y = padding + (effectiveHeight - ((value - min) / range) * effectiveHeight)
        return `${x.toFixed(2)},${y.toFixed(2)}`
      }).join(' ')
    },
    areaPoints() {
      if (!this.points) return ''
      const padding = 3
      const pointsArray = this.points.split(' ')
      if (pointsArray.length === 0) return ''

      // 获取第一个和最后一个点的 x 坐标
      const firstPoint = pointsArray[0].split(',')
      const lastPoint = pointsArray[pointsArray.length - 1].split(',')
      const firstX = firstPoint[0]
      const lastX = lastPoint[0]

      // 构建闭合的多边形：左下角 -> 线条点 -> 右下角
      return `${firstX},${this.height - padding} ${this.points} ${lastX},${this.height - padding}`
    }
  }
}
</script>

<style scoped>
.mini-chart {
  display: inline-block;
}

svg {
  display: block;
  width: 100%;
  height: 100%;
}
</style>
