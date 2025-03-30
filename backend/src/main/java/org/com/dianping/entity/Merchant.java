package org.com.dianping.entity;

import java.sql.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "Merchant")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true, unique = true)
    private long id;
    @Column(name = "merchantName", nullable = false, unique = true)
    private String merchantName;
    @Column(name = "type", nullable = false, unique = false)
    private String type;
    @Column(name = "score", nullable = false, unique = false)//根据网友评论，动态变化？
    private double score;
    @Column(name = "location", nullable = false, unique = true)
    private String location;
    @Column(name = "averageConsumption", nullable = false, unique = false)
    private double averageConsumption;
    @Column(name = "telephoneNumber", nullable = false, unique = true)
    private long telephoneNumber;
    @Column(name = "startTime", nullable = false, unique = false)
    private Date startTime;
    @Column(name = "endTime", nullable = false, unique = false)
    private Date endTime;
    @Column(name = "tags", nullable = false, unique = false, length = 500)
    private String tags;
    @Column(name = "photoUrl", nullable = false, unique = true)
    private String photoUrl;

    public Merchant() {
    }

    public Merchant(long id, String merchantName, String type, double score, String location,
                    double averageConsumption, long telephoneNumber, Date startTime, Date endTime, String tags, String photoUrl) {
        this.id = id;
        this.merchantName = merchantName;
        this.type = type;
        this.score = score;
        this.location = location;
        this.averageConsumption = averageConsumption;
        this.telephoneNumber = telephoneNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.tags = tags;
        this.photoUrl = photoUrl;
    }

    // 参数构造
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getAverageConsumption() {
        return averageConsumption;
    }

    public void setAverageConsumption(double averageConsumption) {
        this.averageConsumption = averageConsumption;
    }

    public long getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(long telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
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
