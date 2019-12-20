package org.magnum.dataup.model;

import org.magnum.dataup.VideoFileManager;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VideoSvc {

    private List<Video> videoList = new ArrayList<>();

    private static final AtomicLong currentId = new AtomicLong(0L);

    private Map<Long, Video> videos = new HashMap<>();

    private Video save(Video entity) {
        checkAndSetId(entity);
        videos.put(entity.getId(), entity);
        return entity;
    }

    private String getDataUrl(long videoId) {
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }

    private String getUrlBaseForLocalServer() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String base =
                "http://" + request.getServerName()
                        + ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
        return base;
    }

    private void checkAndSetId(Video entity) {
        if (entity.getId() == 0) {
            entity.setId(currentId.incrementAndGet());
        }
    }


    public Collection<Video> getVideoList() {
        return videoList;
    }

    public Video addVideo(@RequestBody Video v) {
        checkAndSetId(v);
        v.setDataUrl(getDataUrl(v.getId()));
        save(v);
        videoList.add(v);
        return v;
    }


    public VideoStatus setVideoData(long id, MultipartFile videoData, HttpServletResponse response){
        Video video = videos.get(id);

        if (video != null && videos.containsKey(id)) {
            try {
                VideoFileManager.get().saveVideoData(video, videoData.getInputStream());
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                e.printStackTrace();
            }
            response.setStatus(HttpServletResponse.SC_OK);
            return new VideoStatus(VideoStatus.VideoState.READY);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

    }

    public HttpServletResponse getData(long id, HttpServletResponse response) {
        if(videos.containsKey(id)){
            try {
                Video video = videos.get(id);
                response.setContentType("video/mp4");
                VideoFileManager.get().copyVideoData(video, response.getOutputStream());
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (IOException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return response;
    }

}
