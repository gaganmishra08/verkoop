package com.verkoopapp.offlinechatdata;

import io.realm.RealmObject;

/**
 * Created by intel on 10-05-2019.
 */

public class ChatInboxRealModal extends RealmObject {

    /**
     * is_archive : 0
     *  is_delete : 0
     * chat_user_id :  2
     *  timestamp : 1557422749152
     *  types : 0
     * unread_count : 187
     * sender_id : 2
     *  receiver_id : 1
     *  item_id : 20
     *  message_id : 283
     *  username : Vijay Singh
     * profile_pic : public/images/users/2/1556206788profile_pic
     *  message : xzvczxvczcv
     *  user_id : 9
     *  is_sold : 0
     *  item_name : T-shirt
     *  offer_status : 5
     *  url  : public/images/items/15512789901551278991734.jpg
     */

    private int is_archive;
    private int is_delete;
    private String chat_user_id;
    private String timestamp;
    private int types;
    private int unread_count;
    private int sender_id;
    private int receiver_id;
    private int item_id;
    private int message_id;
    private String username;

    public int getIs_archive() {
        return is_archive;
    }

    public void setIs_archive(int is_archive) {
        this.is_archive = is_archive;
    }

    public int getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(int is_delete) {
        this.is_delete = is_delete;
    }

    public String getChat_user_id() {
        return chat_user_id;
    }

    public void setChat_user_id(String chat_user_id) {
        this.chat_user_id = chat_user_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getTypes() {
        return types;
    }

    public void setTypes(int types) {
        this.types = types;
    }

    public int getUnread_count() {
        return unread_count;
    }

    public void setUnread_count(int unread_count) {
        this.unread_count = unread_count;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getIs_sold() {
        return is_sold;
    }

    public void setIs_sold(int is_sold) {
        this.is_sold = is_sold;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getOffer_status() {
        return offer_status;
    }

    public void setOffer_status(int offer_status) {
        this.offer_status = offer_status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String profile_pic;
    private String message;
    private int user_id;
    private int is_sold;
    private String item_name;
    private int offer_status;
    private String url;


}
