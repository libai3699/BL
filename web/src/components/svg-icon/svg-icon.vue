<template>
  <!-- #ifdef MP -->
  <view class="svg-icon" :style="style" />
  <!-- #endif -->
  <!-- #ifndef MP -->
  <view class="svg-icon" v-html="svgHtml" :style="style" />
  <!-- #endif -->
</template>

<script>
import { getSvg } from '@/components/svg-icon/index.js';
const SVG_FILE_PATH = './static/svgs';

export default {
  name: 'svg-icon',
  options: {
    virtualHost: true
  },
  props: {
    name: {
      type: String,
      default: '',
      required: true
    },
    color: {
      type: String,
      default: ''
    },
    size: {
      type: [Number, String, Array],
      default: ''
    },
    width: {
      type: [String, Number],
      default: 40
    },
    height: {
      type: [String, Number],
      default: 40
    },
    unit: {
      type: String,
      default: 'rpx'
    },
    watch: {
      type: Boolean,
      default: process.env.NODE_ENV === 'development'
    }
  },
  data() {
    return {
      svgHtml: ''
    };
  },
  computed: {
    style() {
      const { size, unit, svgHtml } = this;
      let { width, height } = this;
      if (size) {
        if (Object.prototype.toString.call(size) === '[object Array]') {
          [width = width, height = height] = size;
        } else {
          [width, height] = [size, size];
        }
      }
      let res = `width:${width}${unit};height:${height}${unit};`;
      // #ifdef MP
      res += `background-image:url("data:image/svg+xml,${encodeURIComponent(svgHtml)}");`;
      // #endif
      return res;
    }
  },
  created() {
    this.getSvgHtml();

    if (this.watch) {
      this.$watch('$props', () => this.getSvgHtml(), { deep: true });
    }
  },
  methods: {
    async getSvgHtml() {
      // #ifndef APP-PLUS  || MP
      const raw = getSvg(this.name);
      if (!raw) return;
      this.svgHtml = this.setSvgColor(raw);
      return;
      // #endif

      const ctx = await this.fileReader(this.name);
      const regex = /<svg[\s\S]*?<\/svg>/i;
      let [html] = ctx.match(regex);
      this.svgHtml = this.setSvgColor(html);
    },
    fileReader(name) {
      const path = `${SVG_FILE_PATH}/${name}.svg`;
      return new Promise((resolve, reject) => {
        // #ifdef APP-PLUS
        plus.io.resolveLocalFileSystemURL(
          `_www/${path}`,
          (entry) =>
            entry.file((file) => {
              const fileReader = new plus.io.FileReader();
              fileReader.onloadend = (evt) => resolve(evt.target.result);
              fileReader.onerror = (error) => reject(error);
              fileReader.readAsText(file, 'utf-8');
            }),
          (error) => reject(error)
        );
        // #endif

        // #ifdef MP
        const fs = uni.getFileSystemManager();
        fs.readFile({
          filePath: path,
          encoding: 'binary',
          success: (res) => resolve(res.data),
          fail: reject
        });
        // #endif
      });
    },
    setSvgColor(html) {
      // console.log('setSvgColor', html);
      if (!this.color) return html;
      const res = html.replace(/(stroke|fill)="([^"]*)"/g, (match, p1, p2) => {
        return `${p1}="${p2 === 'none' ? 'none' : this.color}"`;
      });
      return res;
    }
  }
};
</script>

<style>
::v-deep svg {
  width: 100% !important;
  height: 100% !important;
}

/* #ifdef MP */
.svg-icon {
  background-repeat: no-repeat;
  background-size: contain;
}
/* #endif */
</style>
