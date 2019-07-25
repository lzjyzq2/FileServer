<template>
  <a-menu
    :defaultSelectedKeys="['1']"
    mode="vertical"
    :theme="theme"
    :selectedKeys="[getcurrent]"
    @click="handleClick"
  >
    <a-menu-item key="1">
      <router-link to="/">文件上传</router-link>
    </a-menu-item>
    <a-menu-item key="2">
      <router-link to="/manage">文件管理</router-link>
    </a-menu-item>
    <a-menu-item key="3">
      <router-link to="/settings">设置</router-link>
    </a-menu-item>
    <a-menu-item key="4">
      <router-link to="/help">使用帮助</router-link>
    </a-menu-item>
    <a-menu-item key="5">
      <router-link to="/about">关于</router-link>
    </a-menu-item>
  </a-menu>
</template>

<script>
export default {
  name: "NavMenu",
  data: function() {
    return {
      current: "1",
      theme: "dark"
    };
  },
  computed: {
    getcurrent: function() {
      switch (this.$route.path) {
        case "/":
          return "1";
        case "/manage":
          document.title = "文件管理";
          return "2";
        case "/settings":
          document.title = "设置";
          return "3";
        case "/help":
          document.title = "使用帮助";
          return "4";
        case "/about":
          document.title = "关于";
          return "5";
      }
    }
  },
  methods: {
    handleClick: function(e) {
      this.current = e.key;
    }
  }
};
</script>
<style>
</style>
