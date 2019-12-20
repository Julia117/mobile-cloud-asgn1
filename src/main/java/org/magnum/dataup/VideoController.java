/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.dataup;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoSvc;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import retrofit.client.Response;
import retrofit.http.Path;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Controller
public class VideoController {

	private VideoSvc videoSvc = new VideoSvc();

	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH , method = RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList(){
		return videoSvc.getVideoList();
	}

	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video video){
		return videoSvc.addVideo(video);
	}

	@RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH, method = RequestMethod.POST,
//			consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody VideoStatus setVideoData(@PathVariable("id") long id, @RequestPart("data") MultipartFile video, HttpServletResponse response){
		return videoSvc.setVideoData(id, video, response);
	}

	@RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH , method = RequestMethod.GET)
	public HttpServletResponse getData(@PathVariable("id") long id, HttpServletResponse response){
		return videoSvc.getData(id, response);
	}



}
