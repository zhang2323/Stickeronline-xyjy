<template>
  <view>
    <cu-custom bgColor="bg-gradual-green1" :isBack="true">
      <block slot="backText">返回</block>
      <block slot="content">我的关注</block>
    </cu-custom>
    <!-- <view class="gmember-title cu-bar bg-white solid-bottom">
			<view class="action">
				<text class="cuIcon-titles text-green1"></text> 我关注的
				<span class="gmember-count">
					<text>共263人</text>
				</span>
			</view>
		</view> -->
    <view
      v-if="lists.length === 0"
      style="margin: 20px auto; color: #00beb7; text-align: center"
      >您还未关注任何人哦~</view
    >
    <scroll-view
      scroll-y
      class="indexes"
      :scroll-into-view="'indexes-' + listCurID"
      :style="[{ height: 'calc(100vh - ' + CustomBar + 'px - 50px)' }]"
      :scroll-with-animation="true"
      :enable-back-to-top="true"
    >
      <block v-for="(item, index) in lists" :key="index">
        <view
          :class="'indexItem-' + item.name"
          :id="'indexes-' + item.name"
          :data-index="item.name"
        >
          <view class="cu-list menu-avatar no-padding">
            <view class="cu-item">
              <view
                v-if="item.avatarUrl"
                class="cu-avatar round lg"
                :style="'background-image:url(' + item.avatarUrl + ');'"
              ></view>
              <view
                v-else
                class="cu-avatar round lg"
                style="
                  background-image: url('http://www.imapway.cn/Alumni/static/alumnus/default_photo.png');
                "
              ></view>
              <view class="content">
                <view class="text-grey"
                  >{{ item.name }}<text class="text-abc"></text>
                </view>
              </view>
              <view
                class="margin-tb-sm text-center mem-attention"
                v-if="userId === openId"
              >
                <button
                  @click="delAttention(item)"
                  class="cu-btn round bg-yellow"
                >
                  已关注
                </button>
              </view>
              <!-- <view
                class="margin-tb-sm text-center mem-attention"
                v-if="userId === openId"
              >
                <button
                  @click="delAttention(item)"
                  v-if="item.attention && item.attention == 1"
                  class="cu-btn round bg-yellow"
                >
                  已关注
                </button>
                <button
                  @click="payHandler(item)"
                  v-else
                  class="cu-btn round bg-gradual-green1"
                >
                  关注
                </button>
              </view> -->
            </view>
          </view>
        </view>
      </block>
    </scroll-view>
  </view>
</template>

<script>
import {
  queryAttentionListByUserId,
  addWechatUserAttention,
  deleteWechatUserAttention,
} from "@/api/user.js";
export default {
  data() {
    return {
      title: "",
      StatusBar: this.StatusBar,
      CustomBar: this.CustomBar,
      hidden: true,
      listCurID: "",
      lists: [
        // {
        //   name: "张三",
        //   photo:
        //     "http://js.chd.edu.cn/_upload/article/images/df/42/4ad7726f43dfb1815313acc22675/740fe51e-61c5-4cfe-a160-9064c54cee06_s.png",
        //   attention: 0,
        // },
      ],
      listCur: "",
      userId: "",
      openId: "",
    };
  },
  onLoad(options) {
    if (options.userId) {
      this.userId = options.userId;
    } else {
      this.userId = uni.getStorageSync("openid");
    }
    this.openId = uni.getStorageSync("openid");

    this.queryAttentionListByUserId(true);
  },
  onReady() {
    let that = this;
  },
  methods: {
    // payHandler(item) {
    //   //获取用户ID
    //   // let userId = uni.getStorageSync("openid");
    //   let params = {
    //     memberId: item.id,
    //     userId: this.userId,
    //   };
    //   addWechatUserAttention(params).then(data => {
    //     // debugger
    //     let [error, res] = data;
    //     if (res && res.data && res.data.success) {
    //       uni.showToast({
    //         title: "关注成功",
    //         duration: 2000,
    //       });
    //       item.attention = 1;
    //     } else {
    //       uni.showToast({
    //         title: "服务器忙",
    //         duration: 2000,
    //       });
    //     }
    //   });
    // },
    delAttention(item) {
      // let userId = uni.getStorageSync("openid");
      let params = {
        memberId: item.openid,
        userId: this.userId,
      };
      deleteWechatUserAttention(params).then(data => {
        // debugger
        let [error, res] = data;
        if (res && res.data && res.data.success) {
          uni.showToast({
            title: "取消关注",
            duration: 2000,
          });
          item.attention = 0;
          this.queryAttentionListByUserId(true);
        } else {
          uni.showToast({
            title: "服务器忙",
            duration: 2000,
          });
        }
      });
    },
    /**
     * 获取页面数据
     * @param {Object} reload 参数reload值为true时执行列表初始化逻辑，值为false时执行追加下一页数据的逻辑。默认为false
     */
    queryAttentionListByUserId(reload) {
      let that = this;
      this.status = "loading";
      let openid = this.userId;
      if (openid && openid != "") {
        let param = {
          userId: openid,
        };
        queryAttentionListByUserId(param).then(data => {
          var [error, res] = data;
          if (res && res.data.success) {
            const tempList = res.data.result.content;
            // this.lists = res.data.result.content;
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
              that.lists = tempList;
              // 停止刷新
              uni.stopPullDownRefresh();
            } else {
              // 上拉加载后合并数据
              that.lists = that.lists.concat(tempList);
            }
            if (tempList.length) {
              this.current++;
            }
          }
        });
      } else {
        getApp().getUserInfo();
      }
    },
    //获取文字信息
    getCur(e) {
      this.hidden = false;
      this.listCur = this.list[e.target.id].name;
    },
    setCur(e) {
      this.hidden = true;
      this.listCur = this.listCur;
    },
    //滑动选择Item
    tMove(e) {
      let y = e.touches[0].clientY,
        offsettop = this.boxTop,
        that = this;
      //判断选择区域,只有在选择区才会生效
      if (y > offsettop) {
        let num = parseInt((y - offsettop) / 20);
        this.listCur = that.list[num].name;
      }
    },

    //触发全部开始选择
    tStart() {
      this.hidden = false;
    },

    //触发结束选择
    tEnd() {
      this.hidden = true;
      this.listCurID = this.listCur;
    },
    indexSelect(e) {
      let that = this;
      let barHeight = this.barHeight;
      let list = this.list;
      let scrollY = Math.ceil((list.length * e.detail.y) / barHeight);
      for (let i = 0; i < list.length; i++) {
        if (scrollY < i + 1) {
          that.listCur = list[i].name;
          that.movableY = i * 20;
          return false;
        }
      }
    },
  },
};
</script>

<style>
.indexes {
  position: relative;
}

.indexBar {
  position: fixed;
  right: 0px;
  bottom: 0px;
  padding: 20upx 20upx 20upx 60upx;
  display: flex;
  align-items: center;
}

.indexBar .indexBar-box {
  width: 40upx;
  height: auto;
  background: #fff;
  display: flex;
  flex-direction: column;
  box-shadow: 0 0 20upx rgba(0, 0, 0, 0.1);
  border-radius: 10upx;
}

.indexBar-item {
  flex: 1;
  width: 40upx;
  height: 40upx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24upx;
  color: #888;
}

movable-view.indexBar-item {
  width: 40upx;
  height: 40upx;
  z-index: 9;
  position: relative;
}

movable-view.indexBar-item::before {
  content: "";
  display: block;
  position: absolute;
  left: 0;
  top: 10upx;
  height: 20upx;
  width: 4upx;
  background-color: #f37b1d;
}

.indexToast {
  position: fixed;
  top: 0;
  right: 80upx;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  width: 100upx;
  height: 100upx;
  border-radius: 10upx;
  margin: auto;
  color: #fff;
  line-height: 100upx;
  text-align: center;
  font-size: 48upx;
}

.gmember-title {
}

.gmember-count {
  position: absolute;
  right: 10px;
}

.mem-attention {
  margin-right: 10px;
}
</style>
