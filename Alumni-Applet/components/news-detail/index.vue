<template>
  <div class="hm-news-detail">
    <div class="hd">
      <img class="yunshu" :src="options.yunshu ? options.yunshu : yunshu" />
      <div class="container">
        <div class="outer">
          <span class="author">{{ options.createBy }}</span>
          <div class="wrap">
            <button
              class="like hm-btn"
              :class="collect !== 0 ? 'cuIcon-likefill' : 'cuIcon-like'"
              @click="likeHandler"
            ></button>
            <!-- <button open-type="share">s </button> -->
            <!-- <view class="share cuIcon-share hm-btn" @click="shareHandler"></view> -->
            <button
              class="share cuIcon-share hm-btn"
              open-type="share"
            ></button>
          </div>
        </div>
        <span class="time">{{ formatDate(options.createTime) }}</span>
      </div>
    </div>
    <view class="news-title">{{ options.title }}</view>
    <!--  <div class="main" v-for="photo in photos">
      <div class="entryPicWrap">
		  <image class="img" :src="photo" mode="aspectFill"></image>
		  </div>
    </div> -->
    <div class="ft">
      <view class="new-content" v-html="options.contents"></view>
    </div>
    <!-- <view class="hm-new-bottom">
		<text class="hmnb-item cuIcon-forward">10</text>
			<text class="hmnb-item cuIcon-appreciate">10</text>
			
			<text class="hmnb-item cuIcon-like">10</text>
	</view> -->
    <view class="hm-new-bottoms"> </view>
  </div>
</template>
<script>
import { addCollect, delteCollect } from "@/api/user.js";
export default {
  name: "HmNewsDetail",
  props: {
    dataId: {
      type: String,
      default: "hm-news-detail",
    },
    collect: {
      type: Number,
      default: 0,
    },
    photos: {
      type: Array,
      default: [],
    },
    options: {
      type: Object,
      default: function () {
        return {
          createBy: "",
          createTime: 1603468800000,
          title: "",
          thumb: "[]",
          contents: "",
          id: "",
          collect: 0,
        };
      },
    },
  },
  data() {
    return {
      yunshu: "http://cdxyh.stickeronline.cn/logo.jpg",
      like:
        "http://www.imapway.cn/Alumni/static/hm-news-detail/images/img_22946_0_1.png",
      share:
        "http://www.imapway.cn/Alumni/static/hm-news-detail/images/img_22946_0_2.png",
      thumb: "",
      islike: false,
    };
  },
  methods: {
    formatDate(data) {
      return getApp().formatDate(data);
    },
    likeHandler() {
      if (this.collect === 0) {
        this.addCollect();
      } else {
        this.delteCollect();
      }
    },
    addCollect() {
      let openId = uni.getStorageSync("openid");
      let params = {
        type: 1,
        recordId: this.options.id,
        recordTitle: this.options.title,
        userId: openId,
      };
      if (openId) {
        addCollect(params).then(data => {
          var [error, res] = data;
          if (res && res.data.success) {
            uni.showToast({
              title: "收藏成功",
            });
            this.$parent.changeCollect(1);
          }
        });
      } else {
        getApp().getUserInfo();
      }
    },
    delteCollect() {
      let openId = uni.getStorageSync("openid");
      let params = {
        recordId: this.options.id,
        userId: openId,
      };
      if (openId) {
        delteCollect(params).then(data => {
          var [error, res] = data;
          if (res && res.data.success) {
            uni.showToast({
              title: "取消收藏",
            });
            this.$parent.changeCollect(0);
          }
        });
      } else {
        getApp().getUserInfo();
      }
    },
    shareHandler() {
      this.$emit("shareHandler");
    },
  },
};
</script>
<style lang="less" scoped>
@import "./index.response.css";
.new-content {
  width: 100%;
  /* overflow: hidden;
	    text-overflow: ellipsis;
	    line-height: 62rpx;
	    letter-spacing: 0px;
	    white-space: pre-wrap;
	    color: #000000;
	    font-size: 36rpx;
	    font-weight: 400; */
  padding: 0 10rpx;
  // text-indent: 2em;
}
.news-title {
  font-size: 32rpx;
  font-weight: bold;
  text-align: center;
  padding: 10rpx 60rpx;
  width: 100%;
}
.hm-btn {
  color: #ff8901;
  width: 60rpx;
  height: 60rpx;
  border: 0rpx;
  padding: 0;
  margin: 0rpx 2rpx;
  background: #ffffff;
  border-color: #fff;
  line-height: 60rpx;
}
.hd {
  min-height: 80rpx;
}
.ft {
  padding: 10rpx 30rpx;
}
.share {
  width: 60rpx;
  height: 60rpx;
  border: 0rpx;
}
.hm-btn::after {
  border: none;
}
.hm-new-bottom {
  width: 100%;
  padding: 10rpx 32rpx;
}
.hmnb-item {
  line-height: 80rpx;
  padding: 0 10rpx;
  font-size: 26rpx;
  float: right;
}
.hm-new-bottoms {
  height: 60rpx;
  width: 100%;
}
</style>
