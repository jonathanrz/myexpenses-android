package br.com.jonathanzanella.myexpenses.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jzanella on 6/5/16.
 */
public class Server {
    private Retrofit retrofit;

    public Server() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))

                .baseUrl("http://192.168.0.13:3000/")
                .build();
    }

    public SourceInterface sourceInterface() {
        return retrofit.create(SourceInterface.class);
    }
}
