package hao.daslab.blockchain.couchdblogger;


import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
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
import java.util.Collection;
import java.util.Collections;

public class LoggerServlet extends HttpServlet {

    CloseableHttpClient httpclient = HttpClients.createDefault();

    private String coachdbHost;

    private WorkloadLogger logger;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.coachdbHost = config.getInitParameter("couchdbHost");
    }

    protected void copy(HttpResponse from, HttpServletResponse to) throws IOException {
        for (Header h : from.getAllHeaders()) {
            to.setHeader(h.getName(), h.getValue());
        }
        IOUtils.copy(from.getEntity().getContent(), to.getOutputStream());
        to.setStatus(from.getStatusLine().getStatusCode());
    }

    protected void forward(HttpServletRequest req, HttpServletResponse resp, HttpEntityEnclosingRequestBase to) throws IOException {
        String path = req.getContextPath();
        String url = coachdbHost + path;
        to.setURI(URI.create(url));

        for (String s : Collections.list(req.getHeaderNames())) {
            to.setHeader(s, req.getHeader(s));
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        IOUtils.copy(req.getInputStream(), buffer);
        buffer.close();
        to.setEntity(new ByteArrayEntity(buffer.toByteArray()));

        HttpResponse response = httpclient.execute(to);
        copy(response, resp);
    }

    protected void forward(HttpServletRequest req, HttpServletResponse resp, HttpRequestBase to) throws IOException {
        String path = req.getContextPath();
        String url = coachdbHost + path;
        to.setURI(URI.create(url));

        for (String s : Collections.list(req.getHeaderNames())) {
            to.setHeader(s, req.getHeader(s));
        }

        HttpResponse response = httpclient.execute(to);
        copy(response, resp);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpGet get = new HttpGet();
        forward(req, resp, get);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpPost post = new HttpPost();
        forward(req, resp, post);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpHead head = new HttpHead();
        forward(req, resp, head);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpPut put = new HttpPut();
        forward(req, resp, put);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpDelete delete = new HttpDelete();
        forward(req, resp, delete);
    }

}
