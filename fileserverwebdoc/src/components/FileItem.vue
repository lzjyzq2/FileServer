<template>
  <div class="fileitem">
    <a-row type="flex" justify="space-around" align="middle">
      <span v-if="type=='perent'" style="float:left">
        <a-icon type="up" />
      </span>
      <template v-else>
        <a-col :xs="15" :sm="11" class="name">
          <a-checkbox @change="choosefileitem" @click.stop :checked="ischecked"></a-checkbox>
          <a-icon
            :theme="'filled'"
            style="font-size:24px;margin:0px 5px"
            :type="type=='file'?'file':'folder'"
          />
          {{type=='file'?this.itemdata.name:this.itemdata}}
        </a-col>
        <a-col :xs="6" class="size">{{ type=='file'?getfilesize:'-'}}</a-col>
        <a-col :xs="0" :sm="6">{{type=='file'?gettime:'-'}}</a-col>
        <a-col :xs="3" :sm="1" style="text-align: center">
          <a-popover v-model="popover" placement="topRight">
            <template slot="content">
              <a-button-group>
                <a-button v-if="type=='file'" icon="login" size="small" @click="openfile">{{breakpoint?'':'打开'}}</a-button>
                <a-button icon="rollback" size="small" @click="removefile">{{breakpoint?'':'移动'}}</a-button>
                <a-button icon="edit" size="small" @click="renamefile">{{breakpoint?'':'重命名'}}</a-button>
                <a-button v-if="type=='file'" icon="cloud-download" size="small" @click="downloadfile">{{breakpoint?'':'下载'}}</a-button>
                <a-button icon="delete" size="small" @click="ddelfile">{{breakpoint?'':'删除'}}</a-button>
              </a-button-group>
            </template>
            <a-button shape="circle" icon="ellipsis" size="small" @click.stop="popover=true" />
          </a-popover>
        </a-col>
      </template>
    </a-row>
  </div>
</template>
<script>
export default {
  name: "FileItem",
  data: function() {
    return {
      popover: false
    };
  },
  props: {
    breakpoint: Boolean,
    itemdata: Object|String,
    type: String,
    show: Boolean,
    index: Number,
    ischecked: Boolean
  },
  computed: {
    getfilesize: function() {
      let fileSizeByte = this.itemdata.size;
      let fileSizeMsg = "";
      if (fileSizeByte < 1048576) fileSizeMsg = (fileSizeByte / 1024).toFixed(2) + "KB";
      else if (fileSizeByte == 1048576) fileSizeMsg = "1MB";
      else if (fileSizeByte > 1048576 && fileSizeByte < 1073741824)
        fileSizeMsg = (fileSizeByte / (1024 * 1024)).toFixed(2) + "MB";
      else if (fileSizeByte > 1048576 && fileSizeByte == 1073741824)
        fileSizeMsg = "1GB";
      else if (fileSizeByte > 1073741824 && fileSizeByte < 1099511627776)
        fileSizeMsg = (fileSizeByte / (1024 * 1024 * 1024)).toFixed(2) + "GB";
      else fileSizeMsg = "文件超过1TB";
      return fileSizeMsg;
    },
    gettime: function() {
      let date = new Date(this.itemdata.time);
      let myyear = date.getFullYear();
      let mymonth = date.getMonth() + 1;
      let myweekday = date.getDate();
      let myhour = date.getHours();
      let mymuinute = date.getMinutes();
      if (mymonth < 10) {
        mymonth = "0" + mymonth;
      }
      if (myweekday < 10) {
        myweekday = "0" + myweekday;
      }
      if (myhour < 10) myhour = "0" + myhour;
      if (mymuinute < 10) mymuinute = "0" + mymuinute;
      return (
        myyear +
        "-" +
        mymonth +
        "-" +
        myweekday +
        " " +
        myhour +
        ":" +
        mymuinute
      );
    }
  },
  methods: {
    choosefileitem: function(e) {
      this.$emit("chooseFileItem", this.type, this.index, e.target.checked);
    },
    ddelfile: function() {
      this.$emit("ddelfile", this.type, this.index);
    },
    renamefile: function() {
      this.$emit("renamefile", this.type, this.index);
    },
    removefile: function() {
      this.$emit("removefile", this.type, this.index);
    },
    downloadfile: function() {
      this.$emit("downloadfile", this.type, this.index);
    },
    openfile:function(){
      this.$emit("openfile", this.type, this.index);
    }
  }
};
</script>
<style lang="less">
.fileitem {
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
  .size {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
.fileitem:hover {
  border-color: @primary-color;
}
</style>
