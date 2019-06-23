package webserver;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import fi.iki.elonen.NanoFileUpload;
import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import webserver.util.Config;
import webserver.util.ServerFileUtils;

import java.io.*;
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
                InputStream stream = ServerFileUtils.getInputStreamForPathInAndroid(DOCUMENT + "/index.html");
                return newChunkedResponse(Response.Status.OK, mimetype, stream);
            }
        };
        WebServerFilter assetsfilter = new WebServerFilter() {
            @Override
            Response response(IHTTPSession session, String mimetype) {
                InputStream stream = ServerFileUtils.getInputStreamForPathInAndroid(DOCUMENT + session.getUri());
                return newChunkedResponse(Response.Status.OK, mimetype, stream);
            }
        };
        addWebServerFilter("/index.html|/index|/", indexfilter)
                .addWebServerFilter("/assets/css\\S*|/assets/js\\S*|/assets/i\\S*|/assets/fonts\\S*", assetsfilter)
                .addWebServerFilter("/manage|/manage.html", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        InputStream stream = ServerFileUtils.getInputStreamForPathInAndroid(DOCUMENT + "/manage.html");
                        return newChunkedResponse(Response.Status.OK, mimetype, stream);
                    }
                })
                .addWebServerFilter("/upload", new WebServerFilter() {
                    @Override
                    Response response(IHTTPSession session, String mimetype) {
                        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
                        diskFileItemFactory.setRepository(new File(Config.getInstance().getUPLOAD()));
                        NanoFileUpload uploader = new NanoFileUpload(diskFileItemFactory);
                        Map<String, List<FileItem>> files;
                        if (NanoFileUpload.isMultipartContent(session)) {
                            try {
                                files = uploader.parseParameterMap(session);
                                List<FileItem> fileItems = files.get("uploadfile");
                                for (FileItem fileItem : fileItems) {
//                                    InputStream inputStream = fileItem.getInputStream();
//                                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//                                    ServerFileUtils.SaveUploadFileToPath("F:\\VSC Project\\FileServer\\upload",fileItem.getName(),bufferedInputStream);
                                    File file = new File(Config.getInstance().getUPLOAD() + File.separator + fileItem.getName());
                                    String type = fileItem.getName().substring(fileItem.getName().lastIndexOf('.'), fileItem.getName().length());
                                    String name = fileItem.getName().substring(0, fileItem.getName().lastIndexOf('.'));
                                    int index = 1;
                                    while (file.exists()) {
                                        file = new File(Config.getInstance().getUPLOAD() + File.separator + name + "(" + index + ")" + type);
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
                        return newFixedLengthResponse(Response.Status.OK,MIME_PLAINTEXT,s);
                    }
                });
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        String mimetype;
        String type = uri.substring(uri.lastIndexOf('.') + 1, uri.length());
        switch (type) {
            case "html":
            case "htm":
                mimetype = "text/html";
                break;
            case "js":
                mimetype = "text/javascript";
                break;
            case "css":
                mimetype = "text/css";
                break;
            default:
                mimetype = "text/html";
                uri = "/index.html";
                break;
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
        return newChunkedResponse(Response.Status.NOT_FOUND, MIME_HTML, ServerFileUtils.getInputStreamForPathInAndroid(DOCUMENT + "/404.html"));
    }

    private Response getdefaultResponse() {
        Log.e(TAG, "默认的响应");
        return newChunkedResponse(Response.Status.OK, MIME_HTML, ServerFileUtils.getInputStreamForPathInAndroid(DOCUMENT + "/index.html"));
    }

    @Override
    public void start() throws IOException {
        super.start();
        Log.e(TAG, "Server is Running:" + Config.getInstance().getPORT());
    }

    @Override
    public void stop() {
        super.stop();
        Log.e(TAG, "Server is Stop");
    }
}
