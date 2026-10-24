package com.lostandfound.dto.item;

import com.lostandfound.model.Item.ItemStatus;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ItemRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    private ItemStatus status;

    @NotBlank
    private String imageUrl;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String locationName;

    @NotNull
    private LocalDateTime itemDateTime;

    @NotNull
    private String categoryName;

    // getters e setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public LocalDateTime getItemDateTime() { return itemDateTime; }
    public void setItemDateTime(LocalDateTime itemDateTime) { this.itemDateTime = itemDateTime; }

    public String getCategoryName() { return categoryName; }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}

