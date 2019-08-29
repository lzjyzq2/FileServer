<template>
  <div class="uploaditem">
    <a-row type="flex" justify="space-around" align="middle">
      <a-col :xs="5" class="name">{{this.itemdata.file.name}}</a-col>
      <a-col :xs="4" class="type">{{this.itemdata.info.type}}</a-col>
      <a-col :xs="12" :sm="14">
        <my-progress :percent="this.itemdata.info.progress" :subpercent="this.itemdata.info.status=='sub'?100:0" :errpercent="this.itemdata.info.status=='err'?100:0" :strokeWidth="8" />
      </a-col>
      <a-col :xs="3" :sm="1" style="text-align: center">
        <a-button shape="circle" icon="close" size="small" :disabled="this.disable" @click="removefile(index)"/>
      </a-col>
    </a-row>
  </div>
</template>
<script>
import MyProgress from "./MyProgress.vue";

export default {
  name: "UploadItem",
  components: {
    MyProgress
  },
  data: function() {
    return {};
  },
  props: {
      itemdata:Object,
      disable:Boolean,
      index:Number
  },
  methods: {
      removefile:function(){
          this.$emit('removefile', this.index);
      }
  }
};
</script>
<style lang="less">
.uploaditem {
  background-color: white;
  margin: 5px 0px;
  padding-left: 10px;
  padding-right: 0px;
  padding-top: 8px;
  padding-bottom: 8px;
  border: 1px solid @border-color-base;
  border-radius: @border-radius-base;
  .name {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .type{
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
.uploaditem:hover{
    border-color: @primary-color
}
</style>
