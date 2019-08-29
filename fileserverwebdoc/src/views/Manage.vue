<template>
  <a-layout>
    <a-layout-header class="g-header">
      <my-progress
        class="progress"
        :percent="computediskinfo"
        :strokeWidth="8"
        :class="[breakpoint?'progressbreak':'progress']"
      />
      <a-row style="line-height:1.5">
        <a-col :xs="12">
          <a-button-group>
            <a-button type="primary" icon="check" @click="checkAll" />
            <a-button
              type="primary"
              icon="plus"
              @click="mkdirvisible=!mkdirvisible"
            >{{breakpoint?'':'新建文件夹'}}</a-button>
            <a-modal
              title="新建文件夹"
              v-model="mkdirvisible"
              :confirmLoading="mkdirconfirmLoading"
              :okButtonProps="{ props: {disabled: mkdirOkdisable} }"
              @ok="handlemkdirOk"
              @cancel="mkdirname=''"
              :maskClosable="false"
            >
              <a-input
                ref="dirinput"
                placeholder="新文件夹名称"
                v-model="mkdirname"
                @change="checkdirname"
                @blur="checkdirname"
              />
              <p v-if="mkdirtipsvisible" style="color:red">输入正确的文件夹名称</p>
            </a-modal>
          </a-button-group>
        </a-col>
        <a-col :xs="12">
          <div style="float:right">
            <a-button-group>
              <a-button type="primary" icon="cloud-upload" @click="uploadvisible = true" />
              <a-modal
                title="上传文件"
                v-model="uploadvisible"
                @ok="uploadfileOk"
                @cancel="cancelupload"
                :confirmLoading="uploadconfirmLoading"
                class="uploadmodal"
              >
                <div>
                  <input
                    ref="inputfile"
                    type="file"
                    style="display:nono;width:0px;height:0px"
                    @change="inputfile"
                  />
                  <a-button @click="chooseuploadfile" :disabled="uploadbtndisable">
                    <a-icon type="upload" />Upload
                  </a-button>
                  <span>{{uploadfile==null?'暂未选择文件':uploadfile.name}}</span>
                </div>

                <p>文件会上传在当前文件夹下</p>
                <p>一次只能上传一个文件</p>
              </a-modal>

              <a-button type="primary" icon="cloud-download" @click="downloadselectfile" />
              <a-modal></a-modal>

              <a-button type="primary" icon="delete" @click="delfilevisible=true" />
              <a-modal
                title="确认"
                @ok="removeAllSelectFile"
                v-model="delfilevisible"
                :confirmLoading="delfileconfirmLoading"
              >
                <p>确认是否删除选中的文件与文件夹</p>
              </a-modal>
            </a-button-group>
          </div>
        </a-col>
      </a-row>
      <a-row type="flex" justify="space-around" align="middle" class="filepanel">
        <a-col :xs="15" :sm="11">文件名</a-col>
        <a-col :xs="6">文件大小</a-col>
        <a-col :xs="0" :sm="6">最后修改时间</a-col>
        <a-col :xs="3" :sm="1" style="text-align: center">更多</a-col>
      </a-row>
    </a-layout-header>
    <a-layout-content class="g-content">
      <a-spin :spinning="spinning">
        <a-icon slot="indicator" type="loading" style="font-size: 24px" spin />
        <file-item type="perent" v-if="hasPerent" @click.native="backPerent" />
        <file-item
          v-for="(item,index) in diritem"
          :key="item+''+index"
          :itemdata="item"
          :index="index"
          type="folder"
          :breakpoint="breakpoint"
          @chooseFileItem="chooseFileItem"
          @click.native="viewDir(item)"
          :ischecked="isChecked('folder',index)"
          @ddelfile="showdelmodal"
          @removefile="showremovemodal"
          @renamefile="showrenamemodal"
          @downloadfile="downloadfile"
          @openfile="openfile"
        />
        <file-item
          v-for="(item,index) in fileitem"
          :key="item.size+''+index"
          :index="index"
          :itemdata="item"
          type="file"
          :breakpoint="breakpoint"
          :ischecked="isChecked('file',index)"
          @chooseFileItem="chooseFileItem"
          @ddelfile="showdelmodal"
          @removefile="showremovemodal"
          @renamefile="showrenamemodal"
          @downloadfile="downloadfile"
          @openfile="openfile"
        />
      </a-spin>
      <a-modal
        title="删除文件"
        v-model="ddelfilevisible"
        :confirmLoading="ddelfileconfirmLoading"
        @ok="ddelfileOK"
        @cancel="cancelddel"
        :maskClosable="false"
      >
        <p>是否删除文件</p>
        <p>{{getddfilename}}</p>
      </a-modal>
      <a-modal
        title="移动文件"
        v-model="removefilevisible"
        :confirmLoading="removefileconfirmLoading"
        :okButtonProps="{ props: {disabled: hasremoveselect} }"
        @ok="removefileOk"
        @cancel="cancelremove"
      >
        <p>{{getddfilename}}</p>
        <a-tree :loadData="onLoadData" :treeData="treeData" @select="selecttodir" />
      </a-modal>
      <a-modal
        title="文件重命名"
        v-model="renamefilevisible"
        :confirmLoading="renamefileconfirmLoading"
        :okButtonProps="{ props: {disabled: renameOkdisable} }"
        @ok="renamefileOk"
        @cancel="cancelrename"
        :maskClosable="false"
      >
        <p>{{getddfilename}}</p>
        <a-input
          placeholder="新文件名称"
          v-model="renamename"
          @change="checkfilename"
          @blur="checkfilename"
        />
        <p v-if="renametipsvisible">输入合格的文件名</p>
      </a-modal>
    </a-layout-content>
  </a-layout>
</template>
<script>
import MyProgress from "../components/MyProgress";
import FileItem from "../components/FileItem";

export default {
  name: "Manage",
  components: {
    MyProgress,
    FileItem
  },
  props: {
    breakpoint: Boolean
  },
  data: function() {
    return {
      dirs: [],
      fileitem: [],
      diritem: [],
      mkdirname: "",
      mkdirvisible: false,
      mkdirconfirmLoading: false,
      mkdirtipsvisible: false,
      mkdirOkdisable: true,

      uploadfile: null,
      uploadvisible: false,
      uploadconfirmLoading: false,
      uploadbtndisable: false,

      delfilevisible: false,
      delfileconfirmLoading: false,

      spinning: false,

      selectdir: [],
      selectfile: [],

      ddelfilevisible: false,
      ddelfileconfirmLoading: false,

      renamefilevisible: false,
      renamefileconfirmLoading: false,
      renamename: "",
      renameOkdisable: true,
      renametipsvisible: false,

      removefilevisible: false,
      removefileconfirmLoading: false,
      hasremoveselect: true,
      newpath: [],

      ddselecttype: "",
      ddselectindex: null,

      treeData: [],
      totalspace: 0,
      usedspace: 0,
    };
  },
  computed: {
    hasPerent() {
      if (this.dirs.length <= 1) return false;
      else return true;
    },
    getddfilename: function() {
      if (this.fileitem.length > 0 || this.diritem.length > 0) {
        if (this.ddselecttype === "file") {
          return this.fileitem[this.ddselectindex].name;
        } else if (this.ddselecttype === "folder") {
          return this.diritem[this.ddselectindex];
        }
      }
      return "没有文件被选中";
    },
    computediskinfo:function(){
      return this.usedspace / this.totalspace *100;
    }
  },
  mounted: function() {
    this.axios.post("/api/getuploaddirinfo").then(response => {
      this.dirs = response.data.dirs;
      this.fileitem = response.data.allfileinfo.files;
      this.diritem = response.data.allfileinfo.directories;
    });
    this.getdiskinfo();
  },
  methods: {
    deletewarning: function() {},
    handlemkdirOk: function() {
      this.mkdirconfirmLoading = true;
      let name = this.$refs.dirinput.value;
      if (name == "") {
        this.mkdirtipsvisible = true;
      } else {
        this.mkdirtipsvisible = false;
        this.axios
          .post("/api/mkdir", {
            path: this.dirs,
            dirname: name
          })
          .then(response => {
            if (response.data == "suss") this.mkdirvisible = false;
            this.mkdirconfirmLoading = false;
            this.mkdirname = "";
            this.renewDirInfo();
          });
      }
    },
    checkdirname: function(e) {
      if (/[\\/:\*\?"<>\|\.]/.test(this.mkdirname) || this.mkdirname === "") {
        this.mkdirtipsvisible = true;
        this.mkdirOkdisable = true;
      } else {
        this.mkdirtipsvisible = false;
        this.mkdirOkdisable = false;
      }
    },
    backPerent: function(e) {
      this.dirs.pop();
      this.spinning = true;
      this.axios
        .post("/api/getdirectoryallinfo", { path: this.dirs })
        .then(response => {
          this.fileitem = response.data.files;
          this.diritem = response.data.directories;
          this.spinning = false;
          this.spinning = false;
          this.selectdir = [];
          this.selectfile = [];
          this.ddselecttype = "";
          this.ddselectindex = null;
        });
      this.getdiskinfo();
    },
    viewDir: function(e) {
      this.dirs.push(e);
      this.spinning = true;
      this.axios
        .post("/api/getdirectoryallinfo", { path: this.dirs })
        .then(response => {
          this.fileitem = response.data.files;
          this.diritem = response.data.directories;
          this.spinning = false;
          this.selectdir = [];
          this.selectfile = [];
          this.ddselecttype = "";
          this.ddselectindex = null;
        });
        this.getdiskinfo();
    },
    renewDirInfo: function() {
      this.spinning = true;
      this.axios
        .post("/api/getdirectoryallinfo", { path: this.dirs })
        .then(response => {
          this.fileitem = response.data.files;
          this.diritem = response.data.directories;
          this.spinning = false;
          this.ddselecttype = "";
          this.ddselectindex = null;
        });
        this.getdiskinfo();
    },
    chooseFileItem: function(type, index, action) {
      if (type === "file") {
        let length = this.selectfile.length;
        if (action) {
          for (let i = 0; i < length; i++) {
            if (this.selectfile[i] == index) {
              return;
            }
          }
          this.selectfile.push(index);
        } else {
          for (let i = 0; i < this.selectfile.length; i++) {
            if (this.selectfile[i] == index) {
              this.selectfile.splice(i, 1);
              i--;
            }
          }
        }
      }
      if (type === "folder") {
        let length = this.selectdir.length;
        if (action) {
          for (let i = 0; i < length; i++) {
            if (this.selectdir[i] == index) {
              return;
            }
          }
          this.selectdir.push(index);
        } else {
          for (let i = 0; i < this.selectdir.length; i++) {
            if (this.selectdir[i] == index) {
              this.selectdir.splice(i, 1);
              i--;
            }
          }
        }
      }
    },
    removeAllSelectFile: function() {
      let filelist = [];
      let length = this.selectdir.length;
      for (let i = 0; i < length; i++) {
        filelist.push(this.diritem[this.selectdir[i]]);
      }
      length = this.selectfile.length;
      for (let i = 0; i < length; i++) {
        filelist.push(this.fileitem[this.selectfile[i]].name);
      }
      this.delfileconfirmLoading = true;
      this.axios
        .post("/api/delallfile", { path: this.dirs, names: filelist })
        .then(response => {
          this.delfileconfirmLoading = false;
          this.delfilevisible = false;
          this.selectdir = [];
          this.selectfile = [];
          this.renewDirInfo();
        });
    },
    chooseuploadfile: function() {
      this.$refs.inputfile.dispatchEvent(new MouseEvent("click"));
    },
    inputfile: function() {
      let data = this.$refs.inputfile.files; // 获取文件对象
      if (data.length < 1) {
        return;
      }
      this.uploadfile = data[0];
    },
    uploadfileOk: function() {
      this.uploadconfirmLoading = true;
      this.uploadbtndisable = true;
      let config = {
        headers: { "Content-Type": "multipart/form-data" }
      };
      let parms = new FormData();
      parms.append("path", this.dirs);
      parms.append("uploadfile", this.uploadfile, this.uploadfile.name);
      this.axios.post("/api/uploadtodir", parms, config).then(response => {
        this.uploadconfirmLoading = false;
        this.uploadbtndisable = false;
        this.uploadvisible = false;
        this.$refs.inputfile.value = "";
        this.uploadfile = null;
        this.renewDirInfo();
      });
    },
    cancelupload: function() {
      this.$refs.inputfile.value = "";
      this.uploadfile = null;
    },
    isChecked(type, index) {
      if (type == "file") {
        let length = this.selectfile.length;
        for (let i = 0; i < length; i++) {
          if (this.selectfile[i] === index) return true;
        }
      } else if (type == "folder") {
        let length = this.selectdir.length;
        for (let i = 0; i < length; i++) {
          if (this.selectdir[i] === index) return true;
        }
      }
      return false;
    },
    checkAll: function() {
      let length = this.diritem.length;
      for (let i = 0; i < length; i++) {
        this.selectdir.push(i);
      }
      length = this.fileitem.length;
      for (let i = 0; i < length; i++) {
        this.selectfile.push(i);
      }
    },
    showremovemodal: function(type, index) {
      this.ddselecttype = type;
      this.ddselectindex = index;
      this.treeData.push({ title: this.dirs[0], key: "0" });
      this.removefilevisible = true;
    },
    removefileOk: function() {
      this.removefileconfirmLoading = true;
      console.log(this.getddfilename);
      this.axios
        .post("/api/removeto", {
          oldpath: this.dirs,
          newpath: this.newpath,
          name: this.getddfilename
        })
        .then(response => {
          this.removefileconfirmLoading = false;
          this.removefilevisible = false;
          this.treeData = [];
          this.hasremoveselect = true;
          this.renewDirInfo();
        });
    },
    cancelremove: function() {
      this.treeData = [];
    },
    onLoadData: function(treeNode) {
      let dirindex = treeNode.eventKey.split("-");
      let paths = [];
      let data = this.treeData;
      for (let i = 0; i < dirindex.length; i++) {
        paths.push(data[parseInt(dirindex[i])].title);
        data = data[parseInt(dirindex[i])].children;
      }
      return this.axios.post("/api/getdirs", { path: paths }).then(response => {
        if (response.data != "fail") {
          let children = [];
          for (let i = 0; i < response.data.length; i++) {
            children.push({
              title: response.data[i],
              key: treeNode.eventKey + "-" + i
            });
          }
          treeNode.dataRef.children = children;
          this.treeData = [...this.treeData];
        }
      });
    },
    selecttodir: function(selectedKeys, e) {
      let dirindex = selectedKeys[0].split("-");
      let paths = [];
      let data = this.treeData;
      for (let i = 0; i < dirindex.length; i++) {
        paths.push(data[parseInt(dirindex[i])].title);
        data = data[parseInt(dirindex[i])].children;
      }
      this.newpath = paths;
      this.hasremoveselect = false;
    },
    showdelmodal: function(type, index) {
      this.ddelfilevisible = true;
      this.ddselecttype = type;
      this.ddselectindex = index;
    },
    ddelfileOK: function() {
      let filelist = [];
      if (this.ddselecttype === "file") {
        filelist.push(this.fileitem[this.ddselectindex].name);
      } else if (this.ddselecttype === "folder") {
        filelist.push(this.diritem[this.ddselectindex]);
      }
        this.ddelfileconfirmLoading = true;
        this.axios
          .post("/api/delallfile", { path: this.dirs, names: filelist })
          .then(response => {
            this.ddelfilevisible = false;
            this.ddelfileconfirmLoading = false;
            this.selectdir = [];
            this.selectfile = [];
            this.renewDirInfo();
          });
    },

    cancelddel: function() {

    },
    showrenamemodal: function(type, index) {
      this.renamefilevisible = true;
      this.ddselecttype = type;
      this.ddselectindex = index;
    },
    checkfilename: function() {
      if (/^\.|[\\/:\*\?"<>]|\.$/.test(this.renamename)) {
        this.renameOkdisable = true;
        this.renametipsvisible = true;
      } else {
        this.renametipsvisible = false;
        this.renameOkdisable = false;
      }
    },
    renamefileOk: function() {
      this.renamefileconfirmLoading = true;
      let oldname = "";
      if (this.ddselecttype === "file") {
        oldname = this.fileitem[this.ddselectindex].name;
      } else if (this.ddselecttype === "folder") {
        oldname = this.diritem[this.ddselectindex];
      }
      this.axios
        .post("/api/rename", {
          path: this.dirs,
          oldname: oldname,
          newname: this.renamename
        })
        .then(response => {
          this.renamefilevisible = false;
          this.renamefileconfirmLoading = false;
          this.renameOkdisable = false;
          this.renamename = "";
          this.renametipsvisible = false;
          this.selectdir = [];
          this.selectfile = [];
          this.renewDirInfo();
        });
    },
    cancelrename: function() {
      this.renamename = "";
    },
    downloadfile: function(type, index) {
      this.ddselecttype = type;
      this.ddselectindex = index;
      let downloadfilename = this.getddfilename;
      this.axios
        .post(
          "/api/download",
          {
            path: this.dirs,
            name: this.getddfilename
          },
          {
            responseType: "blob"
          }
        )
        .then(response => {
          const blob = new Blob([response.data], {
            type: "application/octet-stream"
          });
          if ("download" in document.createElement("a")) {
            // 非IE下载
            const elink = document.createElement("a");
            elink.download = downloadfilename;
            elink.style.display = "none;width:0px;height:0px";
            elink.href = URL.createObjectURL(blob);
            document.body.appendChild(elink);
            elink.click();
            URL.revokeObjectURL(elink.href); // 释放URL 对象
            document.body.removeChild(elink);
          } else {
            // IE10+下载
            navigator.msSaveBlob(blob, fileName);
          }
        });
    },
    openfile: function(type, index) {
      this.ddselecttype = type;
      this.ddselectindex = index;
      this.axios
        .post("/api/getfileinfo", {
          path: this.dirs,
          name: this.getddfilename
        })
        .then(response => {
          if (response.data != "fail") {
            console.log(response.data);
            const elink = document.createElement("a");
            let url =
              "/api/openfile?id=" +
              response.data.id +
              "&name=" +
              this.getddfilename;
            elink.style.display = "none;width:0px;height:0px";
            elink.href = url;
            elink.target = "_blank";
            document.body.appendChild(elink);
            elink.click();
            URL.revokeObjectURL(elink.href); // 释放URL 对象
            document.body.removeChild(elink);
          }
        });
    },
    downloadselectfile: function() {
      if (this.selectdir.length > 0) {
        this.$message.error('无法下载文件夹');
      } else {
        let downfiles = this.selectfile;
        for (let i = 0; i < downfiles.length; i++) {
          let downloadfilename = this.fileitem[parseInt(downfiles[i])].name;
          this.axios
            .post(
              "/api/download",
              {
                path: this.dirs,
                name: downloadfilename
              },
              {
                responseType: "blob"
              }
            )
            .then(response => {
              const blob = new Blob([response.data], {
                type: "application/octet-stream"
              });
              if ("download" in document.createElement("a")) {
                // 非IE下载
                const elink = document.createElement("a");
                elink.download = downloadfilename;
                elink.style.display = "none;width:0px;height:0px";
                elink.href = URL.createObjectURL(blob);
                document.body.appendChild(elink);
                elink.click();
                URL.revokeObjectURL(elink.href); // 释放URL 对象
                document.body.removeChild(elink);
              } else {
                // IE10+下载
                navigator.msSaveBlob(blob, fileName);
              }
            });
        }
      }
    },
    getdiskinfo: function() {
      this.axios.post("/api/getdiskinfo").then(response => {
        this.usedspace = response.data.usedspace;
        this.totalspace = response.data.totalspace;
      });
    }
  }
};
</script>
<style lang="less">
.g-header {
  background: white;
  height: auto;
  padding: 4px 10px;
  .progress {
    margin: 10px 0px;
  }
  .progressbreak {
    margin: 10px 0px 10px 22px;
  }
}
.g-content {
  background-color: white;
  padding: 0px 10px;
  overflow-x: hidden;
  overflow-y: auto;
}
.g-footer {
  background: white;
  padding: 5px 10px;
}
.filepanel {
  background-color: white;
  margin: 5px 0px;
  padding-left: 10px;
  padding-right: 0px;
  padding-top: 8px;
  padding-bottom: 8px;
  line-height: 1.5;
}
.uploadmodal {
  p {
    margin-bottom: 0;
  }
  span {
    margin-left: 5px;
  }
}
</style>