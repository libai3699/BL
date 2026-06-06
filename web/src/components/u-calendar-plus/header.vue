<template>
	<view class="u-calendar-header u-border-bottom">
		<text class="u-calendar-header__title" v-if="showTitle">{{ title }}</text>
		<text class="u-calendar-header__subtitle" v-if="showSubtitle">{{ displaySubtitle }}</text>
		<view class="u-calendar-header__weekdays">
			<text class="u-calendar-header__weekdays__weekday" v-for="(item, index) in displayWeekText" :key="index">{{
				item }}</text>
		</view>
	</view>
</template>

<script>
import { mpMixin } from './libs/mixin/mpMixin';
import { mixin } from './libs/mixin/mixin';
import { mapState } from 'pinia';
import { useCommonStore } from '@/stores/common';

export default {
	name: 'u-calendar-header',
	mixins: [mpMixin, mixin],
	props: {
		// 标题
		title: {
			type: String,
			default: ''
		},
		// 副标题
		subtitle: {
			type: String,
			default: ''
		},
		// 是否显示标题
		showTitle: {
			type: Boolean,
			default: true
		},
		// 是否显示副标题
		showSubtitle: {
			type: Boolean,
			default: true
		},
		// 星期文本
		weekText: {
			type: Array,
			default: () => {
				return []
			}
		},
	},
	computed: {
		...mapState(useCommonStore, ['locale']),
		displaySubtitle() {
			if (!this.subtitle) return '';
			const currentLocale = this.locale;

			// Try to parse year and month from the subtitle string
			// It might be in "YYYY年MM月" or "MM/YYYY" format
			let year, month;

			// Check for Chinese format "YYYY年MM月"
			const zhMatch = this.subtitle.match(/(\d{4})年(\d{1,2})月/);
			if (zhMatch) {
				year = zhMatch[1];
				month = zhMatch[2];
			} else {
				// Check for "MM/YYYY" format
				const slashMatch = this.subtitle.match(/(\d{1,2})\/(\d{4})/);
				if (slashMatch) {
					month = slashMatch[1];
					year = slashMatch[2];
				} else {
					// If no match, return original (fallback)
					return this.subtitle;
				}
			}

			// Format based on current locale
			const formattedMonth = parseInt(month) < 10 ? '0' + parseInt(month) : parseInt(month);

			if (currentLocale && currentLocale.startsWith('zh')) {
				return `${year}年${formattedMonth}月`;
			} else {
				return `${formattedMonth}/${year}`;
			}
		},
		displayWeekText() {
			const currentLocale = this.locale;
			if (currentLocale && currentLocale.startsWith('zh')) {
				return ['一', '二', '三', '四', '五', '六', '日'];
			} else if (currentLocale && currentLocale.startsWith('es')) {
				return ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
			} else {
				return ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
			}
		}
	},
	methods: {
		name() {

		}
	},
}
</script>

<style lang="scss" scoped>
.u-calendar-header {
	display: flex;
	flex-direction: column;
	padding-bottom: 4px;

	&__title {
		font-size: 16px;
		color: $u-main-color;
		text-align: center;
		height: 42px;
		line-height: 42px;
		font-weight: bold;
	}

	&__subtitle {
		font-size: 14px;
		color: $u-main-color;
		height: 40px;
		text-align: center;
		line-height: 40px;
		font-weight: bold;
	}

	&__weekdays {
		@include flex;
		justify-content: space-between;

		&__weekday {
			font-size: 13px;
			color: $u-main-color;
			line-height: 30px;
			flex: 1;
			text-align: center;
		}
	}
}
</style>
