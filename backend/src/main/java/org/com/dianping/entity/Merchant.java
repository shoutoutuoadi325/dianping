package org.com.dianping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "merchant")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "score", nullable = false)
    private float score;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "average_consumption", nullable = false)
    private float averageConsumption;

    @Column(name = "telephone_number", nullable = false)
    private long telephoneNumber;

    @Column(name = "start_time", nullable = false)
    private String startTime; // 营业时间开始，字符串类型

    @Column(name = "end_time", nullable = false)
    private String endTime; // 营业时间结束，字符串类型

    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getAverageConsumption() {
        return averageConsumption;
    }

    public void setAverageConsumption(float averageConsumption) {
        this.averageConsumption = averageConsumption;
    }

    public long getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(long telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}