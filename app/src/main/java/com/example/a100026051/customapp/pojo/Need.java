package com.example.a100026051.customapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 100026051 on 11/1/16.
 */

public class Need implements Parcelable {

    private String needId;
    private String title;
    private String description;
    private String expireDate;
    private String expireTime;
    private String publishedBy; // publish user name
    private String helpedByUserId;    // helper user id
    private String helpedByUserName;    // helper name
    private String status;      // - draft, published, helped, done...
    private String address;
    private String type;        //
    private String publishTime;
    private String publishUserId;   // publish user id
    private String publisherPhone;  // publisher phone
    private String publisherUserFirebaseId;
    private String helpedTime;
    private String needFirebaseNodeId;  // unique parent node id for need
    private String helpedByUserFirebaseNodeId;  // unique parent node id for need
    private String ratingOfHelper;          // set by publisher
    private String ratingOfPublisher;       // set by helper
    private String feedbackFromHelper;       // set by helper
    private String feedbackFromPublisher;    // set by publisher

    private String completeTime;
    private String closeTime;
    private String reopenTime;
    private String publisherFeedbackTime;
    private String helperFeedbackTime;

    public Need(){

    }

    public Need(String questionId, String title, String description, String expireDate, String expireTime,
                String publishedBy, String helpedByUserId, String helpedByUserName, String status, String address, String type, String publishTime,
                String publishUserId, String publisherPhone, String publisherUserFirebaseId, String helpedTime, String needFirebaseNodeId,
                String helpedByUserFirebaseNodeId, String ratingOfHelper, String ratingOfPublisher, String feedbackFromHelper,
                String feedbackFromPublisher, String completeTime, String closeTime, String reopenTime,
                String publisherFeedbackTime, String helperFeedbackTime)
    {
        update(questionId, title, description,expireDate,expireTime,publishedBy,helpedByUserId,helpedByUserName, status,address,type,publishTime,
                publishUserId,publisherPhone,publisherUserFirebaseId, helpedTime, needFirebaseNodeId, helpedByUserFirebaseNodeId, ratingOfHelper,
                ratingOfPublisher, feedbackFromHelper, feedbackFromPublisher, completeTime, closeTime, reopenTime, publisherFeedbackTime,
                helperFeedbackTime);
    }

    public void update(String needId, String title, String description,String expireDate, String expireTime,
                       String publishedBy,String helpedByUserId, String helpedByUserName, String status, String address, String type,String publishTime,
                       String publishUserId,String publisherPhone, String publisherUserFirebaseId, String helpedTime, String needFirebaseNodeId,
                       String helpedByUserFirebaseNodeId, String ratingOfHelper, String ratingOfPublisher, String feedbackFromHelper,
                       String feedbackFromPublisher, String completeTime, String closeTime, String reopenTime, String publisherFeedbackTime,
                       String helperFeedbackTime)
    {
        this.needId = needId;
        this.title = title;
        this.description = description;
        this.expireDate = expireDate;
        this.expireTime = expireTime;
        this.publishedBy = publishedBy;
        this.helpedByUserId = helpedByUserId;
        this.helpedByUserName = helpedByUserName;
        this.status = status;
        this.address = address;
        this.type = type;
        this.publishTime = publishTime;
        this.publishUserId = publishUserId;
        this.publisherPhone = publisherPhone;
        this.publisherUserFirebaseId = publisherUserFirebaseId;
        this.helpedTime = helpedTime;
        this.needFirebaseNodeId = needFirebaseNodeId;
        this.helpedByUserFirebaseNodeId = helpedByUserFirebaseNodeId;
        this.ratingOfHelper = ratingOfHelper;
        this.ratingOfPublisher = ratingOfPublisher;
        this.feedbackFromHelper = feedbackFromHelper;
        this.feedbackFromPublisher = feedbackFromPublisher;
        this.completeTime = completeTime;
        this.closeTime = closeTime;
        this.reopenTime = reopenTime;
        this.publisherFeedbackTime = publisherFeedbackTime;
        this.helperFeedbackTime = helperFeedbackTime;
    }

    public String getHelperFeedbackTime() {
        return helperFeedbackTime;
    }

    public void setHelperFeedbackTime(String helperFeedbackTime) {
        this.helperFeedbackTime = helperFeedbackTime;
    }

    public String getPublisherFeedbackTime() {
        return publisherFeedbackTime;
    }

    public void setPublisherFeedbackTime(String publisherFeedbackTime) {
        this.publisherFeedbackTime = publisherFeedbackTime;
    }

    public String getReopenTime() {
        return reopenTime;
    }

    public void setReopenTime(String reopenTime) {
        this.reopenTime = reopenTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public String getFeedbackFromPublisher() {
        return feedbackFromPublisher;
    }

    public void setFeedbackFromPublisher(String feedbackFromPublisher) {
        this.feedbackFromPublisher = feedbackFromPublisher;
    }

    public String getFeedbackFromHelper() {
        return feedbackFromHelper;
    }

    public void setFeedbackFromHelper(String feedbackFromHelper) {
        this.feedbackFromHelper = feedbackFromHelper;
    }

    public String getRatingOfPublisher() {
        return ratingOfPublisher;
    }

    public void setRatingOfPublisher(String ratingOfPublisher) {
        this.ratingOfPublisher = ratingOfPublisher;
    }

    public String getRatingOfHelper() {
        return ratingOfHelper;
    }

    public void setRatingOfHelper(String ratingOfHelper) {
        this.ratingOfHelper = ratingOfHelper;
    }

    public String getHelpedByUserFirebaseNodeId() {
        return helpedByUserFirebaseNodeId;
    }

    public void setHelpedByUserFirebaseNodeId(String helpedByUserFirebaseNodeId) {
        this.helpedByUserFirebaseNodeId = helpedByUserFirebaseNodeId;
    }

    public String getNeedFirebaseNodeId() {
        return needFirebaseNodeId;
    }

    public void setNeedFirebaseNodeId(String needFirebaseNodeId) {
        this.needFirebaseNodeId = needFirebaseNodeId;
    }

    public String getHelpedTime() {
        return helpedTime;
    }

    public void setHelpedTime(String helpedTime) {
        this.helpedTime = helpedTime;
    }

    public String getHelpedByUserName() {
        return helpedByUserName;
    }

    public void setHelpedByUserName(String helpedByUserName) {
        this.helpedByUserName = helpedByUserName;
    }

    public String getPublisherUserFirebaseId() {
        return publisherUserFirebaseId;
    }

    public void setPublisherUserFirebaseId(String publisherUserFirebaseId) {
        this.publisherUserFirebaseId = publisherUserFirebaseId;
    }

    public String getPublisherPhone() {
        return publisherPhone;
    }

    public void setPublisherPhone(String publisherPhone) {
        this.publisherPhone = publisherPhone;
    }

    public String getPublishUserId() {
        return publishUserId;
    }

    public void setPublishUserId(String publishUserId) {
        this.publishUserId = publishUserId;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public String getHelpedByUserId() {
        return helpedByUserId;
    }

    public void setHelpedByUserId(String helpedByUserId) {
        this.helpedByUserId = helpedByUserId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNeedId() {
        return needId;
    }

    public String getDescription() {
        return description;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public String getTitle() {
        return title;
    }

    public String getPublishedBy() {
        return publishedBy;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
    }

    public void setNeedId(String needId) {
        this.needId = needId;
    }

    @Override
    public String toString() {
        return needId+";"+title+";"+description+";"+expireDate+";"+expireTime+";"+publishedBy+";"+helpedByUserId+";"+
                helpedByUserName+";"+ status+";"+address+";"+publishTime+";"+publishUserId+";"+publisherPhone+";"+
                publisherUserFirebaseId+";"+helpedTime+";"+needFirebaseNodeId+";"+helpedByUserFirebaseNodeId+";"+
                ratingOfHelper+";"+ratingOfPublisher+";"+feedbackFromHelper+";"+feedbackFromPublisher+";"+
                completeTime+";"+closeTime+";"+reopenTime+";"+publisherFeedbackTime+";"+helperFeedbackTime;
    }

    /** The following block of code parcels / unparcels data for distribution between activities */

    /** Describe the contents in the parcel --
     * interface forces implementation */
    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(needId);
        out.writeString(title);
        out.writeString(description);
        out.writeString(expireDate);
        out.writeString(expireTime);
        out.writeString(publishedBy);
        out.writeString(helpedByUserId);
        out.writeString(helpedByUserName);
        out.writeString(status);
        out.writeString(address);
        out.writeString(type);
        out.writeString(publishTime);
        out.writeString(publishUserId);
        out.writeString(publisherPhone);
        out.writeString(publisherUserFirebaseId);
        out.writeString(helpedTime);
        out.writeString(needFirebaseNodeId);
        out.writeString(helpedByUserFirebaseNodeId);
        out.writeString(ratingOfHelper);
        out.writeString(ratingOfPublisher);
        out.writeString(feedbackFromHelper);
        out.writeString(feedbackFromPublisher);
        out.writeString(completeTime);
        out.writeString(closeTime);
        out.writeString(reopenTime);
        out.writeString(publisherFeedbackTime);
        out.writeString(helperFeedbackTime);
    }

    public static final Parcelable.Creator<Need> CREATOR =
            new Parcelable.Creator<Need>()
            {
                public Need createFromParcel(Parcel in)
                {
                    return new Need(in);
                }

                public Need[] newArray(int size)
                {
                    return new Need[size];
                }
            };

    /** Private constructor called internally only */
    private Need(Parcel in)
    {
        needId = in.readString();
        title = in.readString();
        description = in.readString();
        expireDate = in.readString();
        expireTime = in.readString();
        publishedBy = in.readString();
        helpedByUserId = in.readString();
        helpedByUserName = in.readString();
        status = in.readString();
        address = in.readString();
        type = in.readString();
        publishTime = in.readString();
        publishUserId = in.readString();
        publisherPhone = in.readString();
        publisherUserFirebaseId = in.readString();
        helpedTime = in.readString();
        needFirebaseNodeId = in.readString();
        helpedByUserFirebaseNodeId = in.readString();
        ratingOfHelper = in.readString();
        ratingOfPublisher = in.readString();
        feedbackFromHelper = in.readString();
        feedbackFromPublisher = in.readString();
        completeTime = in.readString();
        closeTime = in.readString();
        reopenTime = in.readString();
        publisherFeedbackTime = in.readString();
        helperFeedbackTime = in.readString();
    }
}
