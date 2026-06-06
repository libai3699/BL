<template>
  <div>
    <div
      v-if="commonStore?.storage_cur?.name"
      class="m-line-switching"
      :class="`s-${commonStore.storage_cur?.status}`"
      @click="open"
    >
      <div class="u-name">{{ commonStore.storage_cur?.name }}</div>
      <div class="u-timeout" v-if="showTimeout">
        {{ commonStore.storage_cur?.timeout }}
      </div>
      <div class="u-dot"></div>
      <svg-icon class="u-icon" name="global" />
    </div>

    <!-- 弹层 -->
    <u-popup :show="refPopupShow" v-if="commonStore?.storage_cur" @close="close">
      <div class="m-line-switching-popup">
        <div class="u-top">
          <div class="u-left" @click="close">
            <svg-icon name="arrow-left-thin" class="u-item-back" />
          </div>
          <div class="u-title">
            <div class="u-name">{{ $t('line.switching.popup.title') }}</div>
            <div class="u-ip">IP: {{ commonStore.ip }}</div>
          </div>
        </div>
        <div class="u-list">
          <div
            class="u-item"
            :class="[
              { 's-cur': item.name === commonStore.storage_cur?.name },
              `s-${item.status}`
            ]"
            v-for="item in commonStore.storage_lines"
            @click="doSwitchLine(item)"
          >
            <div class="u-item-name">{{ item.name }}</div>
            <div class="u-item-timeout">
              <up-loading-icon
                v-if="item.status === 'loading'"
                :size="16"
                class="u-loading"
              />
              <div v-else>{{ item.timeout }}</div>
            </div>
            <svg-icon name="signal" class="u-item-signal" />
          </div>
        </div>
        <div class="u-bottom">
          {{ $t('line.switching.popup.tips') }}
        </div>
      </div>
    </u-popup>
  </div>
</template>

<script>
import { useCommonStore } from '@/stores/common';
import { testApi } from '@/utils';

export default {
  props: {
    // 切换线路后触发
    showTimeout: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      refPopupShow: false,
      commonStore: null
    };
  },
  methods: {
    useCommonStore,
    open() {
      this.refPopupShow = true;
      testApi({
        lines: this.commonStore.storage_lines,
        onEachComplete: (line) => {
          this.$forceUpdate();
        }
      });
    },
    close() {
      this.refPopupShow = false;
    },
    doSwitchLine(line) {
      this.commonStore.switchApiLine(line);
      this.close();
      this.$emit('switch', line);
    }
  },
  mounted() {
    this.commonStore = useCommonStore();
  }
};
</script>

<style lang="scss" scoped>
.m-line-switching {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 26rpx;
  height: 52rpx;
  font-size: 24rpx;
  color: var(--line-switching-color);
  background: var(--line-switching-bg);
  border: var(--line-switching-border);
  padding: 0 7px;
  .u-name {
    flex-shrink: 0;
  }
  .u-timeout {
    margin-left: 12rpx;
    flex-shrink: 0;
    font-size: 20rpx;
    line-height: 20rpx;
  }
  .u-dot {
    width: 12rpx;
    height: 12rpx;
    border-radius: 100%;
    margin: 0 0 0 8rpx;
  }
  &.s-good {
    .u-dot {
      background: #00d962;
    }
    .u-timeout {
      color: #00d962;
    }
  }

  &.s-warning {
    .u-dot {
      background: #efbf00;
    }
    .u-timeout {
      color: #efbf00;
    }
  }

  &.s-bad {
    .u-dot {
      background: #f62d2d;
    }
    .u-timeout {
      color: #f62d2d;
    }
  }
  .u-icon {
    margin-left: 12rpx;
    width: 20rpx;
    height: 20rpx;
  }
}

.m-line-switching-popup {
  margin: 0 auto;
  width: 100%;
  border-radius: 10rpx;
  box-shadow: 0 0 8rpx rgba(0, 0, 0, 0.25);
  background: var(--popup-normal-bg);
  font-size: 32rpx;
  .u-top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20rpx 30rpx;
    .u-item-back {
      font-size: 48rpx;
    }
    .u-left {
      position: absolute;
      top: 28rpx;
      left: 44rpx;
      .back-icon {
        color: var(--text-color) !important;
      }
    }
    .u-title {
      width: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: var(--text-color);
      .u-ip {
        font-size: 24rpx;
        line-height: 28rpx;
        margin-top: 4rpx;
      }
    }
  }
  .u-list {
    .u-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 20rpx 28rpx 20rpx 50rpx;
      background: rgba(75, 75, 77, 0.05);
      color: var(--text-color);
      cursor: pointer;
      &:not(:last-child) {
        margin-bottom: 12rpx;
      }
      .u-item-name {
        flex: 1;
        font-size: 28rpx;
      }
      .u-item-timeout {
        font-size: 24rpx;
        .u-loading {
          position: static;
        }
      }
      .u-item-signal {
        font-size: 28rpx !important;
        margin-left: 22rpx;
      }
      &.s-cur {
        .u-item-timeout,
        .u-item-signal {
          color: var(--primary-color-content) !important;
        }
        background-color: var(--primary-color);
        color: var(--primary-color-content);
      }

      &.s-good {
        .u-item-timeout,
        .u-item-signal {
          color: #00d962;
        }
      }

      &.s-warning {
        .u-item-timeout,
        .u-item-signal {
          color: #efbf00;
        }
      }

      &.s-bad {
        .u-item-timeout,
        .u-item-signal {
          color: #f62d2d;
        }
      }
    }
  }
  .u-bottom {
    padding: 18rpx;
    font-size: 24rpx;
    line-height: 28rpx;
    text-align: center;
    color: var(--line-switching-popup-bottom-color);
  }
}
</style>
