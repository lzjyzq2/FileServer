<template>
  <a-layout class="g-container">
    <a-layout-sider
      breakpoint="xs"
      collapsedWidth="0"
      class="sider"
      :class="{break:breakpoint}"
      :theme="theme"
      :trigger="null"
      @breakpoint="onBreakpoint"
      :collapsed="cancollapse"
    >
      <nav-menu />
      <div class="trigger" v-if="breakpoint" @click="isfold = !isfold">
        <a-icon class="icon" type="menu-fold" />
      </div>
    </a-layout-sider>
    <router-view :breakpoint="breakpoint"></router-view>
  </a-layout>
</template>
<script>
import NavMenu from "./components/NavMenu";

export default {
  name: "App",
  components: {
    NavMenu
  },
  data: function() {
    return {
      theme: "dark",
      breakpoint: false,
      isfold: true
    };
  },
  computed: {
    cancollapse: function() {
      if (!this.breakpoint) {
        return false;
      } else {
        if (this.isfold) return true;
        else {
          return false;
        }
      }
    }
  },
  methods: {
    onBreakpoint: function(broken) {
      this.breakpoint = broken;
    }
  }
};
</script>

<style lang="less">
.g-container {
  height: 100vh;
}
.sider {
  z-index: 1;
  height: 100vh;
  &.break {
    position: absolute;
  }
  .trigger {
    position: absolute;
    top: 4px;
    width: 28px;
    height: 28px;
    right: -28px;
    text-align: center;
    line-height: 28px;
    background-color: white;
    .icon {
      position: relative;
      font-size: 18px;
    }
  }
}
body {
  overflow: hidden;
  position: fixed;
  left: 0;
  top: 0;
}
</style>
