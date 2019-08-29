<template>
    <div class="progress-container">
      <div class="progress-pg" :style="{width:this.percent+'%',height:this.strokeWidth+'px'}"></div>
      <div class="progress-subpg" v-if="this.subpercent!=''" :style="{width:this.subpercent+'%',height:this.strokeWidth+'px'}"></div>
      <div class="progress-errpg" v-if="this.errpercent!=''" :style="{width:this.errpercent+'%',height:this.strokeWidth+'px'}"></div>
    </div>
</template>
<script>
export default {
  name: 'MyProgress',
  data: function() {
    return {};
  },
  props: {
    percent: Number,
    subpercent: Number,
    errpercent:Number,
    strokeWidth:Number
  }
};
</script>

<style>
.progress-container {
  border-radius: 100px;
  background-color: #f5f5f5;
  position: relative;
}
.progress-container .progress-pg {
  background-color: #1890ff;
  border-radius: 100px;
}
.progress-container .progress-subpg {
  background-color: #52c41a;
  position: absolute;
  top: 0px;
  left: 0px;
  border-radius: 100px;
}
.progress-container .progress-errpg {
  background-color: #f03434;
  position: absolute;
  top: 0px;
  left: 0px;
  border-radius: 100px;
}
</style>
