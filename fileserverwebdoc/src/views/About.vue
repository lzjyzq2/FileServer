<template>
    <a-layout-content class="about-content">
        <img src="../assets/logo.png" width="200px" height="200px"/>
        <p class="version">Version：{{version}}</p>
        <p>本软件用于文件上传，远程文件管理、访问等功能</p>
        <p>由setTile lzjyzq2制作</p>
        <p>可以扫描下方二维码进群反馈交流</p>
        <img src="../assets/joinQR.jpg" width="200px" height="274px"/>
        <div><a-button style="margin:5px 0px"><a href="https://jq.qq.com/?_wv=1027&k=5UiaOFF" target="_blank">或点击此处加入</a></a-button></div>
        <p>觉得好用的话，可以捐助~~~</p>
        <img />
    </a-layout-content>
</template>
<script>
export default {
    name:'About',
    data:function(){
        return {
            version:'-'
        }
    },
    mounted:function(){
        this.getVersionInfo()
    },
    methods:{
        getVersionInfo:function(){
            this.axios.get(
                '/api/getversioninfo'
            ).then((response)=>{
                if(response.data!='fail'){
                    this.version = response.data.version;
                }
            })
        }
    }
}
</script>

<style lang="less">
.about-content {
  background-color: white;
  padding: 0px 10px;
  overflow-x: hidden;
  overflow-y: auto;
  text-align: center;
  .version{
      font-size: 24px;
      color: black;
  }
}
</style>
