<template>
  <view class="list">
    <cu-custom bgColor="bg-gradual-green1" :isBack="true">
      <block slot="backText">返回</block>
      <block slot="content">师资力量</block>
    </cu-custom>
    <!-- 刷新页面后的顶部提示框 -->
    <!-- 当前弹出内容没有实际逻辑 ，可根据当前业务修改弹出提示 -->
    <!-- <view class="tips" :class="{ 'tips-ani': tipShow }"
      >为您更新了10条最新新闻动态</view
    > -->
    <view class="cu-bar bg-white solid-bottom">
      <view class="action">
        <text class="cuIcon-titles text-green1"></text> 师资力量
       <!-- <text
          class="lg text-gray teachers-btn"
          :class="waterfall ? 'cuIcon-sort' : 'cuIcon-apps'"
          @click="select"
        ></text> -->
      </view>
    </view>
    <!-- 基于 uni-list 的页面布局 -->
    <uni-list
      class="teachers-list"
      :class="{ 'uni-list--waterfall': waterfall }"
    >
      <!-- 通过 uni-list--waterfall 类决定页面布局方向 -->
      <uni-list-item
        :to="'/pages/teachers/detail/detail?id=' + item.id"
        :border="!waterfall"
        class="uni-list-item--waterfall teachers-item"
        title="自定义商品列表"
        v-for="item in lists"
        :key="item._id"
      >
        <!-- 通过header插槽定义列表左侧图片 -->
        <template v-slot:header>
          <view
            class="uni-thumb shop-picture"
            :class="{ 'shop-picture-column': waterfall }"
          >
            <image :src="item.photos"></image>
          </view>
        </template>
        <!-- 通过body插槽定义商品布局 -->
        <view slot="body" class="shop">
          <view class="uni-title nameBox">
            <text class="uni-ellipsis-2 username">{{ item.name }}</text>
          </view>
          <view>
            <view class="shop-price">
              <text class="shop-price-text">{{ item.rank }}</text>
              <text
                class="shop-price-text"
                style="margin: 0 5px"
                v-if="item.rank !== ''"
                >|</text
              >
              <text class="shop-price-text"> {{ item.college }}</text>
            </view>
          </view>
		  	<view class="uni-note">
		  		<text class="shop-price-text" style="color: red;">{{ item.viewCount?item.viewCount:0 }}</text>
		  		<text class="shop-price-text" style="margin: 0 5px;"></text>
		  		访问
			</view>
          <view class="teachers-icon margin-tb-sm text-center mem-attention">
            <text class="lg text-gray cuIcon-right"></text>
          </view>
        </view>
      </uni-list-item>
    </uni-list>
    <!-- 通过 loadMore 组件实现上拉加载效果，如需自定义显示内容，可参考：https://ext.dcloud.net.cn/plugin?id=29 -->
    <uni-load-more v-if="lists.length > 0" :status="status" />
  </view>
</template>

<script>
// 引入 clientDB 相关文件
import { getTeachersList } from "@/api/teachers.js";
export default {
  components: {},
  data() {
    return {
      lists: [], // 列表数据
      waterfall: false, // 布局方向切换
      status: "more", // 加载状态
      tipShow: false, // 是否显示顶部提示框
      pageSize: 10, // 每页显示的数据条数
      current: 1, // 当前页数
    };
  },
  onLoad(options) {
    // 初始化页面数据
    this.title = options.title;
    this.getTeachersListData();
  },
  methods: {
    getTeachersListData(reload, sort) {
      this.status = 'loading'
      let param = {
        pageNo: this.current,
        pageSize: this.pageSize,
        sort: sort ? sort : "id",
      };
      getTeachersList(param).then(data => {
        var [error, res] = data;
        if (res && res.data.success) {
          const tempList = res.data.result.content;
          // 判断是否可翻页
          if (tempList.length === this.pageSize) {
            this.status = "more";
          } else {
            this.status = "noMore";
          }
          if (reload) {
            // 处理下拉加载提示框
            this.tipShow = true;
            clearTimeout(this.timer);
            this.timer = setTimeout(() => {
              this.tipShow = false;
            }, 2000);
            this.lists = tempList;
            // 停止刷新
            uni.stopPullDownRefresh();
          } else {
            // 上拉加载后合并数据
            this.lists = this.lists.concat(tempList);
          }
          if (tempList.length) {
            this.current++;
          }
        }
      });
    },
    /**
     * 切换商品列表布局方向
     */
    select() {
      this.waterfall = !this.waterfall;
    },
    /**
     * 下拉刷新回调函数
     */
    onPullDownRefresh() {
      this.current = 1;
      this.getTeachersListData(true);
    },
    /**
     * 上拉加载回调函数
     */
    onReachBottom() {
      this.getTeachersListData();
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@/common/uni-ui.scss";

page {
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  background-color: #efeff4;
  min-height: 100%;
  height: auto;
}

.tips {
  color: #67c23a;
  font-size: 14px;
  line-height: 40px;
  text-align: center;
  background-color: #f0f9eb;
  height: 0;
  opacity: 0;
  transform: translateY(-100%);
  transition: all 0.3s;
}

.tips-ani {
  transform: translateY(0);
  height: 40px;
  opacity: 1;
}

.shop {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}
.shop .username {
  font-size: 16px;
}
.shop .nameBox{
	margin:0;
	padding: 0;
}
.shop .uni-note{
	margin-top:0;
}
.shop-picture {
  width: 70px;
  height: 90px;
}

.shop-picture-column {
  width: 100%;
  height: 170px;
  margin-bottom: 10px;
}

.shop-price {
  margin-top: 5px;
  font-size: 14px;
  color: #a8a7a7;
}

.shop-price-text {
  font-size: 14px;
}

.hot-tag {
  background: #ff5a5f;
  border: none;
  color: #fff;
}

.button-box {
  height: 30px;
  line-height: 30px;
  font-size: 12px;
  background: #007aff;
  color: #fff;
}

.uni-link {
  flex-shrink: 0;
}

.ellipsis {
  display: flex;
  overflow: hidden;
}

.uni-ellipsis-1 {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.uni-ellipsis-2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

// 默认加入 scoped ，所以外面加一层提升权重
.list {
  .uni-list--waterfall {
    /* #ifndef H5 || APP-VUE */
    // 小程序 编译后会多一层标签，而其他平台没有，所以需要特殊处理一下
    /deep/ .uni-list {
      /* #endif */
      display: flex;
      flex-direction: row;
      flex-wrap: wrap;
      padding: 5px;
      box-sizing: border-box;

      /* #ifdef H5 || APP-VUE */
      // h5 和 app-vue 使用深度选择器，因为默认使用了 scoped ，所以样式会无法穿透
      /deep/
				/* #endif */
				.uni-list-item--waterfall {
        width: 50%;
        box-sizing: border-box;

        .uni-list-item__container {
          padding: 5px;
          flex-direction: column;
		  align-items: center;
        }
      }

      /* #ifndef H5 || APP-VUE */
    }

    /* #endif */
  }
}

.teachers-btn {
  position: absolute;
  right: 10px;
}

.teachers-list {
  background-color: transparent;
}

.teachers-item {
  margin: 5px 0;
}

.teachers-icon {
  position: absolute;
  right: 20px;
  top: 80rpx;
}
</style>
