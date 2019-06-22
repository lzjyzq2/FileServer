Vue.component('navmenu', {
  data: function () {
    return {
      count: 0,
      show: false
    }
  },
  template: `<div class="am-u-sm-12 am-u-md-2 am-u-lg-2 am-nav-menu">
    <div class="am-cf"><button class="am-btn am-show-sm-only am-btn-primary am-fr" v-on:click="show=!show"><span class="am-icon-bars"></span></button></div>
        <ul class="am-nav-menu-list" v-bind:class="{ 'am-show-md-up': !show }">
            <li><a href="./index.html">文件上传</a></li>
            <li><a href="./manage.html">文件管理</a></li>
            <li>设置</li>
        </ul>
    </div>`
})
Vue.component('fileitem', {
  props: {
    item: Object,
    itemtype: String,
    index: Number
  },
  data: function () {
    return {
      isActive: false,
      itemindex: this.index
    }
  },
  methods: {
    select: function () {
      this.isActive = !this.isActive;
      this.$emit('selectfileitem', this.itemindex, this.isActive);
    }
  },
  template: `
  <div class="am-u-sm-12 am-fileitem-box" @click="select" :class="{'active':isActive}" :style="{background:'linear-gradient(to right,#8de6bc 0%,#8de6bc '+item.info.progress+',white '+item.info.progress+', white 100%)'}">
    <i class="am-u-sm-1 am-text-center" :class="[itemtype=='file'?'am-icon-file':'am-icon-folder']"></i>
    <div class="am-u-sm-7 am-fileitem-name">{{item.file.name}}</div>
    <div class="am-u-md-3 am-show-md-up">{{item.info.type}}</div>
    <div class="am-u-sm-1 am-text-center" :class="[item.info.status?'am-icon-check-circle-o':'am-icon-cloud-upload']"></div>
  </div>
  `
})
Vue.component('uploadpanel', {
  data: function () {
    return {
      fileList: [],
      selectfileitemindex: [],
      index: 0,
      availableSpace: 0,
      usedSpace: 0,
      needSpace: 0,
      totalSpace: 0
    }
  },
  computed: {
    getavailableSpace: function () {
      return (this.availableSpace - this.needSpace) / this.totalSpace * 100 + "%";
    },
    getusedSpace: function () {
      return this.usedSpace / this.totalSpace * 100 + "%";
    },
    getneedSpace: function () {
      return this.needSpace / this.totalSpace * 100 + "%";
    },

  },
  methods: {
    choosefile: function () {
      this.$refs.inputfile.dispatchEvent(new MouseEvent('click'));
    },
    inputfile: function () {
      let data = this.$refs.inputfile.files;  // 获取文件对象
      console.log(data);
      if (data.length < 1) {
        return;
      }
      for (let i = 0; i < data.length; i++) {
        if (data[i].type == '') {
          continue;
        }
        let fileitem = {
          file: null,
          info: {
            progress: 0,
            status: false,
            type: ''
          }
        }
        let filelistlength = this.fileList.length;
        if (filelistlength < 1) {
          fileitem.file = data[i];
          fileitem.info.type = this.getfiletype(data[i].name);
          this.fileList.push(fileitem);
        } else {
          let hasfile = true;
          for (let m = 0; m < filelistlength; m++) {
            if ((data[i].name == this.fileList[m].name) && (data[i].lastModified == this.fileList[m].lastModified)) {
              hasfile = false;
              break;
            }
          }
          if (hasfile) {
            fileitem.file = data[i];
            fileitem.info.type = this.getfiletype(data[i].name);
            this.fileList.push(fileitem);
          }
        }
      }
      this.$refs.inputfile.value = "";
      console.log(this.$refs.inputfile);
    },
    getfiletype: function (name) {
      return name.split('.').pop().toLowerCase();
    },
    selectfileitem: function (index, isActive) {
      console.log(index);
      if (isActive) {
        this.selectfileitemindex.push(index);
      }
      else {
        let length = this.selectfileitemindex.length;
        for (let i = 0; i < length; i++) {
          if (index == this.selectfileitemindex[i]) {
            this.selectfileitemindex.splice(i, 1);
            i--;
          }
        }
      }
    },
    removefileitem: function () {
      let s_fileitem = [];
      for (let index = 0; index < this.selectfileitemindex.length; index++) {
        s_fileitem.push(this.fileList[this.selectfileitemindex[index]]);
      }
      for (let index = 0; index < this.fileList.length; index++) {
        for (let m = 0; m < s_fileitem.length; m++) {
          if (s_fileitem[m] == this.fileList[index]) {
            this.fileList.splice(index, 1);
          }
        }
      }
      this.selectfileitemindex = [];
    }
  },
  mounted: function () {
    this.$refs.uploadlist.ondragleave = (e) => {
      e.preventDefault();  //阻止离开时的浏览器默认行为
    };
    this.$refs.uploadlist.ondrop = (e) => {
      e.preventDefault();    //阻止拖放后的浏览器默认行为
      const data = e.dataTransfer.files;  // 获取文件对象
      if (data.length < 1) {
        return;  // 检测是否有文件拖拽到页面     
      }
      for (let i = 0; i < e.dataTransfer.files.length; i++) {
        if (e.dataTransfer.files[i].type == '') {
          continue;
        }
        let fileitem = {
          file: null,
          info: {
            progress: 0,
            status: false,
            type: ''
          }
        }
        let filelistlength = this.fileList.length;
        if (filelistlength < 1) {
          fileitem.file = e.dataTransfer.files[i];
          fileitem.info.type = this.getfiletype(e.dataTransfer.files[i].name);
          this.fileList.push(fileitem);
        } else {
          let hasfile = true;
          for (let m = 0; m < filelistlength; m++) {
            if ((e.dataTransfer.files[i].name == this.fileList[m].name) && (e.dataTransfer.files[i].lastModified == this.fileList[m].lastModified)) {
              hasfile = false;
              break;
            }
          }
          if (hasfile) {
            fileitem.file = e.dataTransfer.files[i];
            fileitem.info.type = this.getfiletype(e.dataTransfer.files[i].name);
            this.fileList.push(fileitem);
          }
        }
      }
    };
    this.$refs.uploadlist.ondragenter = (e) => {
      e.preventDefault();  //阻止拖入时的浏览器默认行为
      //this.$refs.select_frame.border = '2px dashed red';
    };
    this.$refs.uploadlist.ondragover = (e) => {
      e.preventDefault();    //阻止拖来拖去的浏览器默认行为
    };
    uploadmethod = (e) => {
      if (this.fileList.length > 0) {
        let config = {
          headers: { 'Content-Type': 'multipart/form-data' },
          onUploadProgress: progressEvent => {
            var complete = (progressEvent.loaded / progressEvent.total * 100 | 0);
            this.fileList[this.index].info.progress = complete + '%';
            if (complete == 100) {
              this.fileList[this.index].info.status = true;
              this.index++;
              if (this.index < this.fileList.length) {
                uploadmethod();
              }
            }
          }
        }
        let parms = new FormData();
        parms.append('uploadfile', this.fileList[this.index].file, this.fileList[this.index].file.name);
        axios.post("/upload", parms, config)
          .then(function (response) {

          });
      }
    }
    this.$refs.uploadbtn.onclick = uploadmethod;
    let diskinfoconfig = {

    };
    let vuedata = this;
    axios.post("/api/getdiskinfo", null, diskinfoconfig)
      .then(function (response) {
        vuedata.availableSpace = response.data.freespace;
        vuedata.usedSpace = response.data.usedspace;
        vuedata.totalSpace = response.data.totalspace;
      });
  },
  watch: {
    fileList: function () {
      if (this.fileList.length < 1) {
        this.needSpace = 0;
      } else {
        this.needSpace = 0;
        for (let i in this.fileList) {
          this.needSpace = this.needSpace + this.fileList[i].file.size;
        }
      }

    }
  },
  template:
    `<div class="am-u-sm-12 am-u-md-10 am-u-lg-10 am-main-content">
    <div class="am-tool-bar">
        <div class="am-progress">
            <div class="am-progress-bar am-progress-bar-secondary" v-bind:style="{'width':getusedSpace}"></div>
            <div class="am-progress-bar am-progress-bar-warning" v-bind:style="{'width':getneedSpace}"></div>
            <div class="am-progress-bar" v-bind:style="{'width':getavailableSpace,background:'gray'}"></div>
        </div>
        <input ref="inputfile" type="file" @change='inputfile' multiple="multiple" style="display:nono;width:0px;height:0px"></input>
        <div ref="choosebtn" @click="choosefile" class='am-btn am-btn-block am-main-content-choose'><i class="am-icon-plus-circle"></i>拖拽文件到此处或点击此处来选择上传文件
        </div>
    </div>
    <div>
      <div class="am-btn am-btn-secondary am-u-sm-12" @click="removefileitem"><i class='am-icon-remove'></i>删除</div>
      <!--<div class="am-btn am-btn-success am-u-sm-6"><i class='am-icon-check'></i>全选</div>-->
    </div>
    <div class="am-u-lg-12 am-u-md-12 am-u-sm-12 am-main-content-list" ref="uploadlist">
        <p v-if="fileList.length<1">暂无文件</p>
        <template v-else>
          <fileitem v-for="(item,index) in fileList" :item="item" :index="index" :key="item.file.lastModified+''+index" itemtype='file' v-on:selectfileitem="selectfileitem">
          </fileitem>
        </template>
    </div>
    <div ref="uploadbtn" class="am-btn am-btn-block am-btn-secondary am-main-content-upload">
        <i class="am-icon-cloud-upload"></i>
        上传
    </div>
</div>`
})
new Vue({
  el: "#navmenu"
});
new Vue({
  el: "#uploadpanel"
})