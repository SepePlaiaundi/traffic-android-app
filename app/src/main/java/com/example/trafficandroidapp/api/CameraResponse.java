package com.example.trafficandroidapp.api;
import java.util.List;

public class CameraResponse {
    public int totalPages;
    public int currentPage;
    public List<Camera> cameras;

    public static class Camera {
        public String cameraId;
        public String cameraName;
        public String latitude;
        public String longitude;
        public String road;
        public String kilometer;
        public String urlImage;
    }
}