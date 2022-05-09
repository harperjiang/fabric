package couchdblogger.servlet;

import couchdblogger.WorkloadLogger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Collections;

public class LoggerServlet extends HttpServlet {

    CloseableHttpClient httpclient = HttpClients.createDefault();

    private String couchdbHost = "";
    private String logFolder = "/tmp";

    private WorkloadLogger logger;

    @Override
    public void init(ServletConfig config) throws ServletException {
        String initHost = config.getInitParameter("couchdbHost");
        String logFolder = config.getInitParameter("logFolder");
        if (!StringUtils.isEmpty(initHost)) {
            this.couchdbHost = initHost;
        }
        // Also support override the setting with env
        String envCouchdbHost = System.getenv("COUCHDB_HOST");
        if (!StringUtils.isEmpty(envCouchdbHost)) {
            this.couchdbHost = envCouchdbHost;
        }
        if (!StringUtils.isEmpty(logFolder)) {
            this.logFolder = logFolder;
        }
        String envlogFolder = System.getenv("LOG_FOLDER");
        if (!StringUtils.isEmpty(envlogFolder)) {
            this.logFolder = envlogFolder;
        }

        logger = new WorkloadLogger(this.logFolder);
    }

    protected void copy(HttpResponse from, HttpServletResponse to) throws IOException {
        for (Header h : from.getAllHeaders()) {
            if (h.getName().equalsIgnoreCase("transfer-encoding")) {
                continue;
            }
            to.setHeader(h.getName(), h.getValue());
        }
        to.setStatus(from.getStatusLine().getStatusCode());
        IOUtils.copy(from.getEntity().getContent(), to.getOutputStream());

    }

    protected URI createURI(HttpServletRequest req) {
        String path = req.getRequestURI();
        String query = req.getQueryString();
        String url = MessageFormat.format("http://{0}{1}", couchdbHost, path);
        if (!StringUtils.isEmpty(query)) {
            url += "?" + query;
        }
        return URI.create(url);
    }

    protected void forward(HttpServletRequest req, HttpServletResponse resp, HttpEntityEnclosingRequestBase to) throws IOException {
        URI uri = createURI(req);
        logger.begin(to.getMethod(),uri.toString());
        to.setURI(uri);

        for (String s : Collections.list(req.getHeaderNames())) {
            if (!s.equalsIgnoreCase("content-length")) {
                to.setHeader(s, req.getHeader(s));
            }
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        IOUtils.copy(req.getInputStream(), buffer);
        buffer.close();
        to.setEntity(new ByteArrayEntity(buffer.toByteArray()));

        HttpResponse response = httpclient.execute(to);
        copy(response, resp);
        logger.end(response.getStatusLine().getStatusCode());
    }

    protected void forward(HttpServletRequest req, HttpServletResponse resp, HttpRequestBase to) throws IOException {
        URI uri = createURI(req);
        logger.begin(to.getMethod(),uri.toString());
        to.setURI(uri);
        for (String s : Collections.list(req.getHeaderNames())) {
            to.setHeader(s, req.getHeader(s));
        }
        HttpResponse response = httpclient.execute(to);
        copy(response, resp);
        logger.end(response.getStatusLine().getStatusCode());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forward(req, resp, new HttpGet());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forward(req, resp, new HttpPost());
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forward(req, resp, new HttpHead());
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forward(req, resp, new HttpPut());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forward(req, resp, new HttpDelete());
    }

}
