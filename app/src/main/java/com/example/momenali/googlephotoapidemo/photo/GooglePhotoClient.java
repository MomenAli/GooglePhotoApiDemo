package com.example.momenali.googlephotoapidemo.photo;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * Created by Momen Ali on 7/10/2018.
 * Email: engmomenali@gmail.com
 * : engmomenali.freelancer@gmail.com
 */

public interface GooglePhotoClient {

  @POST("/v1/mediaItems:search")
  Call<List<PhotoInfo>> fetchLibraryContents(
          @HeaderMap Map<String,String> headers
          );
}
