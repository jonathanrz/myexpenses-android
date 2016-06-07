package br.com.jonathanzanella.myexpenses.models;

/**
 * Created by jzanella on 6/6/16.
 */
public interface UnsyncModel {
    String getServerId();
    long getCreatedAt();
    long getUpdatedAt();
    String getData();
    void save();
}