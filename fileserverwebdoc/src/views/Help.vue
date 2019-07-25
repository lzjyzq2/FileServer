<template>
  <a-layout-content class="help-content">
    <a-divider orientation="left">注意事项</a-divider>
      <div class="helpitem">
      <p class="title">注意事项</p>
      <div class="content">
          <p>●由于安全考虑，建议仅在局域网环境下运行。</p>
          <p>●当前设置仅支持清理缓存。</p>
      </div>
    </div>
    <a-divider orientation="left">文件上传</a-divider>
    <div class="helpitem">
      <p class="title">上传路径</p>
      <p class="content">●{{uploadpath}}</p>
    </div>

    <div class="helpitem">
      <p class="title">上传文件类型</p>
      <p class="content">●不支持文件夹、无扩展名、或不常见的扩展类型的文件。</p>
    </div>
    <div class="helpitem">
      <p class="title">操作</p>
      <div class="content">
          <p>●拖拽或点击上传区域选择文件，支持多选。</p>
          <p>●当上传文件大小超过剩余容量容量时，会显示通知。请自行移除部分上传文件，以免上传出错。</p>
          <p>●当开始上传后，会禁止移除上传文件。</p>
      </div>
    </div>
    <a-divider orientation="left">文件管理</a-divider>
    <div class="helpitem">
      <p class="title">上传文件</p>
      <div class="content">
          <p>●不支持文件夹、无扩展名、或不常见的扩展类型的文件。</p>
          <p>●仅支持单个文件上传。</p>
          <p>●上传目录为当前浏览文件夹。</p>
      </div>
    </div>
    <div class="helpitem">
      <p class="title">下载文件</p>
      <div class="content">
          <p>●当选中文件包括文件夹时，无法下载。</p>
          <p>●下载多个文件时，若浏览器提示是否允许下载多个文件，请点击允许。</p>
      </div>
    </div>

    <div class="helpitem">
      <p class="title">删除文件</p>
      <div class="content">
          <p>●删除选中的文件夹和文件。</p>
      </div>
    </div>

    <div class="helpitem">
      <p class="title">新建文件夹</p>
      <div class="content">
          <p>●不允许新文件夹名称包含.\/:*?"&lt;&gt;|</p>
      </div>
    </div>
    <div class="helpitem">
      <p class="title">重命名文件</p>
      <div class="content">
          <p>●不允许新文件夹名称包含\/:*?"&lt;&gt;|</p>
          <p>●(.)不允许在开头和结尾。</p>
      </div>
    </div>
    <div class="helpitem">
      <p class="title">打开文件</p>
      <div class="content">
          <p>●能被浏览器浏览的文件，能被正常打开浏览。（图片、txt文本文件、常见格式视频等）</p>
      </div>
    </div>
    <a-divider orientation="left">设置</a-divider>
    <div class="helpitem">
      <p class="title">清理临时文件与缓存</p>
      <div class="content">
          <p>●清理软件运行时产生的临时文件与缓存。</p>
      </div>
    </div>
  </a-layout-content>
</template>
<script>
export default {
  name: "Help",
  data: function() {
    return {
      uploadpath: "-"
    };
  },
  mounted: function() {
    this.getdefaultuploadpath();
  },
  methods: {
    getdefaultuploadpath: function() {
      this.axios.get("/api/getdefaultuploadpath").then(response => {
        if (response.data != "fail") {
          this.uploadpath = response.data;
        }
      });
    }
  }
};
</script>

<style lang="less">
.help-content {
  background-color: white;
  padding: 0px 10px;
  overflow-x: hidden;
  overflow-y: auto;
  text-align: left;
  .helpitem {
    .title {
      font-size: 15px;
      color: black;
    }
    .content {
      margin-left: 10px;
    }
  }
}
</style>
