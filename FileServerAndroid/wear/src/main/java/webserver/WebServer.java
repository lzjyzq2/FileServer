package webserver;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.j256.ormlite.table.TableUtils;

import cn.settile.lzjyzq2.fileserver.application.myapplication;
import database.DBHelper;
import fi.iki.elonen.NanoFileUpload;
import fi.iki.elonen.NanoHTTPD;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import webserver.responsebean.TempFileInfo;
import webserver.responsebean.VersionInfo;
import webserver.util.Config;
import webserver.util.MimeUtil;
import webserver.util.ServerFileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebServer extends NanoHTTPD {
    static final String TAG = "WebServer";
    String DOCUMENT = Config.getInstance().getWEBDOC();
    static Map<String, WebServerFilter> webServerFilterMap = new HashMap<>();

    public abstract class WebServerFilter {
        abstract Response response(IHTTPSession session, String mimetype);
    }

    private WebServer addWebServerFilter(String key, WebServerFilter webServerFilter) {
        webServerFilterMap.put(key, webServerFilter);
        return this;
    }

    public WebServer() {
        super(Config.getInstance().getPORT());
        initServer();
    }

    private void initServer() {
        WebServerFilter indexfilter = new WebServerFilter() {
            @Override
            public Response response(IHTTPSession s, String mimetype) {
                InputStream stream = ServerFileUtils.getInputStreamForAssets(DOCUMENT + "/index.html");
                return newChunkedResponse(Response.Status.OK, mimetype, stream);
            }
        };
        WebServerFilter assetsfilter = new WebServerFilter() {
            @Override
            Response response(IHTTPSession session, String mimetype) {
                InputStream stream = ServerFileUtils.getInputStreamForAssets(DOCUMENT + session.getUri());
                return newChunkedResponse(Response.Status.OK, mimetype, stream);
            }
        };
        addWebServerFilter("/index.html|/index|^/$", indexfilter)
                .addWebServerFilter("/static/css\\S*|/static/js\\S*|/static/img\\S*", assetsfilter)
//                .addWebServerFilter("/manage|/manage.html", new WebServerFilter() {
//                    @Override
//                    Response response(IHTTPSession session, String mimetype) {
//                        InputStream stream = ServerFileUtils.getInputStreamForAssets(DOCUMENT + "/manage.html");
//                        return newChunkedResponse(Response.Status.OK, mimetype, stream);
//                    }
//                })
                .addWebServerFilter("/favicon.ico", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        InputStream stream = ServerFileUtils.getInputStreamForAssets(DOCUMENT + "/favicon.ico");
                        return newChunkedResponse(Response.Status.OK, mimetype, stream);
                    }
                })
                .addWebServerFilter("/upload", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        Config.createDir(Config.getInstance().getUPLOAD());
                        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
                        diskFileItemFactory.setRepository(new File(Config.getInstance().getUPLOAD()));
                        NanoFileUpload uploader = new NanoFileUpload(diskFileItemFactory);
                        Map<String, List<FileItem>> files;
                        if (NanoFileUpload.isMultipartContent(session)) {
                            try {
                                files = uploader.parseParameterMap(session);
                                List<FileItem> fileItems = files.get("uploadfile");
                                for (FileItem fileItem : fileItems) {
                                    File file = new File(Config.getInstance().getUPLOAD() + File.separator + fileItem.getName());
                                    String type = fileItem.getName().substring(fileItem.getName().lastIndexOf('.'), fileItem.getName().length());
                                    String name = fileItem.getName().substring(0, fileItem.getName().lastIndexOf('.'));
                                    int index = 1;
                                    while (file.exists()) {
                                        file = new File(Config.getInstance().getUPLOAD() + File.separator + name + "(" + index + ")" + type);
                                        index++;
                                    }
                                    fileItem.write(file);
                                    return newFixedLengthResponse("Suss");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/getdiskinfo", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        Map<String, String> map = session.getParms();
                        String s = JSON.toJSONString(ServerFileUtils.getDiskInfo(Config.getInstance().getDISKPATH()));
                        return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, s);
                    }
                })
                .addWebServerFilter("/api/getdirectoryallinfo", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        try {
                            JSONObject jsonObject = WebServer.this.parseParms(session);
                            if (null != jsonObject && jsonObject.containsKey("path")) {
                                JSONArray path = jsonObject.getJSONArray("path");
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < path.size(); i++) {
                                    stringBuilder.append(path.getString(i)).append(ServerFileUtils.separator);
                                }
                                String s = JSON.toJSONString(ServerFileUtils.getDirectoryAllFileInfo(stringBuilder.toString()));
                                return newFixedLengthResponse(s);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/getuploaddirinfo", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        Config.createDir(Config.getInstance().getUPLOAD());
                        String s = JSON.toJSONString(ServerFileUtils.getUploaDirectoryInfo());
                        return newFixedLengthResponse(s);
                    }
                })
                .addWebServerFilter("/api/mkdir", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        try {
                            JSONObject parms = WebServer.this.parseParms(session);
                            if (null != parms && parms.containsKey("path") && parms.containsKey("dirname")) {
                                JSONArray path = parms.getJSONArray("path");
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < path.size(); i++) {
                                    stringBuilder.append(path.getString(i)).append(ServerFileUtils.separator);
                                }
                                ServerFileUtils.mkdir(stringBuilder.toString(), parms.getString("dirname"));
                                return newFixedLengthResponse("suss");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/delfile", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        try {
                            JSONObject parms = WebServer.this.parseParms(session);
                            if (null != parms && parms.containsKey("path") && parms.containsKey("name")) {
                                JSONArray path = parms.getJSONArray("path");
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < path.size(); i++) {
                                    stringBuilder.append(path.getString(i)).append(ServerFileUtils.separator);
                                }
                                ServerFileUtils.DeleteFile(stringBuilder.toString(), parms.getString("name"));
                                return newFixedLengthResponse("suss");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/delallfile", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        try {
                            JSONObject parms = WebServer.this.parseParms(session);
                            if (null != parms && parms.containsKey("path") && parms.containsKey("names")) {
                                JSONArray path = parms.getJSONArray("path");
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < path.size(); i++) {
                                    stringBuilder.append(path.getString(i)).append(ServerFileUtils.separator);
                                }
                                JSONArray names = parms.getJSONArray("names");
                                for (int i = 0; i < names.size(); i++) {
                                    ServerFileUtils.DeleteFile(stringBuilder.toString(), names.getString(i));
                                }
                                return newFixedLengthResponse("suss");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/download", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        try {
                            JSONObject parms = WebServer.this.parseParms(session);
                            if (null != parms && parms.containsKey("path") && parms.containsKey("name")) {
                                JSONArray path = parms.getJSONArray("path");
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < path.size(); i++) {
                                    stringBuilder.append(path.getString(i)).append(ServerFileUtils.separator);
                                }
                                InputStream inputStream = ServerFileUtils.DownloadFile(stringBuilder.toString(), parms.getString("name"));
                                return newFixedLengthResponse(Response.Status.OK, "application/octet-stream", inputStream, inputStream.available());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/rename", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        try {
                            JSONObject parms = WebServer.this.parseParms(session);
                            if (null != parms && parms.containsKey("path") && parms.containsKey("oldname") && parms.containsKey("newname")) {
                                JSONArray path = parms.getJSONArray("path");
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < path.size(); i++) {
                                    stringBuilder.append(path.getString(i)).append(ServerFileUtils.separator);
                                }
                                if (ServerFileUtils.RenameTo(stringBuilder.toString(), parms.getString("oldname"), parms.getString("newname"))) {
                                    return newFixedLengthResponse("suss");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/getdirs", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        try {
                            JSONObject parms = WebServer.this.parseParms(session);
                            if (null != parms && parms.containsKey("path")) {
                                JSONArray path = parms.getJSONArray("path");
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < path.size(); i++) {
                                    stringBuilder.append(path.getString(i)).append(ServerFileUtils.separator);
                                }
                                String s = JSON.toJSONString(ServerFileUtils.getDirs(stringBuilder.toString()));
                                return newFixedLengthResponse(s);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/removeto", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        try {
                            JSONObject parms = WebServer.this.parseParms(session);
                            if (null != parms && parms.containsKey("oldpath") && parms.containsKey("newpath") && parms.containsKey("name")) {
                                JSONArray oldpath = parms.getJSONArray("oldpath");
                                StringBuilder oldpathstr = new StringBuilder();
                                for (int i = 0; i < oldpath.size(); i++) {
                                    oldpathstr.append(oldpath.getString(i)).append(ServerFileUtils.separator);
                                }
                                JSONArray newpath = parms.getJSONArray("newpath");
                                StringBuilder newpathstr = new StringBuilder();
                                for (int i = 0; i < newpath.size(); i++) {
                                    newpathstr.append(newpath.getString(i)).append(ServerFileUtils.separator);
                                }
                                if (ServerFileUtils.RemoveTo(oldpathstr.toString(), newpathstr.toString(), parms.getString("name")))
                                    return newFixedLengthResponse("suss");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/getfileinfo", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        try {
                            JSONObject parms = WebServer.this.parseParms(session);
                            if (null != parms && parms.containsKey("path") && parms.containsKey("name")) {
                                JSONArray path = parms.getJSONArray("path");
                                StringBuilder pathstr = new StringBuilder();
                                for (int i = 0; i < path.size(); i++) {
                                    pathstr.append(path.getString(i)).append(ServerFileUtils.separator);
                                }
                                File file = new File(ServerFileUtils.getDirPath(pathstr.toString()) + parms.getString("name"));
                                if (file.exists()) {
                                    database.bean.TempFile tempFile = new database.bean.TempFile();
                                    tempFile.setPath(pathstr.toString());
                                    tempFile.setName(parms.getString("name"));
                                    if (file.isDirectory()) {
                                        tempFile.setType("folder");
                                    } else {
                                        tempFile.setType("file");
                                    }
                                    List<database.bean.TempFile> tempFiles = DBHelper.getInstance().getTempFiledao().queryBuilder().selectColumns("path", "name").where()
                                            .eq("path", pathstr.toString()).and().eq("name", parms.getString("name")).query();
                                    if (null != tempFiles && tempFiles.size() < 1) {
                                        DBHelper.getInstance().getTempFiledao().create(tempFile);
                                        TempFileInfo tempFileInfo = new TempFileInfo();
                                        tempFileInfo.id = ((database.bean.TempFile) DBHelper.getInstance().getTempFiledao().queryForMatching(tempFile).get(0)).getId();
                                        return newFixedLengthResponse(JSON.toJSONString(tempFileInfo));
                                    } else {
                                        TempFileInfo tempFileInfo = new TempFileInfo();
                                        tempFileInfo.id = tempFiles.get(0).getId();
                                        return newFixedLengthResponse(JSON.toJSONString(tempFileInfo));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/openfile", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        if (session.getParms().containsKey("id") && session.getParms().containsKey("name")) {
                            try {
                                database.bean.TempFile tempFile = (database.bean.TempFile) DBHelper.getInstance().getTempFiledao().queryForEq("id", Integer.parseInt(session.getParms().get("id"))).get(0);
                                mimetype = MimeUtil.getInstance().getMimeTypeforPath(tempFile.getName());
                                return newChunkedResponse(Response.Status.OK, mimetype, ServerFileUtils.DownloadFile(tempFile.getPath(), tempFile.getName()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/uploadtodir", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        Config.createDir(Config.getInstance().getUPLOAD());
                        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
                        diskFileItemFactory.setRepository(new File(Config.getInstance().getUPLOAD()));
                        NanoFileUpload uploader = new NanoFileUpload(diskFileItemFactory);
                        Map<String, List<FileItem>> files;
                        if (NanoFileUpload.isMultipartContent(session)) {
                            try {
                                files = uploader.parseParameterMap(session);
                                if (files.containsKey("uploadfile") && files.containsKey("path")) {
                                    List<FileItem> fileItems = files.get("uploadfile");
                                    String path = ServerFileUtils.getDirPath(files.get("path").get(0).getString().replace(',', ServerFileUtils.separator.charAt(0)));
                                    for (FileItem fileItem : fileItems) {
                                        File file = new File(path + File.separator + fileItem.getName());
                                        String type = fileItem.getName().substring(fileItem.getName().lastIndexOf('.'), fileItem.getName().length());
                                        String name = fileItem.getName().substring(0, fileItem.getName().lastIndexOf('.'));
                                        int index = 1;
                                        while (file.exists()) {
                                            file = new File(path + File.separator + name + "(" + index + ")" + type);
                                            index++;
                                        }
                                        fileItem.write(file);
                                        return newFixedLengthResponse("Suss");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/getversioninfo", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        try {
                            VersionInfo info = new VersionInfo();
                            info.setVersion(String.valueOf(myapplication.getVersionName(myapplication.getContext())));
                            return newFixedLengthResponse(JSON.toJSONString(info));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/getdefaultuploadpath", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        try {
                            return newFixedLengthResponse(Config.getInstance().getUPLOAD());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return newFixedLengthResponse("fail");
                    }
                })
                .addWebServerFilter("/api/cleartempdata", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        try {
                            TableUtils.clearTable(DBHelper.getInstance().getConnectionSource(), database.bean.TempFile.class);
                            return newFixedLengthResponse("suss");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return newFixedLengthResponse("fail");
                    }
                });
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        String mimetype;
        String type = uri.substring(uri.lastIndexOf('.') + 1, uri.length());
        if (type.equals("/")) {
            mimetype = "text/html";
        } else {
            mimetype = MimeUtil.getInstance().getMimeType(type);
        }
        for (Map.Entry<String, WebServerFilter> entry : webServerFilterMap.entrySet()) {
            Pattern uripattern = Pattern.compile(entry.getKey());
            Matcher matcher = uripattern.matcher(session.getUri());
            if (matcher.matches()) {
                return entry.getValue().response(session, mimetype);
            }
        }
        return getdefaultResponse();
    }

    private Response get404Response() {
        return newChunkedResponse(Response.Status.NOT_FOUND, MIME_HTML, ServerFileUtils.getInputStreamForAssets(DOCUMENT + "/404.html"));
    }

    private Response getdefaultResponse() {
        Log.e(TAG, "默认的响应");
        return newChunkedResponse(Response.Status.OK, MIME_HTML, ServerFileUtils.getInputStreamForAssets(DOCUMENT + "/index.html"));
    }

    @Override
    public void start() throws IOException {
        super.start();
        Log.e(TAG, "Server is Running:" + Config.getInstance().getPORT());
    }

    @Override
    public void stop() {
        super.stop();
    }

    public JSONObject parseParms(IHTTPSession session) {
        Map<String, String> parms = new HashMap<>();
        try {
            session.parseBody(parms);
            return JSONObject.parseObject(parms.get("postData"));
        } catch (IOException | ResponseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
